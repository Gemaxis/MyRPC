package com.custom.common.threadlocal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Gemaxis
 * @date 2024/11/18 12:08
 **/
public class NamedInternalThreadFactoryTest {
    @Test
    void newThread() {
        NamedInternalThreadFactory namedInternalThreadFactory = new NamedInternalThreadFactory("MyRPC");
        Thread t = namedInternalThreadFactory.newThread(() -> {
        });
        Assertions.assertEquals(InternalThread.class, t.getClass(), "thread is not InternalThread");
        Assertions.assertEquals("MyRPC-thread-1", t.getName());
    }
}
