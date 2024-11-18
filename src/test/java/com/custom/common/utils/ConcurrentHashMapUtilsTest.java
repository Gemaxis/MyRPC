package com.custom.common.utils;

import com.custom.common.utils.ConcurrentHashMapUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Gemaxis
 * @date 2024/11/03 15:20
 **/
public class ConcurrentHashMapUtilsTest {
    @Test
    void testComputeIfAbsent(){
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        String ifAbsent = ConcurrentHashMapUtils.computeIfAbsent(map, "mxnn", k -> "mxnn");
        assertEquals("mxnn", ifAbsent);

        ifAbsent = ConcurrentHashMapUtils.computeIfAbsent(map, "mxnn", k -> "mxnn1");
        assertEquals("mxnn", ifAbsent);

        map.remove("mxnn");
        ifAbsent = ConcurrentHashMapUtils.computeIfAbsent(map, "mxnn", k -> "mxnn1");
        assertEquals("mxnn1", ifAbsent);
    }

    @Test
    @EnabledForJreRange(max= JRE.JAVA_8)
    void issue11986ForJava8Test(){
        final ConcurrentHashMap<String,Integer> map=new ConcurrentHashMap<>();
        ConcurrentHashMapUtils.computeIfAbsent(map,"AaAa",key->map.computeIfAbsent("BbBb",key2->42));
        assertEquals(2, map.size());
        assertEquals(Integer.valueOf(42), map.get("AaAa"));
        assertEquals(Integer.valueOf(42), map.get("BbBb"));
    }
    @Test
    @EnabledForJreRange(min = org.junit.jupiter.api.condition.JRE.JAVA_9)
    public void issue11986ForJava17Test() {
        // https://github.com/apache/dubbo/issues/11986
        final ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // JDK9+ has been resolved JDK-8161372 bug, when cause dead then throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            ConcurrentHashMapUtils.computeIfAbsent(map, "AaAa", key -> map.computeIfAbsent("BBBB", key2 -> 42));
        });
    }
}
