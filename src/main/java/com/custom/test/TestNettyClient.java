package com.custom.test;

import com.custom.client.NettyRPCClient;
import com.custom.client.RPCClient;
import com.custom.client.RPCClientProxy;
import com.custom.client.SimpleRPCClient;
import com.custom.common.pojo.Blog;
import com.custom.common.pojo.User;
import com.custom.common.service.BlogService;
import com.custom.common.service.UserService;

/**
 * @author Gemaxis
 * @date 2024/07/10 17:17
 **/
public class TestNettyClient {
    public static void main(String[] args) {
        // 构建一个使用java Socket/ netty/....传输的客户端
        RPCClient nettyRPCClient = new NettyRPCClient("127.0.0.1", 9999);
        // 把这个客户端传入代理客户端
        RPCClientProxy rpcClientProxy = new RPCClientProxy(nettyRPCClient);
        // 代理客户端根据不同的服务，获得一个代理类， 并且这个代理类的方法以或者增强（封装数据，发送请求）
        UserService userService = rpcClientProxy.getProxy(UserService.class);
        // 调用查询方法
        User userByUserId = userService.getUserByUserId(10);
        System.out.println("从服务端得到的user为：" + userByUserId);

        // 调用插入方法
        User user = User.builder().userName("张三").sex(1).id(666).build();
        Integer id = userService.insertUserId(user);
        System.out.println("向服务端插入数据：" + id);

        // 获得博客服务的代理类

        BlogService blogService = rpcClientProxy.getProxy(BlogService.class);
        Blog blogById = blogService.getBlogById(100);
        System.out.println("从服务端得到的blog为：" + blogById);


    }
}
