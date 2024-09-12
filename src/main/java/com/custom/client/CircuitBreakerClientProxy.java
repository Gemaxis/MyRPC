package com.custom.client;

import com.custom.client.circuitbreaker.CircuitBreaker;
import com.custom.client.circuitbreaker.CircuitBreakerProvider;
import com.custom.client.fallback.DefaultFallbackStrategy;
import com.custom.client.fallback.FallbackStrategy;
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
public class CircuitBreakerClientProxy implements InvocationHandler {

    private RPCClient rpcClient;

    private ServiceCenter serviceCenter;

    private CircuitBreakerProvider circuitBreakerProvider;
    private FallbackStrategy<String> fallbackStrategy;

    public CircuitBreakerClientProxy() {
        this.serviceCenter = new ZkServiceCenter();
        this.rpcClient = new NettyRPCClient(serviceCenter);
        this.circuitBreakerProvider = new CircuitBreakerProvider();
        this.fallbackStrategy = new DefaultFallbackStrategy(); // 初始化默认的降级策略
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RPCRequest request = RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();
        // 获取熔断器
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());
        // 判断熔断器是否允许请求通过
        if (!circuitBreaker.allowRequest()) {
            // 如果熔断器不允许请求，通过降级策略返回处理结果
            return fallbackStrategy.fallback(new Exception("熔断器已打开，请求被拒绝"));
        }

        RPCResponse response;
        try {
            // 白名单和限流
            // 为保持幂等性，只对白名单上的服务进行重试
            if (serviceCenter.checkRetry(request.getInterfaceName())) {
                response = new GuavaRetry().sendServiceWithRetry(request, rpcClient);
            } else {
                response = rpcClient.sendRequest(request);
            }
            circuitBreaker.recordSuccess();
        }catch (Exception e) {
            // 请求失败时，熔断器记录失败次数并通过降级策略返回处理结果
            circuitBreaker.recordFailure();
            return fallbackStrategy.fallback(e);
        }
        return response.getData();
    }

    public <T> T getProxy(Class<T> tClass) {
        Object object = Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, this);
        return (T) object;
    }
}