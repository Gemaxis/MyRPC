package com.custom.common.utils;

import com.custom.common.logger.Logger;
import com.custom.common.logger.LoggerFactory;
import org.apache.log4j.Level;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Gemaxis
 * @date 2024/10/28 15:30
 **/
public class LogUtil {
    private static final Logger Log = LoggerFactory.getLogger(LogUtil.class);

    private LogUtil() {}

    public static void start() {
        MyRPCAppender.doStart();
    }

    public static void stop() {
        MyRPCAppender.doStop();
    }

    public static boolean checkNoError() {
        if (findLevel(Level.ERROR) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static int findName(String expectedName){
        int count=0;
        List<Log> logList=MyRPCAppender.logList;
        for (int i = 0; i < logList.size(); i++) {
            String logName = logList.get(i).getLogName();
            // 使用模糊查询 contains而不用equals 使得部分匹配也能计入统计
            if(logName.contains(expectedName)){
                count++;
            }
        }
        return count;
    }
    public static int findLevel(Level expectedLevel) {
        int count = 0;
        List<Log> logList = MyRPCAppender.logList;
        for (int i = 0; i < logList.size(); i++) {
            Level logLevel = logList.get(i).getLogLevel();
            if (logLevel.equals(expectedLevel)) {
                count++;
            }
        }
        return count;
    }

    public static int findLevelWithThreadName(Level expectedLevel,String threadName){
        int count=0;
        List<Log> logList=MyRPCAppender.logList;
        for (int i = 0; i < logList.size(); i++) {
            Log log=logList.get(i);
            if(log.getLogLevel().equals(expectedLevel)&&log.getLogThread().equals(threadName)){
                count++;
            }
        }
        return count;
    }

    /**
     * 获取当前时间字符串，格式为yyyy-MM-dd HH:mm:ss
     * @return 当前时间字符串
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
