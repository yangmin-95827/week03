package com.yangmin.nettygateway.filter;

import io.netty.handler.codec.http.FullHttpRequest;

public class HeaderHttpRequestFilter implements HttpRequestFilter {
    @Override
    public void filter(FullHttpRequest request) {
        request.headers().add("gateway-version","1.0");
    }
}
