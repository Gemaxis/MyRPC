package com.custom.loadbalance;

import java.util.List;

/**
 * @author Gemaxis
 * @date 2024/07/12 10:10
 **/
public class RoundLoadBalance implements LoadBalance {
    private int choose = -1;

    @Override
    public String balance(List<String> addressList) {
        ++choose;
        choose = choose % addressList.size();
        System.out.println("负载均衡选择了" + choose + "服务器");

        return addressList.get(choose);
    }
}
