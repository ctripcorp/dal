package com.ctrip.platform.dal.daogen;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserInfo {
    String getEmployee(String userNo);

    String getName(String userNo);

    String getMail(String userNo);

    void logOut(HttpServletRequest request, HttpServletResponse response);
}
