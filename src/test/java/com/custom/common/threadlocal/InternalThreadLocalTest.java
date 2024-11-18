package com.custom.common.threadlocal;

import com.custom.common.threadlocal.InternalThread;
import com.custom.common.threadlocal.InternalThreadLocal;
import com.custom.common.threadlocal.InternalThreadLocalMap;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Gemaxis
 * @date 2024/11/16 17:08
 **/
public class InternalThreadLocalTest {
    private static final int THREADS = 10;
    private static final int PERFORMANCE_THREAD_COUNT = 1000;
    private static final int GET_COUNT = 1000000;

    @AfterEach
    public void setup() {
        InternalThreadLocalMap.remove();
    }

    /**
     * 确保每个线程的变量初始化是隔离的
     */
    @Test
    void testInternalThreadLocal() {
        final AtomicInteger index = new AtomicInteger(0);
        InheritableThreadLocal<Integer> internalThreadLocal = new InheritableThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                Integer v = index.getAndIncrement();
                System.out.println("thread : " + Thread.currentThread().getName() + " init value : " + v);
                return v;
            }
        };
        for (int i = 0; i < THREADS; i++) {
            Thread t = new Thread(internalThreadLocal::get);
            t.start();
        }
        await().until(index::get, is(THREADS));
    }

    @Test
    void testRemoveAll() {
        final InternalThreadLocal<Integer> internalThreadLocal = new InternalThreadLocal<Integer>();
        internalThreadLocal.set(1);
        Assertions.assertEquals(1, (int) internalThreadLocal.get(), "set failed");

        final InternalThreadLocal<String> internalThreadLocalString = new InternalThreadLocal<String>();
        internalThreadLocalString.set("value");
        Assertions.assertEquals("value", internalThreadLocalString.get(), "set failed");

        InternalThreadLocal.removeAll();
        assertNull(internalThreadLocal.get(), "removeAll failed!");
        Assertions.assertNull(internalThreadLocalString.get(), "removeAll failed!");
    }

    @Test
    void testSize() {
        final InternalThreadLocal<Integer> internalThreadLocal = new InternalThreadLocal<>();
        internalThreadLocal.set(1);
        assertEquals(1, InternalThreadLocal.size());

        final InternalThreadLocal<String> internalThreadLocalString = new InternalThreadLocal<String>();
        internalThreadLocalString.set("value");

        assertEquals(2, InternalThreadLocal.size());
        InternalThreadLocal.removeAll();
    }

    @Test
    void testSetAndGet() {
        final Integer testVal = 10;
        final InternalThreadLocal<Integer> internalThreadLocal = new InternalThreadLocal<>();
        internalThreadLocal.set(testVal);
        assertEquals(testVal, internalThreadLocal.get(), "set is not equals get");
    }

    @Test
    void testRemove() {
        final InternalThreadLocal<Integer> internalThreadLocal = new InternalThreadLocal<>();
        internalThreadLocal.set(1);
        assertEquals(1, (int) internalThreadLocal.get(), "get method false!");

        internalThreadLocal.remove();
        assertNull(internalThreadLocal.get(), "remove failed!");
    }

    @Test
    void testOnRemove() {
        final Integer[] valueToRemove = {null};
        final InternalThreadLocal<Integer> internalThreadLocal = new InternalThreadLocal<Integer>() {
            @Override
            protected void onRemoval(Integer value) throws Exception {
                valueToRemove[0] = value + 1;
            }
        };
        internalThreadLocal.set(1);
        assertEquals(1, (int) internalThreadLocal.get());

        internalThreadLocal.remove();
        assertEquals(2, (int) valueToRemove[0]);
    }

    /**
     * 验证 InternalThreadLocal 在多线程环境下的线程隔离性。
     * 也就是说，它确保不同线程使用 InternalThreadLocal 设置和获取的值是互相独立的，每个线程拥有自己的本地存储值。
     *
     * @throws InterruptedException
     */
    @Test
    void testMutilThreadSetAndGet() throws InterruptedException {
        final Integer testVal1 = 10;
        final Integer testVal2 = 20;
        final InternalThreadLocal<Integer> internalThreadLocal = new InternalThreadLocal<Integer>();
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                internalThreadLocal.set(testVal1);
                assertEquals(testVal1, internalThreadLocal.get());
                countDownLatch.countDown();
            }
        });
        t1.start();
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                internalThreadLocal.set(testVal2);
                Assertions.assertEquals(testVal2, internalThreadLocal.get(), "set is not equals get");
                countDownLatch.countDown();
            }
        });
        t2.start();

        countDownLatch.await();
    }

    // take[3047]ms
    @Test
    void testPerformanceTradition() {
        final ThreadLocal<String>[] cache = new ThreadLocal[PERFORMANCE_THREAD_COUNT];
        final Thread mainThread = Thread.currentThread();
        for (int i = 0; i < PERFORMANCE_THREAD_COUNT; i++) {
            cache[i] = new ThreadLocal<String>();
        }
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < PERFORMANCE_THREAD_COUNT; i++) {
                    cache[i].set("float.lu");
                }
                long start = System.nanoTime();
                for (int i = 0; i < PERFORMANCE_THREAD_COUNT; i++) {
                    for (int j = 0; j < GET_COUNT; j++) {
                        cache[i].get();
                    }
                }
                long end = System.nanoTime();
                System.out.println("take[" + TimeUnit.NANOSECONDS.toMillis(end - start) + "]ms");
                LockSupport.unpark(mainThread);
            }
        });
        t1.start();
        LockSupport.park(mainThread);
    }

    // take[14]ms
    @Test
    void testPerformance() {
        final InternalThreadLocal<String>[] cache = new InternalThreadLocal[PERFORMANCE_THREAD_COUNT];
        final Thread mainThread = Thread.currentThread();
        for (int i = 0; i < PERFORMANCE_THREAD_COUNT; i++) {
            cache[i] = new InternalThreadLocal<String>();
        }
        Thread t = new InternalThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < PERFORMANCE_THREAD_COUNT; i++) {
                    cache[i].set("float.lu");
                }
                long start = System.nanoTime();
                for (int i = 0; i < PERFORMANCE_THREAD_COUNT; i++) {
                    for (int j = 0; j < GET_COUNT; j++) {
                        cache[i].get();
                    }
                }
                long end = System.nanoTime();
                System.out.println("take[" + TimeUnit.NANOSECONDS.toMillis(end - start) + "]ms");
                LockSupport.unpark(mainThread);
            }
        });
        t.start();
        LockSupport.park(mainThread);
    }

    @Test
    void testConstructionWithIndex() throws Exception {
        final int NEW_ARRAY_LIST_CAPACITY_MAX_SIZE = 8;
        Field nextIndexField = InternalThreadLocalMap.class.getDeclaredField("NEXT_INDEX");

        nextIndexField.setAccessible(true);
        AtomicInteger nextIndex = (AtomicInteger) nextIndexField.get(AtomicInteger.class);

        int arrayListCapacityMaxSize=InternalThreadLocalMap.ARRAY_LIST_CAPACITY_MAX_SIZE;
        int nextIndex_before=nextIndex.incrementAndGet();

        nextIndex.set(0);
        final AtomicReference<Throwable> throwable=new AtomicReference<>();

        try {
            InternalThreadLocalMap.ARRAY_LIST_CAPACITY_MAX_SIZE=NEW_ARRAY_LIST_CAPACITY_MAX_SIZE;
            while (nextIndex.get()<NEW_ARRAY_LIST_CAPACITY_MAX_SIZE){
                new InternalThreadLocal<Boolean>();
            }
            assertEquals(NEW_ARRAY_LIST_CAPACITY_MAX_SIZE-1,InternalThreadLocalMap.lastVariableIndex());

            try {
                new InternalThreadLocal<Boolean>();
            } catch (Throwable t) {
                throwable.set(t);
            }

            assertThat(throwable.get(), CoreMatchers.is(instanceOf(IllegalStateException.class)));
            assertEquals(NEW_ARRAY_LIST_CAPACITY_MAX_SIZE - 1, InternalThreadLocalMap.lastVariableIndex());

        } finally {
            // Restore the index

            nextIndex.set(nextIndex_before);
            InternalThreadLocalMap.ARRAY_LIST_CAPACITY_MAX_SIZE=arrayListCapacityMaxSize;
        }

    }
    @Test
    void testInternalThreadLocalMapExpand() throws InterruptedException {
        final AtomicReference<Throwable> throwable=new AtomicReference<>();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                int expand_threshold=1<<30;
                try {
                    // 通过给定一个极大的索引值，触发数组扩展逻辑
                    InternalThreadLocalMap.get().setIndexedVariable(expand_threshold,null);
                } catch (Throwable t) {
                    throwable.set(t);
                }
            }
        };
        InternalThread internalThread = new InternalThread(runnable);

        internalThread.start();
        internalThread.join();
        // throwable.get() 为 null（即无异常发生）
        assertThat(throwable.get(),is(not(instanceOf(NegativeArraySizeException.class))));

    }
}

















