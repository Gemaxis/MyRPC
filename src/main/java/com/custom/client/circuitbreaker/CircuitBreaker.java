package com.custom.client.circuitbreaker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gemaxis
 * @date 2024/07/15 19:42
 **/

/**
 * 1.失败 N 次后熔断：当失败次数达到阈值时，熔断器进入打开状态，拒绝请求。
 * 2.打开状态持续 X 时间：在打开状态持续 X 时间后，熔断器进入半开状态，允许部分请求通过。
 * 3.恢复 M% 的请求：在半开状态下，熔断器允许请求通过，并根据请求的成功率决定是否恢复到闭合状态或重新进入打开状态。
 * 4.如果 M% 的请求都成功：恢复到闭合状态，正常处理请求。
 * 5.否则再熔断 Y 时间：如果请求失败，则进入打开状态，等待 Y 时间，然后再次尝试进入半开状态。
 */
public class CircuitBreaker {
    // 当前状态
    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private AtomicInteger failureCount = new AtomicInteger(0);
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger requestCount = new AtomicInteger(0);
    //失败次数阈值
    private final int failureThreshold;
    //半开启-》关闭状态的成功次数比例
    private final double halfOpenSuccessRate;
    //恢复时间
    private final long retryTimePeriod;
    //上一次失败时间
    private long lastFailureTime = 0;

    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate, long retryTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.retryTimePeriod = retryTimePeriod;
    }

    //查看当前熔断器是否允许请求通过
    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        switch (state) {
            case OPEN:
                if (currentTime - lastFailureTime > retryTimePeriod) {
                    state = CircuitBreakerState.HALF_OPEN;
                    resetCounts();
                    return true;
                }
                return false;
            case HALF_OPEN:
                requestCount.incrementAndGet();
                return true;
            case CLOSED:
            default:
                return true;
        }
    }

    //记录成功
    public synchronized void recordSuccess() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            successCount.incrementAndGet();
            if (successCount.get() >= halfOpenSuccessRate * requestCount.get()) {
                state = CircuitBreakerState.CLOSED;
                resetCounts();
            }
        } else {
            resetCounts();
        }
    }

    //记录失败
    public synchronized void recordFailure() {
        failureCount.incrementAndGet();
        lastFailureTime = System.currentTimeMillis();
        if (state == CircuitBreakerState.HALF_OPEN) {
            state = CircuitBreakerState.OPEN;
            lastFailureTime = System.currentTimeMillis();
        } else if (failureCount.get() >= failureThreshold) {
            state = CircuitBreakerState.OPEN;
        }
    }

    //重置次数
    private void resetCounts() {
        failureCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }

    public CircuitBreakerState getState() {
        return state;
    }
}
