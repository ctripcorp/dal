package com.ctrip.platform.dal.dao.configbeans;

import com.ctrip.platform.appinternals.configuration.ConfigBeanManager;

public class ConfigBeanFactory {
	private static HAConfigBean habean = new HAConfigBean();
	private static MarkdownConfigBean mkbean = new MarkdownConfigBean();
	
	public static void init() throws Exception {
		ConfigBeanManager.register(habean, mkbean);
	}
	
	public static HAConfigBean getHAConfigBean(){
		return habean;
	}
	
	public static MarkdownConfigBean getMarkdownConfigBean(){
		return mkbean;
	}
}
