package com.custom.client.fallback;

/**
 * @author Gemaxis
 * @date 2024/08/27 21:00
 **/
public class DefaultFallbackStrategy implements FallbackStrategy<String>{
    @Override
    public String fallback(Throwable t) {
        return "服务目前不可用，请稍后再试";
    }
}
