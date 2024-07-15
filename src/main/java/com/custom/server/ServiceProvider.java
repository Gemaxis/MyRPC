package com.custom.server;

import com.custom.server.ratelimit.RateLimitProvider;
import com.custom.server.register.ServiceRegister;
import com.custom.server.register.impl.ZkServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gemaxis
 * @date 2024/07/10 11:24
 **/


//本地服务存放器
public class ServiceProvider {

    // 集合中存放服务的实例
    private Map<String, Object> interfaceProvider;

    //限流器
    private RateLimitProvider rateLimitProvider;

    public ServiceProvider() {
        this.interfaceProvider = new HashMap<>();
    }

    // 加入注册的功能
    private ServiceRegister serviceRegister;
    private String host;
    private int port;

    public ServiceProvider(String host, int port) {
        // 需要传入服务端自身的服务的网络地址
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister = new ZkServiceRegister();
        this.rateLimitProvider = new RateLimitProvider();
    }

    //本地注册服务
    public void provideServiceInterface(Object service) {
//        String serviceName = service.getClass().getName();
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for (Class<?> clazz : interfaceName) {
            // 本机的映射表
            interfaceProvider.put(clazz.getName(), service);
            // 在注册中心注册服务
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port));
        }

    }

    //重载本地注册服务，入参为是否为白名单
    public void provideServiceInterface(Object service, boolean canRetry) {
//        String serviceName = service.getClass().getName();
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for (Class<?> clazz : interfaceName) {
            // 本机的映射表
            interfaceProvider.put(clazz.getName(), service);
            // 在注册中心注册服务
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port), canRetry);
        }

    }

    //获取服务实例
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }

    public RateLimitProvider getRateLimitProvider(){
        return rateLimitProvider;
    }
}