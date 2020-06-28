package com.ctrip.platform.dal.daogen.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by taochen on 2019/7/3.
 */
public class CatTransactionReport {
    @SerializedName("ips")
    private List<String> hostIPs;

    private JsonObject machines;

    public List<String> getHostIPs() {
        return hostIPs;
    }

    public void setHostIPs(List<String> hostIPs) {
        this.hostIPs = hostIPs;
    }

    public JsonObject getMachines() {
        return machines;
    }

    public void setMachines(JsonObject machines) {
        this.machines = machines;
    }
}
