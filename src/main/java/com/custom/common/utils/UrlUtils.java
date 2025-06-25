package com.custom.common.utils;

/**
 * URL工具类。
 */
public class UrlUtils {

    /**
     * 拼接两个URL字符串
     * @param base 基础URL
     * @param path 路径
     * @return 拼接后的URL
     */
    public static String join(String base, String path) {
        if (base.endsWith("/")) {
            return base + path;
        } else {
            return base + "/" + path;
        }
    }
}
