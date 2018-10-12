package com.ctrip.framework.idgen.server.util;

import com.ctrip.framework.foundation.Foundation;

public class TestUtils {

    public static final String WORKER_ID_PROPERTY_KEY_FORMAT = "workerId_%s";

    public static String getLocalWorkerIdKey() {
        return String.format(WORKER_ID_PROPERTY_KEY_FORMAT, getLocalIP());
    }

    public static String getLocalIP() {
        return Foundation.net().getHostAddress();
    }

}
