package com.ctrip.platform.dal.daogen.utils;

import org.jasig.cas.client.util.AssertionHolder;

public class AssertionHolderManager {
	public static String getEmployee() {
		return AssertionHolder.getAssertion().getPrincipal().getAttributes().get("employee").toString();
	}

	public static String getName() {
		return AssertionHolder.getAssertion().getPrincipal().getAttributes().get("sn").toString();
	}

	public static String getMail() {
		return AssertionHolder.getAssertion().getPrincipal().getAttributes().get("mail").toString();
	}
}
