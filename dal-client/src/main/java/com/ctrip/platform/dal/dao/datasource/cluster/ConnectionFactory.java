package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.Connection;

/**
 * @author c7ch23en
 */
public interface ConnectionFactory {

    Connection getConnectionForHost(Host host);

}
