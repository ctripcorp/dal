package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.resource.UserInfoResource;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RequestUtil {
    public static HttpSession getSession(ServletRequest request) {
        if (request == null) {
            return null;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        return httpRequest.getSession();
    }

    public static LoginUser getUserInfo(HttpServletRequest request) {
        LoginUser user = null;
        HttpSession session = getSession(request);
        if (session == null) {
            return user;
        }
        Object userInfo = session.getAttribute(Consts.USER_INFO);
        if (userInfo != null) {
            user = (LoginUser) userInfo;
        } else {
            user = new LoginUser();
            user.setUserNo(UserInfoResource.getInstance().getEmployee(null));
            user.setUserName(UserInfoResource.getInstance().getName(null));
            user.setUserEmail(UserInfoResource.getInstance().getMail(null));
        }

        return user;
    }

    public static String getUserNo(HttpServletRequest request) {
        String userNo = null;
        LoginUser user = getUserInfo(request);
        if (user != null) {
            userNo = user.getUserNo();
        }
        return userNo;
    }

    public static Boolean isDefaultUser(HttpServletRequest request) {
        Boolean result = null;
        HttpSession session = getSession(request);
        if (session == null) {
            return result;
        }
        Object defaultUser = session.getAttribute(Consts.DEFAULT_USER);
        if (defaultUser != null) {
            result = (Boolean) defaultUser;
        }
        return result;
    }

    public static Boolean isSuperUser(HttpServletRequest request) {
        Boolean result = null;
        HttpSession session = getSession(request);
        if (session == null) {
            return result;
        }
        Object superUser = session.getAttribute(Consts.SUPER_USER);
        if (superUser != null) {
            result = (Boolean) superUser;
        }
        return result;
    }
}
