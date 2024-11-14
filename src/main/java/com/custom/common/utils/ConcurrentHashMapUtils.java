package com.custom.common.utils;

import com.custom.common.logger.JRE;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author Gemaxis
 * @date 2024/10/27 17:13
 **/

/**
 * ConcurrentHashMap 的 computeIfAbsent 方法在键已存在的情况下仍可能会导致阻塞，这与预期行为不符。
 * 所以使用 ConcurrentHashMapUtils 的方法，能够：
 * 1. 6倍性能提升，（对于key已存在的情况下）
 * 2. 可能存在循环依赖问题
 */
public class ConcurrentHashMapUtils {

    public static <K, V> V computeIfAbsent(ConcurrentMap<K, V> map, K key, Function<? super K, ? extends V> func) {
        Objects.requireNonNull(func);
        if (JRE.JAVA_8.isCurrentVersion()) {
            // 解决循环依赖问题，防止 ConcurrentHashMapUtils.computeIfAbsent(map,"AaAa",key->map.computeIfAbsent("BbBb",key2->42)) 死循环
            V v = map.get(key);
            if (v == null) {
                // issue#11986 lock bug
                // v = map.computeIfAbsent(key, func);

                // this bug fix methods maybe cause `func.apply` multiple calls.
                v = func.apply(key);
                if (v == null) {
                    return null;
                }
                final V res = map.putIfAbsent(key, v);
                if (res != null) {
                    // 如果旧值存在，说明其他线程已经赋值成功，putIfAbsent没有执行，返回旧值
                    return res;
                }
            }
            return v;
        } else {
            return map.computeIfAbsent(key, func);
        }
    }
}

// Hutool 解决方法
// 这种方式获取的value有可能为null
//    V value = map.get(key);
//    if (null == value) {
//        map.putIfAbsent(key, mappingFunction.apply(key));
//        value = map.get(key);
//        }
//        return value;