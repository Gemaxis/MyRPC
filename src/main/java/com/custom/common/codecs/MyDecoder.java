package com.custom.common.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author Gemaxis
 * @date 2024/07/11 16:20
 **/

/**
 * 按照自定义的消息格式解码数据
 * 在读取时需要按照顺序读取。这是因为编码和解码的过程需要严格匹配，以确保数据的完整性和正确解析。
 */

@AllArgsConstructor
public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("正在使用自定义解码器");
        // 读取消息类型
        short messageType = in.readShort();
        // 现在还只支持request与response请求
        if (messageType != MessageType.REQUEST.getCode() &&
                messageType != MessageType.RESPONSE.getCode()) {
            System.out.println("暂不支持此种数据");
            return;
        }
        // 读取序列化类型
        short serializerType = in.readShort();
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if (serializer == null) {
            throw new RuntimeException("不存在对应的序列化器");
        }
        int length = in.readInt();
        System.out.println("输出长度：" + length);
        // 读取序列化数组
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        // 用对应的序列化器解码字节数组
        Object deserialize = serializer.deserialize(bytes, messageType);
        out.add(deserialize);
    }
}
