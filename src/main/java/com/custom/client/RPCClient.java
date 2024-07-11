package com.custom.client;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;

public interface RPCClient {
    // 定义底层通信的方法
    // 不同的网络连接，网络传输方式的客户端分别实现这个接口
    RPCResponse sendRequest(RPCRequest request);
}
