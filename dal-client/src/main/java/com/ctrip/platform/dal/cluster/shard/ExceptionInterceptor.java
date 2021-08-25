package com.ctrip.platform.dal.cluster.shard;

import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;

import java.sql.SQLException;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface ExceptionInterceptor {

    void interceptException(SQLException sqlEx, HostConnection conn);
}
