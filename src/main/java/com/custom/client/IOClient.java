package com.custom.client;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 基于Socket的简单RPC客户端。
 */
public class IOClient {

    /**
     * 发送RPC请求，返回响应
     * @param host 服务端主机
     * @param port 服务端端口
     * @param request 请求对象
     * @return 响应对象
     */
    public static RPCResponse sendRequest(String host, int port, RPCRequest request) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            oos.writeObject(request);
            oos.flush();
            return (RPCResponse) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}