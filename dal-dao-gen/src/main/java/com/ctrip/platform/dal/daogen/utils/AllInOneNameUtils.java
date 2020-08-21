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
}
