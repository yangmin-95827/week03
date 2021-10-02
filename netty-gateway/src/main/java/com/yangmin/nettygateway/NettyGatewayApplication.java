package com.yangmin.nettygateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class NettyGatewayApplication {

    public static void main(String[] args) {
        try {
            new GatewayServer(8888).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
