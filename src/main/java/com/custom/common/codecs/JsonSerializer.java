package com.custom.common.codecs;

/**
 * @author Gemaxis
 * @date 2024/07/11 15:02
 **/

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;

/**
 * 由于json序列化的方式是通过把对象转化成字符串，丢失了Data对象的类信息，所以deserialize需要
 * 了解对象对象的类信息，根据类信息把 JsonObject -> 对应的对象
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        byte[] jsonBytes = JSONObject.toJSONBytes(obj);
        return jsonBytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
//        System.out.println("messageType:" + messageType);
        // 传输的消息分为request与response
        switch (messageType) {
            case 0:
                RPCRequest request = JSON.parseObject(bytes, RPCRequest.class);
                // 修bug 参数为空 直接返回
                if (request.getParams() == null) {
                    return request;
                }
                Object[] objects = new Object[request.getParams().length];
                // 把json字串转化成对应的对象， fastjson可以读出基本数据类型，不用转化
                for (int i = 0; i < objects.length; i++) {
                    Class<?> paramsType = request.getParamsType()[i];
                    // 判断参数类型是否与参数的实际类型匹配
                    if (!paramsType.isAssignableFrom(request.getParams()[i].getClass())) {
                        // 如果不匹配，将参数转换为相应的对象类型
                        objects[i] = JSONObject.toJavaObject((JSONObject) request.getParams()[i], request.getParamsType()[i]);
                    } else {
                        // 如果匹配，直接赋值
                        objects[i] = request.getParams()[i];
                    }
                }
                request.setParams(objects);
                obj = request;
                break;
            case 1:
                RPCResponse response = JSON.parseObject(bytes, RPCResponse.class);
                Class<?> dataType = response.getDataType();
                // 判断响应数据类型是否与实际数据类型匹配
                if (!dataType.isAssignableFrom(response.getData().getClass())) {
                    // 如果不匹配，将数据转换为相应的对象类型
                    response.setData(JSONObject.toJavaObject((JSONObject) response.getData(), dataType));
                }
                obj = response;
                break;
            default:
                System.out.println("不支持此种消息");
                throw new RuntimeException();
        }
        return obj;
    }

    @Override
    public int getType() {
        return 1;
    }
}
