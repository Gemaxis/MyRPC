package com.custom.loadbalance.impl;

import com.custom.loadbalance.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @author Gemaxis
 * @date 2024/07/12 09:56
 **/
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String balance(List<String> addressList) {
        Random random = new Random();
        int choose= random.nextInt(addressList.size());
        System.out.println("负载均衡选择了"+choose+"服务器");

        return addressList.get(choose);
    }
}
