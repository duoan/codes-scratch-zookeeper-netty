package com.anduo.filesync.zk;

import com.google.common.util.concurrent.MoreExecutors;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.DebugUtils;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
class ZKClientImpl implements ZKClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKClientImpl.class);

    public static final int MAX_RETRIES       = 3000;
    public static final int BASE_SLEEP_TIMEMS = 3000;

    private final CuratorFramework client;
    private final ExecutorService EVENT_THREAD_POOL = Executors
            .newFixedThreadPool(1, ThreadUtils.newThreadFactory("PathChildrenCache"));
    private final ExecutorService SAME_EXECUTOR     = MoreExecutors.sameThreadExecutor();
    private final AtomicInteger   REFERENCE_COUNT   = new AtomicInteger(0);

    private Lock _lock = new ReentrantLock(true);

    public ZKClientImpl(String adds) {
        System.setProperty(DebugUtils.PROPERTY_DONT_LOG_CONNECTION_ISSUES, "false");
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIMEMS, MAX_RETRIES);
        this.client = CuratorFrameworkFactory.builder().connectString(adds).retryPolicy(retryPolicy)
                                             .connectionTimeoutMs(5000).build();
        waitUntilZkStart();

    }

    private void waitUntilZkStart() {
        CountDownLatch latch = new CountDownLatch(1);
        addConnectionChangeListenter(new ConnectionWatcher(latch));
        client.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.error("start zk latch.await() error", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 使用分布式锁执行任务
     *
     * @param path
     * @param getLockTimeout 获取锁超时时间（单位ms)
     * @param task
     * @auth anduo 2015年5月8日
     */
    public void distributeLock(String path, int getLockTimeout, Runnable task) {
        InterProcessMutex lock = new InterProcessMutex(client, path);
        try {
            LOGGER.debug("尝试获取锁。。。");
            if (lock.acquire(getLockTimeout, TimeUnit.MILLISECONDS)) {
                try {
                    LOGGER.debug("获得锁，开始执行任务。。。");
                    task.run();
                } finally {
                    lock.release();
                    LOGGER.debug("释放锁,path:" + path);
                }
            } else {
                LOGGER.info("任务执行失败，在时间：" + getLockTimeout + "ms内，未获得分布式锁!");
            }
        } catch (Exception e) {
            LOGGER.error("执行分布式锁任务异常。", e);
        }

    }

    @Override
    public List<String> getChildren(String path)
            throws Exception {
        return client.getChildren().forPath(path);
    }

    @Override
    public List<String> listenChildrenPath(final String parent, final NodeListener listener, final boolean sync)
            throws Exception {
        PathChildrenCache cache = new PathChildrenCache(client, parent, false, false, EVENT_THREAD_POOL);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework c, PathChildrenCacheEvent e)
                    throws Exception {
                if (e.getData() == null) { return; }
                switch (e.getType()) {
                    case CHILD_ADDED:
                        listener.nodeChanged(ZKClientImpl.this,
                                             new ChangedEvent(e.getData().getPath(), ChangedEvent.Type.CHILD_ADDED));
                        break;
                    case CHILD_REMOVED:
                        listener.nodeChanged(ZKClientImpl.this,
                                             new ChangedEvent(e.getData().getPath(), ChangedEvent.Type.CHILD_REMOVED));
                        break;
                    case CHILD_UPDATED:
                        listener.nodeChanged(ZKClientImpl.this,
                                             new ChangedEvent(e.getData().getPath(), ChangedEvent.Type.CHILD_UPDATED));
                        break;
                }
            }
        }, SAME_EXECUTOR);
        PathChildrenCache.StartMode mode = sync ? PathChildrenCache.StartMode.BUILD_INITIAL_CACHE : PathChildrenCache.StartMode.NORMAL;
        cache.start(mode);
        List<ChildData> children = cache.getCurrentData();
        List<String> result = new ArrayList<String>();
        for (ChildData child : children) {
            result.add(child.getPath());
        }
        return result;
    }

    @Override
    public String addEphemeralNode(String parent, String node)
            throws Exception {
        return addEphemeralNode(ZKPaths.makePath(parent, node));
    }

    @Override
    public String addEphemeralNode(String path)
            throws Exception {
        return client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
    }

    @Override
    public void addPersistentNode(String path)
            throws Exception {
        try {
            client.newNamespaceAwareEnsurePath(path).ensure(client.getZookeeperClient());
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        } catch (KeeperException.NodeExistsException e) {
            LOGGER.warn("Node already exists: {}", path);
        } catch (Exception e) {
            throw new Exception("addPersistentNode error", e);
        }
    }

    @Override
    public void addEphemeralNodeData(String path, String data)
            throws Exception {
        try {
            client.newNamespaceAwareEnsurePath(path).ensure(client.getZookeeperClient());
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes());
        } catch (KeeperException.NodeExistsException e) {
            LOGGER.warn("Node already exists: {}", path);
        } catch (Exception e) {
            throw new Exception("addEphemeralNodeData error", e);
        }
    }

    /**
     * 创建 节点
     *
     * @param path
     * @param data
     * @auth anduo 2015年5月8日
     */
    @Override
    public void createPath(String path, String data) {
        try {
            client.newNamespaceAwareEnsurePath(path).ensure(client.getZookeeperClient());
            client.setData().forPath(path, data.getBytes());
        } catch (Exception ex) {
            LOGGER.error("创建节点异常,path:" + path + " , data:" + data, ex);
        }
    }

    /**
     * 设置节点值
     *
     * @param path
     * @param data
     * @auth anduo 2015年5月8日
     */
    @Override
    public void updatePathValue(String path, String data) {
        try {
            LOGGER.debug("设置结点值,path:" + path + "，data:" + data);
            this.client.setData().forPath(path, data.getBytes("UTF-8"));
        } catch (Exception e) {
            LOGGER.error("设置zookeeper节点值异常，path:" + path + "，data" + data, e);
        }
    }

    /**
     * 获取节点值
     *
     * @param path
     * @return
     * @throws Exception
     * @auth anduo 2015年5月7日
     */
    @Override
    public String getPathValue(String path)
            throws Exception {
        if (!checkExist(path)) {
            throw new RuntimeException("Path " + path + " does not exists.");
        }
        return new String(client.getData().forPath(path), "UTF-8");
    }

    @PreDestroy
    @Override
    public void close() {
        LOGGER.info("Call close of ZKClient, reference count is: {}", REFERENCE_COUNT.get());
        if (REFERENCE_COUNT.decrementAndGet() == 0) {
            client.close();
            LOGGER.info("ZKClient is closed");
        }
    }

    @Override
    public boolean isConnected() {
        return client.getZookeeperClient().isConnected();
    }

    /**
     * @param listener
     */
    @Override
    public void addConnectionChangeListenter(final ConnectionStateListener listener) {
        if (listener != null) {
            client.getConnectionStateListenable()
                  .addListener((sender, state) -> listener.stateChanged(ZKClientImpl.this, convertTo(state)));
        }
    }

    private ConnectionState convertTo(org.apache.curator.framework.state.ConnectionState state) {
        switch (state) {
            case CONNECTED:
                return ConnectionState.CONNECTED;
            case SUSPENDED:
                return ConnectionState.SUSPENDED;
            case RECONNECTED:
                return ConnectionState.RECONNECTED;
            case LOST:
                return ConnectionState.LOST;
            default:
                return ConnectionState.READ_ONLY;
        }
    }

    /**
     * 删除一个znode,如果该znode下面有子节点则会抛异常
     *
     * @param path
     * @throws Exception
     */
    @Override
    public void deletePath(String path)
            throws Exception {
        client.delete().forPath(path);
    }

    @Override
    public void deletAllPath(String path)
            throws Exception {
        List<String> children = client.getChildren().forPath(path);
        if (children == null || children.size() == 0) {
            deletePath(path);
            return;
        }
        for (String child : children) {
            deletAllPath(ZKPaths.makePath(path, child));
        }

    }

    private class ConnectionWatcher implements ConnectionStateListener {
        CountDownLatch latch;

        ConnectionWatcher(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void stateChanged(ZKClient client, ConnectionState newState) {
            if (newState == newState.CONNECTED) {
                latch.countDown();
            }
        }
    }

    @Override
    public boolean checkExist(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);
            return stat != null;
        } catch (Exception e) {
            LOGGER.error("check exist error", e);
            return false;
        }
    }

    protected void incrementReference() {
        REFERENCE_COUNT.incrementAndGet();
    }

}

