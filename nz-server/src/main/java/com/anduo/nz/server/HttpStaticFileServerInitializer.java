// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsHandler;

import java.util.logging.SocketHandler;

/**
 * Summary: HttpStaticFileServerInitializer
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/1
 * time   : 23:31
 */
public class HttpStaticFileServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch)
            throws Exception {
        // Create a default pipeline implementation.
        CorsConfig corsConfig = CorsConfig.withAnyOrigin().build();
        ChannelPipeline pipeline = ch.pipeline();
        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //pipeline.addLast("ssl", new SslHandler(engine));

        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(8388608)); // 8MB
        //pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("cors", new CorsHandler(corsConfig));
        pipeline.addLast("handler", new HttpStaticFileServerHandler());
    }
}
