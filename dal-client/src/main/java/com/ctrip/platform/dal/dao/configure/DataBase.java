package com.ctrip.platform.dal.dao.configure;

/**
 * @author c7ch23en
 */
public interface DataBase {

    String getName();

    boolean isMaster();

    String getSharding();

    String getConnectionString();

}
