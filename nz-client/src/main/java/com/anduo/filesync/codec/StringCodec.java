// Copyright (C) 2015 meituan
// All rights reserved
package com.anduo.filesync.codec;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

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
 * time   : 17:06
 */
public enum StringCodec {

    UTF8(new StrCodec(CharsetUtil.UTF_8));

    private final ChannelDuplexHandler c;

    StringCodec(ChannelDuplexHandler c) {
        this.c = c;
    }

    public ChannelDuplexHandler getCodec() {
        return c;
    }

    @ChannelHandler.Sharable
    private static class StrCodec extends CombinedChannelDuplexHandler<StringDecoder, StringEncoder> {
        private StrCodec(Charset charset) {
            super(new StringDecoder(charset), new StringEncoder(charset));
        }
    }

}
