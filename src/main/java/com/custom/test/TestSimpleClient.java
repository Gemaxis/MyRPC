package com.custom.test;

import com.custom.client.RPCClientProxy;
import com.custom.client.SimpleRPCClient;
import com.custom.common.pojo.User;
import com.custom.common.service.UserService;

/**
 * @author Gemaxis
 * @date 2024/07/10 17:17
 **/
public class TestSimpleClient {
    public static void main(String[] args) {
        SimpleRPCClient simpleRPCClient = new SimpleRPCClient("127.0.0.1", 9999);
        RPCClientProxy rpcClientProxy = new RPCClientProxy(simpleRPCClient);
        UserService userService = rpcClientProxy.getProxy(UserService.class);
        User userByUserId = userService.getUserByUserId(10);
        System.out.println("从服务端得到的user为：" + userByUserId);

    }
}
