package com.custom.loadbalance.impl;

/**
 * @author Gemaxis
 * @date 2024/07/17 16:56
 **/
public enum LoadFactor {
    Memory8G(5), Memory16G(10), Memory32G(20);

    private int vrNum;

    private LoadFactor(int vrNum) {
        this.vrNum = vrNum;
    }

    public int getVrNum() {
        return vrNum;
    }

}
