package com.custom.client;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Gemaxis
 * @date 2024/07/10 17:21
 **/
@AllArgsConstructor
public class RPCClientProxy implements InvocationHandler {
    private RPCClient client;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RPCRequest request = RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();
        RPCResponse response = client.sendRequest( request);
//        RPCResponse response;
//        // 为保持幂等性，只对白名单上的服务进行重试
//        if (serviceRegister.checkRetry(request.getInterfaceName())) {
//            response = new GuavaRetry().sendServiceWithRetry(request, client);
//        } else {
//            response = client.sendRequest(request);
//        }
        return response.getData();
    }

    public <T> T getProxy(Class<T> tClass) {
        Object object = Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, this);
        return (T) object;
    }
}
