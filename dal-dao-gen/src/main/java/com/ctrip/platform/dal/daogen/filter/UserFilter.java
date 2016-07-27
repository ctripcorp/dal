package com.ctrip.platform.dal.daogen.filter;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.resource.CustomizedResource;
import com.ctrip.platform.dal.daogen.resource.SetupDBResource;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

import javax.servlet.*;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class UserFilter implements Filter {
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpSession session = RequestUtil.getSession(request);
            Object userInfo = session.getAttribute(Consts.USER_INFO);
            if (userInfo == null) {
                if (SetupDBResource.isJdbcInitialized()) {
                    // SSO
                    String userNo = CustomizedResource.getInstance().getEmployee(null);
                    if (userNo != null && !userNo.isEmpty()) {
                        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
                        if (user == null) {
                            user = new LoginUser();
                            user.setUserNo(userNo);
                            user.setUserName(CustomizedResource.getInstance().getName(null));
                            user.setUserEmail(CustomizedResource.getInstance().getMail(null));
                            SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
                        }
                        session.setAttribute(Consts.USER_INFO, user);
                        session.setAttribute(Consts.USER_NAME, user.getUserName());
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}