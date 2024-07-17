package com.custom.client.service;

import com.custom.client.cache.ServiceCache;
import com.custom.loadbalance.LoadBalance;
import com.custom.loadbalance.impl.RoundLoadBalance;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Gemaxis
 * @date 2024/07/15 15:03
 **/
public class ZkServiceCenter implements ServiceCenter {
    //        private static final LoadBalance loadBalance = new RoundLoadBalance();
    // 单例实现轮询负载均衡
    private static final LoadBalance loadBalance = RoundLoadBalance.getInstance();
    //    private static final LoadBalance loadBalance = new RandomLoadBalance();

    // 本地缓存
    private ServiceCache serviceCache;
    // curator 提供的zookeeper客户端
    private CuratorFramework client;
    // zk根路径节点
    private static final String ROOT_PATH = "MyRPC";
    private static final String RETRY = "CanRetry";

    // zk 客户端初始化，并与 zk 服务端建立连接
    public ZkServiceCenter() {
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper 连接成功");
        // 初始化本地缓存
        serviceCache = new ServiceCache();
        // 加入 watch
        WatchZK watchZK = new WatchZK(client, serviceCache);
        watchZK.watchToUpdate(ROOT_PATH);
    }


    /**
     * 根据服务名返回地址
     *
     * @param serviceName
     * @return
     */
    @Override
    public InetSocketAddress serverDiscovery(String serviceName) {
        try {
            // 先去寻找本地缓存
            List<String> serviceList = serviceCache.getServiceFromCache(serviceName);
            // 如果找不到，再去 ZK 中找
            if (serviceList == null) {
                serviceList = client.getChildren().forPath("/" + serviceName);
            }
            System.out.println(serviceList);
            // 默认使用第一个
//            String string = strings.get(0);
            // 使用负载均衡
            String string = loadBalance.balance(serviceList);
            return parseAddress(string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public boolean checkRetry(String serviceName) {
        boolean canRetry = false;
        try {
            List<String> serviceList = client.getChildren().forPath("/" + RETRY);
            for (String s : serviceList) {
                // 如果列表中有该服务
                if (s.equals(serviceName)) {
                    System.out.println("服务" + serviceName + "在白名单上，可进行重试");
                    canRetry = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return canRetry;
    }

    /**
     * address字符串->地址 XXX.XXX.XXX.XXX:port
     *
     * @param address
     * @return
     */
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));

    }
}
