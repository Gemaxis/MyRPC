package com.custom.common.codecs;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.custom.common.message.RPCRequest;
import com.custom.common.message.RPCResponse;

/**
 * @author Gemaxis
 * @date 2024/07/27 17:07
 **/
public class FastJson2Serializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            return new byte[0];
        }
        // 使用 Fastjson2 进行序列化
        return JSON.toJSONBytes(obj);
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Object obj = null;
        // 传输的消息分为request与response
        switch (messageType) {
            case 0:
                RPCRequest request = JSON.parseObject(bytes, RPCRequest.class);
                if (request.getParams() == null) {
                    return request;
                }
                Object[] objects = new Object[request.getParams().length];
                for (int i = 0; i < objects.length; i++) {
                    Class<?> paramsType = request.getParamsType()[i];
                    if (!paramsType.isAssignableFrom(request.getParams()[i].getClass())) {
                        objects[i] = JSON.parseObject(JSON.toJSONString(request.getParams()[i]), request.getParamsType()[i]);
                    } else {
                        objects[i] = request.getParams()[i];
                    }
                }
                request.setParams(objects);
                obj = request;
                break;
            case 1:
                RPCResponse response = JSON.parseObject(bytes, RPCResponse.class);
                Class<?> dataType = response.getDataType();
                if (!dataType.isAssignableFrom(response.getData().getClass())) {
                    response.setData(JSON.parseObject(JSON.toJSONString(response.getData()), dataType));
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
        return 2;
    }
}
