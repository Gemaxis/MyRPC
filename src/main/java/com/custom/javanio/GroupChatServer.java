package com.custom.javanio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Gemaxis
 * @date 2024/04/28 14:27
 **/
public class GroupChatServer {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    public static final int PORT = 6667;

    // 构造方法
    public GroupChatServer() {
        try {
            this.selector = Selector.open();
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", PORT));
            // 设置连接为非阻塞模式，影响accept
            serverSocketChannel.configureBlocking(false);
            // 通道注册到选择器中
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen() {
        while (true) {
            try {
                // 获取监听的事件总数
                int count = selector.select(2000);
                if (count > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            // 设置线程为非阻塞，影响read
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println(socketChannel.getRemoteAddress() + "上线了!~");
                        }
                        if (key.isReadable()) {
                            readData(key);
                        }
                        iterator.remove();
                    }
                } else {
                    System.out.println("等待。。。");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    // 获取客户端发送的消息
    private void readData(SelectionKey selectionKey) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int count = socketChannel.read(byteBuffer);
            if (count > 0) {
                String message = new String(byteBuffer.array());
                System.out.println("from 客户端：" + message);
                notifyALLClient(message, socketChannel);
            }
        } catch (Exception e) {
            try {
                System.out.println(socketChannel.getRemoteAddress() + "离线了。。。");
                selectionKey.cancel();
                // 关闭流
                socketChannel.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    private void notifyALLClient(String message, SocketChannel noNotifyChannel) throws IOException {
        System.out.println("服务器转发消息：");
        for (SelectionKey selectionKey : selector.keys()) {
            SelectableChannel channel = selectionKey.channel();
            if (channel instanceof SocketChannel && channel != noNotifyChannel) {
                SocketChannel socketChannel = (SocketChannel) channel;
                ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
                socketChannel.write(byteBuffer);
            }
        }

    }

    public static void main(String[] args) {
        GroupChatServer chatServer = new GroupChatServer();
        chatServer.listen();
    }
}
