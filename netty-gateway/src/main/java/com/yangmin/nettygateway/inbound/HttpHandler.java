package com.yangmin.nettygateway.inbound;

import com.yangmin.nettygateway.filter.HeaderHttpRequestFilter;
import com.yangmin.nettygateway.filter.HeaderHttpResponseFilter;
import com.yangmin.nettygateway.filter.HttpRequestFilter;
import com.yangmin.nettygateway.filter.HttpResponseFilter;
import com.yangmin.nettygateway.outbound.HttpOutBoundHandler;
import com.yangmin.nettygateway.router.HttpRouter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;


public class HttpHandler extends ChannelInboundHandlerAdapter {

    private List<HttpRouter> HttpRouters;

    private HttpOutBoundHandler handler;
    private HttpResponseFilter responseFilter = new HeaderHttpResponseFilter();
    private HttpRequestFilter requestFilter = new HeaderHttpRequestFilter();


    public HttpHandler(List<HttpRouter> httpRouters) {
        this.HttpRouters = httpRouters;
        handler = new HttpOutBoundHandler(HttpRouters);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        handler.handler(ctx,fullHttpRequest,requestFilter,responseFilter);
    }

}
