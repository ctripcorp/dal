package com.ctrip.datasource.configure;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.util.MysqlApiConnectionStringUtils;
import com.ctrip.datasource.util.entity.ClusterNodeInfo;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfo;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.common.enums.DBModel;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
import com.ctrip.platform.dal.dao.helper.JsonUtils;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.dianping.cat.Cat;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MysqlApiConnectionStringConfigureProvider implements ConnectionStringConfigureProvider, DataSourceConfigureConstants {

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static EnvUtils envUtils = DalElementFactory.DEFAULT.getEnvUtils();
    private static final int THREAD_SIZE = 1;
    private static final String THREAD_NAME = "DAL-MysqlApiConnectionStringChecker";
    private static final int INITIAL_DELAY = 0;
    private static final int FIXED_DELAY = 3 * 1000; //ms
    private static final String SEPARATED = ",";
    private static final String LOAD_BALANCED_JDBC_URL_PARAMETER = "&%s&%s";
    private static final String URL_PARAMETER = "%s=%s";
    private static final String IP_PORT = "%s:%s";
    private static final String SERVER_AFFINITY_ORDER_FORMAT = "address=(type=master)(protocol=tcp)(host=%s)(port=%s):3306";
    private static final String[] IDC_ACCESS_ORDER = new String[] {"shaoy", "sharb", "shafq", "shajq"};
    private static final String CONNECTION_STRING_CHECKER_EVENT_NAME = "connectionStringChecker";
    private static final String CONNECTION_STRING_TYPE = "dal.connectionString";
    private static final String NULL_CONNECTION_STRING = "nullConnectionString";
    private static final String ONLINE = "online";

    private String dbName;
    private DalPropertiesLocator dalPropertiesLocator;
    private DataSourceConfigureLocator dataSourceConfigureLocator;

    private Listener<DalConnectionStringConfigure> listener;

    protected String mysqlApiUrl;
    protected String dbToken;
    protected int callMysqlApiPeriod = FIXED_DELAY;
    protected DBModel dbModel;
    protected boolean localAccess = false;
    protected String[] idcPriority = IDC_ACCESS_ORDER;

    private ScheduledExecutorService executor;

    public MysqlApiConnectionStringConfigureProvider(String dbName) {
        this.dbName = dbName != null ? StringUtils.toTrimmedLowerCase(dbName) : null;
        dalPropertiesLocator = DalPropertiesManager.getInstance().getDalPropertiesLocator();
        dataSourceConfigureLocator = DataSourceConfigureManager.getInstance().getDataSourceConfigureLocator();
    }

    protected void initMysqlApiConfigure() {
        mysqlApiUrl = dalPropertiesLocator.getConnectionStringMysqlApiUrl();

        PropertiesWrapper propertiesWrapper = dataSourceConfigureLocator.getPoolProperties();
        DalPoolPropertiesConfigure mysqlApiConfigureProperties = getMysqlApiConfigureProperties(propertiesWrapper);

        dbToken = mysqlApiConfigureProperties.getDBToken();
        callMysqlApiPeriod = mysqlApiConfigureProperties.getCallMysqlApiPeriod();
        dbModel = mysqlApiConfigureProperties.getDBModel();
        localAccess = Boolean.parseBoolean(mysqlApiConfigureProperties.getLocalAccess());
        String[] idcPriorityConfig = mysqlApiConfigureProperties.getIdcPriority();
        if (idcPriorityConfig != null && idcPriorityConfig.length > 0)
            idcPriority = idcPriorityConfig;
    }

    private DalPoolPropertiesConfigure getMysqlApiConfigureProperties(PropertiesWrapper propertiesWrapper) {
        Properties appProperties = propertiesWrapper.getAppProperties();
        Map<String, Properties> datasourcePropertiesMap = propertiesWrapper.getDatasourceProperties();
        Properties dataSourceProperties = datasourcePropertiesMap.get(dbName);

        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        if (dataSourceProperties == null) {
            if (StringUtils.isEmpty(appProperties.getProperty(DB_TOKEN))) {
                DalRuntimeException e = new DalRuntimeException(String.format("the db token of %s is null", dbName));
                Cat.logError(e);
                throw e;
            }

            dataSourceConfigure.setProperties(appProperties);
            return dataSourceConfigure;
        }
        Properties mysqlApiConfigure = new Properties();

        String dbToken = dataSourceProperties.getProperty(DB_TOKEN) != null ? dataSourceProperties.getProperty(DB_TOKEN) : appProperties.getProperty(DB_TOKEN);
        if (StringUtils.isEmpty(dbToken)) {
            DalRuntimeException e = new DalRuntimeException(String.format("the db token of %s is null", dbName));
            Cat.logError(e);
            throw e;
        }
        mysqlApiConfigure.setProperty(DB_TOKEN, dbToken);

        String callMysqlApiPeriod = dataSourceProperties.getProperty(CALL_MYSQL_API_PERIOD) != null ? dataSourceProperties.getProperty(CALL_MYSQL_API_PERIOD)
                : appProperties.getProperty(CALL_MYSQL_API_PERIOD);
        if (StringUtils.isEmpty(callMysqlApiPeriod)) {
            callMysqlApiPeriod = String.valueOf(FIXED_DELAY);
        }
        mysqlApiConfigure.setProperty(CALL_MYSQL_API_PERIOD, callMysqlApiPeriod);

        String dbModel = dataSourceProperties.getProperty(DB_MODEL) != null ? dataSourceProperties.getProperty(DB_MODEL) : appProperties.getProperty(DB_MODEL);
        if (StringUtils.isEmpty(dbModel)) {
            dbModel = DBModel.STANDALONE.getName();
        }
        mysqlApiConfigure.setProperty(DB_MODEL, dbModel);

        dataSourceConfigure.setProperties(mysqlApiConfigure);
        return dataSourceConfigure;
    }

    @Override
    public DalConnectionStringConfigure getConnectionString() throws Exception {
        DalConnectionStringConfigure dalConnectionStringConfigure = null;

        try {
            dalConnectionStringConfigure = getConnectionStringFromMysqlApi();
            if (dalConnectionStringConfigure == null) {
                dalConnectionStringConfigure = new InvalidVariableConnectionString(dbName, new DalException("result or node info returned by mysql api is empty"));
            }
        } catch (Exception e) {
            dalConnectionStringConfigure = new InvalidVariableConnectionString(dbName, new DalException(e.getMessage(), e));
        }

        initScheduleJob();
        return dalConnectionStringConfigure;
    }

    private void initScheduleJob() {
        if (executor == null) {
            executor = Executors.newScheduledThreadPool(THREAD_SIZE, new CustomThreadFactory(THREAD_NAME));
            executor.scheduleWithFixedDelay(new MysqlApiConnectionStringChecker(), INITIAL_DELAY, callMysqlApiPeriod, TimeUnit.MILLISECONDS);
            LOGGER.logEvent(CONNECTION_STRING_TYPE, CONNECTION_STRING_CHECKER_EVENT_NAME, String.valueOf(callMysqlApiPeriod));
        }
    }

    protected DalConnectionStringConfigure getConnectionStringFromMysqlApi() throws Exception {
        initMysqlApiConfigure();
        String env = envUtils.getEnv();
        MysqlApiConnectionStringInfo info = MysqlApiConnectionStringUtils.getConnectionStringFromMysqlApi(mysqlApiUrl, dbName, env);

        DataSourceConfigure connectionStringConfigure = MysqlApiConnectionStringParser.getInstance().parser(dbName, info, dbToken, dbModel);

        addMGRLocalToLocalParam(connectionStringConfigure, info.getClusternodeinfolist());
        if (connectionStringConfigure == null) {
            LOGGER.logEvent(DalLogTypes.DAL_CONNECTION_STRING, NULL_CONNECTION_STRING, JsonUtils.toJson(info));
        }
        return connectionStringConfigure;
    }

    private void addMGRLocalToLocalParam(DataSourceConfigure connectionStringConfigure, List<ClusterNodeInfo> clusterNodeInfos) {
        if (connectionStringConfigure != null) {
            String connectionUrl = connectionStringConfigure.getConnectionUrl();
            if (connectionUrl.startsWith(DatabaseCategory.REPLICATION_MYSQL_JDBC_URL_PREFIX)) {
                String urlParam1 = String.format(URL_PARAMETER, LOAD_BALANCE_STRATEGY, DEFAULT_LOAD_BALANCE_STRATEGY);
                String urlParam2 = String.format(URL_PARAMETER, SERVER_AFFINITY_ORDER, getServerAffinityOrder(clusterNodeInfos));
                String urlPostfix = String.format(LOAD_BALANCED_JDBC_URL_PARAMETER, urlParam1, urlParam2);

                String loadBalanceUrl = connectionStringConfigure.getConnectionUrl() + urlPostfix;
                connectionStringConfigure.setConnectionUrl(loadBalanceUrl);
            }
        }
    }

    private String getServerAffinityOrder(List<ClusterNodeInfo> clusterNodeInfos) {
        String serverAffinityOrder = "";

        Map<String, String> idcAndIpPort = new HashMap<>();
        for (ClusterNodeInfo clusterNodeInfo : clusterNodeInfos) {
            if (ONLINE.equalsIgnoreCase(clusterNodeInfo.getStatus())) {
                String ipPort = String.format(SERVER_AFFINITY_ORDER_FORMAT, clusterNodeInfo.getIp_business(), clusterNodeInfo.getDns_port());
                idcAndIpPort.put(clusterNodeInfo.getMachine_located_short().toLowerCase(), ipPort);
            }
        }

        if (localAccess) {
            String currentIdc = envUtils.getIdc();
            if (!StringUtils.isEmpty(currentIdc)) {
                currentIdc = currentIdc.toLowerCase();
                String ipPortInCurrentIdc = idcAndIpPort.get(currentIdc);
                if (!StringUtils.isEmpty(ipPortInCurrentIdc)) {
                    serverAffinityOrder += ipPortInCurrentIdc + SEPARATED;
                }
                idcAndIpPort.remove(currentIdc);
            }
        }

        for (String idc : idcPriority) {
            String ipPort = idcAndIpPort.get(idc.toLowerCase());
            if (!StringUtils.isEmpty(ipPort)) {
                serverAffinityOrder += ipPort + SEPARATED;
            }
        }

        return serverAffinityOrder.substring(0, serverAffinityOrder.length() - 1);
    }

    @Override
    public void addListener(Listener<DalConnectionStringConfigure> listener) {
        this.listener = listener;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MysqlApiConnectionStringConfigureProvider that = (MysqlApiConnectionStringConfigureProvider) o;
        return Objects.equals(dbName, that.dbName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbName);
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

            if (listener != null) {
                listener.onChanged(dalConnectionStringConfigure);
            }
        }
    }
}
