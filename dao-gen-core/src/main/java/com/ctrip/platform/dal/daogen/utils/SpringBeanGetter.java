package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.dao.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringBeanGetter {
	private static final Object LOCK = new Object();

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

	static {
		refreshApplicationContext();
	}

	public static void refreshApplicationContext() {
		synchronized (LOCK) {
			context = null;
			context = new ClassPathXmlApplicationContext("spring.xml");
		}
	}

	public static DaoOfProject getDaoOfProject() {
		if (daoOfProject == null) {
			synchronized (LOCK) {
				if (daoOfProject == null) {
					daoOfProject = (DaoOfProject) context.getBean("projectDao");
				}
			}
		}
		return daoOfProject;
	}

	public static DaoBySqlBuilder getDaoBySqlBuilder() {
		if (daoBySqlBuilder == null) {
			synchronized (LOCK) {
				if (daoBySqlBuilder == null) {
					daoBySqlBuilder = (DaoBySqlBuilder) context.getBean("autoTaskDao");
				}
			}
		}
		return daoBySqlBuilder;
	}

	public static DaoByFreeSql getDaoByFreeSql() {
		if (daoByFreeSql == null) {
			synchronized (LOCK) {
				if (daoByFreeSql == null) {
					daoByFreeSql = (DaoByFreeSql) context.getBean("sqlTaskDao");
				}
			}
		}
		return daoByFreeSql;
	}

	public static DaoByTableViewSp getDaoByTableViewSp() {
		if (daoByTableViewSp == null) {
			synchronized (LOCK) {
				if (daoByTableViewSp == null) {
					daoByTableViewSp = (DaoByTableViewSp) context.getBean("daoByTableViewSp");
				}
			}
		}
		return daoByTableViewSp;
	}

	public static DaoOfLoginUser getDaoOfLoginUser() {
		if (daoOfLoginUser == null) {
			synchronized (LOCK) {
				if (daoOfLoginUser == null) {
					daoOfLoginUser = (DaoOfLoginUser) context.getBean("loginUserDao");
				}
			}
		}
		return daoOfLoginUser;
	}

	public static DaoOfUserProject getDaoOfUserProject() {
		if (daoOfUserProject == null) {
			synchronized (LOCK) {
				if (daoOfUserProject == null) {
					daoOfUserProject = (DaoOfUserProject) context.getBean("userProjectDao");
				}
			}
		}
		return daoOfUserProject;
	}

	public static DalGroupDao getDaoOfDalGroup() {
		if (daoOfDalGroup == null) {
			synchronized (LOCK) {
				if (daoOfDalGroup == null) {
					daoOfDalGroup = (DalGroupDao) context.getBean("dalGroup");
				}
			}
		}
		return daoOfDalGroup;
	}

	public static DalGroupDBDao getDaoOfDalGroupDB() {
		if (daoOfDalGroupDB == null) {
			synchronized (LOCK) {
				if (daoOfDalGroupDB == null) {
					daoOfDalGroupDB = (DalGroupDBDao) context.getBean("dalGroupDB");
				}
			}
		}
		return daoOfDalGroupDB;
	}

	public static DaoOfDatabaseSet getDaoOfDatabaseSet() {
		if (daoOfDatabaseSet == null) {
			synchronized (LOCK) {
				if (daoOfDatabaseSet == null) {
					daoOfDatabaseSet = (DaoOfDatabaseSet) context.getBean("dalDatabaseSet");
				}
			}
		}
		return daoOfDatabaseSet;
	}

	public static DalApiDao getDalApiDao() {
		if (dalApiDao == null) {
			synchronized (LOCK) {
				if (dalApiDao == null) {
					dalApiDao = (DalApiDao) context.getBean("dalApiDao");
				}
			}
		}
		return dalApiDao;
	}

	public static UserGroupDao getDalUserGroupDao() {
		if (dalUserGroupDao == null) {
			synchronized (LOCK) {
				if (dalUserGroupDao == null) {
					dalUserGroupDao = (UserGroupDao) context.getBean("dalUserGroupDao");
				}
			}
		}
		return dalUserGroupDao;
	}

	public static GroupRelationDao getGroupRelationDao() {
		if (groupRelationDao == null) {
			synchronized (LOCK) {
				if (groupRelationDao == null) {
					groupRelationDao = (GroupRelationDao) context.getBean("groupRelationDao");
				}
			}
		}
		return groupRelationDao;
	}

	public static ApproveTaskDao getApproveTaskDao() {
		if (approveTaskDao == null) {
			synchronized (LOCK) {
				if (approveTaskDao == null) {
					approveTaskDao = (ApproveTaskDao) context.getBean("approveTaskDao");
				}
			}
		}
		return approveTaskDao;
	}

	public static SetupDBDao getSetupDBDao() {
		if (setupDBDao == null) {
			synchronized (LOCK) {
				if (setupDBDao == null) {
					setupDBDao = (SetupDBDao) context.getBean("setupDBDao");
				}
			}
		}
		return setupDBDao;
	}

	public static ConfigTemplateDao getConfigTemplateDao() {
		if (configTemplateDao == null) {
			synchronized (LOCK) {
				if (configTemplateDao == null) {
					configTemplateDao = (ConfigTemplateDao) context.getBean("configTemplateDao");
				}
			}
		}
		return configTemplateDao;
	}
}
