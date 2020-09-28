package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.datasource.log.SqlContext;

/**
 * @author c7ch23en
 */
public interface DataSourceIdentity {

    String getId();

    SqlContext createSqlContext();

}
