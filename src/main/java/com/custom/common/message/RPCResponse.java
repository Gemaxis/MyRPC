package com.custom.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Gemaxis
 * @date 2024/07/10 11:01
 **/

/**
 * 定义返回信息格式RpcResponse（类似http格式）
 */
@Data
@Builder
@AllArgsConstructor
public class RPCResponse implements Serializable {
    private int code;
    private String message;
    private Object data;
    // 用其它序列化方式（除了java Serialize）得不到data的type
    private Class<?> dataType;

    public static final int CODE_SUCCESS = 200;
    public static final int CODE_FAIL = 500;
    public static final String MSG_FAIL = "Server error!!!";
    public static final String ATTR_KEY = "RPCResponse";

    public static RPCResponse success(Object _data) {
        return RPCResponse.builder().code(CODE_SUCCESS).data(_data).dataType(_data.getClass()).build();
    }

    public static RPCResponse fail() {
        return RPCResponse.builder().code(CODE_FAIL).message(MSG_FAIL).build();
    }
}
