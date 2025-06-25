package com.custom.client;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;

/**
 * RPC客户端通信接口，不同实现可基于不同网络协议。
 */
public interface RPCClient {

    /**
     * 发送RPC请求
     * @param request 请求对象
     * @return 响应对象
     */
    RPCResponse sendRequest(RPCRequest request);
}
