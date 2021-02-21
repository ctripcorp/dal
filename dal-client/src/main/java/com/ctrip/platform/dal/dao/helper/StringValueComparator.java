package com.ctrip.platform.dal.dao.helper;

import java.util.Comparator;

/**
 * @author c7ch23en
 */
public class StringValueComparator<T> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        if (o1 == null && o2 == null)
            return 0;
        if (o1 == null)
            return -1;
        if (o2 == null)
            return 1;
        return compareString(o1.toString(), o2.toString());
    }

    protected int compareString(String s1, String s2) {
        if (s1 == null && s2 == null)
            return 0;
        if (s1 == null)
            return -1;
        if (s2 == null)
            return 1;
        return s1.compareTo(s2);
    }

}
