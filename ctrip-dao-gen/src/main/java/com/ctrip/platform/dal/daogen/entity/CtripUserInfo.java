package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.UserInfo;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import org.jasig.cas.client.util.AssertionHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CtripUserInfo implements UserInfo {
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
        String url = Configuration.get("cas_url") + "/caso/logout?service=" + Configuration.get("codegen_url");
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
