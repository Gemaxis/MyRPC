package com.custom.client;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
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
    // 当使用代理对象调用方法的时候实际会调用到这个 invoke 方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /**
         * proxy :动态生成的代理类
         * method : 与代理类对象调用的方法相对应
         * args : 当前 method 方法的参数
         */
        RPCRequest request = RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();

        // 使用 IOClient 进行数据传输
        RPCResponse response = IOClient.sendRequest(host, port, request);
        return response.getData();
    }

    public <T> T getProxy(Class<T> tClass) {
        Object object = Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, this);
        return (T) object;
    }
}
