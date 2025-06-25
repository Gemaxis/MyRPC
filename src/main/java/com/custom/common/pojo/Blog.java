package com.custom.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 博客实体类。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blog implements Serializable {
    /** 博客ID */
    private Integer id;
    /** 博客标题 */
    private String title;
    /** 博客作者ID */
    private Integer authorId;
}
