package com.ctrip.datasource.dalproperties;

import com.ctrip.platform.dal.dao.configure.ErrorCodeInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.AbstractDalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import org.junit.Test;

import java.util.Map;

public class DalPropertiesTest {

    @Test
    public void testTableParseSwitch() {
        /*
        DalPropertiesManager manager = DalPropertiesManager.getInstance();
        manager.setup();

        displayParameters(manager);

        try {
            Thread.sleep(60 * 1000);
        } catch (Throwable e) {
        }

        displayParameters(manager);
        */
    }

    private void displayParameters(DalPropertiesManager manager) {
        DalPropertiesLocator tableParseLocator = manager.getDalPropertiesLocator();
        System.out.println("Table parse switch:" + tableParseLocator.getTableParseSwitch().toString());

        AbstractDalPropertiesLocator sqlServerLocator = manager.getSqlServerDalPropertiesLocator();
        Map<String, ErrorCodeInfo> sqlServerMap = sqlServerLocator.getErrorCodes();
        String sqlServerErrorCodes = mapToString(sqlServerMap);
        System.out.println("Sql Server Error Codes:" + sqlServerErrorCodes);

        AbstractDalPropertiesLocator mySqlLocator = manager.getMySqlDalPropertiesLocator();
        Map<String, ErrorCodeInfo> mySqlMap = mySqlLocator.getErrorCodes();
        String mySqlErrorCodes = mapToString(mySqlMap);
        System.out.println("My Sql Error Codes:" + mySqlErrorCodes);
    }

    private String mapToString(Map<String, ErrorCodeInfo> map) {
        StringBuilder sb = new StringBuilder();
        if (map == null || map.isEmpty())
            return sb.toString();

        for (Map.Entry<String, ErrorCodeInfo> entry : map.entrySet()) {
            sb.append(entry.getKey());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

}
