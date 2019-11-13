package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.ConnectionCheckInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.ConnectionCheckOutputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.RequestExecutor;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class DbAccessManager implements TitanConstants {
    //field
    private PluginConfig config = null;
//    private static final String testCheckUrl = "http://mysqlapi.db.uat.qa.nt.ctripcorp.com:8080/database/checktitanconnect";
//    private static final String proCheckUrl = "http://mysqlapi.db.ctripcorp.com:8080/database/checktitanconnect";



    //constructor
    public DbAccessManager(PluginConfig config){
        this.config = config;
    }


    //switch for checking
    public boolean needCheckDbConnection(boolean forMHA) throws Exception {
        boolean needCheck = false;
        String param = NEED_CHECK_DB_CONNECTION;
        if(forMHA){
            param = NEED_CHECK_DB_CONNECTION_FOR_MHA;
        }
        String needCheckDbConnection = config.getParamValue(param);
        if(!Strings.isNullOrEmpty(needCheckDbConnection)){
            needCheck = Boolean.parseBoolean(needCheckDbConnection);
        }
        return needCheck;
    }


    //check connection via DBA interface
    public boolean validConnection(Properties properties, String env, boolean forMHA) throws Exception {
        boolean validResult = true;
        if(needCheckDbConnection(forMHA)){
            env = env.toLowerCase();
            String checkUrl = config.getParamValue(DBA_CONNECTION_CHECK_URL);
            if(Strings.isNullOrEmpty(checkUrl)){
                throw new IllegalArgumentException("checkUrl is null or empty, can't validate connection! Please check config item 'dba.connection.check.url' is configured!");
            }


            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");

            //validate both serverName and serverIp when they are not empty
            String serverName = properties.getProperty(CONNECTIONSTRING_SERVER_NAME);
            String serverIp = properties.getProperty(CONNECTIONSTRING_SERVER_IP);
            if(Strings.isNullOrEmpty(serverName) && Strings.isNullOrEmpty(serverIp)){
                throw new IllegalArgumentException("[serverName, serverIp] are all null or empty!");
            }
            boolean validResult_serverName = true;
            if(!Strings.isNullOrEmpty(serverName)){
                ConnectionCheckInputEntity connectionCheckInputEntity = buildConnectionCheckInputEntity(properties, env, serverName);
                validResult_serverName = validConnectionByHost(checkUrl, headers, connectionCheckInputEntity);
            }

            boolean validResult_serverIp = true;
            if(!Strings.isNullOrEmpty(serverIp)){
                ConnectionCheckInputEntity connectionCheckInputEntity = buildConnectionCheckInputEntity(properties, env, serverIp);
                validResult_serverIp = validConnectionByHost(checkUrl, headers, connectionCheckInputEntity);
            }
            validResult = validResult_serverName && validResult_serverIp;
        }
        return validResult;
    }


    //build
    private ConnectionCheckInputEntity buildConnectionCheckInputEntity(Properties properties, String env, String host){
        //get dbType
        String providerName = properties.getProperty(CONNECTIONSTRING_PROVIDER_NAME);
        String dbType = "";
        switch (providerName) {
            case NAME_MYSQL_PROVIDER:
                dbType = NAME_MYSQL;
                break;
            case NAME_SQLSERVER_PROVIDER:
                dbType = NAME_SQLSERVER;
                break;
            default:
                throw new IllegalArgumentException("providerName is invalid. Valid value are [System.Data.SqlClient, MySql.Data.MySqlClient].");
        }

        //get port  Notice: here port maybe integer type
        int port = 0;
        Object portObj = properties.get(CONNECTIONSTRING_PORT);
        if(portObj != null){
            port = Integer.parseInt(portObj.toString());
        }

        //build ConnectionCheckInputEntity
        ConnectionCheckInputEntity connectionCheckInputEntity = new ConnectionCheckInputEntity();
        connectionCheckInputEntity.setDbType(dbType);
        connectionCheckInputEntity.setEnv(env);
        connectionCheckInputEntity.setHost(host);
        connectionCheckInputEntity.setPort(port);
        connectionCheckInputEntity.setUser(properties.getProperty(CONNECTIONSTRING_UID));
        connectionCheckInputEntity.setPassword(properties.getProperty(CONNECTIONSTRING_PASSWORD));
        connectionCheckInputEntity.setDbName(properties.getProperty(CONNECTIONSTRING_DB_NAME));
        return connectionCheckInputEntity;
    }

    //valid connection
    private boolean validConnectionByHost(String checkUrl, Map<String, String> headers, ConnectionCheckInputEntity connectionCheckInputEntity) throws Exception {
        boolean validResult = false;
        String request = GsonUtils.t2Json(connectionCheckInputEntity);

        //log request info
        connectionCheckInputEntity.setPassword("*****");    //temporary confuse password for logging
        String requestEnc = GsonUtils.t2Json(connectionCheckInputEntity);
        Cat.logEvent("DbAccessManager", "connection.check.request", Event.SUCCESS, "requestEnc= " + requestEnc);

        // get timeoutMs
        int timeoutMs = RequestExecutor.DEFAULT_TIMEOUT_MS;    //default 10s
        String httpReadTimeoutMs = config.getParamValue(HTTP_READ_TIMEOUT_MS);
        if(!Strings.isNullOrEmpty(httpReadTimeoutMs)) {
            timeoutMs = Integer.parseInt(httpReadTimeoutMs);
        }
        String body = RequestExecutor.getInstance().executePost(checkUrl, headers, request, timeoutMs);
        ConnectionCheckOutputEntity connectionCheckOutputEntity = GsonUtils.json2T(body, ConnectionCheckOutputEntity.class);
        if(connectionCheckOutputEntity != null){
            validResult = connectionCheckOutputEntity.getSuccess();
            if(!validResult){
                String errMsg = connectionCheckOutputEntity.getMessage();
                Cat.logEvent("DbAccessManager", "connection.check.fail", Event.SUCCESS, errMsg);
            }
        }

        return validResult;
    }

}
