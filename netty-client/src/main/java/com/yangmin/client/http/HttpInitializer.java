package com.yangmin.client.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        final ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpResponseDecoder());
        pipeline.addLast(new HttpRequestEncoder());
//        pipeline.addLast(new HttpObjectAggregator( 1024 * 1024 ));
//        pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new HttpHandler());
    }
}
