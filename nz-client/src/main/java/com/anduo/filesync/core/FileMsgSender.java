// Copyright (C) 2015 meituan
// All rights reserved
package com.anduo.filesync.core;

import com.anduo.filesync.msg.FileMsg;
import com.anduo.filesync.zk.ZKClient;
import com.anduo.filesync.zk.ZKClientCache;
import com.anduo.nodesyn.Constants;
import com.anduo.nodesyn.util.NetUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Summary: 客户端负责连接服务端发送数据报文到服务端
 * Author : anduo@meituan.com
 * Version: 1.0
 * Date   : 15/7/5
 * time   : 17:37
 */
public class FileMsgSender {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileMsgSender.class);

    private final String zkNode;

    private final ZKClient zkClient;

    private volatile Channel channel;

    protected final Bootstrap bootstrap;

    private volatile boolean closed = false;

    private static final EventLoopGroup GROUP = new NioEventLoopGroup(1);

    public FileMsgSender(String zkAddrs, String zkNode) {
        this.zkNode = zkNode;
        this.zkClient = ZKClientCache.get(zkAddrs);
        this.bootstrap = new Bootstrap();
        bootstrap.group(GROUP);
        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new FileMsgSendInitializer());

    }

    /**
     * 传输文件
     * @param msg
     */
    public void send(FileMsg msg) {
        if (isActive()) {
            LOGGER.error("sender is not active");
            return;
        }
        LOGGER.info("send msg to server");
        channel.writeAndFlush(msg);
    }

    public boolean isActive() {
        return channel != null && !closed && channel.isActive();
    }

    public void close() {
        this.closed = true;
        if (this.channel != null) {
            this.channel.close();
        }
    }

    public void destroy()
            throws Exception {
        close();
        bootstrap.group().shutdownGracefully();
    }

    public void connect()
            throws Exception {
        String clientAddr = zkClient.getPathValue(ZKPaths.makePath(Constants.NODE_ROOT, zkNode));
        bootstrap.connect(NetUtil.createSocketAddress(clientAddr));
    }
}
