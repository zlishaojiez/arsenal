package cn.shaojiel.util;

public final class NumberUtils {
    private NumberUtils() {}

    /**
     * 判断数字是否是奇数
     *
     * @param i
     * @return
     */
    public static boolean isOdd(int i) {
        return i % 2 != 0;
    }

    /**
     * 判断数字是否是奇数
     *
     * @param i
     * @return
     */
    public static boolean isOdd_Better(int i) {
        return (i & 1) != 0;
    }
}
