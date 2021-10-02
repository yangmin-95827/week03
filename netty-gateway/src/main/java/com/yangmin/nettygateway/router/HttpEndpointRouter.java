package com.yangmin.nettygateway.router;

import java.util.List;

public interface HttpEndpointRouter {

    HttpRouter getRouter(List<HttpRouter> routes);

}
