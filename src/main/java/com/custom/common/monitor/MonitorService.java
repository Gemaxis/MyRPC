package com.custom.common.monitor;

import com.custom.common.URL;

import java.util.List;

public interface MonitorService {
    void collect(URL statistics);
    List<URL> lookup(URL query);
}
