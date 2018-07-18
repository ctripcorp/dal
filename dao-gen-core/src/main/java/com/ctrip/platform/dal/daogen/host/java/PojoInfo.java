package com.ctrip.platform.dal.daogen.host.java;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PojoInfo {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String userName;
    private String date;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return dateFormat.format(new Date());
    }

    public void setDate(String date) {
        this.date = date;
    }

}
