package com.custom.common.logger;

import com.custom.common.logger.slf4j.Slf4jLoggerAdapter;
import com.custom.common.logger.support.FailsafeErrorTypeAwareLogger;
import com.custom.common.logger.support.FailsafeLogger;
import com.custom.common.utils.ConcurrentHashMapUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Gemaxis
 * @date 2024/10/27 16:03
 **/
public class LoggerFactory {
    private static final ConcurrentMap<String, FailsafeLogger> LOGGERS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, FailsafeErrorTypeAwareLogger> ERROR_TYPE_AWARE_LOGGERS =
            new ConcurrentHashMap<>();
    private static volatile LoggerAdapter loggerAdapter;

    static {
        String logger = System.getProperty("MyRPC.application.logger", "");
        switch (logger) {
            case Slf4jLoggerAdapter.NAME:
                setLoggerAdapter(new Slf4jLoggerAdapter());
                break;
            default:
                List<Class<? extends LoggerAdapter>> candidates = Arrays.asList(
                        Slf4jLoggerAdapter.class
                );
                boolean found = false;
                for (Class<? extends LoggerAdapter> clazz : candidates) {
                    try {
                        // 反射来动态实例化 LoggerAdapter 类
                        LoggerAdapter loggerAdapter = clazz.getDeclaredConstructor().newInstance();
                        loggerAdapter.getLogger(LoggerFactory.class);
                        setLoggerAdapter(loggerAdapter);
                        found = true;
                        break;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (found) {
                    System.err.println(
                            "MyRPC: Using default logger: "
                                    + loggerAdapter.getClass().getName() + ". "
                                    + "If you cannot see any log, please configure -Ddubbo.application.logger property to your preferred logging framework.");
                } else {
                    System.err.println(
                            "MyRPC: Unable to find any available logger adapter to log out. Dubbo logs will be ignored. "
                                    + "Please configure -Ddubbo.application.logger property and add corresponding logging library to classpath.");
                }
        }
    }

    private LoggerFactory() {

    }

    public static void setLoggerAdapter(LoggerAdapter loggerAdapter) {
        if (loggerAdapter != null) {
            if (loggerAdapter == LoggerFactory.loggerAdapter) {
                return;
            }
            loggerAdapter.getLogger(LoggerFactory.class.getName());
            LoggerFactory.loggerAdapter = loggerAdapter;
            for (Map.Entry<String, FailsafeLogger> entry : LOGGERS.entrySet()) {
                entry.getValue().setLogger(LoggerFactory.loggerAdapter.getLogger(entry.getKey()));
            }
        }
    }


    public static Logger getLogger(String key) {
        return ConcurrentHashMapUtils.computeIfAbsent(
                LOGGERS, key, k -> new FailsafeLogger(loggerAdapter.getLogger(k)));
    }

    //    public static Logger getLogger(Class<?> key) {
//        return ConcurrentHashMapUtils.computeIfAbsent(
//                LOGGERS, key.getName(), name -> new FailsafeLogger(loggerAdapter.getLogger(name)));
//    }
    public static Logger getLogger(Class<?> key) {
        return ConcurrentHashMapUtils.computeIfAbsent(
                LOGGERS, key.getName(), name -> new FailsafeLogger(loggerAdapter.getLogger(name)));
    }

    public static ErrorTypeAwareLogger getErrorTypeAwareLogger(Class<?> key){
        return ConcurrentHashMapUtils.computeIfAbsent(
            ERROR_TYPE_AWARE_LOGGERS,
                key.getName(),
                name->new FailsafeErrorTypeAwareLogger(loggerAdapter.getLogger(name))
        );
    }


    public static Level getLevel() {
        return loggerAdapter.getLevel();
    }

    public static void setLevel(Level level) {
        loggerAdapter.setLevel(level);
    }

    public static File getFile() {
        return loggerAdapter.getFile();
    }


}
