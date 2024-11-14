package com.custom.common.utils;

/**
 * @author Gemaxis
 * @date 2024/10/25 19:28
 **/
public final class ArrayUtils {
    private ArrayUtils() {
    }


    public static boolean isEmpty(final Object[] array) {
        return (array == null || array.length == 0);
    }

    public static boolean isNotEmpty(final Object[] array) {
        return !isEmpty(array);
    }

    public static boolean contains(final String[] array, String valueToFind) {
        return indexOf(array, valueToFind, 0) != -1;
    }

    public static int indexOf(String[] array, String valueToFind, int startIndex) {
        if (isEmpty(array) || valueToFind == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }
}
