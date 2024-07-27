package com.custom.javanio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Gemaxis
 * @date 2024/04/28 11:25
 * 使用allocate创建非直接缓冲区和allocateDirect创建直接缓冲区
 **/
public class ByteBufferAllocate {
    public static void main(String[] args) throws IOException {

        long start = System.currentTimeMillis();
        File file = new File("C:\\Users\\admin\\Videos\\Captures\\yhy.mp4");
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel inputChannel = fileInputStream.getChannel();
        FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\admin\\Videos\\Captures\\test.mp4"));
        FileChannel outputChannel = fileOutputStream.getChannel();
//        ByteBuffer byteBuffer = ByteBuffer.allocate(5 * 1024 * 1024);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(5 * 1024 * 1024);

        while (inputChannel.read(byteBuffer) != -1) {
            // 切换读模式
            byteBuffer.flip();
            outputChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        outputChannel.close();
        inputChannel.close();
        fileOutputStream.close();
        fileInputStream.close();
        long end = System.currentTimeMillis();
        System.out.println("消耗时间：" + (end - start) + "毫秒");
    }
}
