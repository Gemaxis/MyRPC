package com.custom.client.circuitbreaker;

import java.util.HashMap;
import java.util.Map;

import static com.custom.common.utils.CommonConstants.CIRCUIT_BREAKER_DEFAULT_FAIL_COUNT;
import static com.custom.common.utils.CommonConstants.CIRCUIT_BREAKER_DEFAULT_FAIL_RATIO;
import static com.custom.common.utils.CommonConstants.CIRCUIT_BREAKER_DEFAULT_RECOVER_TIME_MS;
import static com.custom.common.utils.CommonConstants.NEW_CIRCUIT_BREAKER_SUCCESS_MSG;

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
            circuitBreaker = new CircuitBreaker(CIRCUIT_BREAKER_DEFAULT_FAIL_COUNT, CIRCUIT_BREAKER_DEFAULT_FAIL_RATIO, CIRCUIT_BREAKER_DEFAULT_RECOVER_TIME_MS);
            System.out.println(NEW_CIRCUIT_BREAKER_SUCCESS_MSG);
        }
        return circuitBreaker;
    }
}
