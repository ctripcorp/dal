package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.daogen.UserInfo;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public class DefaultUserInfo implements UserInfo {
	private DefaultUserInfo() {
	}

	private static class Lazy {
		private static final DefaultUserInfo INSTANCE = new DefaultUserInfo();
	}

	public static final DefaultUserInfo getInstance() {
		return Lazy.INSTANCE;
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

}
