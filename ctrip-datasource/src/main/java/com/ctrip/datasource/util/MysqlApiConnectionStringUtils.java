package com.ctrip.datasource.util;

import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfo;
import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfoResponse;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.helper.JsonUtils;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.gson.JsonObject;

public class MysqlApiConnectionStringUtils {

    private static final int DEFAULT_HTTP_TIMEOUT_MS = 1800;

    private static final String DB_MYSQL_API_PRO = "http://mysqlapi.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_PRO_SHAJQ = "http://mysqlapi.jq.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_PRO_SHAOY = "http://mysqlapi.oy.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_PRO_SHARB = "http://mysqlapi.rb.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_PRO_SHAFQ = "http://mysqlapi.fq.db.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_UAT = "http://mysqlapi.db.uat.qa.nt.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_FAT = "http://mysqlapi.db.fat.qa.nt.ctripcorp.com:8080/database/getdbconninfo";

    private static final String DB_MYSQL_API_LPT = "http://mysqlapi.db.lpt.qa.nt.ctripcorp.com:8080/database/getdbconninfo";

    private static final String MYSQL_API = "mysqlApi";

    public static MysqlApiConnectionStringInfo getConnectionStringFromMysqlApi(String mysqlApiUrl, String dbName, String env) throws Exception {
        Transaction t = Cat.newTransaction(DalLogTypes.DAL_CONNECTION_STRING, MYSQL_API);

        MysqlApiConnectionStringInfo info = null;
        String url = !StringUtils.isEmpty(mysqlApiUrl) ? mysqlApiUrl : "FAT".equalsIgnoreCase(env) || "FWS".equalsIgnoreCase(env) ? DB_MYSQL_API_FAT :
                "LPT".equalsIgnoreCase(env) ? DB_MYSQL_API_LPT : "UAT".equalsIgnoreCase(env) ? DB_MYSQL_API_UAT : DB_MYSQL_API_PRO;

        JsonObject json = new JsonObject();
        json.addProperty("env", env);
        json.addProperty("dbname", dbName);

        MysqlApiConnectionStringInfoResponse response = null;
        HttpExecutor executor = HttpExecutor.getInstance();
        try {
            String responseStr = executor.executePost(url, null, json.toString(), DEFAULT_HTTP_TIMEOUT_MS);
            response = JsonUtils.fromJson(responseStr, MysqlApiConnectionStringInfoResponse.class);
            t.addData(responseStr);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            Cat.logError("get mgr info from db api fail, [dbName:" + dbName + "]", e);
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();
        }
        if (response != null && "ok".equalsIgnoreCase(response.getMessage())) {
            info = response.getData();
        }
        return info;
    }
}
