package com.ctrip.platform.dal.appinternals;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import com.ctrip.framework.clogging.agent.log.ILog;
import com.ctrip.framework.clogging.agent.log.LogManager;
import com.ctrip.platform.dal.appinternals.helpers.Helper;
import com.ctrip.platform.dal.appinternals.permission.Permission;

public class AppInternalsServletContainerInitializer implements
		ServletContainerInitializer {
	
	private static ILog logger = LogManager.getLogger(AppInternalsServletContainerInitializer.class);
	
	public void onStartup(Set<Class<?>> c, ServletContext cx)
			throws ServletException {
		logger.info("--- CONTAINER SERVLET CONTAINER INITIALIZER! ---");
		ServletRegistration.Dynamic servlet = cx.addServlet(
				AppInternalsServlet.class.getSimpleName(),
				AppInternalsServlet.class);
		
		servlet.addMapping(AppConst.BASEURL, AppConst.BEANSURL, AppConst.CONFIGBEANURL);
		
		String permissions = cx.getInitParameter(AppConst.PERMISSIONKEY);
		String[] tokens = permissions.split(";");
		if(null != tokens){
			for (String token : tokens) {
				String[] ip = token.split(":");
				int perm = 0;
				if(ip.length == 2){
					perm = Integer.parseInt(ip[1]);
				}
				if(Helper.validateIPV4(ip[0])){
					Permission.getInstance().addUser(ip[0], perm);
				}else{
					logger.error(String.format("The IP Address[%s] format is illegal, pls check.", 
							ip[0]));
				}
			}
		}
		
		logger.info("--- CONTAINER SERVLET CONTAINER INITIALIZER COMPLETED! ---");
	}
}
