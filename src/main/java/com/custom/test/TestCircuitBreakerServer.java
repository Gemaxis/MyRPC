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
 * @date 2024/07/15 12:01
 **/
public class TestCircuitBreakerServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        BlogService blogService = new BlogServiceImpl();

//        ServiceProvider serviceProvider=new ServiceProvider();
//        serviceProvider.provideServiceInterface(userService);

//        RpcServer rpcServer=new SimpleRPCServer(serviceProvider);

//        Map<String,Object> serviceProvider=new HashMap<>();
//        serviceProvider.put("com.custom.common.service.UserService",userService);
//        serviceProvider.put("com.custom.common.service.BlogService",blogService);

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService,true);
        serviceProvider.provideServiceInterface(blogService,true);

        RPCServer nettyRPCServer = new NettyRPCServer(serviceProvider);
        nettyRPCServer.start(9999);
    }
}
