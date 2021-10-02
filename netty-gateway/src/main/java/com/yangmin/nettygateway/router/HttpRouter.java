package com.yangmin.nettygateway.router;

public class HttpRouter {

    private String host;

    private int port;


    public HttpRouter(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHttpAddress(){
        return "http://" + host  + ":" + port + "/";
    }

}
