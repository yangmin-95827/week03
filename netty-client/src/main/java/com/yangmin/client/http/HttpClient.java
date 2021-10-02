package com.yangmin.client.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpClient {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            Bootstrap client = new Bootstrap();
            // 工作线程
            client.group(workerGroup)
                    // 指定用于创建客户端的Channel 和 NioServerSocketChannel 不同
                    .channel(NioSocketChannel.class)
                    .handler(new HttpInitializer());
            // 保持链接
            client.option(ChannelOption.SO_KEEPALIVE,true);

            final ChannelFuture channel = client.connect("127.0.0.1", 8888).sync();

            if (channel.isSuccess()) {
                System.out.println("connect server  success|");
            }
            channel.channel().closeFuture().sync();

        }finally {
            workerGroup.shutdownGracefully();
        }


    }

}
