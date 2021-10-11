package com.ctrip.framework.dal.cluster.client.util;

import java.util.ArrayList;
import java.util.Collections;

public class DalCollections {

    public static <T> ArrayList<T> arrayList(T... t) {
        if (t == null || t.length == 0)
            return new ArrayList<>();

        ArrayList<T> list = new ArrayList<>(t.length);
        Collections.addAll(list, t);
        return list;
    }
}
