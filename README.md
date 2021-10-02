# week03
第三周作业



1.（必做）整合你上次作业的 httpclient/okhttp；
2.（选做）使用 netty 实现后端 http 访问（代替上一步骤）

​	这两道作业在 ` netty-client` ，默认访问的`8888`端口是自定义网关的端口

​		自定义网关在`netty-gateway`  , 包`com.yangmin.nettygateway.service`是模拟的后端服务，启动实在`Server01`和`Server02` 分别使用 8801 和 8802 端口

3.（必做）实现过滤器。

​	参考的是提供的例子，过滤器的定义在包`com.yangmin.nettygateway.filter`

​	过滤器的使用在`com.yangmin.nettygateway.outbound.HttpOutBoundHandler`的 65行和113行

4.（选做）实现路由。

 参考提供的例子，路由的定义在包`com.yangmin.nettygateway.router` 

路由的使用在类`com.yangmin.nettygateway.outbound.HttpOutBoundHandler`63行