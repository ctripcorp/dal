
package com.ctrip.platform.dal.daogen.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ctrip.platform.dal.daogen.dao.DalApiDao;
import com.ctrip.platform.dal.daogen.dao.DalGroupDBDao;
import com.ctrip.platform.dal.daogen.dao.DalGroupDao;
import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.dao.DaoOfDatabaseSet;
import com.ctrip.platform.dal.daogen.dao.DaoOfLoginUser;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.dao.DaoOfUserProject;
import com.ctrip.platform.dal.daogen.dao.GroupRelationDao;
import com.ctrip.platform.dal.daogen.dao.UserGroupDao;

public class SpringBeanGetter {

	private static ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
	
	private static DaoOfProject daoOfProject;

	private static DaoBySqlBuilder daoBySqlBuilder;

	private static DaoByFreeSql daoByFreeSql;
	
	private static DaoByTableViewSp daoByTableViewSp;

	private static DaoOfLoginUser daoOfLoginUser;

	private static DaoOfUserProject daoOfUserProject;
	
	private static DalGroupDao daoOfDalGroup;
	
	private static DalGroupDBDao daoOfDalGroupDB;
	
	private static DaoOfDatabaseSet daoOfDatabaseSet;
	
	private static DalApiDao dalApiDao;
	
	private static UserGroupDao dalUserGroupDao;
	
	private static GroupRelationDao groupRelationDao;
	
	static {
		daoOfProject = (DaoOfProject) context.getBean("projectDao");
		
		daoBySqlBuilder = (DaoBySqlBuilder)context.getBean("autoTaskDao");
		daoByFreeSql = (DaoByFreeSql)context.getBean("sqlTaskDao");
		daoByTableViewSp =  (DaoByTableViewSp)context.getBean("daoByTableViewSp");
		daoOfLoginUser =  (DaoOfLoginUser)context.getBean("loginUserDao");
		daoOfUserProject = (DaoOfUserProject)context.getBean("userProjectDao");
		
		daoOfDalGroup = (DalGroupDao)context.getBean("dalGroup");
		daoOfDalGroupDB = (DalGroupDBDao)context.getBean("dalGroupDB");
		
		daoOfDatabaseSet = (DaoOfDatabaseSet) context.getBean("dalDatabaseSet");
		
		dalApiDao = (DalApiDao) context.getBean("dalApiDao");
		
		dalUserGroupDao = (UserGroupDao) context.getBean("dalUserGroupDao");
		
		groupRelationDao = (GroupRelationDao) context.getBean("groupRelationDao");
	}

	public static DaoOfProject getDaoOfProject() {
		return daoOfProject;
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

	public static DalGroupDao getDaoOfDalGroup(){
		return daoOfDalGroup;
	}
	
	public static DalGroupDBDao getDaoOfDalGroupDB(){
		return daoOfDalGroupDB;
	}
	
	public static DaoOfDatabaseSet getDaoOfDatabaseSet(){
		return daoOfDatabaseSet;
	}

	public static DalApiDao getDalApiDao() {
		return dalApiDao;
	}

	public static UserGroupDao getDalUserGroupDao() {
		return dalUserGroupDao;
	}

	public static GroupRelationDao getGroupRelationDao() {
		return groupRelationDao;
	}

}
