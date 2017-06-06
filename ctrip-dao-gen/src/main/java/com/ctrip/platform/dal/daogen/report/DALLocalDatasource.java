package com.ctrip.platform.dal.daogen.report;

import java.util.Map;

public class DALLocalDatasource {
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

}
