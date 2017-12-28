package com.grpc;

import grpc.example.HelloRequest;
import grpc.example.HelloResponse;
import grpc.example.HelloServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class HelloServer {

    //服务接口
    private int port = 50051;

    //服务端服务管理器
    private Server server;

    private void start() throws IOException{
        //初始化并启动任务
        server = ServerBuilder.forPort(port)
                .addService(new HelloServerImpl())
                .build()
                .start();

        //注册钩子,JVM推出的时候停止服务
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                HelloServer.this.stop();
            }
        });
    }

    //停止服务
    private void stop() {
        if (server != null){
            server.shutdown();
        }
    }

    //阻塞,一直到退出程序
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null){
            server.awaitTermination();
        }
    }

    //实现,定义一个实现服务的类
    private class HelloServerImpl extends HelloServiceGrpc.HelloServiceImplBase {
        public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver){
            //构建返回结果对象
            HelloResponse reply = HelloResponse.newBuilder().setMessage("hello" + req.getName()).build();
            //将返回结果传入Steam，返回给调用者
            responseObserver.onNext(reply);
            //通知Steam结束
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws IOException,InterruptedException{
        final HelloServer server = new HelloServer();
        server.start();
        server.blockUntilShutdown();
    }

}
