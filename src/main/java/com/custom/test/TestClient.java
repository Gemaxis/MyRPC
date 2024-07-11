package com.custom.test;

import com.custom.client.ClientProxy;
import com.custom.common.pojo.Blog;
import com.custom.common.pojo.User;
import com.custom.common.service.BlogService;
import com.custom.common.service.UserService;

/**
 * @author Gemaxis
 * @date 2024/07/10 11:43
 **/
public class TestClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        System.out.println("从服务器得到的user=" + user.toString());

        User insertUser = User.builder()
                .id(666)
                .userName("测试插入")
                .sex(0)
                .build();

        Integer id = proxy.insertUserId(insertUser);
        System.out.println("向服务器插入user的id" + id);

        BlogService blogService = clientProxy.getProxy(BlogService.class);
        Blog blogById = blogService.getBlogById(100);
        System.out.println("从服务端得到的blog为：" + blogById);
    }
}
