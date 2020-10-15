package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.exceptions.InvalidConnectionException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface ConnectionFactory {

    Connection getPooledConnectionForHost(HostSpec host) throws SQLException, InvalidConnectionException;

    Connection createConnectionForHost(HostSpec host) throws SQLException, InvalidConnectionException;

}
