package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.datasource.log.KeyedDbSqlContext;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;

/**
 * @author c7ch23en
 */
public class TraceableDataSourceName extends DataSourceName {

    public TraceableDataSourceName(String name) {
        super(name);
    }

    @Override
    public SqlContext createSqlContext() {
        return new KeyedDbSqlContext(getRawName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TraceableDataSourceName)
            return super.equals(obj);
        return false;
    }

}
