package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.sql.validate.SQLValidation;
import com.ctrip.platform.dal.daogen.sql.validate.ValidateResult;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.SqlBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 复杂查询（额外生成实体类）
 *
 * @author gzxia
 * @modified yn.wang
 */

@Resource
@Singleton
@Path("task/sql")
public class GenTaskByFreeSqlResource extends ApproveResource {
    private static ObjectMapper mapper = new ObjectMapper();

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Status addTask(@Context HttpServletRequest request, @FormParam("id") int id,
            @FormParam("project_id") int project_id, @FormParam("db_name") String set_name,
            @FormParam("class_name") String class_name, @FormParam("pojo_name") String pojo_name,
            @FormParam("method_name") String method_name, @FormParam("crud_type") String crud_type,
            @FormParam("sql_content") String sql_content, @FormParam("params") String params,
            @FormParam("version") int version, @FormParam("action") String action, @FormParam("comment") String comment,
            @FormParam("scalarType") String scalarType, @FormParam("pagination") boolean pagination,
            @FormParam("length") boolean length, @FormParam("sql_style") String sql_style, // C#风格或者Java风格
            @FormParam("hints") String hints) throws Exception {
        try {
            GenTaskByFreeSql task = new GenTaskByFreeSql();

            if (action.equalsIgnoreCase("delete")) {
                task.setId(id);
                if (0 >= BeanGetter.getDaoByFreeSql().deleteTask(task)) {
                    return Status.ERROR();
                }
            } else {
                String userNo = RequestUtil.getUserNo(request);
                LoginUser user = BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);

                task.setProject_id(project_id);
                task.setDatabaseSetName(set_name);
                task.setClass_name(class_name);
                task.setPojo_name(pojo_name);
                task.setMethod_name(method_name);
                task.setCrud_type(crud_type);
                task.setSql_content(sql_content);
                task.setParameters(params);
                task.setUpdate_user_no(user.getUserName() + "(" + userNo + ")");
                task.setUpdate_time(new Timestamp(System.currentTimeMillis()));
                task.setComment(comment);
                task.setScalarType(scalarType);
                task.setPagination(pagination);
                task.setLength(length);
                task.setSql_style(sql_style);

                if ("简单类型".equals(pojo_name)) {
                    task.setPojoType("SimpleType");
                } else {
                    task.setPojoType("EntityType");
                }
                if (needApproveTask(project_id, user.getId())) {
                    task.setApproved(1);
                } else {
                    task.setApproved(2);
                }

                task.setApproveMsg("");
                task.setHints(hints);

                if (action.equalsIgnoreCase("update")) {
                    task.setId(id);
                    task.setVersion(BeanGetter.getDaoByFreeSql().getVersionById(id));
                    if (0 >= BeanGetter.getDaoByFreeSql().updateTask(task)) {
                        Status status = Status.ERROR();
                        status.setInfo("更新出错，数据是否合法？或者已经有同名方法？");
                        return status;
                    }
                } else {
                    task.setGenerated(false);
                    task.setVersion(1);
                    if (0 >= BeanGetter.getDaoByFreeSql().insertTask(task)) {
                        Status status = Status.ERROR();
                        status.setInfo("新增出错，数据是否合法？或者已经有同名方法？");
                        return status;
                    }
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
    @Path("buildPagingSQL")
    public Status buildPagingSQL(@FormParam("db_name") String db_set_name, // dbset// name
            @FormParam("sql_style") String sql_style, // C#风格或者Java风格
            @FormParam("sql_content") String sql_content) {
        try {
            Status status = Status.OK();
            DatabaseSetEntry databaseSetEntry =
                    BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(db_set_name);
            CurrentLanguage lang = (sql_content.contains("@") || "csharp".equals(sql_style)) ? CurrentLanguage.CSharp
                    : CurrentLanguage.Java;
            String pagingSQL = SqlBuilder.pagingQuerySql(sql_content,
                    DbUtils.getDatabaseCategory(databaseSetEntry.getConnectionString()), lang);
            status.setInfo(pagingSQL);
            return status;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    @POST
    @Path("getMockValue")
    public Status getMockValue(@FormParam("params") String params) {
        try {
            Status status = Status.OK();
            int[] sqlTypes = getSqlTypes(params);
            Object[] values = SQLValidation.mockStringValues(sqlTypes);
            try {
                status.setInfo(mapper.writeValueAsString(values));
            } catch (JsonProcessingException e) {
                status = Status.ERROR();
                status.setInfo("获取mock value异常.");
            }
            return status;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            Status status = Status.ERROR();
            status.setInfo(e.getMessage());
            return status;
        }
    }

    private int[] getSqlTypes(String params) {
        if (params == null || params.isEmpty()) {
            return new int[0];
        }
        String[] parameters = params.split(";");
        int[] sqlTypes = new int[parameters.length];
        int i = 0;
        for (String param : parameters) {
            if (param != null && !param.isEmpty()) {
                sqlTypes[i++] = Integer.valueOf(param.split(",")[1]);
            }
        }
        return sqlTypes;
    }

    private int[] getTypes(List<Parameter> list) {
        if (list == null || list.size() == 0) {
            return new int[0];
        }

        int[] array = new int[list.size()];
        int index = 0;
        for (Parameter p : list) {
            array[index] = p.getType();
            index++;
        }

        return array;
    }

    private String[] getValues(List<Parameter> list) {
        if (list == null || list.size() == 0) {
            return new String[0];
        }

        String[] array = new String[list.size()];
        int index = 0;
        for (Parameter p : list) {
            array[index] = p.getValue();
            index++;
        }

        return array;
    }

    private static final String expression = "[@:]\\w+";
    private static final Pattern pattern = Pattern.compile(expression);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("sqlValidate")
    public Status validateSQL(@FormParam("db_name") String set_name, @FormParam("crud_type") String crud_type,
            @FormParam("sql_content") String sql_content, @FormParam("params") String params,
            @FormParam("pagination") boolean pagination, @FormParam("mockValues") String mockValues) {
        Status status = Status.OK();

        try {
            Map<String, Parameter> map = new LinkedHashMap<>();
            List<Parameter> list = new ArrayList<>();
            Matcher matcher = pattern.matcher(sql_content);
            while (matcher.find()) {
                String parameter = matcher.group();
                Parameter p = new Parameter();
                p.setName(parameter.substring(1)); // trim @
                list.add(p);
            }

            String[] values = mockValues.split(";");
            String[] parameters = params.split(";");
            if (parameters != null && parameters.length > 0) {
                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i].isEmpty()) {
                        continue;
                    }
                    String[] array = parameters[i].split(",");
                    if (array != null && array.length > 0) {
                        String name = array[0];
                        if (name.isEmpty()) {
                            continue;
                        }
                        int type = Integer.valueOf(array[1]);
                        if (!map.containsKey(name)) {
                            Parameter p = new Parameter();
                            p.setType(type);
                            p.setValue(values[i]);
                            map.put(name, p);
                        }
                    }
                }
            }

            if (list.size() > 0) {
                for (Parameter p : list) {
                    String name = p.getName();
                    Parameter temp = map.get(name);
                    if (temp != null) {
                        p.setType(temp.getType());
                        p.setValue(temp.getValue());
                    }
                }
            } else {
                for (Map.Entry<String, Parameter> entry : map.entrySet()) {
                    Parameter p = new Parameter();
                    Parameter temp = entry.getValue();
                    p.setType(temp.getType());
                    p.setValue(temp.getValue());
                    list.add(p);
                }
            }

            sql_content = sql_content.replaceAll(expression, "?");
            int[] sqlTypes = getTypes(list);
            values = getValues(list);

            DatabaseSetEntry databaseSetEntry =
                    BeanGetter.getDaoOfDatabaseSet().getMasterDatabaseSetEntryByDatabaseSetName(set_name);
            String dbName = databaseSetEntry.getConnectionString();

            ValidateResult validResult = null;
            String resultPrefix = "The affected rows is ";
            if (pagination && "select".equalsIgnoreCase(crud_type)) {
                sql_content = SqlBuilder.pagingQuerySql(sql_content, DbUtils.getDatabaseCategory(dbName),
                        CurrentLanguage.Java);
                sql_content = String.format(sql_content, 1, 2);
            }

            if ("select".equalsIgnoreCase(crud_type)) {
                validResult = SQLValidation.queryValidate(dbName, sql_content, sqlTypes, values);
                resultPrefix = "The result count is ";
            } else {
                validResult = SQLValidation.updateValidate(dbName, sql_content, sqlTypes, values);
            }

            if (validResult != null && validResult.isPassed()) {
                status.setInfo(resultPrefix + validResult.getAffectRows());
                status.setExplanJson(validResult.getMessage());
            } else {
                status = Status.ERROR();
                status.setInfo(validResult.getMessage());
            }
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getMessage());
        }

        return status;
    }

}
