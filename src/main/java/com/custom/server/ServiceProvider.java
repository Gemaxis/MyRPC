package com.custom.server;

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
    //本地注册服务

    public void provideServiceInterface(Object service) {
        String serviceName = service.getClass().getName();
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for (Class<?> clazz : interfaceName) {
            interfaceProvider.put(clazz.getName(), service);
        }

    }

    //获取服务实例
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}