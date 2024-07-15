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
 * @author Gemaxis
 * @date 2024/07/15 15:54
 **/
public class RetryClientProxy implements InvocationHandler {

    private RPCClient rpcClient;

    private ServiceCenter serviceCenter;

    public RetryClientProxy() {
        this.serviceCenter = new ZkServiceCenter();
        this.rpcClient = new NettyRPCClient(serviceCenter);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RPCRequest request = RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();
        RPCResponse response;
        // 为保持幂等性，只对白名单上的服务进行重试
        if (serviceCenter.checkRetry(request.getInterfaceName())) {
            response = new GuavaRetry().sendServiceWithRetry(request, rpcClient);
        } else {
            response = rpcClient.sendRequest(request);
        }
        return response.getData();
    }

    public <T> T getProxy(Class<T> tClass) {
        Object object = Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, this);
        return (T) object;
    }
}