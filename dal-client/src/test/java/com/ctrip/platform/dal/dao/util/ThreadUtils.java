package com.ctrip.platform.dal.dao.util;

import java.util.concurrent.TimeUnit;

/**
 * @author c7ch23en
 */
public class ThreadUtils {

    public static void sleep(long time, TimeUnit unit) {
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
