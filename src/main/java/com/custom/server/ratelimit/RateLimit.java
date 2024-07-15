package com.custom.server.ratelimit;

public interface RateLimit {

    /**
     * 获取访问许可
     * @return
     */
    boolean getToken();
}
