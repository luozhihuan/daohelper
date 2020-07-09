package com.luozhihuan;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by luozhihuan on 16/7/2.
 */
public class CommonUtils {

    public static String makeFirstWordLow(String word){
        if(word == null) {
            return null;
        }
        if(isBlank(word)) {
            return "";
        }
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }


    public static boolean isBlank(String str){
        if(str == null||str.trim().equals("")) {
            return true;
        }
        return false;
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap();
    }


    public static <K, V> Map<K, V> cloneMap(Map<K,V> map) {
        if(map == null) {
            return null;
        }
        Map<K,V> cloneMap = newHashMap();
        Set<K> keySet = map.keySet();
        for (K key : keySet) {
            cloneMap.put(key, map.get(key));
        }
        return cloneMap;
    }

}

