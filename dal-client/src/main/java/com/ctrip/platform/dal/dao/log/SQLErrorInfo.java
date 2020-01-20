package com.ctrip.platform.dal.dao.log;

import java.util.HashMap;
import java.util.Map;

public class SQLErrorInfo {
    public static final String ERROR = "fx.dal.client.fail";

    public static final String CLIENT = "Client";
    private String version;

    private static final String DB = "DB";
    private String database;


    public SQLErrorInfo(String version, String titanKey) {
        this.version = version;
        this.database = titanKey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Map<String, String> toTag() {
        Map<String, String> tag = new HashMap<String, String>();
        tag.put(CLIENT, version);
        tag.put(DB, database);
        return tag;
    }
}
