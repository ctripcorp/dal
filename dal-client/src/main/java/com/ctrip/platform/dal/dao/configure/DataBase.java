package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;

/**
 * @author c7ch23en
 */
public interface DataBase {

    String getName();

    boolean isMaster();

    String getSharding();

    String getConnectionString();

}
