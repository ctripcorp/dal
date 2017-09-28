package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.java.JavaColumnNameResultSetExtractor;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.sql.validate.SQLValidation;
import com.ctrip.platform.dal.daogen.sql.validate.ValidateResult;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.SqlBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 构建SQL（生成的代码绑定到模板）
 *
 * @author gzxia
 * @modified yn.wang
 */

@Resource
@Singleton
@Path("task/auto")
public class GenTaskBySqlBuilderResource extends ApproveResource {
    private static ObjectMapper mapper = new ObjectMapper();

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Status addTask(@Context HttpServletRequest request, @FormParam("id") int id,
            @FormParam("project_id") int project_id, @FormParam("db_name") String set_name,
            @FormParam("table_name") String table_name, @FormParam("method_name") String method_name,
            @FormParam("crud_type") String crud_type, @FormParam("fields") String fields,
            @FormParam("condition") String condition, @FormParam("sql_content") String sql_content,
            @FormParam("version") int version, @FormParam("action") String action, @FormParam("params") String params,
            @FormParam("comment") String comment, @FormParam("scalarType") String scalarType,
            @FormParam("pagination") boolean pagination, @FormParam("length") boolean length,
            @FormParam("orderby") String orderby, @FormParam("hints") String hints,
            @FormParam("sql_style") String sql_style // C#风格或者Java风格
    ) throws Exception {
        try {
            Status status = Status.OK();
            GenTaskBySqlBuilder task = new GenTaskBySqlBuilder();

            if (action.equalsIgnoreCase("delete")) {
                task.setId(id);
                if (0 >= BeanGetter.getDaoBySqlBuilder().deleteTask(task)) {
                    return Status.ERROR();
                }
            } else {
                String userNo = RequestUtil.getUserNo(request);
                LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);

                task.setProject_id(project_id);
                task.setDatabaseSetName(set_name);
                task.setTable_name(table_name);
                task.setMethod_name(method_name);
                task.setSql_style(sql_style);
                task.setCrud_type(crud_type);
                task.setFields(fields);
                task.setCondition(condition);
                task.setUpdate_user_no(user.getUserName() + "(" + userNo + ")");
                task.setUpdate_time(new Timestamp(System.currentTimeMillis()));
                task.setComment(comment);
                task.setScalarType(scalarType);
                task.setPagination(pagination);
                task.setOrderby(orderby);
                task.setHints(hints);
                task.setLength(length);

                if (needApproveTask(project_id, user.getId())) {
                    task.setApproved(1);
                } else {
                    task.setApproved(2);
                }
                task.setApproveMsg("");

                if (action.equalsIgnoreCase("update")) {
                    task.setId(id);
                    task.setVersion(BeanGetter.getDaoBySqlBuilder().getVersionById(id));
                    task.setSql_content(sql_content);
                    if (0 >= BeanGetter.getDaoBySqlBuilder().updateTask(task)) {
                        status = Status.ERROR();
                        status.setInfo("更新出错，数据是否合法？或者已经有同名方法？");
                        return status;
                    }
                } else {
                    task.setGenerated(false);
                    task.setVersion(1);
                    task.setSql_content(sql_content);
                    if (0 >= BeanGetter.getDaoBySqlBuilder().insertTask(task)) {
                        status = Status.ERROR();
                        status.setInfo("新增出错，数据是否合法？或者已经有同名方法？");
                        return status;
                    }
                }
            }

            return status;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getCause().getMessage());
            return status;
        }
    }

    @POST
    @Path("buildPagingSQL")
    public Status buildPagingSQL(@FormParam("db_name") String db_set_name, // dbset
            // name
            @FormParam("sql_style") String sql_style, // C#风格或者Java风格
            @FormParam("sql_content") String sql_content) {
        Status status = Status.OK();
        try {
            DatabaseSetEntry databaseSetEntry =
                    BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(db_set_name);
            CurrentLanguage lang = "java".equals(sql_style) ? CurrentLanguage.Java : CurrentLanguage.CSharp;
            String pagingSQL = SqlBuilder.pagingQuerySql(sql_content,
                    DbUtils.getDatabaseCategory(databaseSetEntry.getConnectionString()), lang);
            status.setInfo(pagingSQL);
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getCause().getMessage());
            return status;
        }

        return status;
    }

    @POST
    @Path("getDatabaseCategory")
    public Status getDatabaseCategory(@FormParam("db_set_name") String db_set_name) throws SQLException {
        Status status = Status.OK();
        DatabaseSetEntry databaseSetEntry =
                BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(db_set_name);
        try {
            DatabaseCategory category = DbUtils.getDatabaseCategory(databaseSetEntry.getConnectionString());
            if (DatabaseCategory.MySql == category) {
                status.setInfo("MySql");
            } else {
                status.setInfo("SqlServer");
            }
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getCause().getMessage());
            return status;
        }
        return status;
    }

    @POST
    @Path("getMockValue")
    public Status getMockValue(@FormParam("db_name") String set_name, @FormParam("table_name") String table_name,
            @FormParam("crud_type") String crud_type, @FormParam("fields") String fields,
            @FormParam("condition") String condition, @FormParam("pagination") boolean pagination) throws Exception {
        Status status = Status.OK();
        int[] sqlTypes = getSqlTypes(set_name, table_name, crud_type, fields, condition);
        Object[] values = SQLValidation.mockStringValues(sqlTypes);
        try {
            status.setInfo(mapper.writeValueAsString(values));
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getCause().getMessage());
            return status;
        }
        return status;
    }

    private int[] getSqlTypes(String set_name, String table_name, String crud_type, String fields, String condition)
            throws Exception {
        if ("select".equalsIgnoreCase(crud_type)) {
            return getSelectSqlTypes(set_name, table_name, condition);
        } else if ("insert".equalsIgnoreCase(crud_type)) {
            return getInsertSqlTypes(set_name, table_name, fields);
        } else if ("update".equalsIgnoreCase(crud_type)) {
            return getUpdateSqlTypes(set_name, table_name, fields, condition);
        } else if ("delete".equalsIgnoreCase(crud_type)) {
            return getDeleteSqlTypes(set_name, table_name, condition);
        }
        return new int[0];
    }

    private int[] getDeleteSqlTypes(String set_name, String table_name, String condition) throws Exception {
        return getWhereConditionSqlTypes(set_name, table_name, condition);
    }

    private int[] getUpdateSqlTypes(String set_name, String table_name, String fields, String condition)
            throws Exception {
        int[] fieldSqlTypes = getFieldSqlTypes(set_name, table_name, fields);
        int[] whereConditionSqlTypes = getWhereConditionSqlTypes(set_name, table_name, condition);
        int[] mergeSqlTypes = new int[fieldSqlTypes.length + whereConditionSqlTypes.length];
        for (int i = 0; i < fieldSqlTypes.length; i++) {
            mergeSqlTypes[i] = fieldSqlTypes[i];
        }
        for (int i = fieldSqlTypes.length; i < fieldSqlTypes.length + whereConditionSqlTypes.length; i++) {
            mergeSqlTypes[i] = whereConditionSqlTypes[i - fieldSqlTypes.length];
        }
        return mergeSqlTypes;
    }

    private int[] getInsertSqlTypes(String set_name, String table_name, String fields) throws Exception {
        return getFieldSqlTypes(set_name, table_name, fields);
    }

    private int[] getSelectSqlTypes(String set_name, String table_name, String condition) throws Exception {
        return getWhereConditionSqlTypes(set_name, table_name, condition);
    }

    private int[] getFieldSqlTypes(String set_name, String table_name, String fields) throws Exception {
        if (fields == null || "".equals(fields)) {
            return new int[0];
        }
        Map<String, Integer> tableColumnSqlType = getTableColumnSqlType(set_name, table_name);
        String[] insertFields = fields.split(",");
        int[] insertFieldSqlTypes = new int[insertFields.length];
        int index = 0;
        for (String field : insertFields) {
            insertFieldSqlTypes[index++] = tableColumnSqlType.get(field.toLowerCase());
        }
        return insertFieldSqlTypes;
    }

    private int[] getWhereConditionSqlTypes(String set_name, String table_name, String condition) throws Exception {
        int whereConditionCount = 0;
        if (condition != null && !condition.isEmpty()) {
            String[] whereConditions = condition.split(";");
            for (String whereCondition : whereConditions) {
                if ("6".equals(whereCondition.split(",")[1])) {
                    whereConditionCount = whereConditionCount + 2;
                } else {
                    whereConditionCount++;
                }
            }
        }
        if (whereConditionCount < 1) {
            return new int[0];
        }
        int[] whereConditionSqlTypes = new int[whereConditionCount];
        Map<String, Integer> tableColumnSqlType = getTableColumnSqlType(set_name, table_name);
        String[] whereConditions = condition.split(";");
        int index = 0;
        for (String whereCondition : whereConditions) {
            String columnName = whereCondition.split(",")[0].toLowerCase();
            if ("6".equals(whereCondition.split(",")[1])) {
                whereConditionSqlTypes[index++] = tableColumnSqlType.get(columnName);
                whereConditionSqlTypes[index++] = tableColumnSqlType.get(columnName);
            } else {
                whereConditionSqlTypes[index++] = tableColumnSqlType.get(columnName);
            }
        }
        return whereConditionSqlTypes;
    }

    /**
     * @param set_name
     * @param table_name
     * @return <column alias, sqltype>
     */
    private Map<String, Integer> getTableColumnSqlType(String set_name, String table_name) throws Exception {
        DatabaseSetEntry databaseSetEntry =
                BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(set_name);
        String dbName = databaseSetEntry.getConnectionString();
        DatabaseCategory dbCategory = DbUtils.getDatabaseCategory(dbName);
        List<AbstractParameterHost> paramsHost = DbUtils.getAllColumnNames(dbName, table_name,
                new JavaColumnNameResultSetExtractor(dbName, table_name, dbCategory));
        Map<String, Integer> map = new HashMap<>();
        if (paramsHost != null) {
            for (int i = 0; i < paramsHost.size(); i++) {
                JavaParameterHost paramHost = (JavaParameterHost) paramsHost.get(i);
                map.put(paramHost.getAlias().toLowerCase(), paramHost.getSqlType());
            }
        }
        return map;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("sqlValidate")
    public Status validateSQL(@FormParam("db_name") String set_name, @FormParam("table_name") String table_name,
            @FormParam("crud_type") String crud_type, @FormParam("fields") String fields,
            @FormParam("condition") String condition, @FormParam("sql_content") String sql_content,
            @FormParam("pagination") boolean pagination, @FormParam("mockValues") String mockValues) throws Exception {
        Status status = Status.OK();
        sql_content = sql_content.replaceAll("[@:]\\w+", "?");
        int[] sqlTypes = getSqlTypes(set_name, table_name, crud_type, fields, condition);
        String[] vals = mockValues.split(";");

        DatabaseSetEntry databaseSetEntry =
                BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(set_name);
        String dbName = databaseSetEntry.getConnectionString();

        ValidateResult validResult = null;
        String resultPrefix = "The affected rows is ";
        try {
            if (pagination && "select".equalsIgnoreCase(crud_type)) {
                sql_content = SqlBuilder.pagingQuerySql(sql_content, DbUtils.getDatabaseCategory(dbName),
                        CurrentLanguage.Java);
                sql_content = String.format(sql_content, 1, 2);
            }
            if ("select".equalsIgnoreCase(crud_type)) {
                validResult = SQLValidation.queryValidate(dbName, sql_content, sqlTypes, vals);
                resultPrefix = "The result count is ";
            } else {
                validResult = SQLValidation.updateValidate(dbName, sql_content, sqlTypes, vals);
            }
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getCause().getMessage());
            return status;
        }

        if (validResult != null && validResult.isPassed()) {
            status.setInfo(resultPrefix + validResult.getAffectRows());
            status.setExplanJson(validResult.getMessage());
        } else {
            status = Status.ERROR();
            status.setInfo(validResult.getMessage());
        }
        return status;
    }

}
