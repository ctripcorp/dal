package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DatabaseSets;

/**
 * @author c7ch23en
 */
public interface CtripLocalContext {

    String getUserDefinedDatabaseConfigPath();

    String getUserDefinedDatabaseConfigFile();

    boolean isFxLocal();

    DatabaseSets getDatabaseSets();

}
