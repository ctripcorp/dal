package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.dao.DaoOfDatabaseSet;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.enums.ConditionType;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.host.csharp.*;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;
import com.ctrip.platform.dal.daogen.host.java.JavaSelectFieldResultSetExtractor;
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.SqlBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractCSharpDataPreparer {
    private static DaoOfDatabaseSet daoOfDatabaseSet;

    static {
        try {
            daoOfDatabaseSet = BeanGetter.getDaoOfDatabaseSet();
        } catch (SQLException e) {
        }
    }

    protected void addDatabaseSet(CodeGenContext codeGenCtx, String databaseSetName) throws SQLException {
        CSharpCodeGenContext ctx = (CSharpCodeGenContext) codeGenCtx;
        List<DatabaseSet> sets = daoOfDatabaseSet.getAllDatabaseSetByName(databaseSetName);
        if (null == sets || sets.isEmpty())
            return;

        DalConfigHost dalConfigHost = ctx.getDalConfigHost();
        dalConfigHost.addDatabaseSet(sets);
        for (DatabaseSet databaseSet : sets) {
            List<DatabaseSetEntry> entries = daoOfDatabaseSet.getAllDatabaseSetEntryByDbsetid(databaseSet.getId());
            if (null == entries || entries.isEmpty())
                continue;

            dalConfigHost.addDatabaseSetEntry(entries);
        }
    }

    protected CSharpTableHost buildTableHost(CodeGenContext context, GenTaskByTableViewSp tableViewSp, String table,
            DatabaseCategory dbCategory, List<StoredProcedure> allSpNames) throws Exception {
        CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;

        if (!DbUtils.tableExists(tableViewSp.getAllInOneName(), table))
            throw new Exception(String.format("表 %s 不存在，请编辑DAO再生成", table));

        // 主键及所有列
        List<AbstractParameterHost> allColumnsAbstract = DbUtils.getAllColumnNames(tableViewSp.getAllInOneName(), table,
                new CsharpColumnNameResultSetExtractor(tableViewSp.getAllInOneName(), table, dbCategory));
        List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(tableViewSp.getAllInOneName(), table);
        List<CSharpParameterHost> allColumns = new ArrayList<>();
        for (AbstractParameterHost h : allColumnsAbstract) {
            allColumns.add((CSharpParameterHost) h);
        }

        List<CSharpParameterHost> primaryKeys = new ArrayList<>();
        for (CSharpParameterHost h : allColumns) {
            if (primaryKeyNames.contains(h.getName())) {
                h.setPrimary(true);
                primaryKeys.add(h);
            }
        }

        Queue<GenTaskBySqlBuilder> _sqlBuilders = ctx.getSqlBuilders();
        List<GenTaskBySqlBuilder> currentTableBuilders =
                filterExtraMethods(_sqlBuilders, tableViewSp.getAllInOneName(), table);
        List<CSharpMethodHost> methods = buildSqlBuilderMethodHost(allColumns, currentTableBuilders);

        CSharpTableHost tableHost = new CSharpTableHost();
        tableHost.setExtraMethods(methods);
        tableHost.setNameSpace(ctx.getNamespace());
        tableHost.setDatabaseCategory(dbCategory);
        tableHost.setDbSetName(tableViewSp.getDatabaseSetName());
        tableHost.setTableName(table);
        tableHost.setClassName(CommonUtils
                .normalizeVariable(getPojoClassName(tableViewSp.getPrefix(), tableViewSp.getSuffix(), table)));
        tableHost.setTable(true);
        tableHost.setSpa(tableViewSp.getCud_by_sp());

        // SP方式增删改
        if (tableHost.isSpa()) {
            tableHost.setSpaInsert(
                    CSharpSpaOperationHost.getSpaOperation(tableViewSp.getAllInOneName(), table, allSpNames, "i"));
            tableHost.setSpaUpdate(
                    CSharpSpaOperationHost.getSpaOperation(tableViewSp.getAllInOneName(), table, allSpNames, "u"));
            tableHost.setSpaDelete(
                    CSharpSpaOperationHost.getSpaOperation(tableViewSp.getAllInOneName(), table, allSpNames, "d"));
        }

        tableHost.setPrimaryKeys(primaryKeys);
        tableHost.setColumns(allColumns);
        tableHost.setHasPagination(tableViewSp.getPagination());

        StoredProcedure expectSptI = new StoredProcedure();
        expectSptI.setName(String.format("spT_%s_i", table));

        StoredProcedure expectSptU = new StoredProcedure();
        expectSptU.setName(String.format("spT_%s_u", table));

        StoredProcedure expectSptD = new StoredProcedure();
        expectSptD.setName(String.format("spT_%s_d", table));

        tableHost.setHasSptI(allSpNames.contains(expectSptI));
        tableHost.setHasSptU(allSpNames.contains(expectSptU));
        tableHost.setHasSptD(allSpNames.contains(expectSptD));
        tableHost.setHasSpt(tableHost.isHasSptI() || tableHost.isHasSptU() || tableHost.isHasSptD());
        tableHost.setApi_list(tableViewSp.getApi_list());
        tableHost.setProjectName(ctx.getProjectName());
        return tableHost;
    }

    private List<GenTaskBySqlBuilder> filterExtraMethods(Queue<GenTaskBySqlBuilder> sqlBuilders, String dbName,
            String table) {
        List<GenTaskBySqlBuilder> currentTableBuilders = new ArrayList<>();

        Iterator<GenTaskBySqlBuilder> iter = sqlBuilders.iterator();
        while (iter.hasNext()) {
            GenTaskBySqlBuilder currentSqlBuilder = iter.next();
            if (currentSqlBuilder.getAllInOneName().equals(dbName) && currentSqlBuilder.getTable_name().equals(table)) {
                currentTableBuilders.add(currentSqlBuilder);
                iter.remove();
            }
        }

        return currentTableBuilders;
    }

    private List<CSharpMethodHost> buildSqlBuilderMethodHost(List<CSharpParameterHost> allColumns,
            List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
        List<CSharpMethodHost> methods = new ArrayList<>();
        methods.addAll(buildSelectMethodHosts(allColumns, currentTableBuilders));
        methods.addAll(buildDeleteMethodHosts(allColumns, currentTableBuilders));
        methods.addAll(buildInsertMethodHosts(allColumns, currentTableBuilders));
        methods.addAll(buildUpdateMethodHosts(allColumns, currentTableBuilders));
        return methods;
    }

    private List<CSharpMethodHost> buildSelectMethodHosts(List<CSharpParameterHost> allColumns,
            List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
        List<CSharpMethodHost> methods = new ArrayList<>();

        for (GenTaskBySqlBuilder builder : currentTableBuilders) {
            if (!builder.getCrud_type().equals("select")) {
                continue;
            }
            CSharpMethodHost method = new CSharpMethodHost();
            method.setCrud_type(builder.getCrud_type());
            method.setName(builder.getMethod_name());
            String sql = builder.getSql_content();
            int index = 0;
            if (builder.getPagination()) {
                sql = SqlBuilder.pagingQuerySql(sql, getDatabaseCategory(builder.getAllInOneName()),
                        CurrentLanguage.CSharp);
                index += 2;
            }
            Matcher m = CSharpCodeGenContext.inRegxPattern.matcher(builder.getSql_content());
            while (m.find()) {
                sql = sql.replace(m.group(1), String.format("({%d}) ", index));
                index++;
            }
            method.setSql(sql);
            method.setScalarType(builder.getScalarType());
            method.setPaging(builder.getPagination());

            List<AbstractParameterHost> paramAbstractHosts = DbUtils.getSelectFieldHosts(builder.getAllInOneName(),
                    builder.getSql_content(), new JavaSelectFieldResultSetExtractor());
            List<JavaParameterHost> paramHosts = new ArrayList<>();
            for (AbstractParameterHost phost : paramAbstractHosts) {
                paramHosts.add((JavaParameterHost) phost);
            }
            method.setFields(paramHosts);

            List<CSharpParameterHost> whereParams = buildMethodParameterHost4SqlConditin(builder, allColumns);
            method.setParameters(buildSqlParamName(whereParams, method.getSql()));

            String orderBy = builder.getOrderby();
            if (orderBy != null && !orderBy.trim().isEmpty() && orderBy.indexOf("-1,") != 0) {
                String[] str = orderBy.split(",");
                String odyExp = "p => p." + str[0] + ", ";
                odyExp = "asc".equalsIgnoreCase(str[1]) ? odyExp + "true" : odyExp + "false";
                method.setOrderByExp(odyExp);
            }
            methods.add(method);
        }
        return methods;
    }

    private List<CSharpParameterHost> buildSqlParamName(List<CSharpParameterHost> whereParams, String sql) {
        Pattern ptn = Pattern.compile("@([^\\s]+)", Pattern.CASE_INSENSITIVE);
        Matcher mt = ptn.matcher(sql);
        Queue<String> sqlParamQueue = new LinkedList<>();
        while (mt.find()) {
            sqlParamQueue.add(mt.group(1));
        }
        for (CSharpParameterHost param : whereParams) {
            String sqlParamName = sqlParamQueue.poll();
            if (sqlParamName == null)
                sqlParamName = param.getAlias();
            param.setSqlParamName(sqlParamName);
        }
        return whereParams;
    }

    private List<CSharpMethodHost> buildDeleteMethodHosts(List<CSharpParameterHost> allColumns,
            List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
        List<CSharpMethodHost> methods = new ArrayList<>();

        for (GenTaskBySqlBuilder builder : currentTableBuilders) {
            if (!builder.getCrud_type().equals("delete")) {
                continue;
            }
            CSharpMethodHost method = new CSharpMethodHost();
            method.setCrud_type(builder.getCrud_type());
            method.setName(builder.getMethod_name());
            method.setSql(builder.getSql_content());
            method.setScalarType(builder.getScalarType());
            method.setPaging(builder.getPagination());

            List<CSharpParameterHost> whereParams = buildMethodParameterHost4SqlConditin(builder, allColumns);
            method.setParameters(buildSqlParamName(whereParams, method.getSql()));
            methods.add(method);
        }
        return methods;
    }

    private List<CSharpMethodHost> buildInsertMethodHosts(List<CSharpParameterHost> allColumns,
            List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
        List<CSharpMethodHost> methods = new ArrayList<>();

        for (GenTaskBySqlBuilder builder : currentTableBuilders) {
            if (!builder.getCrud_type().equals("insert")) {
                continue;
            }
            CSharpMethodHost method = new CSharpMethodHost();
            method.setCrud_type(builder.getCrud_type());
            method.setName(builder.getMethod_name());
            method.setSql(builder.getSql_content());
            method.setScalarType(builder.getScalarType());
            method.setPaging(builder.getPagination());

            List<CSharpParameterHost> parameters = new ArrayList<>();
            if (method.getCrud_type().equals("insert")) {
                String[] fields = StringUtils.split(builder.getFields(), ",");
                for (String field : fields) {
                    for (CSharpParameterHost pHost : allColumns) {
                        if (pHost.getName().equals(field)) {
                            parameters.add(pHost);
                            break;
                        }
                    }
                }
            }
            method.setParameters(parameters);
            methods.add(method);
        }
        return methods;
    }

    private List<CSharpMethodHost> buildUpdateMethodHosts(List<CSharpParameterHost> allColumns,
            List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
        List<CSharpMethodHost> methods = new ArrayList<>();

        for (GenTaskBySqlBuilder builder : currentTableBuilders) {
            if (!builder.getCrud_type().equals("update")) {
                continue;
            }
            CSharpMethodHost method = new CSharpMethodHost();
            method.setCrud_type(builder.getCrud_type());
            method.setName(builder.getMethod_name());
            method.setSql(builder.getSql_content());
            method.setScalarType(builder.getScalarType());
            method.setPaging(builder.getPagination());

            List<CSharpParameterHost> parameters = new ArrayList<>();

            String[] fields = StringUtils.split(builder.getFields(), ",");
            for (String field : fields) {
                for (CSharpParameterHost pHost : allColumns) {
                    if (pHost.getName().equals(field)) {
                        parameters.add(pHost);
                        break;
                    }
                }
            }
            List<CSharpParameterHost> whereParams = buildMethodParameterHost4SqlConditin(builder, allColumns);

            Pattern pt = Pattern.compile("where.*", Pattern.CASE_INSENSITIVE);
            Matcher mt = pt.matcher(method.getSql());
            if (mt.find())
                parameters.addAll(buildSqlParamName(whereParams, mt.group()));
            else
                parameters.addAll(whereParams);

            method.setParameters(parameters);
            methods.add(method);
        }
        return methods;
    }

    private List<CSharpParameterHost> buildMethodParameterHost4SqlConditin(GenTaskBySqlBuilder builder,
            List<CSharpParameterHost> allColumns) {
        List<CSharpParameterHost> parameters = new ArrayList<>();
        String[] conditions = StringUtils.split(builder.getCondition(), ";");
        for (String condition : conditions) {
            String[] tokens = StringUtils.split(condition, ",");
            if (tokens.length == 1) {
                if (builder.getCrud_type().equals("select")) {
                    CSharpParameterHost host = new CSharpParameterHost();
                    host.setConditionType(ConditionType.valueOf(Integer.parseInt(tokens[0])));
                    parameters.add(host);
                }
                continue;
            }
            String name = tokens[0];
            int type = tokens.length >= 2 ? Integer.parseInt(tokens[1]) : -1;
            String alias = tokens.length >= 3 ? tokens[2] : "";
            for (CSharpParameterHost pHost : allColumns) {
                if (pHost.getName().equals(name)) {
                    CSharpParameterHost host_al = new CSharpParameterHost(pHost);
                    host_al.setAlias(alias);
                    host_al.setInParameter(ConditionType.In == ConditionType.valueOf(type));
                    if (type != -1)
                        host_al.setConditionType(ConditionType.valueOf(type));
                    parameters.add(host_al);
                    // Between need an extra parameter
                    if (ConditionType.valueOf(type) == ConditionType.Between) {
                        CSharpParameterHost host_bw = new CSharpParameterHost(pHost);
                        String alias_bw = tokens.length >= 4 ? tokens[3] : "";
                        host_bw.setAlias(alias_bw);
                        host_bw.setConditionType(ConditionType.Between);
                        parameters.add(host_bw);
                        boolean nullable = tokens.length >= 5 ? Boolean.valueOf(tokens[4]) : false;
                        host_al.setNullable(nullable);
                        host_bw.setNullable(nullable);
                    } else {
                        boolean nullable = tokens.length >= 4 ? Boolean.valueOf(tokens[3]) : false;
                        host_al.setNullable(nullable);
                    }
                    break;
                }
            }
        }
        return parameters;
    }

    protected String getPojoClassName(String prefix, String suffix, String table) {
        String className = table;
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

    protected DatabaseCategory getDatabaseCategory(String dbName) throws Exception {
        DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
        String dbType = DbUtils.getDbType(dbName);
        if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
            dbCategory = DatabaseCategory.MySql;
        }
        return dbCategory;
    }
}
