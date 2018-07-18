package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.dao.*;

import java.sql.SQLException;

public class BeanGetter {
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

    public synchronized static DaoOfProject getDaoOfProject() throws SQLException {
        if (daoOfProject == null) {
            if (daoOfProject == null) {
                daoOfProject = new DaoOfProject();
            }
        }
        return daoOfProject;
    }

    public synchronized static DaoBySqlBuilder getDaoBySqlBuilder() throws SQLException {
        if (daoBySqlBuilder == null) {
            if (daoBySqlBuilder == null) {
                daoBySqlBuilder = new DaoBySqlBuilder();
            }
        }
        return daoBySqlBuilder;
    }

    public synchronized static DaoByFreeSql getDaoByFreeSql() throws SQLException {
        if (daoByFreeSql == null) {
            if (daoByFreeSql == null) {
                daoByFreeSql = new DaoByFreeSql();
            }
        }
        return daoByFreeSql;
    }

    public synchronized static DaoByTableViewSp getDaoByTableViewSp() throws SQLException {
        if (daoByTableViewSp == null) {
            if (daoByTableViewSp == null) {
                daoByTableViewSp = new DaoByTableViewSp();
            }
        }
        return daoByTableViewSp;
    }

    public synchronized static DaoOfLoginUser getDaoOfLoginUser() throws SQLException {
        if (daoOfLoginUser == null) {
            if (daoOfLoginUser == null) {
                daoOfLoginUser = new DaoOfLoginUser();
            }
        }
        return daoOfLoginUser;
    }

    public synchronized static DaoOfUserProject getDaoOfUserProject() throws SQLException {
        if (daoOfUserProject == null) {
            if (daoOfUserProject == null) {
                daoOfUserProject = new DaoOfUserProject();
            }
        }
        return daoOfUserProject;
    }

    public synchronized static DalGroupDao getDaoOfDalGroup() throws SQLException {
        if (daoOfDalGroup == null) {
            if (daoOfDalGroup == null) {
                daoOfDalGroup = new DalGroupDao();
            }
        }
        return daoOfDalGroup;
    }

    public synchronized static DalGroupDBDao getDaoOfDalGroupDB() throws SQLException {
        if (daoOfDalGroupDB == null) {
            if (daoOfDalGroupDB == null) {
                daoOfDalGroupDB = new DalGroupDBDao();
            }
        }
        return daoOfDalGroupDB;
    }

    public synchronized static DaoOfDatabaseSet getDaoOfDatabaseSet() throws SQLException {
        if (daoOfDatabaseSet == null) {
            if (daoOfDatabaseSet == null) {
                daoOfDatabaseSet = new DaoOfDatabaseSet();
            }
        }
        return daoOfDatabaseSet;
    }

    public synchronized static DalApiDao getDalApiDao() throws SQLException {
        if (dalApiDao == null) {
            if (dalApiDao == null) {
                dalApiDao = new DalApiDao();
            }
        }
        return dalApiDao;
    }

    public synchronized static UserGroupDao getDalUserGroupDao() throws SQLException {
        if (dalUserGroupDao == null) {
            if (dalUserGroupDao == null) {
                dalUserGroupDao = new UserGroupDao();
            }
        }
        return dalUserGroupDao;
    }

    public synchronized static GroupRelationDao getGroupRelationDao() throws SQLException {
        if (groupRelationDao == null) {
            if (groupRelationDao == null) {
                groupRelationDao = new GroupRelationDao();
            }
        }
        return groupRelationDao;
    }

    public synchronized static ApproveTaskDao getApproveTaskDao() throws SQLException {
        if (approveTaskDao == null) {
            if (approveTaskDao == null) {
                approveTaskDao = new ApproveTaskDao();
            }
        }
        return approveTaskDao;
    }

    public synchronized static SetupDBDao getSetupDBDao() {
        if (setupDBDao == null) {
            if (setupDBDao == null) {
                setupDBDao = new SetupDBDao();
            }
        }
        return setupDBDao;
    }

    public synchronized static ConfigTemplateDao getConfigTemplateDao() throws SQLException {
        if (configTemplateDao == null) {
            if (configTemplateDao == null) {
                configTemplateDao = new ConfigTemplateDao();
            }
        }
        return configTemplateDao;
    }
}
