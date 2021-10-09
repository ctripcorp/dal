package com.ctrip.framework.dal.cluster.client.config;


import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.dal.cluster.client.extended.CustomDataSourceFactory;
import com.ctrip.framework.dal.cluster.client.extended.JdbcDriver;

public interface DalConfigCustomizedOption {

    /**
     * return string of the drc-consistency-customized class
     * @return
     */
    String getConsistencyTypeCustomizedClass();

    /**
     * true : customized-sharding-strategy class can be not exit
     * false: if customized-sharding-strategy class not exit, throw exception
     * @return
     */
    boolean isIgnoreShardingResourceNotFound();

    /**
     * isForceInitialize
     * @return
     */
    boolean isForceInitialize();

    /**
     * return the shard index of a sharding db which shard to create a datasource
     * @return
     */
    Integer getShardIndex();

    /**
     * master or slave
     * @return
     */
    DatabaseRole getDatabaseRole();

    DalConfigCustomizedOption clone();

    String getRouteStrategy();

    String getTag();

    CustomDataSourceFactory dataSourceFactory();

    JdbcDriver jdbcDriver();
}
