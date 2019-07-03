package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.google.gson.Gson;

/**
 * Created by shenjie on 2019/3/18.
 */
public class Utils {

    public static final Gson gson = new Gson();

    public static void addLocalVmOptions() {
        // local
        System.setProperty("qconfig.admin", "localhost:8082");
        System.setProperty("qserver.http.urls", "localhost:8080");
        System.setProperty("qserver.https.urls", "localhost:8443");
    }

    public static void addQConfig2Fat1VmOptions() {
        // qconfig2: fat1
        System.setProperty("qconfig.admin", "http://qconfig2.fat1.qa.nt.ctripcorp.com");
        System.setProperty("qserver.http.urls", "10.5.61.180:8080");
        System.setProperty("qserver.https.urls", "10.5.61.180:8443");
    }

    public static void addQConfig1Fat16VmOptions() {
        System.setProperty("qconfig.admin", "qconfig.fat16.qa.nt.ctripcorp.com");
        System.setProperty("qserver.http.urls", "10.5.80.175:8080");
        System.setProperty("qserver.https.urls", "10.5.80.175:8443");
    }
}
