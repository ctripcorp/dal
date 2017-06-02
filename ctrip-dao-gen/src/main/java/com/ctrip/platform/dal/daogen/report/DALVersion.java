package com.ctrip.platform.dal.daogen.report;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

public class DALVersion {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private Map<String, Version> names;

    public Map<String, Version> getNames() {
        return names;
    }

    public void setNames(Map<String, Version> names) {
        this.names = names;
    }

    @JSONField(name = "name-domains")
    private Map<String, NameDomain> nameDomains;

    public Map<String, NameDomain> getNameDomains() {
        return nameDomains;
    }

    public void setNameDomains(Map<String, NameDomain> nameDomains) {
        this.nameDomains = nameDomains;
    }

}
