package com.custom.test;

import com.custom.common.service.BlogService;
import com.custom.common.service.Impl.BlogServiceImpl;
import com.custom.common.service.Impl.UserServiceImpl;
import com.custom.common.service.UserService;
import com.custom.server.ServiceProvider;
import com.custom.server.thread.ThreadPoolRPCRPCServer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gemaxis
 * @date 2024/07/10 12:01
 **/
public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        BlogService blogService = new BlogServiceImpl();

//        ServiceProvider serviceProvider=new ServiceProvider();
//        serviceProvider.provideServiceInterface(userService);

//        RpcServer rpcServer=new SimpleRPCServer(serviceProvider);

//        Map<String,Object> serviceProvider=new HashMap<>();
//        serviceProvider.put("com.custom.common.service.UserService",userService);
//        serviceProvider.put("com.custom.common.service.BlogService",blogService);

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);
        serviceProvider.provideServiceInterface(blogService);

//        RpcServer rpcServer=new SimpleRPCServer(serviceProvider);
//        RpcServer rpcServer = new SimpleRPCServer(serviceProvider);
//        rpcServer.start(9999);
        // 线程池版的服务端的实现
        ThreadPoolRPCRPCServer threadPoolRPCRPCServer = new ThreadPoolRPCRPCServer(serviceProvider);
        threadPoolRPCRPCServer.start(9999);
    }
}
