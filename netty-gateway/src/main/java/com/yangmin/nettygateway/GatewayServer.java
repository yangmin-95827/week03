package com.yangmin.nettygateway;

import com.yangmin.nettygateway.inbound.HttpInitializer;
import com.yangmin.nettygateway.router.HttpRouter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.ArrayList;
import java.util.List;

public class GatewayServer {

    private int port;

    public GatewayServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        List<HttpRouter> routers = new ArrayList<>();
        routers.add(new HttpRouter("127.0.0.1",8801));
        routers.add(new HttpRouter("127.0.0.1",8802));

        try{

            ServerBootstrap server = new ServerBootstrap();
            server.channel(NioServerSocketChannel.class)
                    .group(bossGroup,workerGroup)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.SO_RCVBUF,32 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .option(EpollChannelOption.SO_REUSEPORT, true)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpInitializer(routers));

            final Channel channel = server.bind(port).sync().channel();

            channel.closeFuture().sync();


        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}
