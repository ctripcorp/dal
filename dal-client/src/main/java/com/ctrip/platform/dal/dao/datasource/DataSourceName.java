package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.datasource.log.NullSqlContext;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;

/**
 * @author c7ch23en
 */
public class DataSourceName implements DataSourceIdentity {

    private String name;

    public DataSourceName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return name != null ? StringUtils.toTrimmedLowerCase(name) : null;
    }

    @Override
    public SqlContext createSqlContext() {
        return new NullSqlContext();
    }

    public String getRawName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataSourceName) {
            String id = getId();
            String objId = ((DataSourceName) obj).getId();
            return (id == null && objId == null) || (id != null && id.equals(objId));
        }
        return false;
    }

    @Override
    public int hashCode() {
        String id = getId();
        return id != null ? id.hashCode() : 0;
    }

}
