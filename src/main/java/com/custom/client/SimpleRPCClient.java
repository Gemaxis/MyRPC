package com.custom.client;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 简单Socket实现的RPC客户端。
 */
public class SimpleRPCClient implements RPCClient {

    private final String host;
    private final int port;

    /**
     * 构造方法
     * @param host 服务端主机
     * @param port 服务端端口
     */
    public SimpleRPCClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 发送RPC请求
     * @param request 请求对象
     * @return 响应对象
     */
    @Override
    public RPCResponse sendRequest(RPCRequest request) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            return (RPCResponse) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
