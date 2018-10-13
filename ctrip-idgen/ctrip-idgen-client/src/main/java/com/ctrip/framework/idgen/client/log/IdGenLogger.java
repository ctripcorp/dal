package com.ctrip.framework.idgen.client.log;

import com.ctrip.framework.idgen.client.common.Version;
import com.dianping.cat.Cat;
import com.dianping.cat.status.ProductVersionManager;

public class IdGenLogger implements CatConstants {

    public static void registerVersion() {
        ProductVersionManager.getInstance().register(TYPE_VERSION, Version.getVersion());
    }

    public static void logVersion() {
        logEvent(TYPE_VERSION, Version.getVersion());
    }

    public static void logError(String message, Throwable cause) {
        if (message != null) {
            Cat.logError(message, cause);
        } else {
            Cat.logError(cause);
        }
    }

    public static void logError(String message, String errorName) {
        Cat.logError(message, errorName);
    }

    public static void logEvent(String type, String name) {
        Cat.logEvent(type, name);
    }

    public static void logSizeEvent(String type, long size) {
        Cat.logSizeEvent(type, size);
    }

}
