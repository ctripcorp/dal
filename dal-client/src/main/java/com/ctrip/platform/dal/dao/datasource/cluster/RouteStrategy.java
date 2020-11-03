package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface RouteStrategy {

    void initialize(ShardMeta shardMeta, ConnectionFactory connFactory, CaseInsensitiveProperties strategyProperties);

    HostConnection pickConnection(RequestContext request) throws SQLException;

    default ConnectionValidator getConnectionValidator(){
        return new NullConnectionValidator();
    }

    void destroy();

}
