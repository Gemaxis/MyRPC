package com.custom.common.service.Impl;

import com.custom.common.pojo.User;
import com.custom.common.service.UserService;

import java.util.Random;
import java.util.UUID;

/**
 * @author Gemaxis
 * @date 2024/07/10 11:48
 **/
public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserId(Integer id) {
        System.out.println("客户端查询id:" + id + "的用户");
        // 模拟数据库查询
        Random random = new Random();
        User user = User.builder()
                .userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextInt(3)).build();
        System.out.println("数据库查询成功：" + user.toString());
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("插入数据" + user.toString() + "成功");
        return user.getId();
    }
}
