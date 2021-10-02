package com.yangmin.nettygateway.filter;

import io.netty.handler.codec.http.FullHttpResponse;

public class HeaderHttpResponseFilter implements  HttpResponseFilter{
    @Override
    public void filter(FullHttpResponse response) {
        response.headers().add("gateway","netty");
    }
}
