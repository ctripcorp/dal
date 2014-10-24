package com.ctrip.platform.dal.dao.helper;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.framework.clogging.agent.MessageManager;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.sql.logging.DalLogger;
import com.ctrip.platform.dal.sql.logging.DalWatcher;

public class DalClientFactoryListener implements ServletContextListener {
	private Logger logger = LoggerFactory.getLogger(DalClientFactoryListener.class);
	
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Dal Factory Listener is about to start");
		ServletContext context = sce.getServletContext();
		
		String DalConfigPath = context.getInitParameter("com.ctrip.platform.dal.dao.DalConfigPath");
		String warmUp = context.getInitParameter("com.ctrip.platform.dal.dao.DalWarmUp");
		String simplifyLogging = context.getInitParameter("com.ctrip.platform.dal.dao.SimplifyLogging");

		try {
			if(DalConfigPath == null || DalConfigPath.trim().length() == 0)
				DalClientFactory.initClientFactory();
			else
				DalClientFactory.initClientFactory(DalConfigPath.trim());
			
			if(Boolean.parseBoolean(warmUp))
				DalClientFactory.warmUpConnections();
			
			DalLogger.setSimplifyLogging(Boolean.parseBoolean(simplifyLogging));
			ConfigBeanFactory.init();

		} catch (Exception e) {
			logger.error("Error when init client factory", e);
		}
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		DalClientFactory.shutdownFactory();
		ServletContext context = sce.getServletContext();
		
		String closeClogWhenExit = context.getInitParameter("com.ctrip.platform.dal.dao.closeClogWhenExit");

		if(Boolean.parseBoolean(closeClogWhenExit)) {
			logger.info("shutdown clogging");
			MessageManager.getInstance().shutdown();
		}
		
		logger.info("DalWatcher has been destoryed");
		DalWatcher.destroy();
		
		logger.info("Markdown Manager has been destoryed");
		MarkdownManager.shutdown();
	}
}
