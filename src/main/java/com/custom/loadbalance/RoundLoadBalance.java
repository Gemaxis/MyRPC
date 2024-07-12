package com.custom.loadbalance;

import java.util.List;

/**
 * @author Gemaxis
 * @date 2024/07/12 10:10
 **/
public class RoundLoadBalance implements LoadBalance {
    private static final RoundLoadBalance INSTANCE = new RoundLoadBalance();
    private int choose = -1;

    // 私有构造函数，防止外部实例化
    private RoundLoadBalance() {
    }
    // 提供访问实例的静态方法
    public static RoundLoadBalance getInstance() {
        return INSTANCE;
    }

    @Override
    public String balance(List<String> addressList) {
        ++choose;
        choose = choose % addressList.size();
        System.out.println("负载均衡选择了" + choose + "服务器");

        return addressList.get(choose);
    }
}
