package com.ctrip.platform.dal.dao.configure;

public interface DataSourceConfigureConstants {

    // **********Pool properties key**********
    String USER_NAME = "userName";
    String PASSWORD = "password";
    String CONNECTION_URL = "connectionUrl";
    String DRIVER_CLASS_NAME = "driverClassName";
    String TESTWHILEIDLE = "testWhileIdle";
    String TESTONBORROW = "testOnBorrow";
    String TESTONRETURN = "testOnReturn";
    String VALIDATIONQUERY = "validationQuery";
    String VALIDATIONQUERYTIMEOUT = "validationQueryTimeout";
    String VALIDATIONINTERVAL = "validationInterval";
    String TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";
    String MAX_AGE = "maxAge";
    String MAXACTIVE = "maxActive";
    String MINIDLE = "minIdle";
    String MAXWAIT = "maxWait";
    String INITIALSIZE = "initialSize";
    String REMOVEABANDONEDTIMEOUT = "removeAbandonedTimeout";
    String REMOVEABANDONED = "removeAbandoned";
    String LOGABANDONED = "logAbandoned";
    String MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";
    String CONNECTIONPROPERTIES = "connectionProperties";
    String INIT_SQL = "initSql";

    //used by mgr datasource
    String DB_TOKEN = "dbToken";
    String CALL_MYSQL_API_PERIOD = "callMysqlApiPeriod";
    String DB_MODEL = "dbModel";
    String LOAD_BALANCE_STRATEGY = "loadBalanceStrategy";
    String SERVER_AFFINITY_ORDER = "serverAffinityOrder";

    // This is for typo error
    String INIT_SQL2 = "initSQL";

    // This is for backward compatible, option serves the same purpose as connectionProperties
    // If both option and connectionProperties present, the correspond connectionProperties value is used
    // And if only option is set, it will be set into connectionProperties
    String OPTION = "option";
    String VALIDATORCLASSNAME = "validatorClassName";

    // JDBC interceptors
    String JDBC_INTERCEPTORS = "jdbcInterceptors";

    // Host name
    String HOST_NAME = "hostName";

    // **********Pool properties default value**********
    boolean DEFAULT_TESTWHILEIDLE = false;
    boolean DEFAULT_TESTONBORROW = true;
    boolean DEFAULT_TESTONRETURN = false;
    String DEFAULT_VALIDATIONQUERY = "SELECT 1";
    int DEFAULT_VALIDATIONQUERYTIMEOUT = 5;
    long DEFAULT_VALIDATIONINTERVAL = 30000L;
    String DEFAULT_VALIDATORCLASSNAME = "com.ctrip.platform.dal.dao.datasource.DataSourceValidator";
    int DEFAULT_TIMEBETWEENEVICTIONRUNSMILLIS = 5000;
    int DEFAULT_MAXAGE = 28000000;
    int DEFAULT_MAXACTIVE = 100;
    int DEFAULT_MINIDLE = 1;
    int DEFAULT_MAXWAIT = 10000;
    int DEFAULT_INITIALSIZE = 1;
    int DEFAULT_REMOVEABANDONEDTIMEOUT = 65;
    boolean DEFAULT_REMOVEABANDONED = true;
    boolean DEFAULT_LOGABANDONED = false;
    int DEFAULT_MINEVICTABLEIDLETIMEMILLIS = 30000;
    String DEFAULT_CONNECTIONPROPERTIES =
            "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false;socketTimeout=100000;connectTimeout=15000";
    boolean DEFAULT_JMXENABLED = true;
    String DEFAULT_JDBCINTERCEPTORS = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
            + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;";
    // com.ctrip.datasource.interceptor.CtripConnectionState
    // com.ctrip.platform.dal.dao.interceptor.DefaultConnectionState

    //used by mgr datasource
    int DEFAULT_CALL_MYSQL_API_PERIOD = 3 * 1000; //ms
    String DEFAULT_DB_MODEL = "standalone";
    String DEFAULT_LOAD_BALANCE_STRATEGY = "serverAffinity";

    // **********Constants**********
    String USE_LOCAL_CONFIG = "useLocalConfig";
    String IGNORE_EXTERNAL_EXCEPTION = "ignoreExternalException";
    String DATABASE_CONFIG_LOCATION = "databaseConfigLocation";
    String SERVICE_ADDRESS = "serviceAddress";
    String TIMEOUT = "timeout";
    String APPID = "appid";
    String IS_DEBUG = "isDebug";

    String TITAN_KEY_NORMAL = "normal";
    String TITAN_KEY_FAILOVER = "failover";

    String ENABLE_DYNAMIC_POOL_PROPERTIES = "enableDynamicPoolProperties";

}
