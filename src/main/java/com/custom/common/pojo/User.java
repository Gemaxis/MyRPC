package com.custom.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 用户实体类。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    /** 用户ID */
    private Integer id;
    /** 用户名 */
    private String name;
    /** 用户密码 */
    private String password;
}