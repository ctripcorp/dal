package com.ctrip.platform.dal.daogen.report;

import java.util.Set;

public class VersionStats {
    private Set<String> allAppIds;

    private Set<String> ctripDalClientAppIds;

    private Set<String> tempDataSourceAppIds;

    private Set<String> ctripDataSourceAppIds;

    private Set<String> javaDalAppIds;

    private Set<String> netDalAppIds;

    public Set<String> getAllAppIds() {
        return allAppIds;
    }

    public void setAllAppIds(Set<String> allAppIds) {
        this.allAppIds = allAppIds;
    }

    public Set<String> getCtripDalClientAppIds() {
        return ctripDalClientAppIds;
    }

    public void setCtripDalClientAppIds(Set<String> ctripDalClientAppIds) {
        this.ctripDalClientAppIds = ctripDalClientAppIds;
    }

    public Set<String> getTempDataSourceAppIds() {
        return tempDataSourceAppIds;
    }

    public void setTempDataSourceAppIds(Set<String> tempDataSourceAppIds) {
        this.tempDataSourceAppIds = tempDataSourceAppIds;
    }

    public Set<String> getCtripDataSourceAppIds() {
        return ctripDataSourceAppIds;
    }

    public void setCtripDataSourceAppIds(Set<String> ctripDataSourceAppIds) {
        this.ctripDataSourceAppIds = ctripDataSourceAppIds;
    }

    public Set<String> getJavaDalAppIds() {
        return javaDalAppIds;
    }

    public void setJavaDalAppIds(Set<String> javaDalAppIds) {
        this.javaDalAppIds = javaDalAppIds;
    }

    public Set<String> getNetDalAppIds() {
        return netDalAppIds;
    }

    public void setNetDalAppIds(Set<String> netDalAppIds) {
        this.netDalAppIds = netDalAppIds;
    }

}
