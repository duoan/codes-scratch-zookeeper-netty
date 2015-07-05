// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.filesync.core;

import com.anduo.filesync.common.Constants;
import com.anduo.filesync.util.Threads;
import com.anduo.filesync.zk.ZKClient;
import com.anduo.filesync.zk.ZKClientCache;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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
 * Summary: 服务启动器
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/4
 * time   : 01:47
 */
@Component
public class SynServer implements DisposableBean, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynServer.class);

    @Value("${zk.config.nodes.path}")
    private String zkCfgNodesPath;
    @Value("${zk.config.commoncfg.path}")
    private String zkCfgCommoncfgPath;
    @Value("${zk.servers}")
    private String zkAddrs;
    private Map<String, FileMsgServer> serverNodeMap = Maps.newConcurrentMap();

    /**
     * 容器销毁的时候做相关线程和进程的清理工作
     *
     * @throws Exception
     */
    @Override
    public void destroy()
            throws Exception {
        for (String server : serverNodeMap.keySet()) {
            serverNodeMap.get(server).destroy();
        }
        ZKClientCache.get(zkAddrs).close();
    }

    /**
     * 当spring容器加载完毕之后执行以下动作
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet()
            throws Exception {
        //开始启动服务
        //1、从zk nodes配置文件的路径拿到相关配置信息
        Map<String, String> serversCfg = getNodesCfg();
        if (serversCfg == null) { return; }
        for (String serverNode : serversCfg.keySet()) {
            Integer bindPort = Integer.parseInt(serversCfg.get(serverNode));

            FileMsgServer server = new FileMsgServer(zkAddrs, serverNode, bindPort);
            server.start();
            serverNodeMap.put(serverNode, server);
            //暂停10s再启动下一台机器
            Threads.sleep(1, TimeUnit.SECONDS);
        }
        //强行下线测试
        serverNodeMap.get("Leader-3").destroy();
        serverNodeMap.remove("Leader-3");
    }

    private Map<String, String> getNodesCfg()
            throws Exception {
        ZKClient zkClient = ZKClientCache.get(zkAddrs);
        if (!zkClient.checkExist(zkCfgNodesPath)) {
            return null;
        }
        LOGGER.info("开始启动服务");
        String pathValue = zkClient.getPathValue(zkCfgNodesPath);
        if (StringUtils.isBlank(pathValue)) {
            return null;
        }
        //删除之前的节点注册信息
        if (zkClient.checkExist(Constants.NODE_ROOT)) {
            zkClient.deletAllPath(Constants.NODE_ROOT);
        }
        return Splitter.on(",").withKeyValueSeparator("=").split(StringUtils.remove(pathValue, " "));
    }
}

