package com.custom.myrpc.common.utils;

import java.lang.Thread.State;

import com.custom.common.utils.LockUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import static org.awaitility.Awaitility.await;

/**
 * @author Gemaxis
 * @date 2024/11/01 17:37
 **/
public class LockUtilsTest {
    /**
     * 在无法立即获取锁的情况下，safeLock 方法是否在指定超时后放弃锁请求。
     * 测试超时等待锁
     */
    @RepeatedTest(5)
    void testLockFailed() {
        ReentrantLock reentrantLock = new ReentrantLock();
        AtomicBoolean releaseLock = new AtomicBoolean(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                reentrantLock.lock();
                while (!releaseLock.get()) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                reentrantLock.unlock();
            }
        }).start();

        // await() 是一种非阻塞等待方式
        // 以小间隔（默认为 100 毫秒）检查 isLocked 的状态，从而避免线程长时间阻塞。
        await().until(reentrantLock::isLocked);

        AtomicLong lockTime = new AtomicLong(0);
        long startTime = System.currentTimeMillis();
        LockUtils.safeLock(reentrantLock, 1000, () -> {
            lockTime.set(System.currentTimeMillis());
        });

        // 验证在锁被释放之前确实等待了至少 1000 毫秒，证明发生了超时
        Assertions.assertTrue(lockTime.get() - startTime >= 1000);

        // 释放锁
        releaseLock.set(true);
        while (reentrantLock.isLocked()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // 验证锁立即可用时的行为
        lockTime.set(0);
        startTime = System.currentTimeMillis();
        LockUtils.safeLock(reentrantLock, 1000, () -> {
            lockTime.set(System.currentTimeMillis());
        });
        Assertions.assertTrue(lockTime.get() - startTime < 1000);
    }

    /**
     * 可重入性
     * 测试 ReentrantLock 的可重入性，即线程可以多次获取同一锁而不会发生死锁
     */
    @RepeatedTest(5)
    void tsetReentrant() {
        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();

        AtomicLong lockTime = new AtomicLong(0);
        long startTime = System.currentTimeMillis();
        LockUtils.safeLock(reentrantLock, 1000, () -> {
            lockTime.set(System.currentTimeMillis());
        });

        Assertions.assertTrue(lockTime.get() - startTime < 1000);

        reentrantLock.lock();
        lockTime.set(0);
        startTime = System.currentTimeMillis();
        LockUtils.safeLock(reentrantLock, 1000, () -> {
            lockTime.set(System.currentTimeMillis());
        });

        Assertions.assertTrue(lockTime.get() - startTime < 1000);

        Assertions.assertTrue(reentrantLock.isLocked());
        reentrantLock.unlock();
        Assertions.assertTrue(reentrantLock.isLocked());
        reentrantLock.unlock();
        Assertions.assertFalse(reentrantLock.isLocked());
    }

    @RepeatedTest(5)
    void testInterrupt() {
        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();

        AtomicBoolean locked = new AtomicBoolean(false);
        Thread thread = new Thread(() -> {
            LockUtils.safeLock(reentrantLock, 10000, () -> {
                locked.set(true);
            });
        });
        thread.start();
        await().until(() -> thread.getState() == State.TIMED_WAITING);
        thread.interrupt();
        await().until(() -> thread.getState() == State.TERMINATED);

        Assertions.assertFalse(locked.get());

        reentrantLock.unlock();
    }

    @RepeatedTest(5)
    void testHoldLock() throws InterruptedException {
        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();

        AtomicLong lockTime = new AtomicLong(0);
        long startTime = System.currentTimeMillis();
        Thread thread = new Thread(() -> {
            LockUtils.safeLock(reentrantLock, 10000, () -> {
                lockTime.set(System.currentTimeMillis());
            });
        });
        thread.start();

        await().until(() -> thread.getState() == State.TIMED_WAITING);
        // 主线程休眠 1s 释放锁，允许新线程继续。
        Thread.sleep(1000);
        reentrantLock.unlock();

        await().until(() -> thread.getState() == State.TERMINATED);
        Assertions.assertTrue(lockTime.get() - startTime > 1000);
        Assertions.assertTrue(lockTime.get() - startTime < 10000);
    }
}
