package com.custom.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gemaxis
 * @date 2024/07/12 14:25
 **/
public class ServiceCache {
    // key: serviceName 服务名
    // value： addressList 服务提供者列表
    private static Map<String, List<String>> cache = new HashMap<>();

    // 添加服务
    public void addServiceToCache(String serviceName, String address) {
        cache.computeIfAbsent(serviceName, k -> new ArrayList<>()).add(address);
        System.out.println("将name为" + serviceName + "和地址为" + address + "的服务添加到本地缓存中");
    }

    // 取出服务地址
    public List<String> getServiceFromCache(String serviceName) {
        List<String> result = cache.getOrDefault(serviceName, null);
        return result;
    }

    // 删除服务地址
    public void deleteServiceFromCache(String serviceName, String address) {
        if (cache.get(serviceName) == null) {
            System.out.println("无此服务");
            return;
        }
        List<String> addressList = cache.get(serviceName);
        addressList.remove(address);
        System.out.println("将name为" + serviceName + "的地址为" + address + "的服务从本地缓存中删除");
    }
}
