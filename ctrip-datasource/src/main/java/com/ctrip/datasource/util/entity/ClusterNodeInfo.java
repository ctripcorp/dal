package com.ctrip.datasource.util.entity;

public class ClusterNodeInfo {
    private String status;

    private String mastervip;

    private String machine_name;

    private String machine_located_short;

    private String ip_business;

    private int dns_port;

    private String role;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMastervip() {
        return mastervip;
    }

    public void setMastervip(String mastervip) {
        this.mastervip = mastervip;
    }

    public String getMachine_name() {
        return machine_name;
    }

    public void setMachine_name(String machine_name) {
        this.machine_name = machine_name;
    }

    public String getMachine_located_short() {
        return machine_located_short;
    }

    public void setMachine_located_short(String machine_located_short) {
        this.machine_located_short = machine_located_short;
    }

    public String getIp_business() {
        return ip_business;
    }

    public void setIp_business(String ip_business) {
        this.ip_business = ip_business;
    }

    public int getDns_port() {
        return dns_port;
    }

    public void setDns_port(int dns_port) {
        this.dns_port = dns_port;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
