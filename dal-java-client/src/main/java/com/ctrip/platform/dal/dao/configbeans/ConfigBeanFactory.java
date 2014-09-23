package com.ctrip.platform.dal.dao.configbeans;

import com.ctrip.platform.appinternals.configuration.ConfigBeanManager;

public class ConfigBeanFactory {
	private static HAConfigBean habean = new HAConfigBean();
	
	public static void init() throws Exception {
		ConfigBeanManager.register(habean);
	}
	
	public static HAConfigBean getHAConfigBean(){
		return habean;
	}
}
