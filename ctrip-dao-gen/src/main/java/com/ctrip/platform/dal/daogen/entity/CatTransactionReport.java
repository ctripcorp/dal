package com.ctrip.platform.dal.daogen.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by taochen on 2019/7/3.
 */
public class CatTransactionReport {
    @JSONField(name = "ips")
    private List<String> hostIPs;

    private String machines;

    public List<String> getHostIPs() {
        return hostIPs;
    }

    public void setHostIPs(List<String> hostIPs) {
        this.hostIPs = hostIPs;
    }

    public String getMachines() {
        return machines;
    }

    public void setMachines(String machines) {
        this.machines = machines;
    }
}
