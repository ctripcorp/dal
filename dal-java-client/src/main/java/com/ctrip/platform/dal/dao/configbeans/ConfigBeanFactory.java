package com.ctrip.platform.dal.dao.configbeans;

import com.ctrip.platform.appinternals.appinfo.AppInfo;
import com.ctrip.platform.appinternals.configuration.ConfigBeanManager;
import com.ctrip.platform.dal.dao.DalClientFactory;

public class ConfigBeanFactory {
	private static HAConfigBean habean = new HAConfigBean();
	private static MarkdownConfigBean mkbean = new MarkdownConfigBean();
	private static TimeoutMarkDownBean tmkbean = new TimeoutMarkDownBean();
	
	public static void init() throws Exception {
		ConfigBeanManager.register(habean, mkbean, tmkbean);
		AppInfo.setAppID(DalClientFactory.getDalLogger().getAppID());
	}
	
	public static HAConfigBean getHAConfigBean(){
		return habean;
	}
	
	public static MarkdownConfigBean getMarkdownConfigBean(){
		return mkbean;
	}
	
	public static TimeoutMarkDownBean getTimeoutMarkDownBean(){
		return tmkbean;
	}
}
