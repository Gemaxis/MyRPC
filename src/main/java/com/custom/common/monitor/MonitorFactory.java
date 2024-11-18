package com.custom.common.monitor;

import com.custom.common.URL;

public interface MonitorFactory {
    Monitor getMonitor(URL url);
}
