package com.custom.common.utils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Gemaxis
 * @date 2024/10/27 10:16
 **/
public final class StringUtils {

    public static final String EMPTY_STRING = "";
    private static final int PAD_LIMIT = 8192;
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final Pattern NUM_PATTERN = Pattern.compile("^\\d+$");

    public static String repeat(final String str, final int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY_STRING;
        }
        final int inputLength = str.length();
        if (inputLength == 0 || repeat == 1) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return repeat(String.valueOf(str.charAt(0)), repeat);
        }
        final int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                return repeat(String.valueOf(str.charAt(0)), repeat);
            case 2:
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default:
                final StringBuilder builder = new StringBuilder(outputLength);
                for (int i = 0; i < repeat; i++) {
                    builder.append(str);
                }
                return builder.toString();
        }
    }

    public static String repeat(final String str, final String separator, final int repeat) {
        if (str == null || separator == null) {
            return repeat(str, repeat);
        }
        final String result = repeat(str + separator, repeat);
        return removeEnd(result, separator);
    }

    public static String[] split(String str, char ch) {
        if (isEmpty(str)) {
            return EMPTY_STRING_ARRAY;
        }
        return splitToList0(str, ch).toArray(EMPTY_STRING_ARRAY);
    }

    public static String removeEnd(String str, String remove) {
        if (isAnyEmpty(str, remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    public static boolean isAnyEmpty(final String... str) {
        return isNoneEmpty(str);
    }

    public static boolean isNoneEmpty(String... str) {
        if (ArrayUtils.isEmpty(str)) {
            return false;
        }
        for (String ss : str) {
            if (isEmpty(ss)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEqual(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.equals(s2);
    }

    public static boolean isContains(String str, char ch) {
        return isNotEmpty(str) && str.indexOf(ch) >= 0;
    }

    public static boolean isNotContains(String str, char ch) {
        return !isContains(str, ch);
    }

    public static boolean isContains(String[] values, String value) {
        if (ArrayUtils.isNotEmpty(values) && isNotEmpty(value)) {
            for (String v : values) {
                if (value.equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String> splitToList(String str, char ch) {
        if (isEmpty(str)) {
            return Collections.emptyList();
        }
        return splitToList0(str, ch);
    }

    private static List<String> splitToList0(String str, char ch) {
        List<String> result = new ArrayList<>();
        int index = 0, len = str.length();
        for (int i = 0; i < len; i++) {
            if (str.charAt(i) == ch) {
                result.add(str.substring(index, i));
                index = i + 1;
            }
        }
        if (index >= 0) {
            result.add(str.substring(index));
        }
        return result;
    }

    public static String join(String[] array) {
        if (ArrayUtils.isEmpty(array)) {
            return EMPTY_STRING;
        }
        StringBuilder builder = new StringBuilder(array.length);
        for (String s : array) {
            builder.append(s);
        }
        return builder.toString();
    }

    public static String join(String[] array, String split) {
        if (ArrayUtils.isEmpty(array)) {
            return EMPTY_STRING;
        }
        int n = array.length;
        StringBuilder builder = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                builder.append(split);
            }
            builder.append(array[i]);
        }
        return builder.toString();
    }

    public static String join(String[] array, char split) {
        if (ArrayUtils.isEmpty(array)) {
            return EMPTY_STRING;
        }
        int n = array.length;
        StringBuilder builder = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                builder.append(split);
            }
            builder.append(array[i]);
        }
        return builder.toString();
    }

    public static String camelToSplitName(String camelName, String split) {
        if (isEmpty(camelName)) {
            return camelName;
        }
        // for some case with split char
        if (!isWord(camelName)) {
            if (isSplitCase(camelName, split.charAt(0))) {
                return camelName.toLowerCase();
            }
            return camelName;
        }
        StringBuilder builder = null;
        for (int i = 0; i < camelName.length(); i++) {
            char ch = camelName.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                if (builder == null) {
                    builder = new StringBuilder();
                    if (i > 0) {
                        builder.append(camelName, 0, i);
                    }
                }
                if (i > 0) {
                    builder.append(split);
                }
                builder.append(Character.toLowerCase(ch));
            } else if (builder != null) {
                builder.append(ch);
            }
        }
        return builder == null ? camelName.toLowerCase() : builder.toString();
    }

    private static boolean isSplitCase(String str, char separator) {
        if (str == null) {
            return false;
        }
        return str.chars().allMatch(ch -> (ch == separator) || isWord((char) ch));
    }

    private static boolean isWord(String str) {
        if (str == null) {
            return false;
        }
        return str.chars().allMatch(ch -> isWord((char) ch));
    }

    private static boolean isWord(char ch) {
        if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')) {
            return true;
        }
        return false;
    }

    public static String nullSafeToString(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof String) {
            return (String) obj;
        } else {
            String str = obj.toString();
            return str != null ? str : "";
        }
    }

    public static int parseInteger(String str) {
        return isNumber(str) ? Integer.parseInt(str) : 0;
    }

    /**
     * parse str to Long(if str is not number or n < 0, then return 0)
     *
     * @param str a number str
     * @return positive long or zero
     */
    public static long parseLong(String str) {
        return isNumber(str) ? Long.parseLong(str) : 0;

    }

    public static boolean isNumber(String str) {
        return isNotEmpty(str) && NUM_PATTERN.matcher(str).matches();
    }
}