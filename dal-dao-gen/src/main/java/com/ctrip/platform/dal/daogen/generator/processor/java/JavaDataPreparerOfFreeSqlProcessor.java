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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

import static oracle.net.aso.C01.e;
import static oracle.net.aso.C07.i;

public class JavaDataPreparerOfFreeSqlProcessor extends AbstractJavaDataPreparer implements DalProcessor {
    private static DaoByFreeSql daoByFreeSql;

    static {
        try {
            daoByFreeSql = BeanGetter.getDaoByFreeSql();
        } catch (SQLException e) {
        }
    }

    @Override
    public void process(CodeGenContext context) throws Exception {}

    public List<Callable<ExecuteResult>> prepareFreeSql(CodeGenContext codeGenCtx) throws Exception {
        JavaCodeGenContext ctx = (JavaCodeGenContext) codeGenCtx;
        int projectId = ctx.getProjectId();
        final Progress progress = ctx.getProgress();
        final String namespace = ctx.getNamespace();
        final Map<String, JavaMethodHost> freeSqlPojoHosts = ctx.get_freeSqlPojoHosts();
        final Queue<FreeSqlHost> freeSqlHosts = ctx.getFreeSqlHosts();
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
            for (final GenTaskByFreeSql task : entry.getValue()) {
                Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                    @Override
                    public ExecuteResult call() throws Exception {
                        ExecuteResult result = new ExecuteResult("Build  Free SQL[" + entry.getKey() + "] Host");
                        progress.setOtherMessage(result.getTaskName());

                        FreeSqlHost host = new FreeSqlHost();
                        host.setDbSetName(task.getDatabaseSetName());
                        host.setClassName(task.getClass_name());
                        host.setPackageName(namespace);
                        host.setDatabaseCategory(getDatabaseCategory(task.getAllInOneName()));

                        List<JavaMethodHost> methodHosts = new ArrayList<>();
                        try {
                            JavaMethodHost methodHost = new JavaMethodHost();
                            methodHost.setSql(task.getSql_content());
                            methodHost.setName(task.getMethod_name());
                            methodHost.setPackageName(namespace);
                            methodHost.setScalarType(task.getScalarType());
                            methodHost.setPojoType(task.getPojoType());
                            methodHost.setPaging(task.getPagination());
                            methodHost.setCrud_type(task.getCrud_type());
                            methodHost.setComments(task.getComment());
                            if (task.getPojo_name() != null && !task.getPojo_name().isEmpty())
                                methodHost.setPojoClassName(WordUtils.capitalize(task.getPojo_name() + "Pojo"));

                            List<JavaParameterHost> parameterHosts = new ArrayList<>();
                            for (String parameter : StringUtils.split(task.getParameters(), ";")) {
                                String[] array = StringUtils.split(parameter, ",");
                                JavaParameterHost p = new JavaParameterHost();
                                p.setName(array[0]);
                                p.setSqlType(Integer.valueOf(array[1]));
                                p.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(p.getSqlType()));
                                p.setValidationValue(DbUtils.mockATest(p.getSqlType()));
                                boolean sensitive = array.length >= 3 ? Boolean.parseBoolean(array[2]) : false;
                                p.setSensitive(sensitive);
                                parameterHosts.add(p);
                            }

                            SqlBuilder.rebuildJavaInClauseSQL(task.getSql_content(), parameterHosts);
                            methodHost.setParameters(parameterHosts);
                            methodHost.setHints(task.getHints());
                            methodHosts.add(methodHost);

                            if (methodHost.getPojoClassName() != null && !methodHost.getPojoClassName().isEmpty()
                                    && !freeSqlPojoHosts.containsKey(methodHost.getPojoClassName())
                                    && !"update".equalsIgnoreCase(methodHost.getCrud_type())) {
                                List<JavaParameterHost> fieldHosts = new ArrayList<>();
                                for (AbstractParameterHost fieldHost : DbUtils.testAQuerySql(task.getAllInOneName(),
                                        task.getSql_content(), task.getParameters(),
                                        new JavaGivenSqlResultSetExtractor())) {
                                    fieldHosts.add((JavaParameterHost) fieldHost);
                                }

                                methodHost.setFields(fieldHosts);
                                freeSqlPojoHosts.put(methodHost.getPojoClassName(), methodHost);
                            } else if ("update".equalsIgnoreCase(methodHost.getCrud_type())) {
                                DbUtils.testUpdateSql(task.getAllInOneName(), task.getSql_content(),
                                        task.getParameters());
                            }
                        } catch (Throwable e) {
                            throw new Exception(String.format("Task Id[%s]:%s\r\n", task.getId(), e.getMessage()), e);
                        }

                        host.setMethods(methodHosts);
                        freeSqlHosts.add(host);
                        result.setSuccessal(true);
                        return result;
                    }
                };
                results.add(worker);
            }
        }

        return results;
    }

    private void prepareDbFromFreeSql(CodeGenContext context, List<GenTaskByFreeSql> tasks) throws SQLException {
        if (tasks == null || tasks.size() == 0)
            return;

        for (GenTaskByFreeSql task : tasks) {
            addDatabaseSet(context, task.getDatabaseSetName());
        }
    }

    private Map<String, List<GenTaskByFreeSql>> freeSqlGroupBy(List<GenTaskByFreeSql> tasks) {
        Map<String, List<GenTaskByFreeSql>> map = new HashMap<>();
        if (tasks == null || tasks.size() == 0)
            return map;

        for (GenTaskByFreeSql task : tasks) {
            String key = String.format("%s_%s", task.getAllInOneName(), task.getClass_name().toLowerCase());
            if (map.containsKey(key)) {
                map.get(key).add(task);
            } else {
                map.put(key, new ArrayList<GenTaskByFreeSql>());
                map.get(key).add(task);
            }
        }
        return map;
    }

}
