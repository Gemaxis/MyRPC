package com.custom.register;

import com.custom.loadbalance.LoadBalance;
import com.custom.loadbalance.RandomLoadBalance;
import com.custom.loadbalance.RoundLoadBalance;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Gemaxis
 * @date 2024/07/11 21:50
 **/
public class ZkServiceRegister implements ServiceRegister {
//        private static final LoadBalance loadBalance = new RoundLoadBalance();
    // 单例实现轮询负载均衡
    private static final LoadBalance loadBalance = RoundLoadBalance.getInstance();
    //    private static final LoadBalance loadBalance = new RandomLoadBalance();
    // curator 提供的zookeeper客户端
    private CuratorFramework client;
    // zk根路径节点
    private static final String ROOT_PATH = "MyRPC";

    // zk 客户端初始化，并与 zk 服务端建立连接
    public ZkServiceRegister() {
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
    }

    @Override
    public void register(String serviceName, InetSocketAddress serverAddress) {
        try {
            if (client.checkExists().forPath("/" + serviceName) == null) {
                // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath("/" + serviceName);
            }
            // 路径地址，一个/代表一个节点
            String path = "/" + serviceName + "/" + getServiceAddress(serverAddress);
            // 临时节点
            if (client.checkExists().forPath(path) == null) {
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(path);
            }
        } catch (Exception e) {
            System.out.println("此服务已存在");
            throw new RuntimeException(e);
        }
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
            List<String> strings = client.getChildren().forPath("/" + serviceName);
            System.out.println(strings);
            // 默认使用第一个
//            String string = strings.get(0);
            // 使用负载均衡
            String string = loadBalance.balance(strings);
            return parseAddress(string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    /**
     * 地址 -> XXX.XXX.XXX.XXX:port 字符串
     *
     * @param serverAddress
     * @return
     */
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }
}
