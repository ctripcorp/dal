package com.ctrip.framework.db.cluster.util;

import com.google.gson.Gson;

/**
 * Created by shenjie on 2019/3/18.
 */
public class Utils {

    public static final Gson gson = new Gson();

    public static String format(String content) {
        return content.trim().toLowerCase();
    }
}
