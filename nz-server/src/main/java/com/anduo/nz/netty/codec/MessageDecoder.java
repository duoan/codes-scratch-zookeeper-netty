// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Summary: 自定义解码器(具体功能：可以解决TCP粘包分包问题).
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/2
 * time   : 00:33
 */
public class MessageDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        // 防止不发报文就关闭连接出现的错误
        if (!in.isReadable()) {
            return;
        }
        LOGGER.info(String.format("[%s]收到的的报文:[%s]", ctx.channel().localAddress().toString(), ByteBufUtil.hexDump(in)));
        byte[] ss = new byte[in.readableBytes()];
        in.readBytes(ss);
        out.add(in);
    }
}
