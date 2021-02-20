package com.ctrip.platform.dal.dao;

import java.util.concurrent.atomic.AtomicReference;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.Callback;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;


import com.ctrip.platform.dal.dao.client.DalDirectClient;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.dao.configure.DalConfigLoader;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.task.DalRequestExecutor;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class DalClientFactory {
    private static ILogger iLogger = DalElementFactory.DEFAULT.getILogger();
    private static AtomicReference<DalConfigure> configureRef = new AtomicReference<DalConfigure>();
    private static final String INIT_DAL_JAVA_CLIENT = "Initialize";
    private static final String ALREADY_INITIALIZED = "Dal Java Client Factory is already initialized.";
    private static final String THREAD_NAME = "DAL-DalClientFactory-ShutdownHook";
    private static final String CREATE_CUSTOMER_CLIENT_ERROR = "Error while creating customer DalClient";

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setName(THREAD_NAME);
                shutdownFactory();
            }
        }));
    }

    /**
     * Initialize for DB All In One client. Load Dal.config from classpath
     *
     * @throws Exception
     */
    public static void initClientFactory() throws Exception {
        internalInitClientFactory(null);
    }

    /**
     * Initialize for DB All In One client. Load Dal.config from give path
     *
     * @param path Dal.Config file path
     * @throws Exception
     */
    public static void initClientFactory(String path) throws Exception {
        if (path == null)
            throw new NullPointerException("Path is empty");

        internalInitClientFactory(path);
    }

    private static void internalInitClientFactory(final String path) throws Exception {
        if (configureRef.get() != null) {
            iLogger.warn(ALREADY_INITIALIZED);
            iLogger.logEvent(DalLogTypes.DAL, INIT_DAL_JAVA_CLIENT, ALREADY_INITIALIZED);
            return;
        }

        synchronized (DalClientFactory.class) {
            if (configureRef.get() != null) {
                iLogger.warn(ALREADY_INITIALIZED);
                iLogger.logEvent(DalLogTypes.DAL, INIT_DAL_JAVA_CLIENT, ALREADY_INITIALIZED);
                return;
            }
            iLogger.logTransaction(DalLogTypes.DAL, INIT_DAL_JAVA_CLIENT, "Initialize Dal Java Client", new Callback() {
                @Override
                public void execute() throws Exception {
                    DalConfigure config = null;
                    if (path == null) {
                        DalConfigLoader loader = ServiceLoaderHelper.getInstance(DalConfigLoader.class);
                        if (loader == null)
                            config = DalConfigureFactory.load();
                        else
                            config = loader.load();
                        iLogger.info("Successfully initialized Dal Java Client Factory");
                    } else {
                        config = DalConfigureFactory.load(path);
                        iLogger.info("Successfully initialized Dal Java Client Factory with " + path);
                    }
                    config.validate();

                    LogEntry.init();
                    DalRequestExecutor.init(config);

                    DalStatusManager.initialize(config);

                    configureRef.set(config);
                }
            });
        }
    }

    /**
     * Actively initialize connection pools for all the logic db in the Dal.config
     */
    public static void warmUpConnections() {
        getDalConfigure().warmUpConnections();
    }

    /**
     * Prefetch for id generators
     */
    public static void warmUpIdGenerators() {
        getDalConfigure().warmUpIdGenerators();
    }

    public static DalClient getClient(String logicDbName) {
        if (logicDbName == null)
            throw new NullPointerException("Database Set name can not be null");

        DalClient dalClient = null;

        DalConfigure config = getDalConfigure();

        // Verify if it is existed
        config.getDatabaseSet(logicDbName);

        String className = DalPropertiesManager.getInstance().getDalPropertiesLocator().getCustomerClientClassName();
        if (StringUtils.isEmpty(className)) {
            dalClient = new DalDirectClient(config, logicDbName);
        } else {
            try {
                dalClient = (DalClient)Class.forName(className).newInstance();
                ((DalDirectClient)dalClient).init(config, logicDbName);
            } catch (Throwable t) {
                throw new DalRuntimeException(CREATE_CUSTOMER_CLIENT_ERROR, t);
            }
        }

        return dalClient;
    }

    public static DalConfigure getDalConfigure() {
        DalConfigure config = configureRef.get();
        if (config != null)
            return config;

        try {
            initClientFactory();
        } catch (Exception e) {
            throw new IllegalStateException("DalClientFactory initialization fail", e);
        }

        config = configureRef.get();
        if (config != null)
            return config;

        throw new IllegalStateException("DalClientFactory has not been not initialized or initialization fail");
    }

    public static DalLogger getDalLogger() {
        return getDalConfigure().getDalLogger();
    }

    public static DalTaskFactory getTaskFactory() {
        return getDalConfigure().getFactory();
    }

    /**
     * Release All resource the Dal client used.
     */
    public static void shutdownFactory() {
        if (configureRef.get() == null) {
            iLogger.warn("Dal Java Client Factory is already shutdown.");
            return;
        }

        synchronized (DalClientFactory.class) {
            if (configureRef.get() == null) {
                return;
            }

            try {
                iLogger.info("Start shutdown Dal Java Client Factory");
                getDalLogger().shutdown();
                iLogger.info("Dal Logger is shutdown");

                DalRequestExecutor.shutdown();
                iLogger.info("Dal Java Client Factory is shutdown");

                DalStatusManager.shutdown();

                LogEntry.shutdown();
                iLogger.info("DalWatcher has been destroyed");
            } catch (Throwable e) {
                iLogger.error("Error during shutdown", e);
            }

            configureRef.set(null);
        }
    }

}