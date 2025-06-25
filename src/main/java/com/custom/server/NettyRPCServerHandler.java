package com.custom.server;

import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;
import com.custom.server.ratelimit.RateLimit;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.AllArgsConstructor;
import static com.custom.common.utils.CommonConstants.METHOD_EXECUTE_ERROR_MSG;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Gemaxis
 * @date 2024/07/10 18:43
 **/

/**
 * 因为是服务器端，接受到请求格式是RPCRequest
 * Object类型也行，需要强制转型
 */
@AllArgsConstructor
public class NettyRPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    private ServiceProvider serviceProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequest msg) throws Exception {
        RPCResponse response = getResponse(msg);
        ctx.writeAndFlush(response);
        ctx.close();
    }

    private RPCResponse getResponse(RPCRequest request) {
        // 得到服务名
        String interfaceName = request.getInterfaceName();

        // 当获取到调用请求，开始调用前，先加入限流器的判断
        RateLimit rateLimit = serviceProvider.getRateLimitProvider().getRateLimit(interfaceName);
        if (!rateLimit.getToken()) {
            //如果获取令牌失败，进行限流降级，快速返回结果
            return RPCResponse.fail();
        }

        // 得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        // 反射调用方法
        Method method = null;
        try {
            method = service.getClass().getMethod(request.getMethodName(), request.getParamsType());
            Object invoke = method.invoke(service, request.getParams());
            return RPCResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println(METHOD_EXECUTE_ERROR_MSG);
            return RPCResponse.fail();
        }
    }
}
