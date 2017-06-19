package com.ctrip.platform.dal.dao.configure;

public interface DatabasePoolConfigConstants {
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
    // This is for typo error
    String INIT_SQL2 = "initSQL";
    // This is for backward compatible, option serves the same purpose as connectionProperties
    // If both option and connectionProperties present, the correspond connectionProperties value is used
    // And if only option is set, it will be set into connectionProperties
    String OPTION = "option";
    String VALIDATORCLASSNAME = "validatorClassName";
}
