# 项目概述

仿照市场主流的RPC框架的设计思想，使用java语言手动实现一个高性能，高可用性的RPC框架。

项目可以分为调用方（client）和提供方（server），client 端只需要调用接口即可，最终调用信息会通过网络传输到 server，server 通过解码后反射调用对应的方法，并将结果通过网络返回给 client。对于 client 端可以完全忽略网络的存在，就像调用本地方法一样调用 rpc 服务。

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

客户端发起一次请求调用，通过 SimpleRPCClient 的 Socket建立连接，发起请求Request，得到响应Response

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
5. 使用 Netty 实现了客户端与服务端的通信

### 客户端和服务端重构

客户端发起一次请求调用，通过 ClientProxy 动态代理封装 request 对象，并使用 IOClient 进行数据传输

服务端通过 ServiceProvider 类进行本地服务的存放，使用类的方法进行服务注册（服务端注册服务）和获取本地实例（线程或者线程池得到相应服务实现类，再执行反射得到方法执行）

### 使用 Netty 时

客户端发起一次请求调用，通过传入不同的client(simple,netty)，即可调用公共的接口sendRequest发送请求

服务端的 netty 服务线程组 boss 负责建立连接， work 负责具体的请求

## 存在的问题

只是通过 pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4)); 规定了消息格式是 [长度][消息体], 用以解决粘包问题

但是使用的 java 自带序列化方式不够通用，不够高效

# V4 && V5

## 总结

1. 增加了 ObjectSerializer 与 JsonSerializer 两种序列化器
2. 引入 zookeeper 作为注册中心管理 ip 和 port
3. 新增随机和轮询两种负载均衡策略

### 序列化

自定义传输格式和编解码为

[消息类型 2Byte 序列化方式 2Byte 消息长度 4Byte 序列化字节数组 byte[length]]

对应

[writeShort writeShort writeInt writeBytes]

客户端和服务端都通过 bootstrap 启动时配置相应的 NettyInitializer，调用 pipeline.addLast 配置netty对消息的处理机制，比如 JSON 等序列化

### 引入 zookeeper

客户端不需要指定相应的 ip 和 port, 在发送请求时，直接通过 serviceRegister 从注册中心获取host，port 发送请求

服务端需要通过 ServiceProvider 类进行本地服务的存放时，还需要使用 serviceRegister 把自己的ip，端口给注册中心

## 存在的问题

调用方每次调用服务，都要去注册中心zookeeper中查找地址，性能较差

# V6

## 总结

1. 在客户端建立一个本地缓存，缓存服务地址信息
2. 通过在注册中心注册Watcher，监听注册中心的变化，实现**本地缓存的动态更新**

# V7

## 总结

1. 将 ZooKeeper 的客户端和服务端逻辑拆分，ZkServiceRegister 类负责服务注册（服务端逻辑），ZkServiceCenter 负责服务发现（客户端逻辑）
2. 使用 Guava Retry 实现超时重试
3. 为了防止插入数据之类的操作重试，设置白名单，使得对幂等服务才进行超时重试，白名单存放在 ZK 中（充当配置中心的角色）