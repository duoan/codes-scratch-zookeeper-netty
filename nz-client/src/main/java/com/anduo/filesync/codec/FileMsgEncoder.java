// Copyright (C) 2015 meituan
// All rights reserved
package com.anduo.filesync.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

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
 * Summary: 编码器
 * Author : anduo@meituan.com
 * Version: 1.0
 * Date   : 15/7/5
 * time   : 18:08
 */
public class FileMsgEncoder extends MessageToByteEncoder<Object> {
    /**
     * 日志组件.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileMsgEncoder.class);

    public FileMsgEncoder() {}

    /**
     * 编码.
     *
     * @param ctx Netty上下文
     * @param msg 信息实体
     * @param out 缓冲区
     *            <p>
     *            方法添加日期 :2014-10-11<br>
     *            创建者:刘源
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        LOGGER.info(String.format("[%s]发送出的报文:[%s]",
                                  ctx.channel().remoteAddress(),
                                  ByteBufUtil.hexDump((ByteBuf) msg)));
        out.writeBytes((byte[]) msg);
    }

}
