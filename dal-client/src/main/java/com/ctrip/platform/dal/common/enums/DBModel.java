package com.ctrip.platform.dal.common.enums;

public enum DBModel {
    STANDALONE("standalone"),MGR("mgr"), OB("ob");

    private String name;

    DBModel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static DBModel toDBModel(String dbModelStr) {
        if (STANDALONE.name.equalsIgnoreCase(dbModelStr)) {
            return STANDALONE;
        }
        else if (MGR.name.equalsIgnoreCase(dbModelStr)) {
            return MGR;
        }
        return null;
    }


}
