package com.custom.common.threadlocal;

/**
 * @author Gemaxis
 * @date 2024/11/18 11:43
 **/

/**
 *  防止 InternalThreadLocal 引起的内存泄漏
 */
public class InternalRunnable implements Runnable{

    private final Runnable runnable;
    public InternalRunnable(Runnable runnable){
        this.runnable=runnable;
    }
    @Override
    public void run() {
        try {
            runnable.run();
        } finally {
            InternalThreadLocal.removeAll();
        }
    }

    // 将普通的 Runnable 包装为 InternalRunnable
    public static Runnable Wrap(Runnable runnable){
        return runnable instanceof InternalRunnable?runnable:new InternalRunnable(runnable);
    }
}
