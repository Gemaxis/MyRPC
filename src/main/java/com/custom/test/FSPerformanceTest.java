package com.custom.test;

import com.alibaba.fastjson2.JSON;
import com.custom.common.pojo.User;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gemaxis
 * @date 2024/08/27 16:31
 **/
// fastJson序列化和反序列化时间: 216,438,900 纳秒
// fastJson序列化后的大小: 437781 字节
// Java序列化和反序列化时间: 171,000,400 纳秒
// Java序列化后的大小: 339123 字节
public class FSPerformanceTest {
    public static void main(String[] args) {
//        testUser();
        testDate();
    }

    private static void testDate() {
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            dates.add(LocalDate.now().plusDays(i));
        }

        // Test fastJson
        long startTime = System.nanoTime();
        String jsonString = JSON.toJSONString(dates);
        List<LocalDate> deserializedDates = JSON.parseArray(jsonString, LocalDate.class);
        long endTime = System.nanoTime();
        System.out.println("fastJson序列化和反序列化时间: " + (endTime - startTime) + " 纳秒");
        System.out.println("fastJson序列化后的大小: " + jsonString.getBytes().length + " 字节");

        // Test Java Serialization
        try {
            startTime = System.nanoTime();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(dates);
            objectOutputStream.flush();
            byte[] serializedData = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedData);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            List<LocalDate> deserializedDatesJava = (List<LocalDate>) objectInputStream.readObject();
            endTime = System.nanoTime();
            System.out.println("Java序列化和反序列化时间: " + (endTime - startTime) + " 纳秒");
            System.out.println("Java序列化后的大小: " + serializedData.length + " 字节");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void testUser() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            users.add(User.builder().userName("张三" + i).id(i).sex(1).build());
        }

        long startTime = System.nanoTime();
        String jsonString = JSON.toJSONString(users);
        List<User> deserializedUsers = JSON.parseArray(jsonString, User.class);
        long endTime = System.nanoTime();
        System.out.println("fastJson序列化和反序列化时间: " + (endTime - startTime) + " 纳秒");
        System.out.println("fastJson序列化后的大小: " + jsonString.getBytes().length + " 字节");
// Test Java Serialization
        try {
            startTime = System.nanoTime();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(users);
            objectOutputStream.flush();
            byte[] serializedData = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedData);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            List<User> deserializedUsersJava = (List<User>) objectInputStream.readObject();
            endTime = System.nanoTime();
            System.out.println("Java序列化和反序列化时间: " + (endTime - startTime) + " 纳秒");
            System.out.println("Java序列化后的大小: " + serializedData.length + " 字节");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
