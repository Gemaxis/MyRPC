package com.custom.client.circuitbreaker;

/**
 * @author Gemaxis
 * @date 2024/07/15 19:42
 **/
public enum CircuitBreakerState {
    //关闭，开启，半开启
    CLOSED, OPEN, HALF_OPEN
}
