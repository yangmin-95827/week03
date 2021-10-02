package com.yangmin.nettygateway.service;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class HttpHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHandler.class);

    private int port;

    public HttpHandler(int port) {
        this.port = port;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        long statTime = System.currentTimeMillis();

        try{

            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;

            final String uri = fullHttpRequest.uri();

            if(uri.contains("/test")){
                handlerTest(fullHttpRequest,ctx);
            }

        }finally {
            ReferenceCountUtil.release(msg);
        }

    }


    private void handlerTest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx){
        FullHttpResponse response = null;
        try{
            String value = "Hello 我的服务端口是" + this.port;

            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(value.getBytes(StandardCharsets.UTF_8)));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length",response.content().readableBytes());

        }catch (Exception e){
            LOGGER.error("测试接口出错：",e);
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.NO_CONTENT);
        }finally {
            if(fullHttpRequest != null){
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
