package com.luozhihuan;

/**
 * Created by luozhihuan on 16/7/2.
 */
public class StatementUtils {


    /**
     * if ({param} == null){
     * return null;
     * }
     * {returnClass} {returnObj} = new {returnClass}();
     */
    public static final String OBJECT_IS_NULL_JUDGE = "if ({param} == null){" + Constants.BLANK + "    return null;" + Constants.BLANK + "}" + Constants.BLANK + "{returnClass} {returnObj} = new {returnClass}();" + Constants.BLANK;

    public static String getObjectIsNullJudge(String param, String returnClass, String returnObj) {
        return OBJECT_IS_NULL_JUDGE.replace("{param}", param).replace("{returnClass}", returnClass).replace("{returnObj}", returnObj);
    }

    public static String convertToTableFieldName(String name) {
        char[] chars = name.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char word : chars) {
            if (word >= 'a' && word <= 'z') {
                sb.append(word);
            } else {
                sb.append("_");
                sb.append((char)(word - ('A' - 'a')));
            }
        }
        return sb.toString();
    }

    public static String makeFirstWordUpper(String str){
        if(StringUtils.isEmpty(str)) {
            return str;
        }
        str = str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
        return str;
    }
    public static String makeFirstWordLow(String word){
        if(word == null) {
            return null;
        }
        if(isBlank(word)) {
            return "";
        }
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }

    public static String tableName(String str){
        String table = str.replace("DO", "");
        return convertToTableFieldName(table);
    }

    public static String deleteDO(String str){
        return str.replace("DO", "");
    }


    public static boolean isBlank(String str){
        if(str == null||str.trim().equals("")) {
            return true;
        }
        return false;
    }

}

