# 项目概述

仿照市场主流的RPC框架的设计思想，使用java语言手动实现一个高性能，高可用性的RPC框架

# V1

## 概述

服务端：

* 有一个User表

  UserServiceImpl 实现了UserService接口

  UserService里只有一个功能: getUserByUserId(Integer id)

客户端：

* 实现了两个功能

  传一个Id给服务端，服务端查询到User对象返回给客户端

  传递给一个User对象给服务端，服务端能够插入客户端传入的对象

## 总结

1. 定义更加通用的消息格式：Request 与 Response 格式， 从此可能调用不同的方法，与返回各种类型的数据。
2. 使用了动态代理进行不同服务方法的Request的封装。
3. 客户端更加松耦合，不再与特定的Service，host，port绑定。

## 存在的问题

1. 服务端只绑定了 UserService 服务，怎样完成多个服务的注册。
2. 服务端以BIO的方式性能低
3. 服务端功能太复杂：监听，处理。需要松耦合。

# V2 && V3

## 总结

1. 添加线程池版的服务端的实现 
2. 功能上新增了 BlogService 服务 
3. 服务端能够提供不同服务
4. 对客户端进行了重构，能够支持多种版本客户端的扩展 
5. 使用 netty 实现了客户端与服务端的通信

## 存在的问题

java自带序列化方式不够通用，不够高效

# V4 && V5

## 总结

1. 增加了 ObjectSerializer 与 JsonSerializer 两种序列化器
2. 引入 zookeeper 作为注册中心管理 ip 和 port
3. 新增随机和轮询两种负载均衡策略