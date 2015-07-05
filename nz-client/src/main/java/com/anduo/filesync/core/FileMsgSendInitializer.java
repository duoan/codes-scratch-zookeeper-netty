// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.filesync.core;

import com.anduo.filesync.codec.FileMsgDecoder;
import com.anduo.filesync.codec.FileMsgEncoder;
import com.anduo.filesync.handler.FileMsgSendHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

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
 * Summary: FileServerInitializer
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/5
 * time   : 16:56
 */
public class FileMsgSendInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch)
            throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("encod", new FileMsgEncoder());//编码
        pipeline.addLast("idle", new IdleStateHandler(3, 0, 0));//心跳
        pipeline.addLast("handle", new FileMsgSendHandler());//send file msg
    }

}
