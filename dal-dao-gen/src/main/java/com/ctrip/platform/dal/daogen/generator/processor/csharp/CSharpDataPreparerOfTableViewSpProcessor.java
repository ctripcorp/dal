package com.ctrip.platform.dal.daogen.generator.processor.csharp;

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
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.csharp.*;
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

public class CSharpDataPreparerOfTableViewSpProcessor extends AbstractCSharpDataPreparer implements DalProcessor {
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
        final CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
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
        for (GenTaskBySqlBuilder _t : sqlBuilderTasks) {
            sqlBuilders.add(_t);
        }

        final Queue<CSharpTableHost> spHosts = ctx.getSpHosts();
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

            final Queue<CSharpTableHost> tableViewHosts = ctx.getTableViewHosts();
            results.addAll(prepareTable(ctx, progress, tableViewSp, tableNames, dbCategory, tableViewHosts));
            results.addAll(prepareView(ctx, progress, tableViewSp, viewNames, dbCategory, tableViewHosts));
            results.addAll(prepareSp(ctx, progress, spHosts, tableViewSp, spNames, dbCategory));
        }

        return results;
    }

    private List<Callable<ExecuteResult>> prepareSp(final CSharpCodeGenContext ctx, final Progress progress,
            final Queue<CSharpTableHost> spHosts, final GenTaskByTableViewSp tableViewSp, final String[] spNames,
            final DatabaseCategory dbCategory) {
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        for (final String spName : spNames) {
            Callable<ExecuteResult> spWorker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {
                    ExecuteResult result =
                            new ExecuteResult("Build SP[" + tableViewSp.getAllInOneName() + "." + spName + "] Host");

                    progress.setOtherMessage(result.getTaskName());
                    try {
                        CSharpTableHost currentSpHost = buildSpHost(ctx, tableViewSp, dbCategory, spName);
                        if (null != currentSpHost) {
                            spHosts.add(currentSpHost);
                        }
                        result.setSuccessal(true);
                    } catch (Throwable e) {
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

    private List<Callable<ExecuteResult>> prepareView(final CSharpCodeGenContext ctx, final Progress progress,
            final GenTaskByTableViewSp tableViewSp, final String[] viewNames, final DatabaseCategory dbCategory,
            final Queue<CSharpTableHost> tableViewHosts) {
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        for (final String view : viewNames) {
            Callable<ExecuteResult> viewWorker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {

                    ExecuteResult result =
                            new ExecuteResult("Build View[" + tableViewSp.getAllInOneName() + "." + view + "] Host");
                    progress.setOtherMessage(result.getTaskName());
                    try {
                        CSharpTableHost currentViewHost = buildViewHost(ctx, tableViewSp, dbCategory, view);
                        if (null != currentViewHost) {
                            tableViewHosts.add(currentViewHost);
                        }
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

    private List<Callable<ExecuteResult>> prepareTable(final CSharpCodeGenContext ctx, final Progress progress,
            final GenTaskByTableViewSp tableViewSp, final String[] tableNames, final DatabaseCategory dbCategory,
            final Queue<CSharpTableHost> tableViewHosts) throws Exception {
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        final List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(tableViewSp.getAllInOneName());
        for (final String table : tableNames) {
            Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {
                    ExecuteResult result =
                            new ExecuteResult("Build Table[" + tableViewSp.getAllInOneName() + "." + table + "] Host");
                    progress.setOtherMessage(result.getTaskName());
                    CSharpTableHost currentTableHost;
                    try {
                        currentTableHost = buildTableHost(ctx, tableViewSp, table, dbCategory, allSpNames);
                        if (null != currentTableHost) {
                            tableViewHosts.add(currentTableHost);
                        }
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
            List<GenTaskBySqlBuilder> sqlBuilders) throws Exception {
        CSharpCodeGenContext ctx = (CSharpCodeGenContext) codeGenCtx;
        Set<String> existsTable = new HashSet<>();
        Set<String> _tableDaos = ctx.getTableDaos();
        Set<String> _spDaos = ctx.getSpDaos();
        Map<String, DatabaseHost> _dbHosts = ctx.getDbHosts();
        for (GenTaskByTableViewSp task : tableViewSps) {
            for (String table : StringUtils.split(task.getTable_names(), ",")) {
                _tableDaos.add(getPojoClassName(task.getPrefix(), task.getSuffix(), table));
                existsTable.add(table);
            }
            for (String table : StringUtils.split(task.getView_names(), ",")) {
                _tableDaos.add(getPojoClassName(task.getPrefix(), task.getSuffix(), table));
            }
            for (String table : StringUtils.split(task.getSp_names(), ",")) {
                String realSpName = table;
                if (table.contains(".")) {
                    String[] splitSp = StringUtils.split(table, '.');
                    realSpName = splitSp[1];
                }
                _spDaos.add(getPojoClassName(task.getPrefix(), task.getSuffix(), realSpName.replace("_", "")));
            }

            addDatabaseSet(ctx, task.getDatabaseSetName());

            if (!_dbHosts.containsKey(task.getAllInOneName())) {
                String provider = "sqlProvider";
                String dbType = DbUtils.getDbType(task.getAllInOneName());
                if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
                    provider = "mySqlProvider";
                }
                DatabaseHost host = new DatabaseHost();
                host.setAllInOneName(task.getAllInOneName());
                host.setProviderType(provider);
                host.setDatasetName(host.getAllInOneName());
                _dbHosts.put(task.getAllInOneName(), host);
            }
        }

        for (GenTaskBySqlBuilder task : sqlBuilders) {
            if (!existsTable.contains(task.getTable_name())) {
                _tableDaos.add(getPojoClassName("", "", task.getTable_name()));
            }

            addDatabaseSet(ctx, task.getDatabaseSetName());

            if (!_dbHosts.containsKey(task.getAllInOneName())) {
                String provider = "sqlProvider";
                String dbType = DbUtils.getDbType(task.getAllInOneName());
                if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
                    provider = "mySqlProvider";
                }
                DatabaseHost host = new DatabaseHost();
                host.setAllInOneName(task.getAllInOneName());
                host.setProviderType(provider);
                host.setDatasetName(host.getAllInOneName());
                _dbHosts.put(task.getAllInOneName(), host);
            }
        }
    }


    private CSharpTableHost buildViewHost(CodeGenContext codeGenCtx, GenTaskByTableViewSp tableViewSp,
            DatabaseCategory dbCategory, String view) throws Exception {
        CSharpCodeGenContext ctx = (CSharpCodeGenContext) codeGenCtx;

        if (!DbUtils.viewExists(tableViewSp.getAllInOneName(), view)) {
            throw new Exception(String.format("视图 %s 不存在，请编辑DAO再生成", view));
        }

        List<AbstractParameterHost> allColumnsAbstract = DbUtils.getAllColumnNames(tableViewSp.getAllInOneName(), view,
                new CsharpColumnNameResultSetExtractor(tableViewSp.getAllInOneName(), view, dbCategory));
        List<CSharpParameterHost> allColumns = new ArrayList<>();
        for (AbstractParameterHost h : allColumnsAbstract) {
            allColumns.add((CSharpParameterHost) h);
        }

        CSharpTableHost tableHost = new CSharpTableHost();
        tableHost.setNameSpace(ctx.getNamespace());
        tableHost.setDatabaseCategory(dbCategory);
        tableHost.setDbSetName(tableViewSp.getDatabaseSetName());
        tableHost.setTableName(view);
        tableHost.setClassName(CommonUtils
                .normalizeVariable(getPojoClassName(tableViewSp.getPrefix(), tableViewSp.getSuffix(), view)));
        tableHost.setTable(false);
        tableHost.setSpa(false);
        tableHost.setColumns(allColumns);
        tableHost.setHasPagination(tableViewSp.getPagination());
        return tableHost;
    }

    private CSharpTableHost buildSpHost(CodeGenContext codeGenCtx, GenTaskByTableViewSp tableViewSp,
            DatabaseCategory dbCategory, String spName) throws Exception {
        CSharpCodeGenContext ctx = (CSharpCodeGenContext) codeGenCtx;
        String schema = "dbo";
        String realSpName = spName;
        if (spName.contains(".")) {
            String[] splitSp = StringUtils.split(spName, '.');
            schema = splitSp[0];
            realSpName = splitSp[1];
        }

        StoredProcedure currentSp = new StoredProcedure();
        currentSp.setSchema(schema);
        currentSp.setName(realSpName);

        if (!DbUtils.spExists(tableViewSp.getAllInOneName(), currentSp)) {
            throw new Exception(String.format("存储过程 %s 不存在，请修改DAO后再试！", currentSp.getName()));
        }

        List<AbstractParameterHost> params =
                DbUtils.getSpParams(tableViewSp.getAllInOneName(), currentSp, new CsharpSpParamResultSetExtractor());
        List<CSharpParameterHost> realParams = new ArrayList<CSharpParameterHost>();
        for (AbstractParameterHost p : params) {
            realParams.add((CSharpParameterHost) p);
        }

        CSharpTableHost tableHost = new CSharpTableHost();
        tableHost.setNameSpace(ctx.getNamespace());
        tableHost.setDatabaseCategory(dbCategory);
        tableHost.setDbSetName(tableViewSp.getDatabaseSetName());
        tableHost.setClassName(
                getPojoClassName(tableViewSp.getPrefix(), tableViewSp.getSuffix(), realSpName.replace("_", "")));
        tableHost.setTable(false);
        tableHost.setSpName(spName);
        tableHost.setSpParams(realParams);
        tableHost.setApi_list(tableViewSp.getApi_list());

        return tableHost;
    }

}
