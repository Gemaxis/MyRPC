package com.custom.server.ratelimit;

/**
 * @author Gemaxis
 * @date 2024/07/15 17:22
 **/
public class TokenBucketRateLimitImpl implements RateLimit {
    //令牌产生速率（单位为ms）
    private static int RATE;
    //桶容量
    private static int CAPACITY;
    //当前桶容量
    private volatile int curCapcity;
    //时间戳
    private volatile long timeStamp = System.currentTimeMillis();

    /**
     * 传入参数 (100, 10) 时，令牌生成速率为每100毫秒一个令牌，并且桶的最大容量为10个令牌。
     *
     * @param rate
     * @param capacity
     */
    public TokenBucketRateLimitImpl(int rate, int capacity) {
        RATE = rate;
        CAPACITY = capacity;
        curCapcity = capacity;
    }

    @Override
    public synchronized boolean getToken() {
        if (curCapcity > 0) {
            --curCapcity;
            System.out.println("服务成功，当前桶内还有令牌数量：" + curCapcity);
            return true;
        }
        // 如果桶无剩余则开始判断
        long current = System.currentTimeMillis();
        if (current - timeStamp >= RATE) {
            // 如果==1，就不做操作（因为这一次操作要消耗一个令牌）
            // 如果时间差大于等于两个 RATE，则计算生成的令牌数，并更新桶的容量 curCapcity 为桶容量加上（计算的令牌-1）
            if ((current - timeStamp) / RATE >= 2) {
                curCapcity += (int) (current - timeStamp) / RATE - 1;
            }
            //保持桶内令牌容量<=10
            if (curCapcity > CAPACITY) {
                curCapcity = CAPACITY;
            }
            //刷新时间戳为本次请求
            timeStamp = current;
            return true;
        }
        return false;
    }
}
