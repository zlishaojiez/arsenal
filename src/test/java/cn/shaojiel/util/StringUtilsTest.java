package cn.shaojiel.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilsTest {
    @Test
    @DisplayName("upperCaseFirst")
    void upperCaseFirst() {
        String s = "abc";
        assertEquals(StringUtils.upperCaseFirst_Better(s), StringUtils.upperCaseFirst(s));
    }
}
