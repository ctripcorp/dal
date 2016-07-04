package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.daogen.utils.Configuration;
import com.ctrip.platform.dal.daogen.UserInfo;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import org.jasig.cas.client.util.AssertionHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CtripUserInfo implements UserInfo {
    private static final String CAS_URL = Configuration.get("cas_url");
    private static final String CAS_LOGOUT = "/caso/logout?service=";
    private static final String CODEGEN_URL = Configuration.get("codegen_url");

    @Override
    public String getEmployee(String userNo) {
        return AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
    }

    @Override
    public String getName(String userNo) {
        return AssertionHolder.getAssertion().getPrincipal().getAttributes().get("sn").toString();
    }

    @Override
    public String getMail(String userNo) {
        return AssertionHolder.getAssertion().getPrincipal().getAttributes().get("mail").toString();
    }

    @Override
    public void logOut(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = RequestUtil.getSession(request);
        session.invalidate();

        Cookie cookie = new Cookie("memCacheAssertionID", null);
        cookie.setMaxAge(0);
        cookie.setPath(request.getContextPath() + "/");
        response.addCookie(cookie);

        String url = CAS_URL + CAS_LOGOUT + CODEGEN_URL;
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
