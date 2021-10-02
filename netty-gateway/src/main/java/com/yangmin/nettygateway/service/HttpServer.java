package com.yangmin.nettygateway.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);


    private boolean ssl;
    private int port;

    public HttpServer(boolean ssl, int port) {
        this.ssl = ssl;
        this.port = port;
    }

    public void run() throws CertificateException, SSLException, InterruptedException {
        SslContext sslContext;

        if(ssl){
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        }else{
            sslContext = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);

        ServerBootstrap b = new ServerBootstrap();

        try{
            b.option(ChannelOption.SO_BACKLOG,128)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.SO_REUSEADDR,true)
                    .option(ChannelOption.SO_RCVBUF,32 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .option(EpollChannelOption.SO_REUSEPORT,true)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);


            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new com.yangmin.nettygateway.service.HttpInitializer(sslContext,port));

            final Channel channel = b.bind(port).sync().channel();

            LOGGER.info("开启Netty Http服务器，监听地址和端口为{}",(ssl ? "https" : "http") + "://127.0.0.1:" +port + '/' );

            channel.closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

}
