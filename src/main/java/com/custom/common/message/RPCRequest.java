package com.custom.common.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Gemaxis
 * @date 2024/07/10 10:58
 **/
@Data
@Builder
public class RPCRequest implements Serializable {
    // 服务类名
    private String interfaceName;
    // 方法名
    private String methodName;
    // 参数列表
    private Object[] params;
    // 参数类型
    private Class<?>[] paramsType;
}
