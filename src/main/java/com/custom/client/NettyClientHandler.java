package com.custom.client;

import com.custom.common.message.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

/**
 * Netty客户端处理器，接收RPC响应。
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RPCResponse> {

    /**
     * 处理接收到的RPC响应
     * @param ctx 通道上下文
     * @param msg 响应消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCResponse msg) throws Exception {
        AttributeKey<RPCResponse> key = AttributeKey.valueOf(RPCResponse.ATTR_KEY);
        ctx.channel().attr(key).set(msg);
        ctx.channel().close();
    }

    /**
     * 处理异常
     * @param ctx 通道上下文
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
