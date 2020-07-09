package com.luozhihuan;

/**
 * Created by luozhihuan on 16/6/20.
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
         return false;
    }

    public static boolean isBlank(String str) {
        return str == null ? true : isEmpty(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isNumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        } else {
            int sz = cs.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
