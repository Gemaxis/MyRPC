package com.custom.server;

import com.custom.server.thread.WorkThread;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Gemaxis
 * @date 2024/07/10 11:21
 **/

@AllArgsConstructor
public class SimpleRPCServer implements RPCServer {
    private ServiceProvider serviceProvider;
    // 存着服务接口名-> service对象的map
//    private Map<String, Object> serviceProvider;
//    public SimpleRPCServer(Map<String, Object> serviceProvider) {
//        this.serviceProvider = serviceProvider;
//    }

    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器启动");
            // BIO 的方式监听Socket
            while (true) {
                // 如果没有连接，会堵塞在这里
                Socket socket = serverSocket.accept();
                // 有连接则新建线程处理
                new Thread(new WorkThread(socket, serviceProvider)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
