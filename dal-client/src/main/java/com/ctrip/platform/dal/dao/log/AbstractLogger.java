package com.ctrip.platform.dal.dao.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLogger implements ILogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLogger.class);

    @Override
    public void logEvent(String type, String name, String message) {
        info(String.format("Type:%s,name:%s,message:%s", type, name, message));
    }

    @Override
    public void logTransaction(String type, String name, String message, Callback callback) {
        if (callback != null) {
            try {
                callback.execute();
            } catch (Throwable e) {
                error(e.getMessage(), e);
            }
        }

        info(String.format("Type:%s,name:%s,message:%s", type, name, message));
    }


    @Override
    public void warn(final String msg) {
        try {
            LOGGER.warn(msg);
        }catch (Throwable e){
            e.printStackTrace();
        }

    }

    @Override
    public void error(final String msg, final Throwable e) {
        try {
            LOGGER.error(e.getMessage(), e);
        }catch (Throwable ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void info(String msg) {
        try{
            LOGGER.info(msg);
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

}