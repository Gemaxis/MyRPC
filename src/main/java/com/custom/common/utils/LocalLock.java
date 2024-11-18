package com.custom.common.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Gemaxis
 * @date 2024/11/13 22:19
 **/
public class LocalLock {
    // lockCache 是一个基于键值对的缓存，用于存储每个 key 对应的锁对象，并在10分钟后自动过期释放内存。
    private final Cache<String, Lock> lockCache;

    public LocalLock() {
        lockCache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
    }

    public Cache<String, Lock> getLockCache() {
        return lockCache;
    }

    public void executeWithLock(String key, long timeout, TimeUnit unit, Runnable task) throws InterruptedException {
        // computeIfAbsent 来避免重复创建 Lock 实例，从而简化了双重检查锁的写法。
        Lock lock = lockCache.asMap().computeIfAbsent(key, k -> new ReentrantLock());
//        Lock lock = lockCache.getIfPresent(key);
//        if (lock == null) {
//            synchronized (this) {
//                lock = lockCache.getIfPresent(key);
//                if (lock == null) {
//                    lock = new ReentrantLock();
//                    lockCache.put(key, lock);
//                }
//            }
//        }
//        System.out.println(Thread.currentThread().getName() + " 尝试获取锁: " + key);

        boolean locked = lock.tryLock(timeout, unit);
        if (!locked) {
//            System.out.println(Thread.currentThread().getName() + " 获取锁失败: " + key);
            return;
        }
//        System.out.println(Thread.currentThread().getName() + " 成功获取锁: " + key);

        try {
            task.run();
        } finally {
            lock.unlock();
//            System.out.println(Thread.currentThread().getName() + " 释放锁: " + key);
        }
    }
}
