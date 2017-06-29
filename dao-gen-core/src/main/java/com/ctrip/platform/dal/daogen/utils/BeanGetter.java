package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.dao.*;

import java.sql.SQLException;

public class BeanGetter {
    private static final Object LOCK = new Object();

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

    public static DaoOfProject getDaoOfProject() throws SQLException {
        if (daoOfProject == null) {
            synchronized (LOCK) {
                if (daoOfProject == null) {
                    daoOfProject = new DaoOfProject();
                }
            }
        }
        return daoOfProject;
    }

    public static DaoBySqlBuilder getDaoBySqlBuilder() throws SQLException {
        if (daoBySqlBuilder == null) {
            synchronized (LOCK) {
                if (daoBySqlBuilder == null) {
                    daoBySqlBuilder = new DaoBySqlBuilder();
                }
            }
        }
        return daoBySqlBuilder;
    }

    public static DaoByFreeSql getDaoByFreeSql() throws SQLException {
        if (daoByFreeSql == null) {
            synchronized (LOCK) {
                if (daoByFreeSql == null) {
                    daoByFreeSql = new DaoByFreeSql();
                }
            }
        }
        return daoByFreeSql;
    }

    public static DaoByTableViewSp getDaoByTableViewSp() throws SQLException {
        if (daoByTableViewSp == null) {
            synchronized (LOCK) {
                if (daoByTableViewSp == null) {
                    daoByTableViewSp = new DaoByTableViewSp();
                }
            }
        }
        return daoByTableViewSp;
    }

    public static DaoOfLoginUser getDaoOfLoginUser() throws SQLException {
        if (daoOfLoginUser == null) {
            synchronized (LOCK) {
                if (daoOfLoginUser == null) {
                    daoOfLoginUser = new DaoOfLoginUser();
                }
            }
        }
        return daoOfLoginUser;
    }

    public static DaoOfUserProject getDaoOfUserProject() throws SQLException {
        if (daoOfUserProject == null) {
            synchronized (LOCK) {
                if (daoOfUserProject == null) {
                    daoOfUserProject = new DaoOfUserProject();
                }
            }
        }
        return daoOfUserProject;
    }

    public static DalGroupDao getDaoOfDalGroup() throws SQLException {
        if (daoOfDalGroup == null) {
            synchronized (LOCK) {
                if (daoOfDalGroup == null) {
                    daoOfDalGroup = new DalGroupDao();
                }
            }
        }
        return daoOfDalGroup;
    }

    public static DalGroupDBDao getDaoOfDalGroupDB() throws SQLException {
        if (daoOfDalGroupDB == null) {
            synchronized (LOCK) {
                if (daoOfDalGroupDB == null) {
                    daoOfDalGroupDB = new DalGroupDBDao();
                }
            }
        }
        return daoOfDalGroupDB;
    }

    public static DaoOfDatabaseSet getDaoOfDatabaseSet() throws SQLException {
        if (daoOfDatabaseSet == null) {
            synchronized (LOCK) {
                if (daoOfDatabaseSet == null) {
                    daoOfDatabaseSet = new DaoOfDatabaseSet();
                }
            }
        }
        return daoOfDatabaseSet;
    }

    public static DalApiDao getDalApiDao() throws SQLException {
        if (dalApiDao == null) {
            synchronized (LOCK) {
                if (dalApiDao == null) {
                    dalApiDao = new DalApiDao();
                }
            }
        }
        return dalApiDao;
    }

    public static UserGroupDao getDalUserGroupDao() throws SQLException {
        if (dalUserGroupDao == null) {
            synchronized (LOCK) {
                if (dalUserGroupDao == null) {
                    dalUserGroupDao = new UserGroupDao();
                }
            }
        }
        return dalUserGroupDao;
    }

    public static GroupRelationDao getGroupRelationDao() throws SQLException {
        if (groupRelationDao == null) {
            synchronized (LOCK) {
                if (groupRelationDao == null) {
                    groupRelationDao = new GroupRelationDao();
                }
            }
        }
        return groupRelationDao;
    }

    public static ApproveTaskDao getApproveTaskDao() throws SQLException {
        if (approveTaskDao == null) {
            synchronized (LOCK) {
                if (approveTaskDao == null) {
                    approveTaskDao = new ApproveTaskDao();
                }
            }
        }
        return approveTaskDao;
    }

    public static SetupDBDao getSetupDBDao() {
        if (setupDBDao == null) {
            synchronized (LOCK) {
                if (setupDBDao == null) {
                    setupDBDao = new SetupDBDao();
                }
            }
        }
        return setupDBDao;
    }

    public static ConfigTemplateDao getConfigTemplateDao() throws SQLException {
        if (configTemplateDao == null) {
            synchronized (LOCK) {
                if (configTemplateDao == null) {
                    configTemplateDao = new ConfigTemplateDao();
                }
            }
        }
        return configTemplateDao;
    }
}
