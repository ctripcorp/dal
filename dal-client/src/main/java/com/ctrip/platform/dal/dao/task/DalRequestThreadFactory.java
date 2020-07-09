package com.ctrip.platform.dal.dao.task;

import java.util.concurrent.ThreadFactory;

/**
 * @author c7ch23en
 */
public class DalRequestThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        return null;
    }

}
