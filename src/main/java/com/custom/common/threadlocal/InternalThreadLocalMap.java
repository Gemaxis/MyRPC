package com.custom.common.threadlocal;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gemaxis
 * @date 2024/11/15 21:24
 **/
public class InternalThreadLocalMap {
    // 用数组 indexedVariables 提高访问效率
    private Object[] indexedVariables;

    // 对于不支持快速路径的线程，使用 ThreadLocal 存储 InternalThreadLocalMap; 键是 slowThreadLocalMap，值是 InternalThreadLocalMap
    private static ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = new ThreadLocal<>();

    private static final AtomicInteger NEXT_INDEX = new AtomicInteger();

    static final Object UNSET = new Object();

    /**
     * should not be modified after initialization,
     * do not set as final due to unit test
     */
    // Reference: https://hg.openjdk.java.net/jdk8/jdk8/jdk/file/tip/src/share/classes/java/util/ArrayList.java#l229
    static int ARRAY_LIST_CAPACITY_MAX_SIZE = Integer.MAX_VALUE - 8;

    private static final int ARRAY_LIST_CAPACITY_EXPAND_THRESHOLD = 1 << 30;

    // 检查并返回当前线程是否已经设置了 InternalThreadLocalMap，如果没有设置，不会主动创建
    public static InternalThreadLocalMap getIfSet() {
        Thread thread = Thread.currentThread();
        if (thread instanceof InternalThread) {
            return ((InternalThread) thread).threadLocalMap();
        }
        return slowThreadLocalMap.get();
    }

    // 获取当前线程的 InternalThreadLocalMap，如果不存在则创建一个
    public static InternalThreadLocalMap get() {
        Thread thread = Thread.currentThread();
        if (thread instanceof InternalThread) {
            return fastGet((InternalThread) thread);
        }
        return slowGet();
    }

    public static InternalThreadLocalMap getAndRemove() {
        try {
            Thread thread = Thread.currentThread();
            if (thread instanceof InternalThread) {
                return ((InternalThread) thread).threadLocalMap();
            }
            return slowThreadLocalMap.get();
        } finally {
            remove();
        }
    }

    public static void set(InternalThreadLocalMap internalThreadLocalMap) {
        Thread thread = Thread.currentThread();
        if (thread instanceof InternalThread) {
            ((InternalThread) thread).setThreadLocalMap(internalThreadLocalMap);
        }
        slowThreadLocalMap.set(internalThreadLocalMap);
    }

    public static void remove() {
        Thread thread = Thread.currentThread();
        if (thread instanceof InternalThread) {
            ((InternalThread) thread).setThreadLocalMap(null);
        } else {
            slowThreadLocalMap.remove();
        }
    }

    public static int nextVariableIndex() {
        int index = NEXT_INDEX.getAndIncrement();
        if (index >= ARRAY_LIST_CAPACITY_MAX_SIZE || index < 0) {
            NEXT_INDEX.set(ARRAY_LIST_CAPACITY_MAX_SIZE);
            throw new IllegalStateException("Too many thread-local indexed variables");
        }
        return index;
    }

    public static int lastVariableIndex() {
        return NEXT_INDEX.get() - 1;
    }

    private InternalThreadLocalMap() {
        indexedVariables = newIndexedVariableTable();
    }

    public int size() {
        int count = 0;
        for (Object o : indexedVariables) {
            if (o != UNSET) {
                ++count;
            }
        }
        //the fist element in `indexedVariables` is a set to keep all the InternalThreadLocal to remove
        return count - 1;
    }

    public Object indexedVariable(int index) {
        Object[] lookup = indexedVariables;
        return index < lookup.length ? lookup[index] : UNSET;
    }

    private static Object[] newIndexedVariableTable() {
        int variableIndex = NEXT_INDEX.get();
        int newCapacity = variableIndex < 32 ? 32 : newCapacity(variableIndex);
        Object[] array = new Object[newCapacity];
        Arrays.fill(array, UNSET);
        return array;
    }

    /**
     * 使其变为大于等于 index 的最小的 2 的幂次方
     *
     * @param index
     * @return
     */
    private static int newCapacity(int index) {
        int newCapacity;
        if (index < ARRAY_LIST_CAPACITY_EXPAND_THRESHOLD) {
            newCapacity = index;
            newCapacity |= newCapacity >>> 1;
            newCapacity |= newCapacity >>> 2;
            newCapacity |= newCapacity >>> 4;
            newCapacity |= newCapacity >>> 8;
            newCapacity |= newCapacity >>> 16;
            newCapacity++;
        } else {
            newCapacity = ARRAY_LIST_CAPACITY_MAX_SIZE;
        }
        return newCapacity;
    }


    /**
     * slowGet 是普通 Thread, 使用 ThreadLocal 存储
     * 兼容性场景，适配所有线程类型
     *
     * @return
     */
    private static InternalThreadLocalMap slowGet() {
        ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = InternalThreadLocalMap.slowThreadLocalMap;
        InternalThreadLocalMap ret = slowThreadLocalMap.get();
        if (ret == null) {
            ret = new InternalThreadLocalMap();
            slowThreadLocalMap.set(ret);
        }
        return ret;
    }

    /**
     * fastGet 是 InternalThread，直接访问线程的字段
     * 控制线程类型时，优化性能
     *
     * @return
     */
    private static InternalThreadLocalMap fastGet(InternalThread thread) {
        InternalThreadLocalMap threadLocalMap = thread.threadLocalMap();
        if (threadLocalMap == null) {
            thread.setThreadLocalMap(threadLocalMap = new InternalThreadLocalMap());
        }
        return threadLocalMap;
    }

    public boolean setIndexedVariable(int index, Object value) {
        Object[] lookup = indexedVariables;
        if (index < lookup.length) {
            Object oldValue = lookup[index];
            lookup[index] = value;
            return oldValue == UNSET;
        } else {
            expandIndexedVariableTableAndSet(index, value);
            return true;
        }
    }

    private void expandIndexedVariableTableAndSet(int index, Object value) {
        Object[] oldArray = indexedVariables;
        final int oldCapacity = oldArray.length;
        int newCapacity = newCapacity(index);
        Object[] newArray = Arrays.copyOf(oldArray, newCapacity);
        Arrays.fill(newArray, oldCapacity, newArray.length, UNSET);
        newArray[index] = value;
        indexedVariables = newArray;
    }

    public Object removeIndexedVariable(int index) {
        Object[] lookup = indexedVariables;
        if (index < lookup.length) {
            Object v = lookup[index];
            lookup[index] = UNSET;
            return v;
        } else {
            return UNSET;
        }
    }
}
