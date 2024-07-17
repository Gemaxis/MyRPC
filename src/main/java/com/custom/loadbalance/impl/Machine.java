package com.custom.loadbalance.impl;

/**
 * @author Gemaxis
 * @date 2024/07/17 16:56
 **/
public class Machine {
    private String host;

    private LoadFactor memory;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public LoadFactor getMemory() {
        return memory;
    }

    public void setMemory(LoadFactor memory) {
        this.memory = memory;
    }

    public Machine(String host, LoadFactor memory) {
        super();
        this.host = host;
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "Machine [host=" + host + ", memory=" + memory + "]";
    }


}
