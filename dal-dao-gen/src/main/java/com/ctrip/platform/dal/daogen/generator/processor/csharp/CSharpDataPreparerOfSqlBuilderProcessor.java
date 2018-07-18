package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpTableHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;

import java.util.*;
import java.util.concurrent.Callable;

public class CSharpDataPreparerOfSqlBuilderProcessor extends AbstractCSharpDataPreparer implements DalProcessor {

    @Override
    public void process(CodeGenContext context) throws Exception {
        List<Callable<ExecuteResult>> tasks = prepareSqlBuilder(context);
        TaskUtils.invokeBatch(tasks);
    }

    private List<Callable<ExecuteResult>> prepareSqlBuilder(CodeGenContext context) {
        final CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
        final Progress progress = ctx.getProgress();
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        Queue<GenTaskBySqlBuilder> sqlBuilders = ctx.getSqlBuilders();
        final Queue<CSharpTableHost> tableViewHosts = ctx.getTableViewHosts();
        if (sqlBuilders.size() > 0) {
            Map<String, GenTaskBySqlBuilder> tempSqlBuildres = sqlBuilderBroupBy(sqlBuilders);

            for (final Map.Entry<String, GenTaskBySqlBuilder> sqlBuilder : tempSqlBuildres.entrySet()) {
                Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                    @Override
                    public ExecuteResult call() throws Exception {
                        ExecuteResult result = new ExecuteResult("Build Extral SQL["
                                + sqlBuilder.getValue().getAllInOneName() + "." + sqlBuilder.getKey() + "] Host");
                        progress.setOtherMessage(result.getTaskName());
                        CSharpTableHost extraTableHost;
                        try {
                            extraTableHost = buildExtraSqlBuilderHost(ctx, sqlBuilder.getValue());
                            if (null != extraTableHost) {
                                tableViewHosts.add(extraTableHost);
                            }
                            result.setSuccessal(true);
                        } catch (Throwable e) {
                            throw new Exception(
                                    String.format("Task Id[%s]:%s\r\n", sqlBuilder.getValue().getId(), e.getMessage()),
                                    e);
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
        Map<String, GenTaskBySqlBuilder> map = new HashMap<>();
        if (builders == null || builders.size() == 0)
            return map;

        for (GenTaskBySqlBuilder task : builders) {
            String key = String.format("%s_%s", task.getAllInOneName(), task.getTable_name());

            if (!map.containsKey(key)) {
                map.put(key, task);
            }
        }
        return map;
    }

    private CSharpTableHost buildExtraSqlBuilderHost(CodeGenContext codeGenCtx, GenTaskBySqlBuilder sqlBuilder)
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
        if (dbType != null && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
            dbCategory = DatabaseCategory.MySql;
        }

        List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(sqlBuilder.getAllInOneName());
        return buildTableHost(codeGenCtx, tableViewSp, sqlBuilder.getTable_name(), dbCategory, allSpNames);
    }
}
