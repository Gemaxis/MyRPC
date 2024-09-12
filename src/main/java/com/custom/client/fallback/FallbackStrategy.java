package com.custom.client.fallback;

public interface FallbackStrategy <T>{
    T fallback(Throwable t);
}
