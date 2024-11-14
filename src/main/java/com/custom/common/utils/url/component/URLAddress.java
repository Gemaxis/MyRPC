package com.custom.common.utils.url.component;

import com.custom.common.utils.NetUtils;

import java.io.Serializable;

/**
 * @author Gemaxis
 * @date 2024/11/03 22:33
 **/
public class URLAddress implements Serializable {
    private static final long serialVersionUID = -1985165475234910535L;

    protected String host;
    protected int port;

    //cache
    protected transient String rawAddress;
    protected transient long timestamp;

    public URLAddress(String host,int port){
        this(host, port, null);
    }

    public URLAddress(String host, int port, String rawAddress) {
        this.host = host;
        port = Math.max(port, 0);
        this.port = port;

        this.rawAddress = rawAddress;
        this.timestamp = System.currentTimeMillis();
    }
    public String getProtocol(){
        return "";
    }

    public URLAddress setProtocol(String protocol){
        return this;
    }
    public String getUsername() {
        return "";
    }

    public URLAddress setUsername(String username) {
        return this;
    }

    public String getPassword() {
        return "";
    }

    public URLAddress setPassword(String password) {
        return this;
    }

    public String getPath() {
        return "";
    }

    public URLAddress setPath(String path) {
        return this;
    }

    public String getHost() {
        return host;
    }

    public URLAddress setHost(String host) {
        return new URLAddress(host, port, null);
    }

    public int getPort() {
        return port;
    }

    public URLAddress setPort(int port) {
        return new URLAddress(host, port, null);
    }

    public String getAddress() {
        if (rawAddress == null) {
            rawAddress = getAddress(getHost(), getPort());
        }
        return rawAddress;
    }

    public URLAddress setAddress(String host, int port) {
        return new URLAddress(host, port, rawAddress);
    }

    public String getIp() {
        return NetUtils.getIpByHost(getHost());
    }

    public String getRawAddress() {
        return rawAddress;
    }

    protected String getAddress(String host, int port) {
        return port <= 0 ? host : host + ':' + port;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
