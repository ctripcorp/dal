package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.configure.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.DefaultDalPropertiesLocator;
import com.ctrip.platform.dal.dao.log.DefaultLoggerImpl;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by lilj on 2018/7/29.
 */
public class DefaultDalElementFactory implements DalElementFactory {
    private static Logger log = LoggerFactory.getLogger(DefaultDalElementFactory.class);
    private volatile ILogger iLogger;
    private volatile DalPropertiesLocator dalPropertiesLocator;
    private Lock dalPropertiesLocatorLock = new ReentrantLock();
    private Lock iLoggerLock = new ReentrantLock();

    public DalPropertiesLocator getDalPropertiesLocator() {
        if (dalPropertiesLocator == null) {
            dalPropertiesLocatorLock.lock();
            try {
                if (dalPropertiesLocator == null)
                    dalPropertiesLocator = new DefaultDalPropertiesLocator();
            } catch (Exception e) {
                log.error("Get DalPropertiesLocator Error!", e);
            } finally {
                dalPropertiesLocatorLock.unlock();
            }
        }
        return dalPropertiesLocator;
    }

    public ILogger getILogger() {
        if (iLogger == null) {
            iLoggerLock.lock();
            try {
                if (iLogger == null) {
                    iLogger = new DefaultLoggerImpl();
                }
            } catch (Exception e) {
                log.error("Get ILogger Error", e);
            } finally {
                iLoggerLock.unlock();
            }
        }
        return iLogger;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
