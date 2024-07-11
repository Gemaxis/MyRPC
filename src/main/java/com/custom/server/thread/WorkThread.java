package com.custom.server.thread;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import com.custom.server.ServiceProvider;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author Gemaxis
 * @date 2024/07/10 12:09
 **/

@AllArgsConstructor
public class WorkThread implements Runnable {

    private Socket socket;
    private ServiceProvider serviceProvider;
//    private Map<String, Object> serviceProvider;

    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            // 读取客户端的 request
            RPCRequest rpcRequest = (RPCRequest) ois.readObject();
            // 反射调用服务方法返回值
            RPCResponse rpcResponse = getResponse(rpcRequest);
            oos.writeObject(rpcResponse);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private RPCResponse getResponse(RPCRequest rpcRequest) {
        // 得到服务名
        String interfaceName = rpcRequest.getInterfaceName();
        // 得到相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        // 通过反射调用方法

        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            Object invoke = method.invoke(service, rpcRequest.getParams());
            return RPCResponse.success(invoke);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RPCResponse.fail();
        }
    }
}
