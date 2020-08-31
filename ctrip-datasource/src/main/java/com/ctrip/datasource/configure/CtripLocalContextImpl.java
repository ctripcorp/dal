package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DatabaseSets;

/**
 * @author c7ch23en
 */
public class CtripLocalContextImpl implements CtripLocalContext {

    private final String userDefinedDatabaseConfigPath;
    private final String userDefinedDatabaseConfigFile;
    private final boolean isFxLocal;
    private final DatabaseSets databaseSets;

    public CtripLocalContextImpl() {
        this(null);
    }

    public CtripLocalContextImpl(String userDefinedDatabaseConfigPath) {
        this(userDefinedDatabaseConfigPath, true);
    }

    public CtripLocalContextImpl(String userDefinedDatabaseConfigPath, boolean isFxLocal) {
        this(userDefinedDatabaseConfigPath, null, isFxLocal, null);
    }

    public CtripLocalContextImpl(String userDefinedDatabaseConfigPath, String userDefinedDatabaseConfigFile,
                                 boolean isFxLocal, DatabaseSets databaseSets) {
        this.userDefinedDatabaseConfigPath = userDefinedDatabaseConfigPath;
        this.userDefinedDatabaseConfigFile = userDefinedDatabaseConfigFile;
        this.isFxLocal = isFxLocal;
        this.databaseSets = databaseSets;
    }

    @Override
    public String getUserDefinedDatabaseConfigPath() {
        return userDefinedDatabaseConfigPath;
    }

    @Override
    public String getUserDefinedDatabaseConfigFile() {
        return userDefinedDatabaseConfigFile;
    }

    @Override
    public boolean isFxLocal() {
        return isFxLocal;
    }

    @Override
    public DatabaseSets getDatabaseSets() {
        return databaseSets;
    }

}
