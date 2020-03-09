package com.ctrip.datasource.configure;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.util.EnvUtil;
import com.ctrip.datasource.util.MysqlApiConnectionStringUtils;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfo;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.platform.dal.common.enums.DBModel;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.DalPoolPropertiesConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.InvalidVariableConnectionString;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MysqlApiConnectionStringConfigureProvider implements ConnectionStringConfigureProvider {

    private static final int THREAD_SIZE = 1;
    private static final String THREAD_NAME = "DAL-MysqlApiConnectionStringChecker";
    private static final int INITIAL_DELAY = 0;
    private static final int FIXED_DELAY = 3 * 1000; //ms

    private String dbName;
    private DalPropertiesLocator dalPropertiesLocator;
    private DataSourceConfigureLocator dataSourceConfigureLocator;

    private Listener<DalConnectionStringConfigure> listener;

    private String mysqlApiUrl;
    private String dbToken;
    private int callMysqlApiPeriod = FIXED_DELAY;
    private DBModel dbModel;

    private ScheduledExecutorService executor;

    public MysqlApiConnectionStringConfigureProvider(String dbName) {
        this.dbName = dbName;
        dalPropertiesLocator = DalPropertiesManager.getInstance().getDalPropertiesLocator();
        dataSourceConfigureLocator = DataSourceConfigureManager.getInstance().getDataSourceConfigureLocator();

        executor = Executors.newScheduledThreadPool(THREAD_SIZE, new CustomThreadFactory(THREAD_NAME));
        executor.scheduleWithFixedDelay(new MysqlApiConnectionStringChecker(), INITIAL_DELAY, callMysqlApiPeriod, TimeUnit.MILLISECONDS);
    }

    private void initMysqlApiConfigure() {
        mysqlApiUrl = dalPropertiesLocator.getConnectionStringMysqlApiUrl();

        DalPoolPropertiesConfigure poolProperties = dataSourceConfigureLocator.getDataSourceConfigure(new ApiDataSourceIdentity(this));
        dbToken = poolProperties.getDBToken();
        callMysqlApiPeriod = poolProperties.getCallMysqlApiPeriod();
        dbModel = poolProperties.getDBModel();
    }

    @Override
    public DalConnectionStringConfigure getConnectionString() throws Exception {
        initMysqlApiConfigure();

        DalConnectionStringConfigure dalConnectionStringConfigure = null;

        try {
            dalConnectionStringConfigure = getConnectionStringFromMysqlApi();
        } catch (Exception e) {
            dalConnectionStringConfigure = new InvalidVariableConnectionString(dbName, new DalException(e.getMessage(), e));
        }

        return dalConnectionStringConfigure;
    }

    private DalConnectionStringConfigure getConnectionStringFromMysqlApi() throws Exception {
        String env = EnvUtil.getEnv();
        MysqlApiConnectionStringInfo info = MysqlApiConnectionStringUtils.getConnectionStringFromMysqlApi(mysqlApiUrl, dbName, env);

        return MysqlApiConnectionStringParser.getInstance().parser(dbName, info, dbToken, dbModel);
    }

    @Override
    public void addListener(Listener<DalConnectionStringConfigure> listener) {
        this.listener = listener;
    }

    private class MysqlApiConnectionStringChecker implements Runnable {

        @Override
        public void run() {
            DalConnectionStringConfigure dalConnectionStringConfigure = null;

            try {
                dalConnectionStringConfigure = getConnectionStringFromMysqlApi();
            } catch (Exception e) {
                Cat.logError("timed get connection string from mysql api failed!", e);
            }
            listener.onChanged(dalConnectionStringConfigure);
        }
    }
}
