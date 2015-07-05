// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.netty.handler;

import com.anduo.nz.entity.EchoFile;
import com.anduo.nz.netty.NettyProtoServer;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Summary: EchoServerHandler
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/2
 * time   : 00:23
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LogManager.getLogger(NettyProtoServer.class);

    private String file_dir   = "D:";
    private int    dataLength = 1024;

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        LOGGER.debug(String.format("[%s]\n========打开连接=======", ctx.channel().localAddress().toString()));
        ctx.channel().attr(AttributeKey.valueOf("haha")).set("1");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception {
        LOGGER.debug(String.format("[%s]\n========关闭连接=======", ctx.channel().localAddress().toString()));
        LOGGER.debug(ctx.channel().remoteAddress().toString());
        LOGGER.debug(ctx.channel().attr(AttributeKey.valueOf("haha")).get().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof EchoFile) {
            EchoFile ef = (EchoFile) msg;
            int SumCountPackage = ef.getSumCountPackage();
            int countPackage = ef.getCountPackage();
            byte[] bytes = ef.getBytes();
            String md5 = ef.getFile_md5();//文件名

            String path = file_dir + File.separator + md5;
            File file = new File(path);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(countPackage * dataLength - dataLength);
            randomAccessFile.write(bytes);
            LOGGER.debug("总包数：" + ef.getSumCountPackage());
            LOGGER.debug("收到第" + countPackage + "包");
            LOGGER.debug("本包字节数:" + bytes.length);
            countPackage = countPackage + 1;

            if (countPackage <= SumCountPackage) {
                ef.setCountPackage(countPackage);
                ctx.writeAndFlush(ef);
                randomAccessFile.close();
            } else {
                randomAccessFile.close();
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        LOGGER.error("[" + ctx.channel().localAddress().toString() + "]" + "通讯异常:", cause);
        ctx.close();
    }
}
