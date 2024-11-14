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
    private static final long serialVersionUID=-1985165475234910535L;

    private static final Map<String,URL> cachedURLs=new LRUCache<>();

    private final URLAddress urlAddress;


    public URL(URLAddress urlAddress) {
        this.urlAddress = urlAddress;
    }
}
