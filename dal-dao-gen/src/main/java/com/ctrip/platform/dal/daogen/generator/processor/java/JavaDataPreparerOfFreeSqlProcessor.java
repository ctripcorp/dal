package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.java.FreeSqlHost;
import com.ctrip.platform.dal.daogen.host.java.JavaGivenSqlResultSetExtractor;
import com.ctrip.platform.dal.daogen.host.java.JavaMethodHost;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.SqlBuilder;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

public class JavaDataPreparerOfFreeSqlProcessor extends AbstractJavaDataPreparer implements DalProcessor {
    @Override
    public void process(CodeGenContext context) throws Exception {
        List<Callable<ExecuteResult>> tasks = prepareFreeSql(context);
        TaskUtils.invokeBatch(tasks);
    }

    private List<Callable<ExecuteResult>> prepareFreeSql(CodeGenContext context) throws Exception {
        final JavaCodeGenContext ctx = (JavaCodeGenContext) context;
        int projectId = ctx.getProjectId();
        final Progress progress = ctx.getProgress();
        final String namespace = ctx.getNamespace();
        final Map<String, JavaMethodHost> freeSqlPojoHosts = ctx.get_freeSqlPojoHosts();
        final Queue<FreeSqlHost> freeSqlHosts = ctx.getFreeSqlHosts();
        DaoByFreeSql daoByFreeSql = BeanGetter.getDaoByFreeSql();
        List<GenTaskByFreeSql> freeSqlTasks;
        if (ctx.isRegenerate()) {
            freeSqlTasks = daoByFreeSql.updateAndGetAllTasks(projectId);
            prepareDbFromFreeSql(ctx, freeSqlTasks);
        } else {
            freeSqlTasks = daoByFreeSql.updateAndGetTasks(projectId);
            prepareDbFromFreeSql(ctx, daoByFreeSql.getTasksByProjectId(projectId));
        }

        if (!ctx.isIgnoreApproveStatus() && freeSqlTasks != null && freeSqlTasks.size() > 0) {
            Iterator<GenTaskByFreeSql> ite = freeSqlTasks.iterator();
            while (ite.hasNext()) {
                int approved = ite.next().getApproved();
                if (approved != 2 && approved != 0) {
                    ite.remove();
                }
            }
        }

        final Map<String, List<GenTaskByFreeSql>> groupBy = freeSqlGroupBy(freeSqlTasks);
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        for (final Map.Entry<String, List<GenTaskByFreeSql>> entry : groupBy.entrySet()) {
            Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {
                    ExecuteResult result = new ExecuteResult("Build  Free SQL[" + entry.getKey() + "] Host");
                    progress.setOtherMessage(result.getTaskName());
                    List<GenTaskByFreeSql> currentTasks = entry.getValue();
                    if (currentTasks.size() < 1)
                        return result;

                    FreeSqlHost host = new FreeSqlHost();
                    host.setDbSetName(currentTasks.get(0).getDatabaseSetName());
                    host.setClassName(currentTasks.get(0).getClass_name());
                    host.setPackageName(namespace);
                    host.setDatabaseCategory(getDatabaseCategory(currentTasks.get(0).getAllInOneName()));
                    host.setLength(currentTasks.get(0).getLength());

                    List<JavaMethodHost> methods = new ArrayList<>();
                    for (GenTaskByFreeSql task : currentTasks) {
                        try {
                            processMethodHost(task, namespace, methods, freeSqlPojoHosts);
                        } catch (Throwable e) {
                            progress.setOtherMessage(e.getMessage());
                            throw new Exception(String.format("Task Id[%s]:%s\r\n", task.getId(), e.getMessage()), e);
                        }
                    }
                    host.setMethods(methods);
                    freeSqlHosts.add(host);
                    result.setSuccessal(true);
                    return result;
                }
            };
            results.add(worker);
        }

        return results;
    }

    private void prepareDbFromFreeSql(CodeGenContext codeGenCtx, List<GenTaskByFreeSql> freeSqls) throws SQLException {
        for (GenTaskByFreeSql task : freeSqls) {
            addDatabaseSet(codeGenCtx, task.getDatabaseSetName());
        }
    }

    private Map<String, List<GenTaskByFreeSql>> freeSqlGroupBy(List<GenTaskByFreeSql> tasks) {
        Map<String, List<GenTaskByFreeSql>> groupBy = new HashMap<>();
        for (GenTaskByFreeSql task : tasks) {
            String key = String.format("%s_%s", task.getAllInOneName(), task.getClass_name().toLowerCase());
            if (groupBy.containsKey(key)) {
                groupBy.get(key).add(task);
            } else {
                groupBy.put(key, new ArrayList<GenTaskByFreeSql>());
                groupBy.get(key).add(task);
            }
        }
        return groupBy;
    }

    private void processMethodHost(GenTaskByFreeSql task, String namespace, List<JavaMethodHost> methods,
            Map<String, JavaMethodHost> freeSqlPojoHosts) throws Exception {
        JavaMethodHost method = new JavaMethodHost();
        method.setSql(task.getSql_content());
        method.setName(task.getMethod_name());
        method.setPackageName(namespace);
        method.setScalarType(task.getScalarType());
        method.setPojoType(task.getPojoType());
        method.setPaging(task.getPagination());
        method.setCrud_type(task.getCrud_type());
        method.setComments(task.getComment());
        method.setLength(task.getLength());

        if (task.getPojo_name() != null && !task.getPojo_name().isEmpty())
            method.setPojoClassName(WordUtils.capitalize(task.getPojo_name() + "Pojo"));

        List<JavaParameterHost> params = new ArrayList<>();
        for (String param : StringUtils.split(task.getParameters(), ";")) {
            String[] splitedParam = StringUtils.split(param, ",");
            JavaParameterHost p = new JavaParameterHost();
            p.setName(splitedParam[0]);
            p.setSqlType(Integer.valueOf(splitedParam[1]));
            p.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(p.getSqlType()));
            p.setValidationValue(DbUtils.mockATest(p.getSqlType()));
            boolean sensitive = splitedParam.length >= 3 ? Boolean.parseBoolean(splitedParam[2]) : false;
            p.setSensitive(sensitive);
            params.add(p);
        }

        SqlBuilder.rebuildJavaInClauseSQL(task.getSql_content(), params);
        method.setParameters(params);
        method.setHints(task.getHints());
        methods.add(method);

        if (method.getPojoClassName() != null && !method.getPojoClassName().isEmpty()
                && !freeSqlPojoHosts.containsKey(method.getPojoClassName())
                && !"update".equalsIgnoreCase(method.getCrud_type())) {
            List<JavaParameterHost> paramHosts = new ArrayList<>();
            for (AbstractParameterHost _ahost : DbUtils.testAQuerySql(task.getAllInOneName(), task.getSql_content(),
                    task.getParameters(), new JavaGivenSqlResultSetExtractor())) {
                paramHosts.add((JavaParameterHost) _ahost);
            }

            method.setFields(paramHosts);
            freeSqlPojoHosts.put(method.getPojoClassName(), method);
        } else if ("update".equalsIgnoreCase(method.getCrud_type())) {
            DbUtils.testUpdateSql(task.getAllInOneName(), task.getSql_content(), task.getParameters());
        }
    }

}
