// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.netty;

import com.anduo.nz.netty.handler.EchoServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Summary: 装载Netty处理链路.
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/2
 * time   : 00:20
 */
public class InitializerPipeline extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch)
            throws Exception {
        //使用Netty实现的线程池
        //        DefaultEventExecutorGroup e1=new DefaultEventExecutorGroup(16);
        ChannelPipeline pipeline = ch.pipeline();
        //		pipeline.addLast("decoder", new MessageDecoder());
        //      pipeline.addLast("encoder", new MessageEncoder());
        //		pipeline.addLast(e1,"handler", new CommonHandler());
        ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
        ch.pipeline().addLast(new ObjectEncoder());
        pipeline.addLast("handler", new EchoServerHandler());
    }
}
