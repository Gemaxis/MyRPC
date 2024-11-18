package com.custom.common.threadlocal;

import com.custom.common.utils.NamedThreadFactory;

/**
 * @author Gemaxis
 * @date 2024/11/18 11:31
 **/

/**
 * ThreadFactory which produce{@link InternalThread}
 */
public class NamedInternalThreadFactory extends NamedThreadFactory {

    public NamedInternalThreadFactory() {
        super();
    }

    public NamedInternalThreadFactory(String prefix) {
        super(prefix, false);
    }

    public NamedInternalThreadFactory(String prefix, boolean damon) {
        super(prefix, damon);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = mPrefix + mThreadNum.getAndIncrement();
        InternalThread ret = new InternalThread(mGroup, InternalRunnable.Wrap(runnable), name, 0);
        ret.setDaemon(mDaemon);
        return ret;
    }
}

















