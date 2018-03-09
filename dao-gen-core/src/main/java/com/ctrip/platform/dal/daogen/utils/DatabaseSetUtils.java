package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseSetUtils {
    private static Map<String, String> databaseSetDBNameCache = null;

    static {
        databaseSetDBNameCache = new ConcurrentHashMap<>();
    }

    public static String getDBName(String databaseSetName) {
        if (databaseSetDBNameCache.containsKey(databaseSetName)) {
            return databaseSetDBNameCache.get(databaseSetName);
        } else {
            try {
                String dbName = BeanGetter.getDaoOfDatabaseSet()
                        .getMasterDatabaseSetEntryByDatabaseSetName(databaseSetName).getConnectionString();
                if (null != dbName)
                    databaseSetDBNameCache.put(databaseSetName, dbName);
                return dbName;
            } catch (Exception e) {
                return databaseSetName;
            }
        }
    }

    public static String getAllInOneName(String db_set_name) throws SQLException {
        DatabaseSetEntry databaseSetEntry =
                BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(db_set_name);
        if (null != databaseSetEntry) {
            return databaseSetEntry.getConnectionString();
        } else {
            return "";
        }
    }
}
