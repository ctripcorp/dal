package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;

/**
 * @author c7ch23en
 */
public interface DatabasePropertiesParser {

    String getHost();

    int getPort();

    String getDbName();

    String getUid();

    String getPwd();

    DatabaseCategory getDatabaseCategory();

    String getProperty(String key);

}
