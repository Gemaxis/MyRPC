package com.custom.server.register;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    /**
     * 注册：保存服务与地址
     *
     * @param serviceName
     * @param serverAddress
     */
    void register(String serviceName, InetSocketAddress serverAddress);

    void register(String serviceName, InetSocketAddress serverAddress, boolean canRetry);




}
