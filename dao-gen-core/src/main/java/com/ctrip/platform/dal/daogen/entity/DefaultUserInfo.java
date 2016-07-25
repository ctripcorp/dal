package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.daogen.UserInfo;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class DefaultUserInfo implements UserInfo {
    private DefaultUserInfo() {
    }

    private static final DefaultUserInfo INSTANCE = new DefaultUserInfo();

    public static final DefaultUserInfo getInstance() {
        return INSTANCE;
    }

    private static final DaoOfLoginUser userDao = SpringBeanGetter.getDaoOfLoginUser();

    private LoginUser getLoginUser(String userNo) {
        return userDao.getUserByNo(userNo);
    }

    @Override
    public String getEmployee(String userNo) {
        String number = null;
        LoginUser user = getLoginUser(userNo);
        if (user != null) {
            number = user.getUserNo();
        }
        return number;
    }

    @Override
    public String getName(String userNo) {
        String name = null;
        LoginUser user = getLoginUser(userNo);
        if (user != null) {
            name = user.getUserName();
        }
        return name;
    }

    @Override
    public String getMail(String userNo) {
        String email = null;
        LoginUser user = getLoginUser(userNo);
        if (user != null) {
            email = user.getUserEmail();
        }
        return email;
    }

    @Override
    public void logOut(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = RequestUtil.getSession(request);
        session.invalidate();
        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();
        int endIndex = url.length() - uri.length();
        StringBuilder loginUrl = new StringBuilder(url.substring(0, endIndex));
        loginUrl.append("/login.jsp");
        try {
            response.sendRedirect(loginUrl.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DalGroupDB getDefaultDBInfo(String dbType) {
        DalGroupDB db = new DalGroupDB();

        if (dbType == null || dbType.isEmpty()) {
            return db;
        }

        db.setDb_address("");
        db.setDb_port("");
        db.setDb_user("");
        db.setDb_password("");
        return db;
    }

}
