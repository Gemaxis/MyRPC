package com.custom.test;

import com.custom.common.service.BlogService;
import com.custom.common.service.Impl.BlogServiceImpl;
import com.custom.common.service.Impl.UserServiceImpl;
import com.custom.common.service.UserService;
import com.custom.server.NettyRPCServer;
import com.custom.server.RPCServer;
import com.custom.server.ServiceProvider;

/**
 * @author Gemaxis
 * @date 2024/07/10 12:01
 **/
public class TestZKNettyServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        BlogService blogService = new BlogServiceImpl();

//        ServiceProvider serviceProvider = new ServiceProvider();

        // 服务端需要把自己的ip，端口给注册中心
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService);
        serviceProvider.provideServiceInterface(blogService);

//        RpcServer rpcServer=new SimpleRPCServer(serviceProvider);
//        RpcServer rpcServer = new SimpleRPCServer(serviceProvider);
//        rpcServer.start(9999);
        // 线程池版的服务端的实现
//        ThreadPoolRPCRPCServer threadPoolRPCRPCServer = new ThreadPoolRPCRPCServer(serviceProvider);
//        threadPoolRPCRPCServer.start(9999);
        RPCServer nettyRPCServer = new NettyRPCServer(serviceProvider);
        nettyRPCServer.start(9999);
    }
}
