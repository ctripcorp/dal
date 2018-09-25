package com.ctrip.framework.idgen.server.util;

import java.util.Iterator;
import java.util.Set;

public class StringUtils {

    public static String setToString(Set<String> set, String separator) {
        if (null == set || set.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static boolean isEmpty(String string) {
        return null == string || string.trim().isEmpty();
    }

}
