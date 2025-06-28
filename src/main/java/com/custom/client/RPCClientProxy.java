package com.custom.client;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPC客户端动态代理，封装请求对象并通过RPCClient发送。
 */
public class RPCClientProxy implements InvocationHandler {

    private final RPCClient client;

    /**
     * 构造方法
     * @param client RPC客户端实现
     */
    public RPCClientProxy(RPCClient client) {
        this.client = client;
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
        RPCResponse response = client.sendRequest(request);
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
