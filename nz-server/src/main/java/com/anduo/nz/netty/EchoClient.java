// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.netty;

import com.anduo.nz.netty.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Summary: TODO 描述信息
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/2
 * time   : 00:40
 */
public class EchoClient {
    public void connect(int port, String host, final String filePath) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    // ch.pipeline().addLast(new StringEncoder());
                    // ch.pipeline().addLast(new FixedLengthFrameDecoder(100));
                    // ch.pipeline().addLast(new ChunkedWriteHandler());
                    // ch.pipeline().addLast(new StringDecoder());
                    // ch.pipeline().addLast(new EchoClientHandler());
                    // ch.pipeline().addLast(new
                    // LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                    // ch.pipeline().addLast(new LengthFieldPrepender(4,false));
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(new EchoClientHandler(filePath));
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
