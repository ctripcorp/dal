
package com.ctrip.platform.dal.daogen.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.dao.DaoOfUserProject;

public class SpringBeanGetter {

	private static ApplicationContext context = new ClassPathXmlApplicationContext(
			"spring.xml");
	
	private static DaoOfProject daoOfProject;

	private static DaoBySqlBuilder daoBySqlBuilder;

	private static DaoByFreeSql daoByFreeSql;

	private static DaoOfDbServer daoOfDbServer;
	
	private static DaoByTableViewSp daoByTableViewSp;

	private static DaoOfLoginUser daoOfLoginUser;

	private static DaoOfUserProject daoOfUserProject;
	
	static {
		daoOfProject = (DaoOfProject) context.getBean("projectDao");
		daoOfDbServer = (DaoOfDbServer)context.getBean("dataSourceDao");
		
		daoBySqlBuilder = (DaoBySqlBuilder)context.getBean("autoTaskDao");
		daoByFreeSql = (DaoByFreeSql)context.getBean("sqlTaskDao");
		daoByTableViewSp =  (DaoByTableViewSp)context.getBean("daoByTableViewSp");
		daoOfLoginUser =  (DaoOfLoginUser)context.getBean("loginUserDao");
		daoOfUserProject = (DaoOfUserProject)context.getBean("userProjectDao");
	}

	public static DaoOfProject getDaoOfProject() {
		return daoOfProject;
	}
	
	public static DaoOfDbServer getDaoOfDbServer(){
		return daoOfDbServer;
	}
	
	public static DaoBySqlBuilder getDaoBySqlBuilder(){
		return daoBySqlBuilder;
	}
	
	public static DaoByFreeSql getDaoByFreeSql(){
		return daoByFreeSql;
	}
	
	public static DaoByTableViewSp getDaoByTableViewSp(){
		return daoByTableViewSp;
	}
	
	public static DaoOfLoginUser getDaoOfLoginUser(){
		return daoOfLoginUser;
	}
	
	public static DaoOfUserProject getDaoOfUserProject(){
		return daoOfUserProject;
	}

}
