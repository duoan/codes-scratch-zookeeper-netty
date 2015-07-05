// Copyright (C) 2015 meituan
// All rights reserved
package com.anduo.filesync.core;

import com.anduo.filesync.common.Constants;
import com.anduo.filesync.util.NetUtil;
import com.anduo.filesync.zk.ChangedEvent;
import com.anduo.filesync.zk.ZKClient;
import com.anduo.filesync.zk.ZKClientCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.RandomUtils;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑, 永无BUG!
 * 　　　　┃　　　┃Code is far away from bug with the animal protecting
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━感觉萌萌哒━━━━━━
 * Summary: 服务端负责启动服务并且监听node节点的server，如果节点的id大于自己的id就建立一个client，并传输文件
 * Author : anduo@meituan.com
 * Version: 1.0
 * Date   : 15/7/5
 * time   : 16:59
 */
public class FileMsgServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileMsgServer.class);
    static final         int    PORT   = Integer.parseInt(System.getProperty("port", "1843"));
    private final    ServerBootstrap bootstrap;
    private final    String          serverName;
    //NettyServer绑定端口号
    private final    int             bindPort;
    private final    String          zkAddrs;
    private final    ZKClient        zkClient;
    //当前服务所注册的zk节点名
    private volatile String          zkNode;
    private Set<String> lastNodes = Sets.newHashSet();
    Map<String, FileMsgSender> clientMap = Maps.newConcurrentMap();

    //数据发送并发控制
    private final ExecutorService senderThreadPool;

    public FileMsgServer(String zkAddrs, String serverName, int bindPort) {
        this.zkAddrs = zkAddrs;
        this.zkClient = ZKClientCache.get(zkAddrs);
        this.serverName = serverName;
        this.bindPort = bindPort;
        this.senderThreadPool = Executors
                .newFixedThreadPool(5, runnable -> new Thread(runnable, serverName + "-send-thread"));
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup();
        this.bootstrap = new ServerBootstrap().group(boss, worker);
        this.bootstrap.channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG));
        this.bootstrap.childHandler(new FileRecvInitializer());
    }

    public void start()
            throws InterruptedException {
        bootstrap.bind(bindPort).addListener(future -> register()).addListener(future -> initLocalFiles())
                 .addListener(future -> addNodeWatchListenner()).sync().channel().closeFuture().sync();
    }

    //注册节点到zk
    private void register() {
        try {
            if (!zkClient.checkExist(Constants.NODE_ROOT)) {
                zkClient.addPersistentNode(Constants.NODE_ROOT);
            }
            String serverAddr = NetUtil.getLocalAddress().getHostAddress() + ":" + bindPort;
            String node = ZKPaths.makePath(Constants.NODE_ROOT, serverName);
            if (zkClient.checkExist(node)) {
                try {
                    zkClient.deletePath(node);
                } catch (Exception e) {
                    LOGGER.error("删除节点{}失败", node);
                }
            }
            //注册节点，并将节点的服务地址写入节点数据
            zkClient.addEphemeralNodeData(node, serverAddr);
            if (LOGGER.isDebugEnabled()) {
                String pathValue = zkClient.getPathValue(node);
                LOGGER.debug("ZK _node {} , value {}", node, pathValue);
            }
            this.zkNode = node;
            this.lastNodes.add(serverName);
            LOGGER.info("ZK 注册成功, _node {}", node);
        } catch (Exception e) {
            LOGGER.error("注册服务失败", e);
        }
    }

    private void initLocalFiles()
            throws IOException {
        LOGGER.info("{}开始生成随机文件", serverName);
        //随机在data目录下生成100个随机文本文件，内容长度随机控制在10K到10M，内容最后都为You are Best! 这些是待发送文件
        String dir = Constants.DEFAULT_DATA_FILE_PATH + "/source/" + serverName;

        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < Constants.RANDOM_FILE_COUNT; i++) {
            final String fileName = dir + "/data-" + i + ".txt";
            createRandomFile(fileName);
            Runnable task = () -> {
                try {
                    createRandomFile(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            threadPool.submit(task);
        }
        threadPool.shutdown();
        LOGGER.info("{}生成随机文件结束,路径{}", serverName, dir);
    }

    private void createRandomFile(String filePath)
            throws IOException {
        String lastStr = "You are Best!";
        //自己补全字母和数字,这个字符数是作为随机取值的源
        String str = "012345678vasdjhklsadfqwiurewopt";
        File distFile = new File(filePath);
        if (!distFile.getParentFile().exists()) { distFile.getParentFile().mkdirs(); }
        PrintWriter pw = new PrintWriter(new FileWriter(filePath));
        int len = str.length();
        int size = RandomUtils.nextInt(1, 2);
        //每次写入10K,写入1024次就是 10M
        for (int i = 0; i < size; i++) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < 10240; j++) {
                s.append(str.charAt((int) (Math.random() * len)));
            }
            pw.println(s.toString());
        }

        pw.write(lastStr);
        pw.close();
    }

    /***
     * 监听节点
     */
    private void addNodeWatchListenner() {
        try {
            zkClient.listenChildrenPath(Constants.NODE_ROOT, (sender, event) -> {
                ImmutableSet<String> diffNodeServers = getDiffNodes();
                if (event.getType().equals(ChangedEvent.Type.CHILD_ADDED)) {
                    LOGGER.info("{}监听到有新节点添加进来了!", serverName);
                    sendFileMsgs(diffNodeServers);
                }
            }, true);
        } catch (Exception e) {
            LOGGER.error("{}添加监听器失败", serverName);
        }
    }

    private void sendFileMsgs(ImmutableSet<String> diffNodes)
            throws Exception {
        for (String _node : diffNodes) {
            try {
                int currentNodeId = Integer.parseInt(serverName.split("-")[1]);
                int diffNodeId = Integer.parseInt(_node.split("-")[1]);
                if (currentNodeId > diffNodeId) {
                    LOGGER.info("当前节点{}大于新添加的节点{},尝试连接！", serverName, _node);
                    FileMsgSender sender = new FileMsgSender(zkAddrs, _node);
                }
            } catch (Exception e) {
                LOGGER.error("发送数据失败!");
            }
        }
    }

    private ImmutableSet<String> getDiffNodes()
            throws Exception {
        Set<String> currentNodes = Sets.newHashSet(zkClient.getChildren(Constants.NODE_ROOT));
        ImmutableSet<String> diffNodes = Sets.difference(currentNodes, lastNodes).immutableCopy();
        lastNodes = currentNodes;
        return diffNodes;
    }

    public void destroy()
            throws Exception {
        if (zkNode != null) {
            zkClient.deletePath(zkNode);
        }
        Set<String> clientNodes = clientMap.keySet();
        for (String clientNode : clientNodes) {
            clientMap.get(clientNode).destroy();
            clientMap.remove(clientNode);
        }
        senderThreadPool.shutdown();
        bootstrap.group().shutdownGracefully();
        bootstrap.childGroup().shutdownGracefully();
    }

    public static void main(String[] args)
            throws InterruptedException {
        LOGGER.info("Server set up on port: {}", PORT);
        new FileMsgServer("127.0.0.1:2181", "server-1", 8411).start();
    }
}
