package com.custom.myrpc.common.utils;

import com.custom.common.utils.Log;
import com.custom.common.utils.LogUtil;
import com.custom.common.utils.MyRPCAppender;
import org.apache.log4j.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author Gemaxis
 * @date 2024/10/28 16:49
 **/
public class LogUtilTest {
    @AfterEach
    public void tearDown() {
        MyRPCAppender.logList.clear();
    }

    @Test
    void testStartStop(){
        LogUtil.start();
        assertThat(MyRPCAppender.available,is(true));
        LogUtil.stop();
        assertThat(MyRPCAppender.available,is(false));
    }

    @Test
    void testCheckNoError(){
        Log log=mock(Log.class);
        MyRPCAppender.logList.add(log);
        when(log.getLogLevel()).thenReturn(Level.ERROR);
        assertThat(LogUtil.checkNoError(),is(false));
        when(log.getLogLevel()).thenReturn(Level.INFO);
        assertThat(LogUtil.checkNoError(),is(true));
    }
    @Test
    void testFindName(){
        Log log=mock(Log.class);
        MyRPCAppender.logList.add(log);
        when(log.getLogName()).thenReturn("custom-name");
        assertThat(LogUtil.findName("name"),equalTo(1));
    }

    @Test
    void testFindLevel(){
        Log log=mock(Log.class);
        MyRPCAppender.logList.add(log);
        when(log.getLogLevel()).thenReturn(Level.ERROR);
        assertThat(LogUtil.findLevel(Level.ERROR),equalTo(1));
        assertThat(LogUtil.findLevel(Level.INFO),equalTo(0));
        Log log1=mock(Log.class);
        MyRPCAppender.logList.add(log1);
        when(log1.getLogLevel()).thenReturn(Level.ERROR);
        assertThat(LogUtil.findLevel(Level.ERROR),equalTo(2));

    }

    @Test
    void testFindLevelWithThreadName(){
        Log log=mock(Log.class);
        MyRPCAppender.logList.add(log);
        when(log.getLogLevel()).thenReturn(Level.ERROR);
        when(log.getLogThread()).thenReturn("thread-1");
        log = mock(Log.class);
        MyRPCAppender.logList.add(log);
        when(log.getLogLevel()).thenReturn(Level.ERROR);
        when(log.getLogThread()).thenReturn("thread-2");
        assertThat(LogUtil.findLevelWithThreadName(Level.ERROR, "thread-2"), equalTo(1));
        assertThat(LogUtil.findLevelWithThreadName(Level.ERROR, "thread-1"), equalTo(1));

    }

}
