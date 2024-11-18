package com.custom.common.monitor;

import com.custom.common.URL;
import com.custom.common.logger.ErrorTypeAwareLogger;
import com.custom.common.logger.LoggerFactory;
import com.custom.common.utils.NamedThreadFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Gemaxis
 * @date 2024/11/14 18:28
 **/
public abstract class AbstractMonitorFactory implements MonitorFactory {

    private static final ErrorTypeAwareLogger logger = LoggerFactory.getErrorTypeAwareLogger(AbstractMonitorFactory.class);

    private static final ReentrantLock LOCK = new ReentrantLock();

    private static final Map<String, Monitor> MONITORS = new ConcurrentHashMap<>();

    // FUTURES 记录了正在创建的 Monitor 的任务状态，防止重复创建和阻塞等待，提升了并发性能和资源利用效率。
    private static final Map<String, Future<Monitor>> FUTURES = new ConcurrentHashMap<>();
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(
            0,
            10,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new NamedThreadFactory("MyRPCMonitorCreator", true));

    public static Collection<Monitor> getMonitors() {
        return Collections.unmodifiableCollection(MONITORS.values());
    }

    @Override
    public Monitor getMonitor(URL url) {
        String key = "URLKey";
        Monitor monitor = MONITORS.get(key);
        Future<Monitor> future = FUTURES.get(key);
        if (monitor != null || future != null) {
            return monitor;
        }

        // 创建监视器
        LOCK.lock();
        try {
            // 通过两次检查和加锁保证了线程安全和对象的单例性
            monitor = MONITORS.get(key);
            future = FUTURES.get(key);
            if (monitor != null || future != null) {
                return monitor;
            }

            final URL monitorUrl = url;
            future = EXECUTOR.submit(() -> {
                try {
                    Monitor m = createMonitor(monitorUrl);
                    MONITORS.put(key, m);
                    FUTURES.remove(key);
                    return m;
                } catch (Exception e) {
                    logger.warn(
                            "502",
                            "",
                            "",
                            "Create monitor failed, monitor data will not be collected until you fix this problem. monitorUrl: "
                                    + monitorUrl,
                            e);
                    throw new RuntimeException(e);
                }
            });
            FUTURES.put(key, future);
            return null;

        } finally {
            LOCK.unlock();
        }
    }

    protected abstract Monitor createMonitor(URL url);

}
