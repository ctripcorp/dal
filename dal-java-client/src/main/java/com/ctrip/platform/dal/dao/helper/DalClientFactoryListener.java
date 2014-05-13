package com.ctrip.platform.dal.dao.helper;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class DalClientFactoryListener  implements ServletContextListener {
	
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		String DalConfigPath = context.getInitParameter("com.ctrip.platform.dal.dao.DalConfigPath");
		try {
			if(DalConfigPath == null || DalConfigPath.trim().length() == 0)
				DalClientFactory.initClientFactory();
			else
				DalClientFactory.initClientFactory(DalConfigPath.trim());
			System.out.println("Dal Java Client Factory initialized");
		} catch (Exception e) {
			System.out.println("Dal Java Client Factory initializing error!!!");
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		DalClientFactory.shutdownFactory();
		System.out.println("Dal Java Client Factory finalized");
	}
}
