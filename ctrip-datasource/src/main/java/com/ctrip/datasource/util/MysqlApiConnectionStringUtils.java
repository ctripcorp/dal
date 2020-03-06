package com.ctrip.datasource.util;

import com.ctrip.datasource.util.entity.HttpMethod;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfo;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfoResponse;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.common.enums.DBModel;
import com.dianping.cat.Cat;

import java.util.HashMap;
import java.util.Map;

public class MysqlApiConnectionStringUtils {

    private static final String DB_MYSQL_API_PRO = "http://mysqlapi.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_PRO_SHAJQ = "http://mysqlapi.jq.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_PRO_SHAOY = "http://mysqlapi.oy.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_PRO_SHARB = "http://mysqlapi.rb.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_PRO_SHAFQ = "http://mysqlapi.fq.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_UAT = "http://mysqlapi.db.uat.qa.nt.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_FAT = "http://mysqlapi.db.fat.qa.nt.ctripcorp.com:8080/database/getdbconninfo";

    public static MysqlApiConnectionStringInfo getConnectionStringFromMysqlApi(String mysqlApiUrl, String dbName, String env) throws Exception {
        MysqlApiConnectionStringInfo info = null;
        String url = !StringUtils.isEmpty(mysqlApiUrl) ? mysqlApiUrl : "FAT".equalsIgnoreCase(env) ? DB_MYSQL_API_FAT : "UAT".equalsIgnoreCase(env) ?
                DB_MYSQL_API_UAT : DB_MYSQL_API_PRO;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("env", env);
        parameters.put("dbname", dbName);
        MysqlApiConnectionStringInfoResponse response = null;
        try {
            response = HttpUtils.getJSONEntity(MysqlApiConnectionStringInfoResponse.class, url, parameters, HttpMethod.HttpPost);
        } catch (Exception e) {
            Cat.logError("get mgr info from db api fail, [dbName:" + dbName + "]", e);
            throw e;
        }
        if (response != null && "ok".equalsIgnoreCase(response.getMessage())) {
            info = response.getData();
        }
        return info;
    }
}
