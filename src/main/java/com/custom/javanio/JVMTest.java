package com.custom.javanio;

import java.nio.ByteBuffer;

/**
 * @author Gemaxis
 * @date 2024/05/03 17:13
 **/
public class JVMTest {
    static int _1GB=1024*1024*1024;

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_1GB);
        System.out.println("分配完毕");
        byteBuffer=null;
        System.gc();
        System.out.println("回收完毕");
    }
}
