// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Summary: 自定义编码器
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/2
 * time   : 00:27
 */
public class MessageEncoder extends MessageToByteEncoder<Object> {

    private static final Logger LOGGER = LogManager.getLogger(MessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
            throws Exception {
        LOGGER.info(String.format("[%s]发送出的报文:[%s]",
                                  ctx.channel().localAddress().toString(),
                                  ByteBufUtil.hexDump((ByteBuf) msg)));
        out.writeBytes((byte[]) msg);
    }
}
