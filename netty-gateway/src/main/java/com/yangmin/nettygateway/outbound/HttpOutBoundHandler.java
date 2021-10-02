package com.yangmin.nettygateway.outbound;

import com.yangmin.nettygateway.filter.HttpRequestFilter;
import com.yangmin.nettygateway.filter.HttpResponseFilter;
import com.yangmin.nettygateway.router.HttpEndpointRouter;
import com.yangmin.nettygateway.router.HttpRouter;
import com.yangmin.nettygateway.router.RandomHttpEndpointRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.concurrent.*;

public class HttpOutBoundHandler {

    private final ExecutorService proxyService;
    private final CloseableHttpAsyncClient httpclient;
    private List<HttpRouter> routers;
    private HttpEndpointRouter router = new RandomHttpEndpointRouter();




    public HttpOutBoundHandler(List<HttpRouter> httpRouters) {
        routers = httpRouters;
        int cores = Runtime.getRuntime().availableProcessors();
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();

        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                r -> new Thread(r,"proxyService"), handler);

        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();

        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response,context) -> 6000)
                .build();
        httpclient.start();

    }

    public void handler(ChannelHandlerContext ctx , FullHttpRequest fullHttpRequest ,
                        HttpRequestFilter requestFilter , HttpResponseFilter responseFilter){
        final HttpRouter router = this.router.getRouter(routers);
        String url = router.getHttpAddress()  + fullHttpRequest.uri();
        requestFilter.filter(fullHttpRequest);

        proxyService.submit( ()->{
            fetchGet(fullHttpRequest,ctx,url,responseFilter);
        });

    }


    private void fetchGet(FullHttpRequest request ,ChannelHandlerContext ctx , String url,HttpResponseFilter responseFilter){
        HttpGet get = new HttpGet(url);
        get.setHeader(HTTP.CONN_DIRECTIVE,HTTP.CONN_KEEP_ALIVE);
        get.setHeader("gateway-version", (String)request.headers().get("gateway-version"));

        httpclient.execute(get, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                handleResponse(request,httpResponse,ctx,responseFilter);
            }

            @Override
            public void failed(Exception e) {
                get.abort();
                e.printStackTrace();
            }

            @Override
            public void cancelled() {
                get.abort();
            }
        });

    }


    private void handleResponse(FullHttpRequest request, HttpResponse endpointResponse,
                                ChannelHandlerContext ctx,HttpResponseFilter filter){
        FullHttpResponse response = null;
        try{

            byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());

            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(body));

            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));

            filter.filter(response);


        }catch (Exception e){
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
            ctx.close();
        }finally {
            if(request != null){
                if(!HttpUtil.isKeepAlive(request)){
                    ctx.write(response).addListeners(ChannelFutureListener.CLOSE);
                }else{
                    ctx.write(response);
                }
            }
            ctx.flush();
        }

    }





}
