package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.java.*;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

public class JavaDataPreparerOfTableViewSpProcessor extends AbstractJavaDataPreparer implements DalProcessor {
    private static DaoBySqlBuilder daoBySqlBuilder;
    private static DaoByTableViewSp daoByTableViewSp;

    static {
        try {
            daoBySqlBuilder = BeanGetter.getDaoBySqlBuilder();
            daoByTableViewSp = BeanGetter.getDaoByTableViewSp();
        } catch (SQLException e) {
        }
    }

    @Override
    public void process(CodeGenContext context) throws Exception {
        List<Callable<ExecuteResult>> tasks = prepareTableViewSp(context);
        TaskUtils.invokeBatch(tasks);
    }

    private List<Callable<ExecuteResult>> prepareTableViewSp(CodeGenContext context) throws Exception {
        final JavaCodeGenContext ctx = (JavaCodeGenContext) context;
        int projectId = ctx.getProjectId();
        boolean regenerate = ctx.isRegenerate();
        final Progress progress = ctx.getProgress();
        List<GenTaskByTableViewSp> tableViewSpTasks;
        List<GenTaskBySqlBuilder> sqlBuilderTasks;
        if (regenerate) {
            tableViewSpTasks = daoByTableViewSp.updateAndGetAllTasks(projectId);
            sqlBuilderTasks = daoBySqlBuilder.updateAndGetAllTasks(projectId);
            prepareDbFromTableViewSp(ctx, tableViewSpTasks, sqlBuilderTasks);
        } else {
            tableViewSpTasks = daoByTableViewSp.updateAndGetTasks(projectId);
            sqlBuilderTasks = daoBySqlBuilder.updateAndGetTasks(projectId);
            prepareDbFromTableViewSp(ctx, daoByTableViewSp.getTasksByProjectId(projectId),
                    daoBySqlBuilder.getTasksByProjectId(projectId));
        }

        if (!ctx.isIgnoreApproveStatus() && tableViewSpTasks != null && tableViewSpTasks.size() > 0) {
            Iterator<GenTaskByTableViewSp> ite = tableViewSpTasks.iterator();
            while (ite.hasNext()) {
                int approved = ite.next().getApproved();
                if (approved != 2 && approved != 0) {
                    ite.remove();
                }
            }
        }

        if (!ctx.isIgnoreApproveStatus() && sqlBuilderTasks != null && sqlBuilderTasks.size() > 0) {
            Iterator<GenTaskBySqlBuilder> ite = sqlBuilderTasks.iterator();
            while (ite.hasNext()) {
                int approved = ite.next().getApproved();
                if (approved != 2 && approved != 0) {
                    ite.remove();
                }
            }
        }

        Queue<GenTaskBySqlBuilder> sqlBuilders = ctx.getSqlBuilders();
        for (GenTaskBySqlBuilder sqlBuilder : sqlBuilderTasks) {
            sqlBuilders.add(sqlBuilder);
        }

        final Queue<JavaTableHost> tableHosts = ctx.getTableHosts();
        final Queue<ViewHost> viewHosts = ctx.getViewHosts();
        final Queue<SpHost> spHosts = ctx.getSpHosts();
        final Map<String, SpDbHost> spHostMaps = ctx.getSpHostMaps();
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        for (final GenTaskByTableViewSp tableViewSp : tableViewSpTasks) {
            final String[] viewNames = StringUtils.split(tableViewSp.getView_names(), ",");
            final String[] tableNames = StringUtils.split(tableViewSp.getTable_names(), ",");
            final String[] spNames = StringUtils.split(tableViewSp.getSp_names(), ",");

            final DatabaseCategory dbCategory;
            String dbType = DbUtils.getDbType(tableViewSp.getAllInOneName());
            if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
                dbCategory = DatabaseCategory.MySql;
            } else {
                dbCategory = DatabaseCategory.SqlServer;
            }

            try {
                results.addAll(prepareTable(ctx, progress, tableHosts, tableViewSp, tableNames, dbCategory));
                results.addAll(prepareView(ctx, progress, viewHosts, tableViewSp, viewNames, dbCategory));
                results.addAll(prepareSp(ctx, progress, spHosts, spHostMaps, tableViewSp, spNames));
            } catch (Throwable e) {
                throw e;
            }
        }

        return results;
    }

    private List<Callable<ExecuteResult>> prepareSp(final JavaCodeGenContext context, final Progress progress,
            final Queue<SpHost> spHosts, final Map<String, SpDbHost> spHostMaps, final GenTaskByTableViewSp tableViewSp,
            final String[] spNames) {
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        for (final String spName : spNames) {
            Callable<ExecuteResult> spWorker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {
                    ExecuteResult result =
                            new ExecuteResult("Build SP[" + tableViewSp.getAllInOneName() + "." + spName + "] Host");
                    progress.setOtherMessage(result.getTaskName());
                    try {
                        SpHost spHost = buildSpHost(context, tableViewSp, spName);
                        if (null != spHost) {
                            if (!spHostMaps.containsKey(spHost.getDbName())) {
                                SpDbHost spDbHost = new SpDbHost(spHost.getDbName(), spHost.getPackageName());
                                spHostMaps.put(spHost.getDbName(), spDbHost);
                            }
                            spHostMaps.get(spHost.getDbName()).addSpHost(spHost);
                            spHosts.add(spHost);
                        }
                        result.setSuccessal(true);
                    } catch (Exception e) {
                        progress.setOtherMessage(e.getMessage());
                        throw new Exception(String.format("Task Id[%s]:%s\r\n", tableViewSp.getId(), e.getMessage()),
                                e);
                    }
                    return result;
                }
            };
            results.add(spWorker);
        }
        return results;
    }

    private List<Callable<ExecuteResult>> prepareView(final JavaCodeGenContext ctx, final Progress progress,
            final Queue<ViewHost> _viewHosts, final GenTaskByTableViewSp tableViewSp, final String[] viewNames,
            final DatabaseCategory dbCategory) {
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        for (final String view : viewNames) {
            Callable<ExecuteResult> viewWorker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {
                    ExecuteResult result =
                            new ExecuteResult("Build View[" + tableViewSp.getAllInOneName() + "." + view + "] Host");
                    progress.setOtherMessage(result.getTaskName());
                    try {
                        ViewHost vhost = buildViewHost(ctx, tableViewSp, dbCategory, view);
                        if (null != vhost)
                            _viewHosts.add(vhost);
                        result.setSuccessal(true);
                    } catch (Throwable e) {
                        throw new Exception(String.format("Task Id[%s]:%s\r\n", tableViewSp.getId(), e.getMessage()),
                                e);
                    }
                    return result;
                }
            };
            results.add(viewWorker);
        }
        return results;
    }

    private List<Callable<ExecuteResult>> prepareTable(final JavaCodeGenContext ctx, final Progress progress,
            final Queue<JavaTableHost> _tableHosts, final GenTaskByTableViewSp tableViewSp, final String[] tableNames,
            final DatabaseCategory dbCategory) {
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        for (final String tableName : tableNames) {
            Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {
                    ExecuteResult result = new ExecuteResult(
                            "Build Table[" + tableViewSp.getAllInOneName() + "." + tableName + "] Host");
                    progress.setOtherMessage(result.getTaskName());
                    try {
                        JavaTableHost tableHost = buildTableHost(ctx, tableViewSp, tableName, dbCategory);
                        result.setSuccessal(true);
                        if (null != tableHost)
                            _tableHosts.add(tableHost);
                        result.setSuccessal(true);
                    } catch (Throwable e) {
                        throw new Exception(String.format("Task Id[%s]:%s\r\n", tableViewSp.getId(), e.getMessage()),
                                e);
                    }
                    return result;
                }
            };
            results.add(worker);
        }
        return results;
    }

    private void prepareDbFromTableViewSp(CodeGenContext codeGenCtx, List<GenTaskByTableViewSp> tableViewSps,
            List<GenTaskBySqlBuilder> sqlBuilders) throws SQLException {
        for (GenTaskByTableViewSp task : tableViewSps) {
            addDatabaseSet(codeGenCtx, task.getDatabaseSetName());
        }
        for (GenTaskBySqlBuilder task : sqlBuilders) {
            addDatabaseSet(codeGenCtx, task.getDatabaseSetName());
        }
    }

    private ViewHost buildViewHost(CodeGenContext context, GenTaskByTableViewSp tableViewSp,
            DatabaseCategory dbCategory, String viewName) throws Exception {
        JavaCodeGenContext ctx = (JavaCodeGenContext) context;
        if (!DbUtils.viewExists(tableViewSp.getAllInOneName(), viewName)) {
            return null;
        }

        ViewHost vhost = new ViewHost();
        String className = viewName.replace("_", "");
        className = getPojoClassName(tableViewSp.getPrefix(), tableViewSp.getSuffix(), className);

        vhost.setPackageName(ctx.getNamespace());
        vhost.setDatabaseCategory(getDatabaseCategory(tableViewSp.getAllInOneName()));
        vhost.setDbSetName(tableViewSp.getDatabaseSetName());
        vhost.setPojoClassName(className);
        vhost.setViewName(viewName);
        vhost.setLength(tableViewSp.getLength());

        List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(tableViewSp.getAllInOneName(), viewName);
        List<AbstractParameterHost> params = DbUtils.getAllColumnNames(tableViewSp.getAllInOneName(), viewName,
                new JavaColumnNameResultSetExtractor(tableViewSp.getAllInOneName(), viewName, dbCategory));
        List<JavaParameterHost> realParams = new ArrayList<>();
        if (params == null || params.size() == 0) {
            throw new Exception(
                    String.format("The column names of view[%s, %s] is null", tableViewSp.getAllInOneName(), viewName));
        }
        for (AbstractParameterHost p : params) {
            JavaParameterHost jHost = (JavaParameterHost) p;
            if (primaryKeyNames.contains(jHost.getName())) {
                jHost.setPrimary(true);
            }
            realParams.add(jHost);
        }

        vhost.setFields(realParams);
        return vhost;
    }

    private SpHost buildSpHost(CodeGenContext context, GenTaskByTableViewSp tableViewSp, String spName)
            throws Exception {
        JavaCodeGenContext ctx = (JavaCodeGenContext) context;
        String schema = "dbo";
        String realSpName = spName;
        if (spName.contains(".")) {
            String[] splitSp = StringUtils.split(spName, '.');
            schema = splitSp[0];
            realSpName = splitSp[1];
        }

        StoredProcedure sp = new StoredProcedure();
        sp.setSchema(schema);
        sp.setName(realSpName);

        if (!DbUtils.spExists(tableViewSp.getAllInOneName(), sp)) {
            throw new Exception(String.format("The store procedure[%s, %s] doesn't exist, pls check",
                    tableViewSp.getAllInOneName(), sp.getName()));
        }

        SpHost host = new SpHost();
        String className = realSpName.replace("_", "");
        className = getPojoClassName(tableViewSp.getPrefix(), tableViewSp.getSuffix(), className);

        host.setPackageName(ctx.getNamespace());
        host.setDatabaseCategory(getDatabaseCategory(tableViewSp.getAllInOneName()));
        host.setDbName(tableViewSp.getDatabaseSetName());
        host.setPojoClassName(className);
        host.setSpName(spName);
        host.setLength(tableViewSp.getLength());

        List<AbstractParameterHost> params = DbUtils.getSpParams(tableViewSp.getAllInOneName(), sp,
                new JavaSpParamResultSetExtractor(tableViewSp.getAllInOneName(), sp.getName()));
        List<JavaParameterHost> realParams = new ArrayList<>();
        String callParams = "";
        if (params == null) {
            throw new Exception(
                    String.format("The sp[%s, %s] parameters is null", tableViewSp.getAllInOneName(), sp.getName()));
        }
        for (AbstractParameterHost p : params) {
            callParams += "?,";
            realParams.add((JavaParameterHost) p);
        }

        host.setCallParameters(StringUtils.removeEnd(callParams, ","));
        host.setFields(realParams);
        return host;
    }

}
