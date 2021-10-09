package com.ctrip.framework.dal.cluster.client.extended;

/**
 * @Author limingdong
 * @create 2021/10/8
 */
public interface JdbcDriver {

    String KEY_DRIVER = "driverClass";

    String driverClassName();
}
