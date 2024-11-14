package com.custom.common.utils;

import java.util.*;

/**
 * @author Gemaxis
 * @date 2024/11/02 16:20
 **/
public class CollectionUtils {
    /**
     * 根据字符串的“简单名称”排序
     * 1. 只根据最后一段简单名称（即最后一个 . 后的部分）
     * 2. compareToIgnoreCase 忽略大小写
     * 3. 对于可能包含 null 的字符串列表更加安全
     */
    private static final Comparator<String> SIMPLE_NAME_COMPARATOR = (s1, s2) -> {
        if (s1 == null && s2 == null) {
            return 0;
        }
        if (s1 == null) {
            return -1;
        }
        if (s2 == null) {
            return 1;
        }
        int i1 = s1.lastIndexOf('.');
        if (i1 >= 0) {
            s1 = s1.substring(i1 + 1);
        }
        int i2 = s2.lastIndexOf('.');
        if (i2 >= 0) {
            s2 = s2.substring(i2 + 1);
        }
        return s1.compareToIgnoreCase(s2);
    };

    public static <T> List<T> sort(List<T> list) {
        if (isNotEmpty(list)) {
            Collections.sort((List) list);
        }
        return list;
    }

    public static List<String> sortSimpleName(List<String> list) {
        if (list != null && list.size() > 0) {
            Collections.sort(list, SIMPLE_NAME_COMPARATOR);
        }
        return list;
    }

    public static <T> boolean isNotEmpty(Collection<?> collections) {
        return !isEmpty(collections);
    }


    public static boolean isEmpty(Collection<?> collections) {
        return collections == null || collections.isEmpty();
    }

    public static List<String> join(Map<String, String> map, String separator) {
        if (map == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        if (map.size() == 0) {
            return list;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isEmpty(value)) {
                list.add(key);
            } else {
                list.add(key + separator + value);
            }
        }
        return list;
    }

    public static String join(List<String> list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static boolean mapEquals(Map<?, ?> map1, Map<?, ?> map2) {
        if (map1 == null && map2 == null) {
            return true;
        }
        if (map1 == null || map2 == null) {
            return false;
        }
        if (map1.size() != map2.size()) {
            return false;
        }
        for (Map.Entry<?, ?> entry : map1.entrySet()) {
            Object key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = map2.get(key);
            if (!objectEquals(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    private static boolean objectEquals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }

    public static Map<String, String> toStringMap(String... pairs) {
        Map<String, String> parameters = new HashMap<>();
        if (ArrayUtils.isEmpty(pairs)) {
            return parameters;
        }
        if (pairs.length > 0) {
            if (pairs.length % 2 != 0) {
                throw new IllegalArgumentException("pairs must be even.");
            }
            for (int i = 0; i < pairs.length; i = i + 2) {
                parameters.put(pairs[i], pairs[i + 1]);
            }
        }
        return parameters;
    }
    public static <T> T first(Collection<T> values) {
        if (isEmpty(values)) {
            return null;
        }
        if (values instanceof List) {
            List<T> list = (List<T>) values;
            return list.get(0);
        } else {
            return values.iterator().next();
        }
    }
}
