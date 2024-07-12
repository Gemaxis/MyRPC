package com.custom.server;

import com.custom.register.ServiceRegister;
import com.custom.register.ZkServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gemaxis
 * @date 2024/07/10 11:24
 **/


//本地服务存放器
public class ServiceProvider {
    // 一个实现类可能实现多个接口
    private Map<String, Object> interfaceProvider;

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

    //获取服务实例
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}