package com.custom.test;

import com.alibaba.fastjson2.JSON;
import com.custom.common.pojo.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gemaxis
 * @date 2024/08/27 16:37
 **/
// fastJson2序列化和反序列化时间: 447,578,700 纳秒
// fastJson2序列化后的大小: 437781 字节
public class FS2PerformanceTest {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            users.add(User.builder().userName("张三" + i).id(i).sex(1).build());
        }

        long startTime = System.nanoTime();
        String jsonString = JSON.toJSONString(users);
        System.out.println(jsonString);
        List<User> deserializedUsers = JSON.parseArray(jsonString, User.class);
        long endTime = System.nanoTime();
        System.out.println("fastJson2序列化和反序列化时间: " + (endTime - startTime) + " 纳秒");
        System.out.println("fastJson2序列化后的大小: " + jsonString.getBytes().length + " 字节");

    }
}
