package com.custom.register;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    /**
     * 注册：保存服务与地址
     *
     * @param serviceName
     * @param serverAddress
     */
    void register(String serviceName, InetSocketAddress serverAddress);

    /**
     * 查询：根据服务名查找地址
     *
     * @param serviceName
     * @return
     */
    InetSocketAddress serverDiscovery(String serviceName);
}
