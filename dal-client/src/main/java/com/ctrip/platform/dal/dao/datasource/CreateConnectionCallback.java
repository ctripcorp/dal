package com.ctrip.platform.dal.dao.datasource;

import java.sql.Connection;

public interface CreateConnectionCallback {
    Connection createConnection() throws Exception;
}
