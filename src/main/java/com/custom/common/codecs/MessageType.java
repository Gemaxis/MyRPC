package com.custom.common.codecs;

import lombok.AllArgsConstructor;

/**
 * @author Gemaxis
 * @date 2024/07/11 16:14
 **/
@AllArgsConstructor
public enum MessageType {
    REQUEST(0), RESPONSE(1);
    private int code;

    public int getCode() {
        return code;
    }
}
