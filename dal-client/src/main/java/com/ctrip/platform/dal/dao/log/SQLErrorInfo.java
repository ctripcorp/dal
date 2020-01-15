package com.ctrip.platform.dal.dao.log;

import java.util.HashMap;
import java.util.Map;

public class SQLErrorInfo {
    public static final String ERROR = "fx.dal.client.fail";

    public static final String CLIENT = "Client";
    private String version;

    private static final String TITANKEY = "TitanKey";
    private String titanKey;


    public SQLErrorInfo(String version, String titanKey) {
        this.version = version;
        this.titanKey = titanKey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitanKey() {
        return titanKey;
    }

    public void setTitanKey(String titanKey) {
        this.titanKey = titanKey;
    }

    public Map<String, String> toTag() {
        Map<String, String> tag = new HashMap<String, String>();
        tag.put(CLIENT, version);
        tag.put(TITANKEY, titanKey);
        return tag;
    }
}
