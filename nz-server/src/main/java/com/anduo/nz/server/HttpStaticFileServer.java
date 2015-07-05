// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Summary: HttpStaticFileServer
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/1
 * time   : 23:39
 */
public class HttpStaticFileServer {

    private static final Logger LOGGER = LogManager.getLogger(HttpStaticFileServer.class);

    private final int port;

    public HttpStaticFileServer(int port) {
        this.port = port;
    }

    public void run()
            throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new HttpStaticFileServerInitializer());

            Channel ch = b.bind(port).sync().channel();
            LOGGER.info("File server started at port " + port + '.');
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args)
            throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new HttpStaticFileServer(port).run();
    }
}
