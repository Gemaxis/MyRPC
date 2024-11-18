package com.custom.common;

import com.custom.common.utils.LRUCache;
import com.custom.common.utils.url.component.URLAddress;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Gemaxis
 * @date 2024/11/03 22:20
 **/
public class URL implements Serializable {
    private static final long serialVersionUID = -1985165475234910535L;

    private static final Map<String, URL> cachedURLs = new LRUCache<>();

    private final URLAddress urlAddress;


    public URL(URLAddress urlAddress) {
        this.urlAddress = urlAddress;
    }

    public static URL valueOf(String url) {
        return valueOf(url, false);
    }

    private static URL valueOf(String url, boolean encoded) {
        URLAddress url1 = new URLAddress("127.0.0.1", 8878);
        if (encoded) {
            return new URL(url1);
        }
        return new URL(url1);
    }
}
