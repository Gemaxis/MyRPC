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

    public static RPCResponse success(Object _data) {
        return RPCResponse.builder().code(200).data(_data).dataType(_data.getClass()).build();
    }

    public static RPCResponse fail() {
        return RPCResponse.builder().code(500).message("Server error!!!").build();
    }
}
