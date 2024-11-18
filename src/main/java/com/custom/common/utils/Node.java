package com.custom.common.utils;

import com.custom.common.URL;

/**
 * Node. (API/SPI, Prototype, ThreadSafe)
 */
public interface Node {
    URL getUrl();

    boolean isAvailable();

    void destory();
}
