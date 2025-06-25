package com.custom.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

/**
 * RPC请求消息体，包含服务名、方法名、参数等。
 */
@Data
@Builder
@AllArgsConstructor
public class RPCRequest implements Serializable {
    /** 服务类名 */
    private String interfaceName;
    /** 方法名 */
    private String methodName;
    /** 参数列表 */
    private Object[] params;
    /** 参数类型 */
    private Class<?>[] paramsType;
}
