package com.custom.common.threadlocal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Gemaxis
 * @date 2024/11/18 12:08
 **/
public class NamedInternalThreadFactoryTest {
    @Test
    void newThread(){
        NamedInternalThreadFactory namedInternalThreadFactory=new NamedInternalThreadFactory("MyRPC");
        Thread t=namedInternalThreadFactory.newThread(()->{});
        Assertions.assertEquals(t.getClass(),InternalThread.class,"thread is not InternalThread");
        Assertions.assertEquals(t.getName(),"MyRPC-thread-1");
    }
}
