package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.dao.DalGroupDBDao;
import com.ctrip.platform.dal.daogen.domain.ColumnMetaData;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.domain.TableSpNames;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Resource
@Singleton
@Path("db")
public class DatabaseResource {
    private static ClassLoader classLoader;
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = Configuration.class.getClassLoader();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("merge")
    public Status mergeDB(@Context HttpServletRequest request) throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);
            Status status = Status.OK();

            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            List<UserGroup> urGroups = BeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
            boolean havePersimion = false;
            if (urGroups != null && urGroups.size() > 0) {
                for (UserGroup ug : urGroups) {
                    if (ug.getGroup_id() == DalGroupResource.SUPER_GROUP_ID) {
                        havePersimion = true;
                        break;
                    }
                }
            }

            if (!havePersimion) {
                status = Status.ERROR();
                status.setInfo("You have no permision, only DAL Admin Team can do this.");
                return status;
            }

            DalGroupDBDao allDbDao = BeanGetter.getDaoOfDalGroupDB();

            Map<String, DalGroupDB> allDbs =
                    new AllInOneConfigParser(Configuration.get("all_in_one")).getDBAllInOneConfig();
            Set<String> keys = allDbs.keySet();
            for (String key : keys) {
                DalGroupDB db = allDbDao.getGroupDBByDbName(key);
                if (db == null) {
                    allDbDao.insertDalGroupDB(allDbs.get(key));
                } else {
                    DalGroupDB fileDB = allDbs.get(key);
                    allDbDao.updateGroupDB(db.getId(), key, fileDB.getDb_address(), fileDB.getDb_port(),
                            fileDB.getDb_user(), fileDB.getDb_password(), fileDB.getDb_catalog(),
                            fileDB.getDb_providerName());
                }
            }

            return Status.OK();
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("connectionTest")
    public Status connectionTest(@FormParam("dbtype") String dbtype, @FormParam("dbaddress") String dbaddress,
            @FormParam("dbport") String dbport, @FormParam("dbuser") String dbuser,
            @FormParam("dbpassword") String dbpassword) throws Exception {
        Connection conn = null;
        ResultSet rs = null;
        try {
            Status status = Status.OK();
            try {
                conn = DataSourceUtil.getConnection(dbaddress, dbport, dbuser, dbpassword,
                        DatabaseType.valueOf(dbtype).getValue());
                // conn.setNetworkTimeout(Executors.newFixedThreadPool(1), 5000);
                rs = conn.getMetaData().getCatalogs();
                Set<String> allCatalog = new HashSet<String>();
                while (rs.next()) {
                    allCatalog.add(rs.getString("TABLE_CAT"));
                }
                status.setInfo(mapper.writeValueAsString(allCatalog));
            } catch (SQLException e) {
                status = Status.ERROR();
                status.setInfo(e.getMessage());
                return status;
            } catch (JsonProcessingException e) {
                status = Status.ERROR();
                status.setInfo(e.getMessage());
                return status;
            }
            return status;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        } finally {
            ResourceUtils.close(rs);
            ResourceUtils.close(conn);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("addNewAllInOneDB")
    public Status addNewAllInOneDB(@Context HttpServletRequest request, @FormParam("dbtype") String dbtype,
            @FormParam("allinonename") String allinonename, @FormParam("dbaddress") String dbaddress,
            @FormParam("dbport") String dbport, @FormParam("dbuser") String dbuser,
            @FormParam("dbpassword") String dbpassword, @FormParam("dbcatalog") String dbcatalog,
            @FormParam("addtogroup") boolean addToGroup, @FormParam("dalgroup") String groupId,
            @FormParam("gen_default_dbset") boolean isGenDefault) throws Exception {
        try {
            Status status = Status.OK();
            DalGroupDBDao allDbDao = BeanGetter.getDaoOfDalGroupDB();

            if (allDbDao.getGroupDBByDbName(allinonename) != null) {
                status = Status.ERROR();
                status.setInfo(allinonename + "已经存在!");
                return status;
            } else {
                DalGroupDB groupDb = new DalGroupDB();
                groupDb.setDbname(allinonename);
                groupDb.setDb_address(dbaddress);
                groupDb.setDb_port(dbport);
                groupDb.setDb_user(dbuser);
                groupDb.setDb_password(dbpassword);
                groupDb.setDb_catalog(dbcatalog);
                groupDb.setDb_providerName(DatabaseType.valueOf(dbtype).getValue());
                groupDb.setDal_group_id(-1);

                // add to current user's group
                if (addToGroup) {
                    int gid = -1;
                    if (groupId != null && !groupId.isEmpty()) {
                        gid = Integer.parseInt(groupId);
                        groupDb.setDal_group_id(gid);
                    } else {
                        LoginUser user = RequestUtil.getUserInfo(request);
                        if (user != null) {
                            int userId = user.getId();
                            List<UserGroup> list = BeanGetter.getDalUserGroupDao().getUserGroupByUserId(userId);
                            if (list != null && list.size() > 0) {
                                gid = list.get(0).getGroup_id();
                                groupDb.setDal_group_id(gid);
                            }
                        }
                    }

                    // generate default databaseset
                    if (isGenDefault) {
                        status = DalGroupDbResource.genDefaultDbset(gid, allinonename, dbtype);
                    }
                }

                allDbDao.insertDalGroupDB(groupDb);
            }

            return Status.OK();
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    private boolean validatePermision(int userId, int db_group_id) throws SQLException {
        boolean havaPermision = false;
        List<UserGroup> urGroups = BeanGetter.getDalUserGroupDao().getUserGroupByUserId(userId);
        if (urGroups != null && urGroups.size() > 0) {
            for (UserGroup urGroup : urGroups) {
                if (urGroup.getGroup_id() == DalGroupResource.SUPER_GROUP_ID) {
                    havaPermision = true;
                }
                if (urGroup.getGroup_id() == db_group_id) {
                    havaPermision = true;
                }
            }
        }
        return havaPermision;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("deleteAllInOneDB")
    public Status deleteAllInOneDB(@Context HttpServletRequest request, @FormParam("allinonename") String allinonename)
            throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);
            Status status = Status.OK();

            DalGroupDBDao allDbDao = BeanGetter.getDaoOfDalGroupDB();
            DalGroupDB groupDb = allDbDao.getGroupDBByDbName(allinonename);
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);

            if (!validatePermision(user.getId(), groupDb.getDal_group_id())) {
                status = Status.ERROR();
                status.setInfo("你没有当前DataBase的操作权限.");
            } else {
                allDbDao.deleteDalGroupDB(groupDb.getId());
            }
            return status;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getOneDB")
    public Status getOneDB(@Context HttpServletRequest request, @FormParam("allinonename") String allinonename)
            throws Exception {
        try {
            String userNo = RequestUtil.getUserNo(request);
            Status status = Status.OK();

            DalGroupDBDao allDbDao = BeanGetter.getDaoOfDalGroupDB();
            DalGroupDB groupDb = allDbDao.getGroupDBByDbName(allinonename);
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);

            if (!validatePermision(user.getId(), groupDb.getDal_group_id())) {
                status = Status.ERROR();
                status.setInfo("你没有当前DataBase的操作权限.");
                return status;
            }

            try {
                if (DatabaseType.MySQL.getValue().equals(groupDb.getDb_providerName())) {
                    groupDb.setDb_providerName(DatabaseType.MySQL.toString());
                } else if (DatabaseType.SQLServer.getValue().equals(groupDb.getDb_providerName())) {
                    groupDb.setDb_providerName(DatabaseType.SQLServer.toString());
                } else {
                    groupDb.setDb_providerName("no");
                }
                status.setInfo(mapper.writeValueAsString(groupDb));
            } catch (JsonProcessingException e) {
                status = Status.ERROR();
                status.setInfo(e.getMessage());
                return status;
            }

            return Status.OK();
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updateDB")
    public Status updateDB(@Context HttpServletRequest request, @FormParam("id") int id,
            @FormParam("dbtype") String dbtype, @FormParam("allinonename") String allinonename,
            @FormParam("dbaddress") String dbaddress, @FormParam("dbport") String dbport,
            @FormParam("dbuser") String dbuser, @FormParam("dbpassword") String dbpassword,
            @FormParam("dbcatalog") String dbcatalog) throws Exception {
        try {
            Status status = Status.OK();
            DalGroupDBDao allDbDao = BeanGetter.getDaoOfDalGroupDB();
            DalGroupDB db = allDbDao.getGroupDBByDbName(allinonename);

            if (db != null && db.getId() != id) {
                status = Status.ERROR();
                status.setInfo(allinonename + "已经存在!");
                return status;
            }

            String userNo = RequestUtil.getUserNo(request);
            LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            DalGroupDB groupDb = allDbDao.getGroupDBByDbId(id);

            if (!validatePermision(user.getId(), groupDb.getDal_group_id())) {
                status = Status.ERROR();
                status.setInfo("你没有当前DataBase的操作权限.");
                return status;
            }

            allDbDao.updateGroupDB(id, allinonename, dbaddress, dbport, dbuser, dbpassword, dbcatalog,
                    DatabaseType.valueOf(dbtype).getValue());
            return Status.OK();
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("dbs")
    public String getDbNames(@QueryParam("groupDBs") boolean groupDBs, @QueryParam("groupId") int groupId)
            throws SQLException {
        try {
            if (groupDBs) {
                if (-1 != groupId && groupId > 0) {
                    Set<String> sets = new HashSet<>();
                    List<DalGroupDB> dbs = BeanGetter.getDaoOfDalGroupDB().getGroupDBsByGroup(groupId);
                    for (DalGroupDB db : dbs) {
                        sets.add(db.getDbname());
                    }
                    try {
                        return mapper.writeValueAsString(sets);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    List<String> dbAllinOneNames = BeanGetter.getDaoOfDalGroupDB().getAllDbAllinOneNames();
                    return mapper.writeValueAsString(dbAllinOneNames);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            return null;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("tables")
    public String getTableNames(@QueryParam("db_name") String db_set) throws Exception {
        try {
            DatabaseSetEntry databaseSetEntry =
                    BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(db_set);
            String dbName = databaseSetEntry.getConnectionString();
            List<String> results = DbUtils.getAllTableNames(dbName);
            java.util.Collections.sort(results);
            return mapper.writeValueAsString(results);
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("fields")
    public List<ColumnMetaData> getFieldNames(@QueryParam("db_name") String dbName,
            @QueryParam("table_name") String tableName) throws Exception {
        List<ColumnMetaData> fields = new ArrayList<>();
        Connection connection = null;
        try {
            DatabaseSetEntry databaseSetEntry =
                    BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(dbName);
            String db_Name = databaseSetEntry.getConnectionString();
            connection = DataSourceUtil.getConnection(db_Name);
            Set<String> indexedColumns = new HashSet<>();
            Set<String> primaryKeys = new HashSet<>();
            Set<String> allColumns = new HashSet<>();

            // 获取所有主键
            ResultSet primaryKeyRs = null;
            try {
                primaryKeyRs = connection.getMetaData().getPrimaryKeys(null, null, tableName);

                while (primaryKeyRs.next()) {
                    primaryKeys.add(primaryKeyRs.getString("COLUMN_NAME"));
                }
            } catch (SQLException e) {
                LoggerManager.getInstance().error(e);
            } finally {
                ResourceUtils.close(primaryKeyRs);
            }

            // 获取所有列
            ResultSet allColumnsRs = null;
            try {
                allColumnsRs = connection.getMetaData().getColumns(null, null, tableName, null);
                while (allColumnsRs.next()) {
                    allColumns.add(allColumnsRs.getString("COLUMN_NAME"));
                }
            } catch (SQLException e) {
                LoggerManager.getInstance().error(e);
            } finally {
                ResourceUtils.close(allColumnsRs);
            }

            // 获取所有索引信息
            ResultSet indexColumnsRs = null;

            try {
                indexColumnsRs = connection.getMetaData().getIndexInfo(null, null, tableName, false, false);
                while (indexColumnsRs.next()) {
                    String column = indexColumnsRs.getString("COLUMN_NAME");
                    if (column != null) {
                        indexedColumns.add(column);
                    }
                }
            } catch (SQLException e) {
                LoggerManager.getInstance().error(e);
            } finally {
                ResourceUtils.close(indexColumnsRs);
            }

            for (String str : allColumns) {
                ColumnMetaData field = new ColumnMetaData();

                field.setName(str);
                field.setIndexed(indexedColumns.contains(str));
                field.setPrimary(primaryKeys.contains(str));
                fields.add(field);
            }
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        } finally {
            ResourceUtils.close(connection);
        }

        return fields;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("table_sps")
    public Status getTableSPNames(@QueryParam("db_name") String setName) {
        Status status = Status.OK();
        TableSpNames tableSpNames = new TableSpNames();
        List<String> views;
        List<String> tables;
        List<StoredProcedure> sps;
        try {
            DatabaseSetEntry databaseSetEntry =
                    BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(setName);
            String dbName = databaseSetEntry.getConnectionString();
            views = DbUtils.getAllViewNames(dbName);
            tables = DbUtils.getAllTableNames(dbName);
            sps = DbUtils.getAllSpNames(dbName);
            sps = filterSP(tables, sps);

            java.util.Collections.sort(views, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.toLowerCase().compareTo(o2.toLowerCase());
                }
            });
            java.util.Collections.sort(tables, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.toLowerCase().compareTo(o2.toLowerCase());
                }
            });
            java.util.Collections.sort(sps);

            tableSpNames.setSps(sps);
            tableSpNames.setViews(views);
            tableSpNames.setTables(tables);
            tableSpNames.setDbType(DbUtils.getDbType(dbName));

            status.setInfo(mapper.writeValueAsString(tableSpNames));
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
        return status;
    }

    private List<StoredProcedure> filterSP(List<String> tables, List<StoredProcedure> sps) {
        List<StoredProcedure> result = new ArrayList<>();
        if (tables != null && sps != null && tables.size() > 0 && sps.size() > 0) {
            for (StoredProcedure sp : sps) {
                String spName = sp.getName() != null ? sp.getName().toLowerCase() : null;
                boolean isSpAOrSp3orSpT = false;
                for (String tableName : tables) {
                    tableName = tableName.toLowerCase();
                    if (spName == null || "".equals(spName)) {
                        isSpAOrSp3orSpT = true;
                        break;
                    }
                    if (spName.indexOf(String.format("spa_%s", tableName)) > -1
                            || spName.indexOf(String.format("sp3_%s", tableName)) > -1
                            || spName.indexOf(String.format("spt_%s", tableName)) > -1) {
                        isSpAOrSp3orSpT = true;
                        break;
                    }
                }
                if (!isSpAOrSp3orSpT) {
                    result.add(sp);
                }
            }
        }
        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("validation")
    public Status validationKey(@QueryParam("key") String key) throws Exception {
        try {
            Status status = Status.ERROR();
            Response res = WebUtil.getAllInOneResponse(key, null);
            String httpCode = res.getStatus();
            if (!httpCode.equals(WebUtil.HTTP_CODE)) {
                status.setInfo("Access error.");
                return status;
            }

            status = Status.OK();
            ResponseData[] data = res.getData();
            if (data != null && data.length > 0) {
                String error = data[0].getErrorMessage();
                if (error != null && !error.isEmpty()) {
                    status.setInfo(error);
                } else {
                    status.setInfo("");
                }
            }

            return status;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }
}
