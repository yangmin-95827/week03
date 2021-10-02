package com.yangmin.nettygateway.service;

public class Server02 {

    public static void main(String[] args) {
        HttpServer server = new HttpServer(false,8802);
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
