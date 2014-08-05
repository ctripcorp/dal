package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.java.FreeSqlHost;
import com.ctrip.platform.dal.daogen.host.java.JavaMethodHost;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class JavaDataPreparerOfFreeSqlProcessor extends AbstractJavaDataPreparer implements Processor {

	private static Logger log = Logger.getLogger(JavaDataPreparerOfFreeSqlProcessor.class);
	
	@Override
	public void process(Context context) {
		
		List<Callable<ExecuteResult>> _freeSqlCallables = prepareFreeSql((CodeGenContext)context);
		
		TaskUtils.invokeBatch(log, _freeSqlCallables);
		
	}
	
	private List<Callable<ExecuteResult>> prepareFreeSql(CodeGenContext codeGenCtx) {
		JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
		int projectId = ctx.getProjectId();
		boolean regenerate = ctx.isRegenerate();
		final Progress progress = ctx.getProgress();
		final String namespace = ctx.getNamespace();
		final Map<String, JavaMethodHost> _freeSqlPojoHosts = ctx.get_freeSqlPojoHosts();
		final Queue<FreeSqlHost> _freeSqlHosts = ctx.get_freeSqlHosts();
		DaoByFreeSql daoByFreeSql = SpringBeanGetter.getDaoByFreeSql();
		List<GenTaskByFreeSql> _freeSqls;
		if (regenerate) {
			_freeSqls = daoByFreeSql.updateAndGetAllTasks(projectId);
			prepareDbFromFreeSql(ctx, _freeSqls);
		} else {
			_freeSqls = daoByFreeSql.updateAndGetTasks(projectId);
			prepareDbFromFreeSql(ctx, daoByFreeSql.getTasksByProjectId(projectId));
		}

		// 首先按照ServerID, DbName以及ClassName做一次GroupBy，但是ClassName不区分大小写
		final Map<String, List<GenTaskByFreeSql>> groupBy = freeSqlGroupBy(_freeSqls);

		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		// 随后，以ServerID, DbName以及ClassName为维度，为每个维度生成一个DAO类
		for (final Map.Entry<String, List<GenTaskByFreeSql>> entry : groupBy
				.entrySet()) {
			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() throws Exception {				
					ExecuteResult result  = new ExecuteResult("Build  Free SQL[" + entry.getKey() + "] Host");				
					progress.setOtherMessage(result.getTaskName());
					List<GenTaskByFreeSql> currentTasks = entry.getValue();
					if (currentTasks.size() < 1)
						return result;

					FreeSqlHost host = new FreeSqlHost();
					host.setDbName(currentTasks.get(0).getDatabaseSetName());
					host.setClassName(currentTasks.get(0).getClass_name());
					host.setPackageName(namespace);
					host.setDatabaseCategory(getDatabaseCategory(currentTasks.get(0).getDb_name()));

					List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();
					// 每个Method可能就有一个Pojo
					for (GenTaskByFreeSql task : currentTasks) {
						JavaMethodHost method = new JavaMethodHost();
						method.setSql(task.getSql_content());
						method.setName(task.getMethod_name());
						method.setPackageName(namespace);
						method.setScalarType(task.getScalarType());
						method.setPojoType(task.getPojoType());
						method.setPaging(task.isPagination());
						method.setCrud_type(task.getCrud_type());
						method.setPojoClassName(WordUtils.capitalize(task
								.getPojo_name() + "Pojo"));
						List<JavaParameterHost> params = new ArrayList<JavaParameterHost>();
						for (String param : StringUtils.split(
								task.getParameters(), ";")) {
							if (param.contains("HotelAddress")) {
								System.out.println("");
							}
							String[] splitedParam = StringUtils.split(param,
									",");
							JavaParameterHost p = new JavaParameterHost();
							p.setName(splitedParam[0]);
							p.setSqlType(Integer.valueOf(splitedParam[1]));
							p.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(p
									.getSqlType()));
							p.setValidationValue(DbUtils.mockATest(p
									.getSqlType()));
							params.add(p);
						}
						method.setParameters(params);
						methods.add(method);

						if (!_freeSqlPojoHosts.containsKey(method
								.getPojoClassName())) {

							List<JavaParameterHost> paramHosts = new ArrayList<JavaParameterHost>();

							for (AbstractParameterHost _ahost : DbUtils
									.testAQuerySql(task.getDb_name(),
											task.getSql_content(),
											task.getParameters(),
											CurrentLanguage.Java, false)) {
								paramHosts.add((JavaParameterHost) _ahost);
							}

							method.setFields(paramHosts);
							_freeSqlPojoHosts.put(method.getPojoClassName(),
									method);
						}
					}
					host.setMethods(methods);
					_freeSqlHosts.add(host);
					result.setSuccessal(true);
					return result;
				}
			};
			results.add(worker);
		}

		return results;
	}
	
	private void prepareDbFromFreeSql(CodeGenContext codeGenCtx, List<GenTaskByFreeSql> freeSqls) {
		for (GenTaskByFreeSql task : freeSqls) {		
			addDatabaseSet(codeGenCtx, task.getDatabaseSetName());
		}
	}
	
	private Map<String, List<GenTaskByFreeSql>> freeSqlGroupBy(
			List<GenTaskByFreeSql> tasks) {
		Map<String, List<GenTaskByFreeSql>> groupBy = new HashMap<String, List<GenTaskByFreeSql>>();

		for (GenTaskByFreeSql task : tasks) {
			String key = String.format("%s_%s", task.getDb_name(), task
					.getClass_name().toLowerCase());
			if (groupBy.containsKey(key)) {
				groupBy.get(key).add(task);
			} else {
				groupBy.put(key, new ArrayList<GenTaskByFreeSql>());
				groupBy.get(key).add(task);
			}
		}
		return groupBy;
	}


}
