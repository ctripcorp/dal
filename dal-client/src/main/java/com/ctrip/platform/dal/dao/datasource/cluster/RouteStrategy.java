package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.exceptions.DalException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface RouteStrategy {

    void initialize(Set<HostSpec> configuredHosts, ConnectionFactory connFactory, Properties strategyOptions);

    Connection pickConnection(RequestContext request) throws SQLException;

    default ConnectionValidator getConnectionValidator(){
        return new NullConnectionValidator();
    }

    void destroy();

}
