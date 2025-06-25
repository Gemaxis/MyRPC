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
 * @author Gemaxis
 * @date 2024/07/10 19:13
 **/
public class NettyRPCClient implements RPCClient {

    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private String host;
    private int port;
    private ServiceCenter serviceCenter;

    public NettyRPCClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public NettyRPCClient() {
//        this.serviceRegister = new ZkServiceRegister();
        this.serviceCenter = new ZkServiceCenter();
    }

    public NettyRPCClient(ServiceCenter serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    static {
        bootstrap = new Bootstrap();
        // Netty 的 Reactor 线程池 是 EventLoopGroup
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                // NettyClientInitializer这里 配置netty对消息的处理机制
                .handler(new NettyClientInitializer());
    }

    /**
     * 这里需要操作一下，因为netty的传输都是异步的，你发送request，会立刻返回， 而不是想要的相应的response
     */
    @Override
    public RPCResponse sendRequest(RPCRequest request) {
        // 从注册中心获取host，port
        InetSocketAddress address = serviceCenter.serverDiscovery(request.getInterfaceName());
        host = address.getHostName();
        port = address.getPort();
        try {
            // 创建一个channelFuture对象，代表这一个操作事件，sync方法表示堵塞直到connect完成
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            // 发送数据
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
            // AttributeKey是，线程隔离的，不会由线程安全问题。
            // 实际上不应通过阻塞，可通过回调函数
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
