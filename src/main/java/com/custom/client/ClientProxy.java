package com.custom.client;

import com.custom.common.message.RpcRequest;
import com.custom.common.message.RpcResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Gemaxis
 * @date 2024/07/10 11:10
 **/

// 动态代理封装request对象
@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    // 反射封装成request
    private String host;
    private int port;

    // jdk 动态代理，反射获取 request，socket 发送到服务端
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramsType(method.getParameterTypes())
                .build();
        RpcResponse response = IOClient.sendRequest(host, port, request);
        return response.getData();
    }

    public <T> T getProxy(Class<T> tClass) {
        Object object = Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, this);
        return (T) object;
    }
}
