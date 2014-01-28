package com.ctrip.platform.dal.daogen.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ctrip.platform.dal.daogen.dao.AutoTaskDAO;
import com.ctrip.platform.dal.daogen.dao.DbServerDAO;
import com.ctrip.platform.dal.daogen.dao.ProjectDAO;
import com.ctrip.platform.dal.daogen.dao.SPTaskDAO;
import com.ctrip.platform.dal.daogen.dao.ServerDbMapDAO;
import com.ctrip.platform.dal.daogen.dao.SqlTaskDAO;

public class BeanGetter {

	private static ApplicationContext context = new ClassPathXmlApplicationContext(
			"spring.xml");

	public static ProjectDAO getProjectDao() {
		return (ProjectDAO) context.getBean("projectDao");
	}
	
	public static AutoTaskDAO getAutoTaskDao(){
		return (AutoTaskDAO)context.getBean("autoTaskDao");
	}
	
	public static SPTaskDAO getSpTaskDao(){
		return (SPTaskDAO)context.getBean("spTaskDao");
	}
	
	public static SqlTaskDAO getSqlTaskDao(){
		return (SqlTaskDAO)context.getBean("sqlTaskDao");
	}
	
	public static DbServerDAO getDBServerDao(){
		return (DbServerDAO)context.getBean("dataSourceDao");
	}
	
	public static ServerDbMapDAO getServerDbMapDao(){
		return (ServerDbMapDAO)context.getBean("serverDbmapDao");
	}

}
