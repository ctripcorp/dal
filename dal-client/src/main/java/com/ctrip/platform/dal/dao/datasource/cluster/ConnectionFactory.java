package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface ConnectionFactory {

    Connection getConnectionForHost(HostSpec host) throws SQLException;

}
