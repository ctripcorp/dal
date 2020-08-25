package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.enums.DbModeTypeEnum;

import java.sql.SQLException;

public class AllInOneNameUtils {

    public static String getAllInOneName(String dbName, String modeType) throws SQLException {
        if (DbModeTypeEnum.Cluster.getDes().equals(modeType)) {
            return dbName;
        } else {
            DatabaseSetEntry databaseSetEntry =
                    BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(dbName);
            return databaseSetEntry.getConnectionString();
        }
    }

    public static String getAllInOneNameByNameOnly (String dbName) throws SQLException {
        if (dbName != null && dbName.length() > 11 && DbModeTypeEnum.Cluster.getDes().equals(dbName.substring(dbName.length() - 10))) {
            return dbName;
        } else {
            return BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(dbName).getConnectionString();
        }
    }
}
