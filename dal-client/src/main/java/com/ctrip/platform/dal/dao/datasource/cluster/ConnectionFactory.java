package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.exceptions.InvalidConnectionException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface ConnectionFactory {

    Connection getPooledConnectionForHost(HostSpec host) throws SQLException, InvalidConnectionException;

    Connection getPooledConnectionForValidate(HostSpec host) throws SQLException;
}
