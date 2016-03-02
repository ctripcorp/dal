package com.ctrip.platform.dal.daogen.entity;

import org.jasig.cas.client.util.AssertionHolder;
import com.ctrip.platform.dal.daogen.UserInfo;

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
}
