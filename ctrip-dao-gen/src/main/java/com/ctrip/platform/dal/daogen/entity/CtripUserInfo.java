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

    @Override
    public DalGroupDB getDefaultDBInfo(String dbType) {
        DalGroupDB db = new DalGroupDB();

        if (dbType == null || dbType.isEmpty()) {
            return db;
        }

        if (dbType.equals("MySQL")) {
            db.setDbAddress("pub.mysql.db.dev.sh.ctripcorp.com");
            db.setDbPort("28747");
            db.setDbUser("uws_dbticket");
            db.setDbPassword("kgd8v5CenyoMjtg1uwzj");
        } else if (dbType.equals("SQLServer")) {
            db.setDbAddress("devdb.dev.sh.ctriptravel.com");
            db.setDbPort("28747");
            db.setDbUser("uws_AllInOneKey_dev");
            db.setDbPassword("!QAZ@WSX1qaz2wsx");
        } else {
            db.setDbAddress("");
            db.setDbPort("");
            db.setDbUser("");
            db.setDbPassword("");
        }
        return db;
    }

}
