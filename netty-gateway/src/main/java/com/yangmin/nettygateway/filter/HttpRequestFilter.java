package com.yangmin.nettygateway.filter;

import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpRequestFilter {

    void filter(FullHttpRequest request);

}
