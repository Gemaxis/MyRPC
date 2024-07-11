package com.custom.common.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Gemaxis
 * @date 2024/07/10 11:01
 **/
@Data
@Builder
public class RPCResponse implements Serializable {
    private int code;
    private String message;
    private Object data;

    public static RPCResponse success(Object _data) {
        return RPCResponse.builder().code(200).data(_data).build();
    }

    public static RPCResponse fail() {
        return RPCResponse.builder().code(500).message("Server error!!!").build();
    }
}
