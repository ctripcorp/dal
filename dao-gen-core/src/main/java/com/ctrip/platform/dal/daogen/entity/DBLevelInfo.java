package com.ctrip.platform.dal.daogen.entity;

public class DBLevelInfo {
    private int organization_id;

    private String organization_name;

    private int level;

    private String db_type;

    private String db_name_base;

    private int dns_port;

    private String db_name;

    private String dns;

    private String dbowners;

    public int getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(int organization_id) {
        this.organization_id = organization_id;
    }

    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDb_type() {
        return db_type;
    }

    public void setDb_type(String db_type) {
        this.db_type = db_type;
    }

    public String getDb_name_base() {
        return db_name_base;
    }

    public void setDb_name_base(String db_name_base) {
        this.db_name_base = db_name_base;
    }

    public int getDns_port() {
        return dns_port;
    }

    public void setDns_port(int dns_port) {
        this.dns_port = dns_port;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getDbowners() {
        return dbowners;
    }

    public void setDbowners(String dbowners) {
        this.dbowners = dbowners;
    }
}
