package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.java.JavaTableHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;

import java.util.*;
import java.util.concurrent.Callable;

public class JavaDataPreparerOfSqlBuilderProcessor extends AbstractJavaDataPreparer implements DalProcessor {
    @Override
    public void process(CodeGenContext context) throws Exception {
        try {
            List<Callable<ExecuteResult>> _sqlBuilderCallables = prepareSqlBuilder(context);
            TaskUtils.invokeBatch(_sqlBuilderCallables);
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    private List<Callable<ExecuteResult>> prepareSqlBuilder(CodeGenContext codeGenCtx) {
        final JavaCodeGenContext ctx = (JavaCodeGenContext) codeGenCtx;
        final Progress progress = ctx.getProgress();
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        Queue<GenTaskBySqlBuilder> _sqlBuilders = ctx.getSqlBuilders();
        final Queue<JavaTableHost> _tableHosts = ctx.getTableHosts();
        if (_sqlBuilders.size() > 0) {
            // 按照DbName和TableName进行分组
            Map<String, GenTaskBySqlBuilder> _TempSqlBuildres = sqlBuilderBroupBy(_sqlBuilders);

            for (final Map.Entry<String, GenTaskBySqlBuilder> _table : _TempSqlBuildres.entrySet()) {
                Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

                    @Override
                    public ExecuteResult call() throws Exception {
                        /*
                         * progress.setOtherMessage("正在整理表 " + _table.getValue().getClass_name());
                         */
                        ExecuteResult result = new ExecuteResult("Build Extral SQL["
                                + _table.getValue().getAllInOneName() + "." + _table.getKey() + "] Host");
                        progress.setOtherMessage(result.getTaskName());
                        try {
                            JavaTableHost extraTableHost = buildExtraSqlBuilderHost(ctx, _table.getValue());
                            if (null != extraTableHost) {
                                _tableHosts.add(extraTableHost);
                            }
                            result.setSuccessal(true);
                        } catch (Throwable e) {
                            String message =
                                    String.format("Task id[%s]:\r\n %s", _table.getValue().getId(), e.getMessage());
                            progress.setOtherMessage(message);
                            throw new Exception(message);
                        }
                        return result;
                    }
                };
                results.add(worker);
            }
        }
        return results;
    }

    private Map<String, GenTaskBySqlBuilder> sqlBuilderBroupBy(Queue<GenTaskBySqlBuilder> builders) {
        Map<String, GenTaskBySqlBuilder> groupBy = new HashMap<>();

        for (GenTaskBySqlBuilder task : builders) {
            String key = String.format("%s_%s", task.getAllInOneName(), task.getTable_name());

            if (!groupBy.containsKey(key)) {
                groupBy.put(key, task);
            }
        }
        return groupBy;
    }

    private JavaTableHost buildExtraSqlBuilderHost(CodeGenContext codeGenCtx, GenTaskBySqlBuilder sqlBuilder)
            throws Exception {
        GenTaskByTableViewSp tableViewSp = new GenTaskByTableViewSp();
        tableViewSp.setCud_by_sp(false);
        tableViewSp.setPagination(false);
        tableViewSp.setAllInOneName(sqlBuilder.getAllInOneName());
        tableViewSp.setDatabaseSetName(sqlBuilder.getDatabaseSetName());
        tableViewSp.setPrefix("");
        tableViewSp.setSuffix("");

        DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
        String dbType = DbUtils.getDbType(sqlBuilder.getAllInOneName());
        if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
            dbCategory = DatabaseCategory.MySql;
        }

        return buildTableHost(codeGenCtx, tableViewSp, sqlBuilder.getTable_name(), dbCategory);
    }

}
