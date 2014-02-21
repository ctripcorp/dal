package com.ctrip.platform.dal.daogen.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.dao.DaoBySp;
import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;

public class SpringBeanGetter {

	private static ApplicationContext context = new ClassPathXmlApplicationContext(
			"spring.xml");

	public static DaoOfProject getProjectDao() {
		return (DaoOfProject) context.getBean("projectDao");
	}
	
	public static DaoBySqlBuilder getAutoTaskDao(){
		return (DaoBySqlBuilder)context.getBean("autoTaskDao");
	}
	
	public static DaoBySp getSpTaskDao(){
		return (DaoBySp)context.getBean("spTaskDao");
	}
	
	public static DaoByFreeSql getSqlTaskDao(){
		return (DaoByFreeSql)context.getBean("sqlTaskDao");
	}
	
	public static DaoOfDbServer getDBServerDao(){
		return (DaoOfDbServer)context.getBean("dataSourceDao");
	}
	
	public static DaoByTableViewSp getDaoByTableViewSp(){
		return (DaoByTableViewSp)context.getBean("daoByTableViewSp");
	}
	
	


}
