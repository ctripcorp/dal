package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.dao.DaoOfDatabaseSet;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.ConditionType;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.host.java.*;
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.sql.SQLException;
import java.util.*;

public class AbstractJavaDataPreparer {
    protected void addDatabaseSet(CodeGenContext codeGenCtx, String databaseSetName) throws SQLException {
        DaoOfDatabaseSet daoOfDatabaseSet = BeanGetter.getDaoOfDatabaseSet();
        List<DatabaseSet> sets = daoOfDatabaseSet.getAllDatabaseSetByName(databaseSetName);
        if (null == sets || sets.isEmpty()) {
            return;
        }
        JavaCodeGenContext ctx = (JavaCodeGenContext) codeGenCtx;
        ContextHost contextHost = ctx.getContextHost();
        DalConfigHost dalConfigHost = ctx.getDalConfigHost();
        dalConfigHost.addDatabaseSet(sets);
        for (DatabaseSet databaseSet : sets) {
            List<DatabaseSetEntry> entries = daoOfDatabaseSet.getAllDatabaseSetEntryByDbsetid(databaseSet.getId());
            if (entries == null || entries.isEmpty()) {
                continue;
            }
            dalConfigHost.addDatabaseSetEntry(entries);
            Map<String, DatabaseSetEntry> map = dalConfigHost.getDatabaseSetEntryMap();

            for (DatabaseSetEntry entry : entries) {
                String key = entry.getConnectionString();
                if (map.containsKey(key)) {
                    DatabaseSetEntry value = map.get(key);
                    Resource resource = new Resource(value.getConnectionString(), value.getUserName(),
                            value.getPassword(), value.getDbAddress(), value.getDbPort(), value.getDbCatalog(),
                            value.getProviderName());
                    contextHost.addResource(resource);
                }
            }
        }
    }

    protected JavaTableHost buildTableHost(CodeGenContext context, GenTaskByTableViewSp tableViewSp, String tableName,
            DatabaseCategory dbCategory) throws Exception {
        JavaCodeGenContext ctx = (JavaCodeGenContext) context;
        if (!DbUtils.tableExists(tableViewSp.getAllInOneName(), tableName)) {
            throw new Exception(String.format("Table[%s.%s] doesn't exist.", tableViewSp.getAllInOneName(), tableName));
        }
        JavaTableHost tableHost = new JavaTableHost();
        tableHost.setPackageName(ctx.getNamespace());
        tableHost.setDatabaseCategory(getDatabaseCategory(tableViewSp.getAllInOneName()));
        tableHost.setDbSetName(tableViewSp.getDatabaseSetName());
        tableHost.setTableName(tableName);
        tableHost.setPojoClassName(getPojoClassName(tableViewSp.getPrefix(), tableViewSp.getSuffix(), tableName));
        tableHost.setSp(tableViewSp.getCud_by_sp());
        tableHost.setApi_list(tableViewSp.getApi_list());
        tableHost.setLength(tableViewSp.getLength());

        // 主键及所有列
        List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(tableViewSp.getAllInOneName(), tableName);
        List<AbstractParameterHost> allColumnsAbstract = DbUtils.getAllColumnNames(tableViewSp.getAllInOneName(),
                tableName, new JavaColumnNameResultSetExtractor(tableViewSp.getAllInOneName(), tableName, dbCategory));
        if (null == allColumnsAbstract) {
            throw new Exception(String.format("The column names of table[%s, %s] is null",
                    tableViewSp.getAllInOneName(), tableName));
        }
        List<JavaParameterHost> allColumns = new ArrayList<>();
        for (AbstractParameterHost h : allColumnsAbstract) {
            allColumns.add((JavaParameterHost) h);
        }

        List<JavaParameterHost> primaryKeys = new ArrayList<>();
        boolean hasIdentity = false;
        String identityColumnName = null;
        for (JavaParameterHost h : allColumns) {
            if (!hasIdentity && h.isIdentity()) {
                hasIdentity = true;
                identityColumnName = h.getName();
            }
            if (primaryKeyNames.contains(h.getName())) {
                h.setPrimary(true);
                primaryKeys.add(h);
            }
        }

        List<GenTaskBySqlBuilder> currentTableBuilders =
                filterExtraMethods(ctx, tableViewSp.getAllInOneName(), tableName);
        List<JavaMethodHost> methods = buildSqlBuilderMethodHost(allColumns, currentTableBuilders);

        tableHost.setFields(allColumns);
        tableHost.setPrimaryKeys(primaryKeys);
        tableHost.setHasIdentity(hasIdentity);
        tableHost.setIdentityColumnName(identityColumnName);
        tableHost.setMethods(methods);

        if (tableHost.isSp()) {
            tableHost.setSpInsert(getSpaOperation(tableViewSp.getAllInOneName(), tableName, "i"));
            tableHost.setSpUpdate(getSpaOperation(tableViewSp.getAllInOneName(), tableName, "u"));
            tableHost.setSpDelete(getSpaOperation(tableViewSp.getAllInOneName(), tableName, "d"));
        }
        return tableHost;
    }

    protected DatabaseCategory getDatabaseCategory(String allInOneName) throws Exception {
        DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
        String dbType = DbUtils.getDbType(allInOneName);
        if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
            dbCategory = DatabaseCategory.MySql;
        }
        return dbCategory;
    }

    protected String getPojoClassName(String prefix, String suffix, String tableName) {
        String className = tableName;
        if (null != prefix && !prefix.isEmpty() && className.indexOf(prefix) == 0) {
            className = className.replaceFirst(prefix, "");
        }
        if (null != suffix && !suffix.isEmpty()) {
            className = className + WordUtils.capitalize(suffix);
        }

        StringBuilder result = new StringBuilder();
        for (String str : StringUtils.split(className, "_")) {
            result.append(WordUtils.capitalize(str));
        }

        return WordUtils.capitalize(result.toString());
    }

    private List<GenTaskBySqlBuilder> filterExtraMethods(CodeGenContext codeGenCtx, String allInOneName,
            String tableName) {
        JavaCodeGenContext ctx = (JavaCodeGenContext) codeGenCtx;
        List<GenTaskBySqlBuilder> currentTableBuilders = new ArrayList<>();
        Queue<GenTaskBySqlBuilder> sqlBuilders = ctx.getSqlBuilders();
        Iterator<GenTaskBySqlBuilder> iter = sqlBuilders.iterator();
        while (iter.hasNext()) {
            GenTaskBySqlBuilder currentSqlBuilder = iter.next();
            if (currentSqlBuilder.getAllInOneName().equals(allInOneName)
                    && currentSqlBuilder.getTable_name().equals(tableName)) {
                currentTableBuilders.add(currentSqlBuilder);
                iter.remove();
            }
        }

        return currentTableBuilders;
    }

    private SpOperationHost getSpaOperation(String dbName, String tableName, String operation) throws Exception {
        List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(dbName);
        return SpOperationHost.getSpaOperation(dbName, tableName, allSpNames, operation);
    }

    private List<JavaMethodHost> buildSqlBuilderMethodHost(List<JavaParameterHost> allColumns,
            List<GenTaskBySqlBuilder> currentTableSqlBuilders) throws Exception {
        List<JavaMethodHost> methods = new ArrayList<>();
        methods.addAll(buildSelectMethodHosts(allColumns, currentTableSqlBuilders));
        methods.addAll(buildDeleteMethodHosts(allColumns, currentTableSqlBuilders));
        methods.addAll(buildInsertMethodHosts(allColumns, currentTableSqlBuilders));
        methods.addAll(buildUpdateMethodHosts(allColumns, currentTableSqlBuilders));
        return methods;
    }

    private String buildSelectFieldExp(GenTaskBySqlBuilder sqlBuilder) throws Exception {
        String fieldStr = sqlBuilder.getFields();

        if ("*".equalsIgnoreCase(fieldStr)) {
            return fieldStr;
        }

        String[] fields = fieldStr.split(",");

        String[] result = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            String field = "\"" + fields[i] + "\"";
            result[i] = field;
        }
        return StringUtils.join(result, ",");
    }

    private List<JavaMethodHost> buildSelectMethodHosts(List<JavaParameterHost> allColumns,
            List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
        List<JavaMethodHost> methods = new ArrayList<>();

        for (GenTaskBySqlBuilder builder : currentTableBuilders) {
            if (!builder.getCrud_type().equals("select")) {
                continue;
            }
            JavaMethodHost method = new JavaMethodHost();
            method.setCrud_type(builder.getCrud_type());
            method.setName(builder.getMethod_name());
            method.setSql(builder.getSql_content());
            method.setScalarType(builder.getScalarType());
            method.setPaging(builder.getPagination());
            method.setComments(builder.getComment());
            method.setField(buildSelectFieldExp(builder));
            method.setTableName(builder.getTable_name());

            String orderBy = builder.getOrderby();
            if (orderBy != null && !orderBy.trim().isEmpty() && orderBy.indexOf("-1,") != 0) {
                String[] str = orderBy.split(",");
                String odyExp = "\"" + str[0] + "\", ";
                odyExp = "asc".equalsIgnoreCase(str[1]) ? odyExp + "true" : odyExp + "false";
                method.setOrderByExp(odyExp);
            }
            // select sql have select field and where condition clause
            List<AbstractParameterHost> paramAbstractHosts = DbUtils.getSelectFieldHosts(builder.getAllInOneName(),
                    builder.getSql_content(), new JavaSelectFieldResultSetExtractor());
            List<JavaParameterHost> paramHosts = new ArrayList<>();
            for (AbstractParameterHost phost : paramAbstractHosts) {
                paramHosts.add((JavaParameterHost) phost);
            }
            method.setFields(paramHosts);
            method.setParameters(buildMethodParameterHost4SqlConditin(builder, allColumns));
            method.setHints(builder.getHints());
            methods.add(method);
        }
        return methods;
    }

    private List<JavaMethodHost> buildDeleteMethodHosts(List<JavaParameterHost> allColumns,
            List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
        List<JavaMethodHost> methods = new ArrayList<>();

        for (GenTaskBySqlBuilder builder : currentTableBuilders) {
            if (!builder.getCrud_type().equals("delete")) {
                continue;
            }
            JavaMethodHost method = new JavaMethodHost();
            method.setCrud_type(builder.getCrud_type());
            method.setName(builder.getMethod_name());
            method.setSql(builder.getSql_content());
            method.setScalarType(builder.getScalarType());
            method.setPaging(builder.getPagination());
            method.setComments(builder.getComment());
            method.setTableName(builder.getTable_name());
            // Only have condition clause
            method.setParameters(buildMethodParameterHost4SqlConditin(builder, allColumns));
            method.setHints(builder.getHints());
            methods.add(method);
        }
        return methods;
    }

    private List<JavaMethodHost> buildInsertMethodHosts(List<JavaParameterHost> allColumns,
            List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
        List<JavaMethodHost> methods = new ArrayList<>();

        for (GenTaskBySqlBuilder builder : currentTableBuilders) {
            if (!builder.getCrud_type().equals("insert")) {
                continue;
            }
            JavaMethodHost method = new JavaMethodHost();
            method.setCrud_type(builder.getCrud_type());
            method.setName(builder.getMethod_name());
            method.setSql(builder.getSql_content());
            method.setScalarType(builder.getScalarType());
            method.setPaging(builder.getPagination());
            method.setComments(builder.getComment());
            method.setTableName(builder.getTable_name());
            List<JavaParameterHost> parameters = new ArrayList<>();

            // Have no where condition
            String[] fields = StringUtils.split(builder.getFields(), ",");
            Map<String, Boolean> sensitive = new HashMap<>();
            String conditions = builder.getCondition();
            if (conditions != null) {
                String[] temp = conditions.split(";");
                for (String field : temp) {
                    sensitive.put(field.split(",")[0], Boolean.parseBoolean(field.split(",")[4]));
                }
            }
            for (String field : fields) {
                for (JavaParameterHost pHost : allColumns) {
                    if (pHost.getName().equals(field)) {
                        pHost.setSensitive(sensitive.get(field) == null ? false : sensitive.get(field));
                        parameters.add(pHost);
                        break;
                    }
                }
            }

            method.setParameters(parameters);
            method.setHints(builder.getHints());
            methods.add(method);
        }
        return methods;
    }

    private List<JavaMethodHost> buildUpdateMethodHosts(List<JavaParameterHost> allColumns,
            List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
        List<JavaMethodHost> methods = new ArrayList<>();

        for (GenTaskBySqlBuilder builder : currentTableBuilders) {
            if (!builder.getCrud_type().equals("update")) {
                continue;
            }
            JavaMethodHost method = new JavaMethodHost();
            method.setCrud_type(builder.getCrud_type());
            method.setName(builder.getMethod_name());
            method.setSql(builder.getSql_content());
            method.setScalarType(builder.getScalarType());
            method.setPaging(builder.getPagination());
            method.setComments(builder.getComment());
            method.setField(buildSelectFieldExp(builder));
            method.setTableName(builder.getTable_name());
            List<JavaParameterHost> updateSetParameters = new ArrayList<>();
            // Have both set and condition clause
            String[] fields = StringUtils.split(builder.getFields(), ",");
            for (String field : fields) {
                for (JavaParameterHost pHost : allColumns) {
                    if (pHost.getName().equals(field)) {
                        JavaParameterHost host_ls = new JavaParameterHost(pHost);
                        updateSetParameters.add(host_ls);
                        break;
                    }
                }
            }
            method.setUpdateSetParameters(updateSetParameters);
            method.setParameters(buildMethodParameterHost4SqlConditin(builder, allColumns));
            method.setHints(builder.getHints());
            methods.add(method);
        }
        return methods;
    }

    private List<JavaParameterHost> buildMethodParameterHost4SqlConditin(GenTaskBySqlBuilder builder,
            List<JavaParameterHost> allColumns) {
        List<JavaParameterHost> parameters = new ArrayList<>();
        String[] conditions = StringUtils.split(builder.getCondition(), ";");
        for (String condition : conditions) {
            String[] tokens = StringUtils.split(condition, ",");
            if (tokens.length == 1) { //
                JavaParameterHost host = new JavaParameterHost();
                host.setConditionType(ConditionType.valueOf(Integer.parseInt(tokens[0])));
                host.setOperator(true);
                parameters.add(host);
                continue;
            }
            String name = tokens[0];
            int type = tokens.length >= 2 ? CommonUtils.tryParse(tokens[1], -1) : -1;
            String alias = tokens.length >= 3 ? tokens[2] : "";
            for (JavaParameterHost pHost : allColumns) {
                if (pHost.getName().equals(name)) {
                    JavaParameterHost host_ls = new JavaParameterHost(pHost);
                    host_ls.setAlias(alias);
                    host_ls.setConditional(true);
                    if (type != -1)
                        host_ls.setConditionType(ConditionType.valueOf(type));

                    parameters.add(host_ls);
                    // Between need an extra parameter
                    if (host_ls.getConditionType() == ConditionType.Between) {
                        JavaParameterHost host_bw = new JavaParameterHost(host_ls);
                        String alias_bw = tokens.length >= 4 ? tokens[3] : "";
                        host_bw.setAlias(alias_bw);
                        host_bw.setConditionType(ConditionType.Between);
                        parameters.add(host_bw);
                        boolean nullable = tokens.length >= 5 ? Boolean.valueOf(tokens[4]) : false;
                        host_ls.setNullable(nullable);
                        host_bw.setNullable(nullable);
                        boolean sensitive = tokens.length >= 6 ? Boolean.valueOf(tokens[5]) : false;
                        host_ls.setSensitive(sensitive);
                        host_bw.setSensitive(sensitive);
                    } else {
                        boolean nullable = tokens.length >= 4 ? Boolean.valueOf(tokens[3]) : false;
                        host_ls.setNullable(nullable);
                        boolean sensitive = tokens.length >= 5 ? Boolean.valueOf(tokens[4]) : false;
                        host_ls.setSensitive(sensitive);
                    }
                    break;
                }
            }
        }
        return parameters;
    }
}
