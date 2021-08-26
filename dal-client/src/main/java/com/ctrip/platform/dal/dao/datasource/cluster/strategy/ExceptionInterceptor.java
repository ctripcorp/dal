package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;

import java.sql.SQLException;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface ExceptionInterceptor {

    SQLException interceptException(SQLException sqlEx, HostConnection conn);
}
