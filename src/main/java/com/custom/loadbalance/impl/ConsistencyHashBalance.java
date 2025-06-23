package com.custom.loadbalance.impl;

import com.custom.loadbalance.LoadBalance;

import java.util.*;

/**
 * @author Gemaxis
 * @date 2024/07/17 15:24
 **/
public class ConsistencyHashBalance implements LoadBalance {
    // 虚拟节点的个数
    private static final int VIRTUAL_NUM = 5;
    // 虚拟节点分配，key是hash值，value是虚拟节点服务器名称
    private static SortedMap<Integer, String> shards = new TreeMap<Integer, String>();
    // 真实节点列表
    private static List<String> realNodes = new LinkedList<String>();
    //模拟初始服务器
    private static String[] servers = {"192.168.1.1", "192.168.1.2", "192.168.1.3", "192.168.1.5", "192.168.1.6"};

    static {
        initServer(Arrays.asList(servers));
    }


    /**
     * 服务器初始化
     *
     * @param serviceList
     */
    private static void initServer(List<String> serviceList) {
        for (String server : serviceList) {
            realNodes.add(server);
            System.out.println("真实节点[" + server + "] 被添加");
            addVirtualNodes(server);
        }
    }

    /**
     * 获取被分配的节点名
     *
     * @param node
     * @return
     */
    public static String getServer(String node) {
//        initServer(serviceList);
        int hash = getHash(node);
        Integer key = null;
        // 使用 tailMap 方法获取一致性哈希环 shards 中所有大于等于 hash 的键值对
        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()) {
            // 如果为空，说明没有比 hash 更大的键，此时应该循环到哈希环的第一个节点
            key = shards.lastKey();
        } else {
            // 如果不为空，说明有比 hash 更大的键，将 key 设置为离 hash 最近的一个节点
            key = subMap.firstKey();
        }
        String virtualNode = shards.get(key);
        //提取 && 前面的部分，返回原始节点名称
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    /**
     * 添加节点
     *
     * @param node
     */
    public static void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            System.out.println("真实节点[" + node + "] 上线添加");
            addVirtualNodes(node);
        }
    }

    /**
     * 删除节点
     *
     * @param node
     */
    public static void delNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            System.out.println("真实节点[" + node + "] 下线移除");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被移除");
            }
        }
    }

    /**
     * FNV1_32_HASH算法
     */
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    @Override
    public String balance(List<String> addressList) {
        String random = UUID.randomUUID().toString();
        initServer(addressList);
        return getServer(random);
    }

    public static void main(String[] args) {
        //模拟客户端的请求
        String[] nodes = {"127.0.0.1", "10.9.3.253", "192.168.10.1"};

        for (String node : nodes) {
            System.out.println("[" + node + "]的hash值为" + getHash(node) + ", 被路由到结点[" + getServer(node) + "]");
        }

        // 添加一个节点(模拟服务器上线)
        addNode("192.168.1.7");
        // 删除一个节点（模拟服务器下线）
        delNode("192.168.1.2");

        for (String node : nodes) {
            System.out.println("[" + node + "]的hash值为" + getHash(node) + ", 被路由到结点[" + getServer(node) + "]");
        }


    }

    /**
     * 为指定节点添加虚拟节点到哈希环
     *
     * @param node 真实节点名称
     */
    private static void addVirtualNodes(String node) {
        for (int i = 0; i < VIRTUAL_NUM; i++) {
            String virtualNode = node + "&&VN" + i;
            int hash = getHash(virtualNode);
            shards.put(hash, virtualNode);
            System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
        }
    }


}
