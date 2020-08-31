package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.Map;

/**
 * @author c7ch23en
 */
public class LocalDatabaseSetAdapter implements DatabaseSetAdapter {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private final Map<String, DalConnectionString> connectionStrings;

    public LocalDatabaseSetAdapter(Map<String, DalConnectionString> connectionStrings) {
        this.connectionStrings = connectionStrings;
    }

    @Override
    public DatabaseSet adapt(DatabaseSet original) {
        if (connectionStrings == null || connectionStrings.isEmpty())
            return original;
        if (original instanceof DefaultDatabaseSet) {
            try {
                boolean dbShardingDisabled = true;
                boolean tableShardingDisabled = true;
                String masterConnUrl = null;
                String slaveConnUrl = null;
                for (DataBase db : original.getDatabases().values()) {
                    String databaseKey = db.getConnectionString().toLowerCase();
                    DalConnectionString connStr = connectionStrings.get(databaseKey);
                    String connUrl = connStr.getIPConnectionStringConfigure().getConnectionUrl();
                    if (db.isMaster()) {
                        dbShardingDisabled &= !checkMultiHosts(connUrl, masterConnUrl);
                        masterConnUrl = connUrl;
                    } else {
                        dbShardingDisabled &= !checkMultiHosts(connUrl, slaveConnUrl);
                        slaveConnUrl = connUrl;
                    }
                    if (connStr instanceof DalLocalConnectionString)
                        tableShardingDisabled &= ((DalLocalConnectionString) connStr).tableShardingDisabled();
                    else
                        return original;
                }
                String msg = String.format("DatabaseSet '%s' adapted to LocalDatabaseSet", original.getName());
                if (tableShardingDisabled)
                    msg += ", tableSharding is disabled";
                LOGGER.info(msg);
                return new LocalDefaultDatabaseSet((DefaultDatabaseSet) original,
                        dbShardingDisabled, tableShardingDisabled);
            } catch (Throwable t) {
                LOGGER.warn("Adapt DefaultDatabaseSet to LocalDefaultDatabaseSet exception", t);
            }
        }
        return original;
    }

    private boolean checkMultiHosts(String currConnUrl, String prevConnUrl) {
        return currConnUrl != null && prevConnUrl != null && !currConnUrl.equalsIgnoreCase(prevConnUrl);
    }

}
