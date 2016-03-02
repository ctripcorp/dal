package com.ctrip.platform.dal.daogen.utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.resource.UserInfoResource;

public class RequestUtil {
	public static HttpSession getSession(ServletRequest request) {
		if (request == null) {
			return null;
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		return httpRequest.getSession();
	}

	public static LoginUser getUserInfo(ServletRequest request) {
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

	public static String getUserNo(ServletRequest request) {
		String userNo = null;
		LoginUser user = getUserInfo(request);
		if (user != null) {
			userNo = user.getUserNo();
		}
		return userNo;
	}

}
