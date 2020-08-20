package com.ctrip.platform.dal.dao.configure;

/**
 * @author c7ch23en
 */
public interface DatabasePropertiesParser {

    String getHost();

    int getPort();

    String getDbName();

    String getUid();

    String getPwd();

    String getProperty(String key);

}
