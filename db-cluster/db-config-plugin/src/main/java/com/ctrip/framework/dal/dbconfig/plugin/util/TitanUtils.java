package com.ctrip.framework.dal.dbconfig.plugin.util;

/**
 * @author c7ch23en
 */
public class TitanUtils {

    //format titanKey fileName, lowercase
    public static String formatTitanFileName(String titanKey) {
        String result = titanKey;
        //to lowercase
        if (result != null) {
            result = result.toLowerCase();
        }
        return result;
    }

}
