package com.yangmin.nettygateway.router;

import java.util.List;
import java.util.Random;

/**
 * 随机站点路由
 */
public class RandomHttpEndpointRouter implements HttpEndpointRouter {
    @Override
    public HttpRouter getRouter(List<HttpRouter> routes) {
        return routes.get(new Random().nextInt(routes.size()));
    }
}
