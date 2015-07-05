// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Summary: HelloWorldClient
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/1
 * time   : 23:15
 */
public class HelloWorldClient {
    static final boolean SSL  = System.getProperty("ssl") != null;
    static final String  HOST = System.getProperty("host", "127.0.0.1");
    static final int     PORT = Integer.parseInt(System.getProperty("port", "8007"));

    public static void main(String[] args)
            throws Exception {
        // Configure SSL.git
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = null;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch)
                                 throws Exception {
                             ChannelPipeline p = ch.pipeline();
                             if (sslCtx != null) {
                                 p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                             }
                             p.addLast(new ObjectEncoder(),
                                       new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                       new HelloWorldClientHandler());
                         }
                     });

            // Start the client.
            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();

            // Wait until the connection is closed.
            channelFuture.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}

class HelloWorldClientHandler extends ChannelInboundHandlerAdapter {

    private final String msg = "hello java world";

    /**
     * Creates a client-side handler.
     */
    public HelloWorldClientHandler() {
        //TODO
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("hello this is client");

    }
}
