package com.custom.common.utils;

import com.custom.common.utils.LocalLock;
import com.google.common.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Gemaxis
 * @date 2024/11/14 16:03
 **/
public class LocalLockTest {

    private LocalLock localLock;

    @BeforeEach
    void setUp() {
        localLock = new LocalLock();
    }

    @Test
    void testExecuteWithLock_success() throws InterruptedException {
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        localLock.executeWithLock("testKey", 1, TimeUnit.SECONDS, () -> taskExecuted.set(true));
        assertTrue(taskExecuted.get());
    }

    @Test
    void testExecuteWithLock_LockNotAcquired() throws InterruptedException {
        Lock mockedLock = mock(Lock.class);
        when(mockedLock.tryLock(1, TimeUnit.SECONDS)).thenReturn(false);

        // 在缓存中预先放置该mock锁
        Cache<String, Lock> lockCache = localLock.getLockCache(); // 直接访问 lockCache
        lockCache.put("testKey", mockedLock);
//        localLock.lockCache.put("testKey", mockedLock);

        // 创建一个标志变量，用于检查任务是否被执行
        AtomicBoolean taskExecuted = new AtomicBoolean(false);

        localLock.executeWithLock("testKey", 1, TimeUnit.SECONDS, () -> taskExecuted.set(true));

        assertFalse(taskExecuted.get(), "Acquired lock failed");
        verify(mockedLock, times(1)).tryLock(1, TimeUnit.SECONDS);

    }

    @Test
    void testExecuteWithLock_ConcurrentAccess() throws InterruptedException {
        AtomicBoolean taskExecuted1 = new AtomicBoolean(false);
        AtomicBoolean taskExecuted2 = new AtomicBoolean(false);
        Thread thread1 = new Thread(() -> {
            try {
                localLock.executeWithLock("sharedKey", 1, TimeUnit.SECONDS, () -> {
                    // 模拟长时间任务
                    try {
                        // 因为trylock是 1s 所以任务执行必须大于 1s
                        Thread.sleep(2000);
                        taskExecuted1.set(true);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (InterruptedException e) {
                fail("第一个线程被中断");
                throw new RuntimeException(e);
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                localLock.executeWithLock("sharedKey", 1, TimeUnit.SECONDS, () -> {
                    taskExecuted2.set(true);
                });
            } catch (InterruptedException e) {
                fail("第二个线程被中断");
                throw new RuntimeException(e);
            }
        });


        thread1.start();
        Thread.sleep(200);
        thread2.start();

        //等待线程结束
        thread1.join();
        thread2.join();

        // 检查结果
        assertTrue(taskExecuted1.get(), "第一个线程应执行任务");
        assertFalse(taskExecuted2.get(), "第二个线程不应执行任务，因为锁未释放");
    }

}
