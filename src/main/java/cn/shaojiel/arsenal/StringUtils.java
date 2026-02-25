package cn.shaojiel.arsenal;

public final class StringUtils {

    private StringUtils() {}

    public static  String upperCaseFirst(final String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    public static String upperCaseFirst_Better(final String str) {
        final char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}
