package com.yangmin.nettygateway.inbound;



import com.yangmin.nettygateway.router.HttpRouter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.List;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    private List<HttpRouter> HttpRouters;

    public HttpInitializer(List<HttpRouter> httpRouters) {
        HttpRouters = httpRouters;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator( 1024 * 1024 ));
        pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new HttpHandler(HttpRouters));


    }
}
