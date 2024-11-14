package com.custom.myrpc.common.utils;

import com.custom.common.utils.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Gemaxis
 * @date 2024/11/02 21:47
 **/
public class CollectionUtilsTest {

    @Test
    void testSort() {
        List<Integer> list = new ArrayList<>();
        list.add(100);
        list.add(10);
        list.add(20);

        List<Integer> expected = new ArrayList<>();
        expected.add(10);
        expected.add(20);
        expected.add(100);

        assertEquals(expected, CollectionUtils.sort(list));
    }

    @Test
    void testSortSimpleName() {
        List<String> list = new ArrayList<>();
        list.add("aaa.z");
        list.add("b");
        list.add(null);
        list.add("zzz.a");
        list.add("c");
        list.add(null);

        List<String> sorted = CollectionUtils.sortSimpleName(list);
        System.out.println(sorted);
        assertNull(sorted.get(0));
        assertNull(sorted.get(1));
        assertEquals("zzz.a", sorted.get(2));
    }

    @Test
    void testSortSimpleNameNull() {
        assertNull(CollectionUtils.sortSimpleName(null));

        assertTrue(CollectionUtils.sortSimpleName(new ArrayList<String>()).isEmpty());
    }

    @Test
    void testJoinList(){
        List<String> list=emptyList();
        assertEquals("", CollectionUtils.join(list, "/"));

        list = Arrays.asList("x");
        assertEquals("x", CollectionUtils.join(list, "-"));

        list = Arrays.asList("a", "b");
        assertEquals("a/b", CollectionUtils.join(list, "/"));

    }

    @Test
    void tstsStringMap1(){
        assertThat(CollectionUtils.toStringMap("key","value"),equalTo(Collections.singletonMap("key", "value")));
    }
    @Test
    void testStringMap2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> CollectionUtils.toStringMap("key", "value", "odd"));
    }
    @Test
    void testMapEquals() {
        assertTrue(CollectionUtils.mapEquals(null, null));
        assertFalse(CollectionUtils.mapEquals(null, new HashMap<String, String>()));
        assertFalse(CollectionUtils.mapEquals(new HashMap<String, String>(), null));

        assertTrue(CollectionUtils.mapEquals(
                CollectionUtils.toStringMap("1", "a", "2", "b"), CollectionUtils.toStringMap("1", "a", "2", "b")));
        assertFalse(CollectionUtils.mapEquals(
                CollectionUtils.toStringMap("1", "a"), CollectionUtils.toStringMap("1", "a", "2", "b")));
    }
}
