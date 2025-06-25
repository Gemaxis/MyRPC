package com.custom.client;

import com.custom.client.retry.GuavaRetry;
import com.custom.client.service.ServiceCenter;
import com.custom.client.service.ZkServiceCenter;
import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 支持重试的RPC客户端动态代理。
 */
public class RetryClientProxy implements InvocationHandler {

    private RPCClient rpcClient;
    private ServiceCenter serviceCenter;

    /**
     * 默认构造，使用ZkServiceCenter和NettyRPCClient。
     */
    public RetryClientProxy() {
        this.serviceCenter = new ZkServiceCenter();
        this.rpcClient = new NettyRPCClient(serviceCenter);
    }

    /**
     * 动态代理方法调用
     * @param proxy 代理对象
     * @param method 被调用方法
     * @param args 方法参数
     * @return 方法返回值
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RPCRequest request = RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();
        RPCResponse response;
        // 只对白名单上的服务进行重试
        if (serviceCenter.checkRetry(request.getInterfaceName())) {
            response = new GuavaRetry().sendServiceWithRetry(request, rpcClient);
        } else {
            response = rpcClient.sendRequest(request);
        }
        return response.getData();
    }

    /**
     * 获取代理对象
     * @param tClass 接口类
     * @param <T> 泛型
     * @return 代理对象
     */
    public <T> T getProxy(Class<T> tClass) {
        Object object = Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, this);
        return tClass.cast(object);
    }
}