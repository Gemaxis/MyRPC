package com.custom.client.circuitbreaker;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gemaxis
 * @date 2024/07/15 19:47
 **/
public class CircuitBreakerProvider {
    private Map<String, CircuitBreaker> circuitBreakerMap = new HashMap<>();

    public CircuitBreaker getCircuitBreaker(String serviceName) {
        CircuitBreaker circuitBreaker;
        if (circuitBreakerMap.containsKey(serviceName)) {
            circuitBreaker = circuitBreakerMap.get(serviceName);
        } else {
            // 新建熔断器，默认失败次数为3，比例为0.5，恢复时间为10 000ms
            circuitBreaker = new CircuitBreaker(3, 0.5, 10000);
            System.out.println("新建熔断器成功");
        }
        return circuitBreaker;
    }
}
