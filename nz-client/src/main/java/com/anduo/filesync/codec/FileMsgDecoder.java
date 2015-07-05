// Copyright (C) 2015 meituan
// All rights reserved
package com.anduo.filesync.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

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
 * Summary: TODO 描述信息
 * Author : anduo@meituan.com
 * Version: 1.0
 * Date   : 15/7/5
 * time   : 18:05
 */
public class FileMsgDecoder extends ByteToMessageDecoder {
    /**
     * 日志组件.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileMsgDecoder.class);

    public FileMsgDecoder() {
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buff, List<Object> objs)
            throws Exception {
        // 防止不发报文就关闭连接出现的错误
        if (!buff.isReadable()) {
            return;
        }
        LOGGER.info(String.format("[%s]收到的的报文:[%s]", ctx.channel().remoteAddress(), ByteBufUtil.hexDump(buff)));
        byte[] ss = new byte[buff.readableBytes()];
        buff.readBytes(ss);
        objs.add(ss);
    }

}
