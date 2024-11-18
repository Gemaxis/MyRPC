package com.custom.common.utils;

import com.custom.common.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.custom.common.utils.StringUtils.splitToList;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Gemaxis
 * @date 2024/10/27 14:18
 **/
class StringUtilsTest {

    @Test
    void testSplitToList() {
        String str = "d,1,2,4";

        assertEquals(asList("d", "1", "2", "4"), splitToList(str, ','));
        assertEquals(4, splitToList(str, ',').size());
        assertEquals(asList(str.split(",")), splitToList(str, ','));

        assertEquals(1, splitToList(str, 'a').size());
        assertEquals(asList(str.split("a")), splitToList(str, 'a'));

        assertEquals(0, splitToList("", 'a').size());
        assertEquals(0, splitToList(null, 'a').size());

    }

    @Test
    void tsetJoin() {
        String[] s = {"1", "2", "3"};
        assertEquals(StringUtils.join(s), "123");
        assertEquals(StringUtils.join(s, ","), "1,2,3");
        assertEquals(StringUtils.join(s, ','), "1,2,3");

    }

    @Test
    void testCamelToSplitName(){
        assertEquals("ab-cd-ef", StringUtils.camelToSplitName("abCdEf", "-"));
        assertEquals("ab-cd-ef", StringUtils.camelToSplitName("AbCdEf", "-"));
        assertEquals("abcdef", StringUtils.camelToSplitName("abcdef", "-"));

        assertEquals("ab-cd-ef", StringUtils.camelToSplitName("ab-cd-ef", "-"));
        assertEquals("ab-cd-ef", StringUtils.camelToSplitName("Ab-Cd-Ef", "-"));

        assertEquals("Ab_Cd_Ef", StringUtils.camelToSplitName("Ab_Cd_Ef", "-"));
        assertEquals("AB_CD_EF", StringUtils.camelToSplitName("AB_CD_EF", "-"));

        assertEquals("ab.cd.ef", StringUtils.camelToSplitName("AbCdEf", "."));

    }


}
