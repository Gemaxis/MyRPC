package com.custom.myrpc.common.utils;

import com.custom.common.utils.NamedThreadFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Gemaxis
 * @date 2024/11/07 15:21
 **/
public class CompletableFutureTaskTest {

    private static final ExecutorService executor = new ThreadPoolExecutor(
            0,
            10,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new NamedThreadFactory("MyRPCCreator", true));

    @Test
    void testCreate() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    countDownLatch.countDown();
                    return true;
                },
                executor
        );
        countDownLatch.await();
    }

    @Test
    void testRunnableResponse() throws ExecutionException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                },
                executor
        );
        Assertions.assertNull(completableFuture.getNow(null));
        latch.countDown();
        Boolean result = completableFuture.get();
        assertThat(result, is(true));
    }

    @Test
    void testListener() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean run = new AtomicBoolean(false);
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    run.set(true);
                    return "Hello";
                },
                executor
        );
        completableFuture.thenRunAsync(countDownLatch::countDown);
        countDownLatch.await();
        assertTrue(run.get());
    }

    @Test
    void tsetCustomExecutor() {
        Executor mockedExecutor = mock(Executor.class);
        CompletableFuture<Integer> completavleFuture = CompletableFuture.supplyAsync(() -> {
            return 0;
        });
        completavleFuture
                .thenRunAsync(mock(Runnable.class), mockedExecutor)
                .whenComplete((s, e) -> verify(mockedExecutor, times(1)).execute(any(Runnable.class)));
    }
}
