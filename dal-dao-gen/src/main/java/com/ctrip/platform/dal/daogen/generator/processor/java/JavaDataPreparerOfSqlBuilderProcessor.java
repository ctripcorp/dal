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
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;

import java.util.*;
import java.util.concurrent.Callable;

public class JavaDataPreparerOfSqlBuilderProcessor extends AbstractJavaDataPreparer implements DalProcessor {
    @Override
    public void process(CodeGenContext context) throws Exception {
        List<Callable<ExecuteResult>> tasks = prepareSqlBuilder(context);
        TaskUtils.invokeBatch(tasks);
    }

    private List<Callable<ExecuteResult>> prepareSqlBuilder(CodeGenContext context) throws Exception {
        final JavaCodeGenContext ctx = (JavaCodeGenContext) context;
        final Progress progress = ctx.getProgress();
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        Queue<GenTaskBySqlBuilder> sqlBuilders = ctx.getSqlBuilders();
        final Queue<JavaTableHost> tableHosts = ctx.getTableHosts();
        if (sqlBuilders.size() > 0) {
            Map<String, List<GenTaskBySqlBuilder>> tempSqlBuildres = sqlBuilderBroupBy(sqlBuilders);

            for (final Map.Entry<String, List<GenTaskBySqlBuilder>> sqlBuilder : tempSqlBuildres.entrySet()) {
                for (final GenTaskBySqlBuilder builder : sqlBuilder.getValue()) {
                    Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                        @Override
                        public ExecuteResult call() throws Exception {
                            ExecuteResult result = new ExecuteResult("Build Extral SQL[" + builder.getAllInOneName()
                                    + "." + sqlBuilder.getKey() + "] Host");
                            progress.setOtherMessage(result.getTaskName());
                            try {
                                JavaTableHost extraTableHost = buildExtraSqlBuilderHost(ctx, builder);
                                if (null != extraTableHost) {
                                    tableHosts.add(extraTableHost);
                                }
                                result.setSuccessal(true);
                            } catch (Throwable e) {
                                progress.setOtherMessage(e.getMessage());
                                throw new Exception(
                                        String.format("Task Id[%s]:%s\r\n", builder.getId(), e.getMessage()), e);
                            }
                            return result;
                        }
                    };
                    results.add(worker);
                }
            }
        }
        return results;
    }

    private Map<String, List<GenTaskBySqlBuilder>> sqlBuilderBroupBy(Queue<GenTaskBySqlBuilder> tasks) {
        Map<String, List<GenTaskBySqlBuilder>> map = new HashMap<>();
        if (tasks == null || tasks.size() == 0)
            return map;

        for (GenTaskBySqlBuilder task : tasks) {
            String key = String.format("%s_%s", task.getAllInOneName(), task.getTable_name());

            if (!map.containsKey(key))
                map.put(key, new ArrayList<GenTaskBySqlBuilder>());
            map.get(key).add(task);
        }

        return map;
    }

    private JavaTableHost buildExtraSqlBuilderHost(CodeGenContext context, GenTaskBySqlBuilder sqlBuilder)
            throws Exception {
        GenTaskByTableViewSp tableViewSp = new GenTaskByTableViewSp();
        tableViewSp.setCud_by_sp(false);
        tableViewSp.setPagination(false);
        tableViewSp.setAllInOneName(sqlBuilder.getAllInOneName());
        tableViewSp.setDatabaseSetName(sqlBuilder.getDatabaseSetName());
        tableViewSp.setPrefix("");
        tableViewSp.setSuffix("");
        tableViewSp.setLength(sqlBuilder.getLength());

        DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
        String dbType = DbUtils.getDbType(sqlBuilder.getAllInOneName());
        if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
            dbCategory = DatabaseCategory.MySql;
        }

        return buildTableHost(context, tableViewSp, sqlBuilder.getTable_name(), dbCategory);
    }

}
