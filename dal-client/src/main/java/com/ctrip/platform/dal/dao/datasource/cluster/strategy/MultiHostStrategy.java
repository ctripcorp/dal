package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.*;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.HostConnectionValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.NullConnectionValidator;

import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public interface MultiHostStrategy {

    void initialize(ShardMeta shardMeta, ConnectionFactory connFactory, CaseInsensitiveProperties strategyProperties);

    HostConnection pickConnection(RequestContext request) throws SQLException;

    default HostConnectionValidator getConnectionValidator(){
        return new NullConnectionValidator();
    }

    void destroy();

}
