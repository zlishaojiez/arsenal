package cn.shaojiel.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumberUtilsTest {

    @Test
    void isOdd() {
        assertFalse(NumberUtils.isOdd(0));
        assertTrue(NumberUtils.isOdd(1));
        assertTrue(NumberUtils.isOdd(-1));
        assertFalse(NumberUtils.isOdd(2));
        assertFalse(NumberUtils.isOdd(-2));
    }

    @Test
    void isOdd_Better() {
        assertFalse(NumberUtils.isOdd_Better(0));
        assertTrue(NumberUtils.isOdd_Better(1));
        assertTrue(NumberUtils.isOdd_Better(-1));
        assertFalse(NumberUtils.isOdd_Better(2));
        assertFalse(NumberUtils.isOdd_Better(-2));
    }
}