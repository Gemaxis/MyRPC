package com.custom.server;

import com.custom.common.service.Impl.UserServiceImpl;
import com.custom.common.service.UserService;

/**
 * @author Gemaxis
 * @date 2024/07/10 12:01
 **/
public class TestServer {
    public static void main(String[] args) {
        UserService userService=new UserServiceImpl();

        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer=new SimpleRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
