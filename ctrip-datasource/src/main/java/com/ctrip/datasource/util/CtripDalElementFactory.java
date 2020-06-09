package com.ctrip.datasource.util;

import com.ctrip.datasource.configure.qconfig.DalPropertiesProviderImpl;
import com.ctrip.datasource.datasource.CtripDatasourceBackgroundExecutor;
import com.ctrip.datasource.datasource.CtripLocalizationValidatorFactory;
import com.ctrip.datasource.log.CtripLoggerImpl;
import com.ctrip.framework.dal.cluster.client.util.ObjectHolder;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesProvider;
import com.ctrip.platform.dal.dao.datasource.DatasourceBackgroundExecutor;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidatorFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
import com.ctrip.platform.dal.dao.helper.NullEnvUtils;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lilj on 2018/7/31.
 */
public class CtripDalElementFactory implements DalElementFactory {

    private static Logger log = LoggerFactory.getLogger(CtripDalElementFactory.class);

    private volatile ILogger iLogger;
    private Lock iLoggerLock = new ReentrantLock();

    private volatile DalPropertiesProvider dalPropertiesProvider;
    private Lock dalPropertiesProviderLock = new ReentrantLock();

    private volatile DatasourceBackgroundExecutor datasourceBackgroundExecutor;
    private Lock datasourceBackgroundExecutorLock = new ReentrantLock();

    private final AtomicReference<LocalizationValidatorFactory> localizationValidatorFactoryRef = new AtomicReference<>();

    private final ObjectHolder<EnvUtils> envUtilsHolder = new ObjectHolder<>();

    @Override
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
    public DalPropertiesProvider getDalPropertiesProvider() {
        if (dalPropertiesProvider == null) {
            dalPropertiesProviderLock.lock();
            try {
                if (dalPropertiesProvider == null)
                    dalPropertiesProvider = new DalPropertiesProviderImpl();
            } catch (Exception e) {
                log.error("Get DalPropertiesLocator Error!", e);
            } finally {
                dalPropertiesProviderLock.unlock();
            }
        }
        return dalPropertiesProvider;
    }

    @Override
    public DatasourceBackgroundExecutor getDatasourceBackgroundExecutor() {
        if (datasourceBackgroundExecutor == null) {
            datasourceBackgroundExecutorLock.lock();
            try {
                if (datasourceBackgroundExecutor == null)
                    datasourceBackgroundExecutor = new CtripDatasourceBackgroundExecutor();
            } catch (Throwable e) {
                log.error("An error occurred while getting CtripDalBackgroundExecutor.", e);
            } finally {
                datasourceBackgroundExecutorLock.unlock();
            }
        }

        return datasourceBackgroundExecutor;
    }

    @Override
    public LocalizationValidatorFactory getLocalizationValidatorFactory() {
        LocalizationValidatorFactory factory = localizationValidatorFactoryRef.get();
        if (factory == null) {
            synchronized (localizationValidatorFactoryRef) {
                factory = localizationValidatorFactoryRef.get();
                if (factory == null) {
                    factory = new CtripLocalizationValidatorFactory(DalPropertiesManager.getInstance().getDalPropertiesLocator());
                    localizationValidatorFactoryRef.set(factory);
                }
            }
        }
        return factory;
    }

    @Override
    public EnvUtils getEnvUtils() {
        return envUtilsHolder.getOrCreate(CtripEnvUtils::new);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
