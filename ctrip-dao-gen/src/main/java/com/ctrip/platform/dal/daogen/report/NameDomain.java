package com.ctrip.platform.dal.daogen.report;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

public class NameDomain {
    private String name;

    @JSONField(name = "name-domain-counts")
    private Map<String, NameDomainCount> nameDomainCounts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, NameDomainCount> getNameDomainCounts() {
        return nameDomainCounts;
    }

    public void setNameDomainCounts(Map<String, NameDomainCount> nameDomainCounts) {
        this.nameDomainCounts = nameDomainCounts;
    }
}
