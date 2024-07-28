package com.custom.common.codecs;

/**
 * Dubbo 的 Serialization 接口和 MyRPC对应关系
 * getContentTypeId 对应 getType
 * getContentType 是取得对应类型的文本，在 MyRPC中没有实现
 * serialize 和 deserialize 两版类似
 * 此外 Dubbo 实现：使用 Fastjson2SecurityManager 提供安全管理机制来防止反序列化漏洞攻击。
 */
public interface Serializer {
    // 把对象序列化成字节数组
    byte[] serialize(Object obj);

    // 从字节数组反序列化成消息, 使用java自带序列化方式不用messageType也能得到相应的对象（序列化字节数组里包含类信息）
    // 其它方式需指定消息格式，再根据message转化成相应的对象
    Object deserialize(byte[] bytes, int messageType);

    // 返回使用的序列器，是哪个
    // 0: java自带序列化方式
    // 1: json序列化方式
    // 2: fastjson2序列化方式
    int getType();

    // 根据序号取出序列化器，暂时有两种实现方式，需要其它方式，实现这个接口即可
    static Serializer getSerializerByCode(int code) {
        switch (code) {
            case 0:
                return new ObjectSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new FastJson2Serializer();
            default:
                return null;
        }
    }
}
