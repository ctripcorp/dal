
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

	public static DaoOfProject getDaoOfProject() {
		return (DaoOfProject) context.getBean("projectDao");
	}
	
	public static DaoBySqlBuilder getDaoBySqlBuilder(){
		return (DaoBySqlBuilder)context.getBean("autoTaskDao");
	}
	
	public static DaoByFreeSql getDaoByFreeSql(){
		return (DaoByFreeSql)context.getBean("sqlTaskDao");
	}
	
	public static DaoOfDbServer getDaoOfDbServer(){
		return (DaoOfDbServer)context.getBean("dataSourceDao");
	}
	
	public static DaoByTableViewSp getDaoByTableViewSp(){
		return (DaoByTableViewSp)context.getBean("daoByTableViewSp");
	}
	
	public static DaoOfLoginUser getDaoOfLoginUser(){
		return (DaoOfLoginUser)context.getBean("loginUserDao");
	}
	
	public static DaoOfUserProject getDaoOfUserProject(){
		return (DaoOfUserProject)context.getBean("userProjectDao");
	}


}
