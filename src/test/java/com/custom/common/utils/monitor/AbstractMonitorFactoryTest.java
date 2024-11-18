package com.custom.common.utils.monitor;

import com.custom.common.URL;
import com.custom.common.monitor.AbstractMonitorFactory;
import com.custom.common.monitor.Monitor;
import com.custom.common.monitor.MonitorFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Gemaxis
 * @date 2024/11/15 19:13
 **/
public class AbstractMonitorFactoryTest {
    private MonitorFactory monitorFactory = new AbstractMonitorFactory() {
        @Override
        protected Monitor createMonitor(final URL url) {
            return new Monitor() {
                @Override
                public void collect(URL statistics) {

                }

                @Override
                public List<URL> lookup(URL query) {
                    return null;
                }

                @Override
                public URL getUrl() {
                    return url;
                }

                @Override
                public boolean isAvailable() {
                    return true;
                }

                @Override
                public void destory() {

                }
            };
        }
    };

    @Test
    void testMonitorFactoryCache() throws InterruptedException {
        URL url = URL.valueOf("MyRPC://" + "127.0.0.1" + ":2233");
        Monitor monitor1 = monitorFactory.getMonitor(url);
        Monitor monitor2 = monitorFactory.getMonitor(url);
        if (monitor1 == null || monitor2 == null) {
            Thread.sleep(2000);
            monitor1 = monitorFactory.getMonitor(url);
            monitor2 = monitorFactory.getMonitor(url);
        }
        assertEquals(monitor1, monitor2);
    }
}
