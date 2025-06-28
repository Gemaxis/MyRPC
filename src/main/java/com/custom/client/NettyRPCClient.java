package com.custom.client;

import com.custom.client.service.ServiceCenter;
import com.custom.client.service.ZkServiceCenter;
import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * 基于Netty的RPC客户端实现。
 */
public class NettyRPCClient implements RPCClient {

    private static final Bootstrap BOOTSTRAP;
    private static final EventLoopGroup EVENT_LOOP_GROUP;
    private String host;
    private int port;
    private ServiceCenter serviceCenter;

    static {
        BOOTSTRAP = new Bootstrap();
        EVENT_LOOP_GROUP = new NioEventLoopGroup();
        BOOTSTRAP.group(EVENT_LOOP_GROUP).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    /**
     * 构造方法，指定host和port
     */
    public NettyRPCClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 默认构造，使用ZkServiceCenter
     */
    public NettyRPCClient() {
        this.serviceCenter = new ZkServiceCenter();
    }

    /**
     * 构造方法，指定ServiceCenter
     */
    public NettyRPCClient(ServiceCenter serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    /**
     * 发送RPC请求
     * @param request 请求对象
     * @return 响应对象
     */
    @Override
    public RPCResponse sendRequest(RPCRequest request) {
        InetSocketAddress address = serviceCenter.serverDiscovery(request.getInterfaceName());
        host = address.getHostName();
        port = address.getPort();
        try {
            ChannelFuture channelFuture = BOOTSTRAP.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            AttributeKey<RPCResponse> key = AttributeKey.valueOf(RPCResponse.ATTR_KEY);
            RPCResponse response = channel.attr(key).get();
            System.out.println(response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
