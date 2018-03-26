package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.daogen.entity.DalGroupDB;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public interface UserInfo {
    String getEmployee(String userNo) throws SQLException;

    String getName(String userNo) throws SQLException;

    String getMail(String userNo) throws SQLException;

    void logOut(HttpServletRequest request, HttpServletResponse response);

    DalGroupDB getDefaultDBInfo(String dbType);
}
