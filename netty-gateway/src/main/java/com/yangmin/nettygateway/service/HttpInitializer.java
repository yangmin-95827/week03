package com.yangmin.nettygateway.service;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class HttpInitializer  extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;
    private int port;

    public HttpInitializer(SslContext sslContext,int port) {
        this.sslContext = sslContext;
        this.port = port;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        final ChannelPipeline pipeline = socketChannel.pipeline();
        if(sslContext != null){
            pipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024) );
        pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new HttpHandler(port));
    }
}
