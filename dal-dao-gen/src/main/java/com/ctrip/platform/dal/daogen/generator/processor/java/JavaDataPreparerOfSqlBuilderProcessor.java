package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.java.JavaTableHost;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class JavaDataPreparerOfSqlBuilderProcessor extends AbstractJavaDataPreparer implements Processor {

	private static Logger log = Logger.getLogger(JavaDataPreparerOfSqlBuilderProcessor.class);
	
	@Override
	public void process(Context context) {
		
		List<Callable<ExecuteResult>> _sqlBuilderCallables = prepareSqlBuilder((CodeGenContext )context);
		
		TaskUtils.invokeBatch(log, _sqlBuilderCallables);
	}

	private List<Callable<ExecuteResult>> prepareSqlBuilder(CodeGenContext codeGenCtx) {
		final JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
		final Progress progress = ctx.getProgress();
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		Queue<GenTaskBySqlBuilder> _sqlBuilders = ctx.get_sqlBuilders();
		final Queue<JavaTableHost> _tableHosts = ctx.get_tableHosts();
		if (_sqlBuilders.size() > 0) {
			Map<String, GenTaskBySqlBuilder> _TempSqlBuildres = sqlBuilderBroupBy(_sqlBuilders);

			for (final Map.Entry<String, GenTaskBySqlBuilder> _table : _TempSqlBuildres
					.entrySet()) {
				Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

					@Override
					public ExecuteResult call() throws Exception {
						/*progress.setOtherMessage("正在整理表 "
								+ _table.getValue().getClass_name());*/
						ExecuteResult result = new ExecuteResult("Build Extral SQL[" + _table.getValue().getDb_name() + "." + _table.getKey() + "] Host");
						progress.setOtherMessage(result.getTaskName());
						try{
							JavaTableHost extraTableHost = buildExtraSqlBuilderHost(ctx, 
									_table.getValue());
							if (null != extraTableHost) {
								_tableHosts.add(extraTableHost);
							}
							result.setSuccessal(true);
						}catch(Exception e){
							log.error(result.getTaskName() + " exception.", e);
							progress.setOtherMessage(e.getMessage());
						}
						return result;
					}
				};
				results.add(worker);
			}
		}
		return results;
	}
	
	private Map<String, GenTaskBySqlBuilder> sqlBuilderBroupBy(
			Queue<GenTaskBySqlBuilder> builders) {
		Map<String, GenTaskBySqlBuilder> groupBy = new HashMap<String, GenTaskBySqlBuilder>();

		for (GenTaskBySqlBuilder task : builders) {
			String key = String.format("%s_%s", task.getDb_name(),
					task.getTable_name());

			if (!groupBy.containsKey(key)) {
				groupBy.put(key, task);
			}
		}
		return groupBy;
	}
	
	private JavaTableHost buildExtraSqlBuilderHost(CodeGenContext codeGenCtx,
			GenTaskBySqlBuilder sqlBuilder) throws Exception {
		GenTaskByTableViewSp tableViewSp = new GenTaskByTableViewSp();
		tableViewSp.setCud_by_sp(false);
		tableViewSp.setPagination(false);
		tableViewSp.setDb_name(sqlBuilder.getDb_name());
		tableViewSp.setDatabaseSetName(sqlBuilder.getDatabaseSetName());
		tableViewSp.setPrefix("");
		tableViewSp.setSuffix("Gen");

		return buildTableHost(codeGenCtx, tableViewSp, sqlBuilder.getTable_name());
	}

}
