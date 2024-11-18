package com.custom.common.utils;

/**
 * @author Gemaxis
 * @date 2024/11/03 16:20
 **/
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 3, time = 5)
@State(Scope.Benchmark)
@Fork(2)
public class ConcurrentHashMapUtilsJMHTest {

    private static final String KEY = "mxnn";

    private final Map<String, Object> concurrentMap = new ConcurrentHashMap<>();

    @Setup(Level.Iteration)
    public void setup() {
        concurrentMap.clear();
    }

    @Benchmark
    @Threads(16)
    public Object benchmarkGetBeforeComputeIfAbsent() {
        Object result = concurrentMap.get(KEY);
        if (null == result) {
            result = concurrentMap.computeIfAbsent(KEY, key -> 1);
        }
        return result;
    }

    @Benchmark
    @Threads(16)
    public Object benchmarkComputeIfAbsent() {
        return concurrentMap.computeIfAbsent(KEY, key -> 1);
    }
    @Test
    public void runJmhBenchmark() throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ConcurrentHashMapUtilsJMHTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(options).run();
    }

}

/**
 * Benchmark                                                         Mode  Cnt           Score           Error  Units
 * ConcurrentHashMapUtilsJMHTest.benchmarkComputeIfAbsent           thrpt    3    17493295.686 ±  10081089.165  ops/s
 * ConcurrentHashMapUtilsJMHTest.benchmarkGetBeforeComputeIfAbsent  thrpt    3  1358511613.727 ± 174865622.468  ops/s
 */