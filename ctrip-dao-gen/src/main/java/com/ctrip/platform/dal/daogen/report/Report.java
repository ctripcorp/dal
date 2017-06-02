package com.ctrip.platform.dal.daogen.report;

import com.alibaba.fastjson.annotation.JSONField;

public class Report {
    private String[] ips;

    public String[] getIps() {
        return ips;
    }

    public void setIps(String[] ips) {
        this.ips = ips;
    }

    private Machines machines;

    public Machines getMachines() {
        return machines;
    }

    public void setMachines(Machines machines) {
        this.machines = machines;
    }

    @JSONField(name = "type-domains")
    private TypeDomains typeDomains;

    public TypeDomains getTypeDomains() {
        return typeDomains;
    }

    public void setTypeDomains(TypeDomains typeDomains) {
        this.typeDomains = typeDomains;
    }
}
