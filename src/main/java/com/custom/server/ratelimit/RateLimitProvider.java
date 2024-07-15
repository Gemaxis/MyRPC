package com.custom.server.ratelimit;

/**
 * @author Gemaxis
 * @date 2024/07/15 17:27
 **/

import java.util.HashMap;
import java.util.Map;

/**
 * RateLimitProvider类维护每个服务对应限流器，并负责向外提供限流器
 */
public class RateLimitProvider {
    private Map<String, RateLimit> rateLimitMap = new HashMap<>();

    public RateLimit getRateLimit(String interfaceName) {
        if (!rateLimitMap.containsKey(interfaceName)) {
            RateLimit rateLimit = new TokenBucketRateLimitImpl(100000, 10);
            rateLimitMap.put(interfaceName, rateLimit);
            System.out.println("生成了 " + interfaceName + " 服务的限流器");
            return rateLimit;
        }
        return rateLimitMap.get(interfaceName);
    }
}
