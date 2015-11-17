package com.ctrip.platform.dal.daogen.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ctrip.platform.dal.daogen.dao.ApproveTaskDao;
import com.ctrip.platform.dal.daogen.dao.ConfigTemplateDao;
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
import com.ctrip.platform.dal.daogen.dao.SetupDBDao;
import com.ctrip.platform.dal.daogen.dao.UserGroupDao;

public class SpringBeanGetter {

	private static ApplicationContext context = null;

	private static DaoOfProject daoOfProject = null;

	private static DaoBySqlBuilder daoBySqlBuilder = null;

	private static DaoByFreeSql daoByFreeSql = null;

	private static DaoByTableViewSp daoByTableViewSp = null;

	private static DaoOfLoginUser daoOfLoginUser = null;

	private static DaoOfUserProject daoOfUserProject = null;

	private static DalGroupDao daoOfDalGroup = null;

	private static DalGroupDBDao daoOfDalGroupDB = null;

	private static DaoOfDatabaseSet daoOfDatabaseSet = null;

	private static DalApiDao dalApiDao = null;

	private static UserGroupDao dalUserGroupDao = null;

	private static GroupRelationDao groupRelationDao = null;

	private static ApproveTaskDao approveTaskDao = null;

	private static SetupDBDao setupDBDao = null;

	private static ConfigTemplateDao configTemplateDao = null;

	public static void initializeApplicationContext() {
		context = null;
		context = new ClassPathXmlApplicationContext("spring.xml");
	}

	public static void initializeDao() {
		daoOfProject = null;
		daoBySqlBuilder = null;
		daoByFreeSql = null;
		daoByTableViewSp = null;
		daoOfLoginUser = null;
		daoOfUserProject = null;
		daoOfDalGroup = null;
		daoOfDalGroupDB = null;
		daoOfDatabaseSet = null;
		dalApiDao = null;
		dalUserGroupDao = null;
		groupRelationDao = null;
		approveTaskDao = null;
		setupDBDao = null;
		configTemplateDao = null;
	}

	public static DaoOfProject getDaoOfProject() {
		if (daoOfProject == null) {
			daoOfProject = (DaoOfProject) context.getBean("projectDao");
		}
		return daoOfProject;
	}

	public static DaoBySqlBuilder getDaoBySqlBuilder() {
		if (daoBySqlBuilder == null) {
			daoBySqlBuilder = (DaoBySqlBuilder) context.getBean("autoTaskDao");
		}
		return daoBySqlBuilder;
	}

	public static DaoByFreeSql getDaoByFreeSql() {
		if (daoByFreeSql == null) {
			daoByFreeSql = (DaoByFreeSql) context.getBean("sqlTaskDao");
		}
		return daoByFreeSql;
	}

	public static DaoByTableViewSp getDaoByTableViewSp() {
		if (daoByTableViewSp == null) {
			daoByTableViewSp = (DaoByTableViewSp) context
					.getBean("daoByTableViewSp");
		}
		return daoByTableViewSp;
	}

	public static DaoOfLoginUser getDaoOfLoginUser() {
		if (daoOfLoginUser == null) {
			daoOfLoginUser = (DaoOfLoginUser) context.getBean("loginUserDao");
		}
		return daoOfLoginUser;
	}

	public static DaoOfUserProject getDaoOfUserProject() {
		if (daoOfUserProject == null) {
			daoOfUserProject = (DaoOfUserProject) context
					.getBean("userProjectDao");
		}
		return daoOfUserProject;
	}

	public static DalGroupDao getDaoOfDalGroup() {
		if (daoOfDalGroup == null) {
			daoOfDalGroup = (DalGroupDao) context.getBean("dalGroup");
		}
		return daoOfDalGroup;
	}

	public static DalGroupDBDao getDaoOfDalGroupDB() {
		if (daoOfDalGroupDB == null) {
			daoOfDalGroupDB = (DalGroupDBDao) context.getBean("dalGroupDB");
		}
		return daoOfDalGroupDB;
	}

	public static DaoOfDatabaseSet getDaoOfDatabaseSet() {
		if (daoOfDatabaseSet == null) {
			daoOfDatabaseSet = (DaoOfDatabaseSet) context
					.getBean("dalDatabaseSet");
		}
		return daoOfDatabaseSet;
	}

	public static DalApiDao getDalApiDao() {
		if (dalApiDao == null) {
			dalApiDao = (DalApiDao) context.getBean("dalApiDao");
		}
		return dalApiDao;
	}

	public static UserGroupDao getDalUserGroupDao() {
		if (dalUserGroupDao == null) {
			dalUserGroupDao = (UserGroupDao) context.getBean("dalUserGroupDao");
		}
		return dalUserGroupDao;
	}

	public static GroupRelationDao getGroupRelationDao() {
		if (groupRelationDao == null) {
			groupRelationDao = (GroupRelationDao) context
					.getBean("groupRelationDao");
		}
		return groupRelationDao;
	}

	public static ApproveTaskDao getApproveTaskDao() {
		if (approveTaskDao == null) {
			approveTaskDao = (ApproveTaskDao) context.getBean("approveTaskDao");
		}
		return approveTaskDao;
	}

	public static SetupDBDao getSetupDBDao() {
		if (setupDBDao == null) {
			setupDBDao = (SetupDBDao) context.getBean("setupDBDao");
		}
		return setupDBDao;
	}

	public static ConfigTemplateDao getConfigTemplateDao() {
		if (configTemplateDao == null) {
			configTemplateDao = (ConfigTemplateDao) context
					.getBean("configTemplateDao");
		}
		return configTemplateDao;
	}
}
