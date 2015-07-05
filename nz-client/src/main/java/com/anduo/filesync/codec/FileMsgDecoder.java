// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.filesync.codec;

import com.anduo.filesync.msg.FileMsg;
import com.anduo.filesync.msg.MsgType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import io.netty.util.CharsetUtil;
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
 * Summary: 解码器
 * Author : anduo@qq.com
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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        // 防止不发报文就关闭连接出现的错误
        if (!in.isReadable()) {
            return;
        }
        LOGGER.info(String.format("[%s]收到的的报文:[%s]", ctx.channel().remoteAddress(), ByteBufUtil.hexDump(in)));

        in.markReaderIndex();//标记初始位置
        if (in.readableBytes() < 3) {
            return;
        }
        byte msgType = in.readByte();
        short dataLength = in.readShort();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();//恢复到标记位
            return;
        }
        if (MsgType.HEADER == msgType) {
            //文件头
            // 1、文件名
            FileMsg msg = getMsgHeader(in);
            out.add(msg);
        } else if (MsgType.BODY == msgType) {
            //数据段
        } else if (MsgType.TAILER == msgType) {
            //数据尾端
        } else if (MsgType.COMMAND == msgType) {
            // 命令工具
        }

    }

    private FileMsg getMsgHeader(ByteBuf in) {
        long sumCountPackage = in.readLong();
        short fileNameLength = in.readShort();
        byte[] fileNameData = new byte[fileNameLength];
        in.readBytes(fileNameData);
        String filename = new String(fileNameData, CharsetUtil.UTF_8);

        // 2、数据源node节点名称
        short srcNodeLength = in.readShort();
        byte[] srcNodeData = new byte[srcNodeLength];
        in.readBytes(srcNodeData);
        String srcNode = new String(srcNodeData, CharsetUtil.UTF_8);

        //3、数据文件md5值
        short fileMd5Length = in.readShort();
        byte[] fileMd5Data = new byte[fileMd5Length];
        in.readBytes(fileMd5Data);
        String fileMd5 = new String(fileMd5Data, CharsetUtil.UTF_8);

        FileMsg msg = new FileMsg();
        msg.setSumCountPackage(sumCountPackage);
        msg.setFileName(filename);
        msg.setSrcNode(srcNode);
        msg.setFileMd5(fileMd5);
        return msg;
    }

}

}
