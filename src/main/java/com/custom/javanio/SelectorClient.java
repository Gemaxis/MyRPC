package com.custom.javanio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Gemaxis
 * @date 2024/04/28 14:05
 **/
public class SelectorClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
//        socketChannel.bind(address);
        socketChannel.configureBlocking(false);
        boolean connect = socketChannel.connect(address);
        if(!connect){
            while (!socketChannel.finishConnect()){
                System.out.println("等待服务器连接。。。");
            }
        }
        String message="Hello 欢迎连接";
        ByteBuffer bytebuffer = ByteBuffer.wrap(message.getBytes());
        socketChannel.write(bytebuffer);
        System.in.read();
    }
}
