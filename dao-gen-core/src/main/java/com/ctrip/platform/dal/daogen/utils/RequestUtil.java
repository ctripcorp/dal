package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.resource.CustomizedResource;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RequestUtil {
    public static HttpSession getSession(ServletRequest request) {
        if (request == null)
            return null;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        return httpRequest.getSession();
    }

    public static LoginUser getUserInfo(HttpServletRequest request) throws Exception {
        LoginUser user = null;
        HttpSession session = getSession(request);
        if (session == null)
            return user;

        Object userInfo = session.getAttribute(Consts.USER_INFO);
        if (userInfo != null) {
            user = (LoginUser) userInfo;
        } else {
            user = new LoginUser();
            user.setUserNo(CustomizedResource.getInstance().getEmployee(null));
            user.setUserName(CustomizedResource.getInstance().getName(null));
            user.setUserEmail(CustomizedResource.getInstance().getMail(null));
        }

        return user;
    }

    public static String getUserNo(HttpServletRequest request) throws Exception {
        String userNo = null;
        LoginUser user = getUserInfo(request);
        if (user != null)
            userNo = user.getUserNo();
        return userNo;
    }

    public static Boolean isDefaultUser(HttpServletRequest request) {
        Boolean result = null;
        HttpSession session = getSession(request);
        if (session == null)
            return result;
        Object defaultUser = session.getAttribute(Consts.DEFAULT_USER);
        if (defaultUser != null)
            result = (Boolean) defaultUser;
        return result;
    }

    public static Boolean isSuperUser(HttpServletRequest request) {
        Boolean result = null;
        HttpSession session = getSession(request);
        if (session == null)
            return result;
        Object superUser = session.getAttribute(Consts.SUPER_USER);
        if (superUser != null)
            result = (Boolean) superUser;
        return result;
    }
}
