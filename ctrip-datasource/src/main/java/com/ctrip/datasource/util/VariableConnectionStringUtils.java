package com.ctrip.datasource.util;

import com.ctrip.datasource.util.entity.HttpMethod;
import com.ctrip.datasource.util.entity.VariableConnectionStringInfo;
import com.ctrip.datasource.util.entity.VariableConnectionStringInfoResponse;
import com.dianping.cat.Cat;

import java.util.HashMap;
import java.util.Map;

public class VariableConnectionStringUtils {

    private static final String DB_MGR_API_PRO = "http://mysqlapi.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MGR_API_UAT = "http://mysqlapi.db.uat.qa.nt.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MGR_API_FAT = "http://mysqlapi.db.fat.qa.nt.ctripcorp.com:8080/database/getdbconninfo";

    public static VariableConnectionStringInfo getConnectionStringFromDBAPI(String dbName, String env) {
        VariableConnectionStringInfo info = null;
        String url = "FAT".equalsIgnoreCase(env) ? DB_MGR_API_FAT : "UAT".equalsIgnoreCase(env) ?
                DB_MGR_API_UAT : DB_MGR_API_PRO;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("env", env);
        parameters.put("dbname", dbName);
        VariableConnectionStringInfoResponse response = null;
        try {
            response = HttpUtils.getJSONEntity(VariableConnectionStringInfoResponse.class, url, parameters, HttpMethod.HttpPost);
        } catch (Exception e) {
            Cat.logError("get mgr info from db api fail, [dbName:" + dbName + "]", e);
        }
        if (response != null && "ok".equalsIgnoreCase(response.getMessage())) {
            info = response.getData();
        }
        return info;
    }
}
