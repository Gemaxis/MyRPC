package com.custom.client.service;

import java.net.InetSocketAddress;

public interface ServiceCenter {

    /**
     * 查询：根据服务名查找地址
     *
     * @param serviceName
     * @return
     */
    InetSocketAddress serverDiscovery(String serviceName);


    /**
     * 判断：判断是否在白名单中
     *
     * @param serviceName
     * @return
     */
    boolean checkRetry(String serviceName);
}
