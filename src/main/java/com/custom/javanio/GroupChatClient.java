package com.custom.javanio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author Gemaxis
 * @date 2024/04/28 16:02
 **/
public class GroupChatClient {
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;

    public GroupChatClient() {
        try {
            this.selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 6667));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            // socketName:/127.0.0.1:xxxx substring为了去掉前面的/
            userName = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(userName + " is ok~");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 发送消息到服务端
    private void sendMessage(String messag) {
        messag = userName + "说：" + messag;
        try {
            socketChannel.write(ByteBuffer.wrap(messag.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 读取服务端发送过来的消息
    private void readMessage() {
        try {
            int count = selector.select();
            if (count > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    // 读事件就绪
                    if (selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        channel.read(byteBuffer);
                        System.out.println(new String(byteBuffer.array()));
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        GroupChatClient chatClient = new GroupChatClient();
        new Thread(() -> {
            while (true) {
                chatClient.readMessage();
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String message = scanner.nextLine();
            chatClient.sendMessage(message);
        }
    }
}
