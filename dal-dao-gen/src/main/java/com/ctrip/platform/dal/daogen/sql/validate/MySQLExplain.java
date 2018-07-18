package com.ctrip.platform.dal.daogen.sql.validate;

public class MySQLExplain {
    private Integer id;
    private String select_type;
    private String type;
    private String possible_keys;
    private String key;
    private Integer rows;
    private String extra;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSelect_type() {
        return select_type;
    }

    public void setSelect_type(String select_type) {
        this.select_type = select_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPossible_keys() {
        return possible_keys;
    }

    public void setPossible_keys(String possible_keys) {
        this.possible_keys = possible_keys;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
