package com.yangmin.client.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class HttpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        URI uri = new URI("/test");
        FullHttpRequest request =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET,uri.toString());

        request.headers().add(HttpHeaderNames.CONTENT_LENGTH,String.valueOf(request.content().readableBytes()));
//        request.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE );
        request.headers().add(HttpHeaderNames.ACCEPT, HttpHeaderValues.TEXT_PLAIN);
        request.headers().add(HttpHeaderNames.HOST, "127.0.0.1:8088");

        ctx.writeAndFlush(request);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if( msg instanceof  DefaultLastHttpContent ){
            final DefaultLastHttpContent response = (DefaultLastHttpContent) msg;
            final ByteBuf content = response.content();
            final String result = content.toString(CharsetUtil.UTF_8);
            System.out.println(result);
        }

    }
}
