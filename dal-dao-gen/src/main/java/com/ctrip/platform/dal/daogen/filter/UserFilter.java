package com.ctrip.platform.dal.daogen.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.utils.AssertionHolderManager;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public class UserFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		try {
			HttpServletRequest httprequest = (HttpServletRequest) arg0;
			HttpSession session = httprequest.getSession();
			if (session.getAttribute("loginUserName") == null || "".equals(session.getAttribute("loginUserName"))) {
				String userNo = AssertionHolderManager.getEmployee();
				LoginUser user = null;
				user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
				if (user == null) {
					user = new LoginUser();
					user.setUserNo(userNo);
					user.setUserName(AssertionHolderManager.getName());
					user.setUserEmail(AssertionHolderManager.getMail());
					SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
				}
				session.setAttribute("loginUserName", user.getUserName());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			arg2.doFilter(arg0, arg1);
		}

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}