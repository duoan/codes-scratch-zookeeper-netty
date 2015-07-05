// Copyright (C) 2015 meituan
// All rights reserved
package com.anduo.filesync.handler;

import com.anduo.filesync.msg.FileMsg;
import com.anduo.filesync.util.CleanUpUtil;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

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
 * Summary: 文件接收
 * Author : anduo@meituan.com
 * Version: 1.0
 * Date   : 15/7/5
 * time   : 16:55
 */
public class FileMsgRecvHandler extends SimpleChannelInboundHandler<FileMsg> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileMsgRecvHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent state = (IdleStateEvent) evt;
            LOGGER.info("Closing timeout connection : {}", ctx.channel().remoteAddress());
            if (state.state() == IdleState.READER_IDLE) {
                CleanUpUtil.closeOnFlush(ctx.channel());
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 收到文件消息
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileMsg msg)
            throws Exception {
        try {

        } catch (Exception e) {
            CleanUpUtil.closeOnFlush(ctx.channel());
            CleanUpUtil.closeOnFlush(ctx.channel().parent());
        }
    }

    /***
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     * channelActive 和 channelInActive 在后面的内容中讲述，这里先不做详细的描述
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        LOGGER.info("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
        ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
        super.channelActive(ctx);
    }

}
