package com.custom.client.retry;

import com.custom.client.RPCClient;
import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import com.github.rholder.retry.*;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Gemaxis
 * @date 2024/07/12 22:49
 **/

/**
 * 当调用端发起的请求失败时，RPC 框架自身可以进行重试，再重新发送请求，通过这种方式保证系统的容错率
 * 使用Guava Retry进行超时重试
 * 但是需要设置一个白名单，服务端在注册节点时，将幂等性的服务注册在白名单中，客户端在请求服务前，先去白名单中查看该服务是否为幂等服务，如果是的话使用重试框架进行调用
 */
public class GuavaRetry {
    private RPCClient rpcClient;

    public RPCResponse sendServiceWithRetry(RPCRequest request, RPCClient rpcClient) {
        this.rpcClient = rpcClient;
        Retryer<RPCResponse> retryer = RetryerBuilder.<RPCResponse>newBuilder()
                //无论出现什么异常，都进行重试
                .retryIfException()
                //返回结果为 error时进行重试
                .retryIfResult(response -> Objects.equals(response.getCode(), 500))
                //重试等待策略：等待 2s 后再进行重试
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                //重试停止策略：重试达到 3 次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("RetryListener: 第" + attempt.getAttemptNumber() + "次调用");
                    }
                })
                .build();
        try {
            return retryer.call(() -> rpcClient.sendRequest(request));
        } catch (ExecutionException | RetryException e) {
            e.printStackTrace();
        }
        return RPCResponse.fail();
    }
}
