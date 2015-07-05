package com.anduo.filesync.zk;

import java.util.List;

/**
 *
 */
public interface ZKClient {

    /**
     * 使用分布式锁执行任务
     *
     * @param path
     * @param getLockTimeout 获取锁超时时间（单位ms)
     * @param task
     * @auth anduo 2015年5月8日
     */
    public void distributeLock(String path, int getLockTimeout, Runnable task);

    /**
     * 获取给定路径的子节点
     *
     * @param path
     * @return
     * @throws Exception
     */
    List<String> getChildren(String path)
            throws Exception;

    /**
     * 监听给定路径子节点变化，并且获取子节点集合
     *
     * @param parent   父路径
     * @param listener 监听器
     * @param sync     该方法是否同步获取子节点，如果为true，
     *                 则方法返回的时候一定获得最新子节点集合
     * @return
     * @throws Exception
     */
    List<String> listenChildrenPath(final String parent, final NodeListener listener, final boolean sync)
            throws Exception;

    /**
     * 在给定路径下添加临时子节点
     *
     * @param parent
     * @param node
     * @return
     * @throws Exception
     */
    String addEphemeralNode(String parent, String node)
            throws Exception;

    /**
     * 添加临时节点
     *
     * @param path 临时节点全路径
     * @return
     * @throws Exception
     */
    String addEphemeralNode(String path)
            throws Exception;

    /**
     * 添加永久节点
     *
     * @param path 永久节点全路径
     * @throws Exception
     */
    void addPersistentNode(String path)
            throws Exception;

    /**
     * 是否已经连接上
     *
     * @return
     */
    boolean isConnected();

    /**
     * 注册连接状态监听器
     *
     * @param listener
     */
    void addConnectionChangeListenter(final ConnectionStateListener listener);

    /**
     * 删除给定路径
     *
     * @param path
     * @throws Exception
     */
    void deletePath(String path)
            throws Exception;

    //递归删除所有节点
    void deletAllPath(String path)
            throws Exception;

    /**
     * 判断给定路径是否存在
     *
     * @param path
     * @return
     */
    boolean checkExist(String path);

    /**
     * 添加临时节点数据
     * @param path
     * @param data
     * @throws Exception
     */
    void addEphemeralNodeData(String path, String data)
            throws Exception;

    /**
     *
     * @param path
     * @param data
     */
    void createPath(String path, String data);

    public void updatePathValue(String path, String data);

    String getPathValue(String path)
            throws Exception;

    /**
     * 关闭
     */
    void close();
}
