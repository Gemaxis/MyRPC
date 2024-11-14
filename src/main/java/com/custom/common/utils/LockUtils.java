package com.custom.common.utils;

import com.custom.common.logger.ErrorTypeAwareLogger;
import com.custom.common.logger.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;

/**
 * @author Gemaxis
 * @date 2024/11/01 16:59
 **/

/**
 * 类似于 ReentrantLock 的 tryLock()
 * 改进是带有超时日志记录和异常处理的封装
 */
public class LockUtils {
    private static final ErrorTypeAwareLogger logger = LoggerFactory.getErrorTypeAwareLogger(LockUtils.class);

    private static final int DEFAULT_TIMEOUT = 60_000;

    public static void safeLock(Lock lock, int timeout, Runnable runnable) {
        try {
            if (!lock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                logger.error(
                        "Error",
                        "",
                        "",
                        "Try to lock failed, timeout: " + timeout,
                        new TimeoutException());
            }
            runnable.run();
        } catch (InterruptedException e) {
            logger.warn("99-0", "", "", "Try to lock failed", e);
            // 不抛出异常，直接中断
            Thread.currentThread().interrupt();
//            throw new RuntimeException(e);
        } finally {
            try {
                lock.unlock();
            } catch (Exception e) {
                // ignore
//                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 默认不带超时时间的构造方法
     * @param lock
     * @param runnable
     */
    public static void safeLock(Lock lock, Runnable runnable) {
        safeLock(lock, DEFAULT_TIMEOUT, runnable);
    }

}
