package com.custom.common.utils;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Gemaxis
 * @date 2024/11/18 12:50
 **/
public class NamedThreadFactoryTest {
    private static final int INITIAL_THREAD_NUM = 1;

    @Test
    void testNewThread() {
        NamedThreadFactory factory = new NamedThreadFactory();
        Thread t = factory.newThread(Mockito.mock(Runnable.class));
        assertThat(t.getName(), allOf(containsString("pool-"), containsString("-thread-")));
        assertFalse(t.isDaemon());

        assertSame(t.getThreadGroup(), Thread.currentThread().getThreadGroup());
    }

    @Test
    void testPrefixAndDaemon() {
        NamedThreadFactory factory = new NamedThreadFactory("prefix-MyRPC", true);
        Thread t = factory.newThread(Mockito.mock(Runnable.class));
        assertThat(t.getName(), allOf(containsString("prefix-MyRPC"), containsString("-thread-")));
        assertTrue(t.isDaemon());
    }

    @Test
    public void testGetThreadNum() {
        NamedThreadFactory threadFactory = new NamedThreadFactory();
        AtomicInteger threadNum = threadFactory.getThreadNum();
        assertNotNull(threadNum);
        assertEquals(INITIAL_THREAD_NUM, threadNum.get());
    }

    @Test
    void testMultipleThreadsThreadNum() throws InterruptedException {
        NamedThreadFactory namedThreadFactory = new NamedThreadFactory();
        final int threadCount = 10;
        final CountDownLatch latch = new CountDownLatch(threadCount);

        Set<String> threadNames = Collections.synchronizedSet(new HashSet<>());

        for (int i = 0; i < threadCount; i++) {
            Thread t1 = new Thread(() -> {
                Thread thread = namedThreadFactory.newThread(() -> {
                    threadNames.add(Thread.currentThread().getName());
                    latch.countDown();
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            t1.start();
        }
        latch.await();
        assertEquals(threadCount, threadNames.size());

        AtomicInteger threadNum = namedThreadFactory.getThreadNum();
        assertEquals(threadCount + 1, threadNum.get());
    }

    @Test
    void testConcurrentThreadNum() throws InterruptedException {
        NamedThreadFactory namedThreadFactory = new NamedThreadFactory();
        final int threadCount = 1000;
        final CountDownLatch latch = new CountDownLatch(threadCount);

        Set<String> threadNames = Collections.synchronizedSet(new HashSet<>());
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                Thread thread = namedThreadFactory.newThread(() -> {
                    threadNames.add(Thread.currentThread().getName());
                    latch.countDown();
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        latch.await();
        executor.shutdown();
        assertEquals(threadCount, threadNames.size());

        AtomicInteger threadNum = namedThreadFactory.getThreadNum();
        assertEquals(threadCount + 1, threadNum.get());
    }
    @Test
    public void testGetThreadGroup() {
        NamedThreadFactory threadFactory = new NamedThreadFactory();
        ThreadGroup threadGroup = threadFactory.getThreadGroup();
        assertNotNull(threadGroup);
    }
}

