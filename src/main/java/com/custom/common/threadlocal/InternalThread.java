package com.custom.common.threadlocal;

/**
 * @author Gemaxis
 * @date 2024/11/15 21:23
 **/

/**
 * InternalThread 是优化过的线程，拥有更高效的 InternalThreadLocalMap 访问路径（通过成员变量直接访问）
 */
public class InternalThread extends Thread{
    private InternalThreadLocalMap threadLocalMap;
    public InternalThread() {}

    public InternalThread(Runnable target) {
        super(target);
    }

    public InternalThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public InternalThread(String name) {
        super(name);
    }

    public InternalThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public InternalThread(Runnable target, String name) {
        super(target, name);
    }

    public InternalThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public InternalThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }
    public InternalThreadLocalMap threadLocalMap() {
        return threadLocalMap;
    }
    public final void setThreadLocalMap(InternalThreadLocalMap threadLocalMap) {
        this.threadLocalMap = threadLocalMap;
    }

}
