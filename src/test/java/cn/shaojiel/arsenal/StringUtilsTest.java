package cn.shaojiel.arsenal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilsTest {
    @Test
    @DisplayName("upperCaseFirst")
    void upperCaseFirst() {
        final String s = "abc";
        assertEquals(StringUtils.upperCaseFirst_Better(s), StringUtils.upperCaseFirst(s));
    }
}
