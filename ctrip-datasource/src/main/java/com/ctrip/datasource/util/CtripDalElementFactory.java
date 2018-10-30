package com.ctrip.datasource.util;

import com.ctrip.datasource.configure.CtripDalPropertiesLocator;
import com.ctrip.datasource.configure.qconfig.IDalPropertiesProviderImpl;
import com.ctrip.datasource.helper.CtripDatabaseDomainChecker;
import com.ctrip.datasource.log.CtripLoggerImpl;
import com.ctrip.platform.dal.dao.configure.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.IDalPropertiesProvider;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.DatabaseDomainChecker;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lilj on 2018/7/31.
 */
public class CtripDalElementFactory implements DalElementFactory {
    private static Logger log = LoggerFactory.getLogger(CtripDalElementFactory.class);
    private volatile ILogger iLogger;
    private volatile DalPropertiesLocator dalPropertiesLocator;
    private Lock dalPropertiesLocatorLock = new ReentrantLock();
    private Lock iLoggerLock = new ReentrantLock();

    private volatile DatabaseDomainChecker domainChecker;
    private Lock domainCheckerLock = new ReentrantLock();

    private volatile IDalPropertiesProvider dalPropertiesProvider;
    private Lock dalPropertiesProviderLock = new ReentrantLock();

    public DalPropertiesLocator getDalPropertiesLocator() {
        if (dalPropertiesLocator == null) {
            dalPropertiesLocatorLock.lock();
            try {
                if (dalPropertiesLocator == null)
                    dalPropertiesLocator = new CtripDalPropertiesLocator();
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
                    iLogger = new CtripLoggerImpl();
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
    public DatabaseDomainChecker getDatabaseDomainChecker() {
        if (domainChecker == null) {
            domainCheckerLock.lock();
            try {
                if (domainChecker == null) {
                    domainChecker = new CtripDatabaseDomainChecker();
                }
            } catch (Throwable e) {
                log.error("An error occured while getting CtripDatabaseDomainChecker", e);
            } finally {
                domainCheckerLock.unlock();
            }
        }

        return domainChecker;
    }

    @Override
    public IDalPropertiesProvider getDalPropertiesProvider() {
        if (dalPropertiesProvider == null) {
            dalPropertiesProviderLock.lock();
            try {
                if (dalPropertiesProvider == null) {
                    dalPropertiesProvider = new IDalPropertiesProviderImpl();
                }
            } catch (Throwable e) {
                log.error("An error occured while getting DefaultDatabaseDomainChecker", e);
            } finally {
                dalPropertiesProviderLock.unlock();
            }
        }

        return dalPropertiesProvider;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
