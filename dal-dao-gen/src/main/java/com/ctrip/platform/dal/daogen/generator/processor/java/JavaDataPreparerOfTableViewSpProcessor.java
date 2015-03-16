package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.java.JavaColumnNameResultSetExtractor;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;
import com.ctrip.platform.dal.daogen.host.java.JavaSpParamResultSetExtractor;
import com.ctrip.platform.dal.daogen.host.java.JavaTableHost;
import com.ctrip.platform.dal.daogen.host.java.SpDbHost;
import com.ctrip.platform.dal.daogen.host.java.SpHost;
import com.ctrip.platform.dal.daogen.host.java.ViewHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;

public class JavaDataPreparerOfTableViewSpProcessor extends AbstractJavaDataPreparer implements DalProcessor {

	private static Logger log = Logger.getLogger(JavaDataPreparerOfTableViewSpProcessor.class);
	
	private static DaoBySqlBuilder daoBySqlBuilder;
	private static DaoByTableViewSp daoByTableViewSp;
	
	static {
		daoBySqlBuilder = SpringBeanGetter.getDaoBySqlBuilder();
		daoByTableViewSp = SpringBeanGetter.getDaoByTableViewSp();
	}
	
	@Override
	public void process(CodeGenContext context) throws Exception {
		List<Callable<ExecuteResult>> _tableViewSpCallables;
		
		try {
			_tableViewSpCallables = prepareTableViewSp((CodeGenContext)context);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		TaskUtils.invokeBatch(log, _tableViewSpCallables);
	}

	private List<Callable<ExecuteResult>> prepareTableViewSp(CodeGenContext codeGenCtx) throws Exception {
		final JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
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
			prepareDbFromTableViewSp(ctx,
					daoByTableViewSp.getTasksByProjectId(projectId),
					daoBySqlBuilder.getTasksByProjectId(projectId));
		}
		
		if (!ctx.isIgnoreApproveStatus() && tableViewSpTasks!=null && tableViewSpTasks.size()>0) {
			Iterator<GenTaskByTableViewSp> ite = tableViewSpTasks.iterator();
			while (ite.hasNext()) {
				int approved = ite.next().getApproved(); 
				if (approved!=2 && approved!=0) {
					ite.remove();
				}
			}
		}
		
		if (!ctx.isIgnoreApproveStatus() && sqlBuilderTasks!=null && sqlBuilderTasks.size()>0) {
			Iterator<GenTaskBySqlBuilder> ite = sqlBuilderTasks.iterator();
			while (ite.hasNext()) {
				int approved = ite.next().getApproved(); 
				if (approved!=2 && approved!=0) {
					ite.remove();
				}
			}
		}
		
		Queue<GenTaskBySqlBuilder> _sqlBuilders = ctx.getSqlBuilders();
		for (GenTaskBySqlBuilder _t : sqlBuilderTasks) {
			_sqlBuilders.add(_t);
		}

		final Queue<JavaTableHost> _tableHosts = ctx.getTableHosts();
		final Queue<ViewHost> _viewHosts = ctx.getViewHosts();
		final Queue<SpHost> _spHosts = ctx.getSpHosts();
		final Map<String, SpDbHost> _spHostMaps = ctx.getSpHostMaps();
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		for (final GenTaskByTableViewSp tableViewSp : tableViewSpTasks) {
			final String[] viewNames = StringUtils.split(tableViewSp.getView_names(), ",");
			final String[] tableNames = StringUtils.split(tableViewSp.getTable_names(), ",");
			final String[] spNames = StringUtils.split(tableViewSp.getSp_names(), ",");

			results.addAll(prepareTable(ctx, progress, _tableHosts, tableViewSp, tableNames));
			
			results.addAll(prepareView(ctx, progress, _viewHosts, tableViewSp, viewNames));

			results.addAll(prepareSp(ctx, progress, _spHosts, _spHostMaps, tableViewSp, spNames));
		}

		return results;
	}

	private List<Callable<ExecuteResult>> prepareSp(final JavaCodeGenContext ctx,
			final Progress progress, final Queue<SpHost> _spHosts,
			final Map<String, SpDbHost> _spHostMaps,
			final GenTaskByTableViewSp tableViewSp, final String[] spNames) {
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		for (final String spName : spNames) {
			Callable<ExecuteResult> spWorker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() throws Exception {
				/*	progress.setOtherMessage("正在为所有表/存储过程生成DAO准备数据.<br/>buildSp:"
							+ spName);*/
					ExecuteResult result = new ExecuteResult("Build SP[" + tableViewSp.getAllInOneName() + "." + spName + "] Host");
					progress.setOtherMessage(result.getTaskName());
					try{
						SpHost spHost = buildSpHost(ctx, tableViewSp, spName);
						if (null != spHost) {
							if (!_spHostMaps.containsKey(spHost.getDbName())) {
								SpDbHost spDbHost = new SpDbHost(spHost.getDbName(),
										spHost.getPackageName());
								_spHostMaps.put(spHost.getDbName(), spDbHost);
							}
							_spHostMaps.get(spHost.getDbName()).addSpHost(spHost);
							_spHosts.add(spHost);
						}
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + " exception.", e);
						progress.setOtherMessage(e.getMessage());
					}
					return result;
				}
			};
			results.add(spWorker);
		}
		return results;
	}

	private List<Callable<ExecuteResult>> prepareView(final JavaCodeGenContext ctx,
			final Progress progress, final Queue<ViewHost> _viewHosts,
			final GenTaskByTableViewSp tableViewSp, final String[] viewNames) {
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		for (final String view : viewNames) {
			Callable<ExecuteResult> viewWorker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() throws Exception {
					/*progress.setOtherMessage("正在为所有表/存储过程生成DAO准备数据.<br/>buildView:"
							+ view);*/
					ExecuteResult result = new ExecuteResult("Build View[" + tableViewSp.getAllInOneName() + "." + view + "] Host");
					progress.setOtherMessage(result.getTaskName());
					try{
						ViewHost vhost = buildViewHost(ctx, tableViewSp, view);
						if (null != vhost)
							_viewHosts.add(vhost);
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + " exception.", e);
					}
					return result;
				}
			};
			results.add(viewWorker);
		}
		return results;
	}

	private List<Callable<ExecuteResult>> prepareTable(final JavaCodeGenContext ctx,
			final Progress progress, final Queue<JavaTableHost> _tableHosts,
			final GenTaskByTableViewSp tableViewSp, final String[] tableNames) {
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		for (final String tableName : tableNames) {
			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() throws Exception {
					/*progress.setOtherMessage("正在为所有表/存储过程生成DAO准备数据.<br/>buildTable:"
							+ table);*/
					ExecuteResult result = new ExecuteResult("Build Table[" + tableViewSp.getAllInOneName() + "." + tableName + "] Host");
					progress.setOtherMessage(result.getTaskName());
					try{
						JavaTableHost tableHost = buildTableHost(ctx, tableViewSp, tableName);
						result.setSuccessal(true);
					if (null != tableHost)
						_tableHosts.add(tableHost);
					result.setSuccessal(true);
					} catch (Exception e) {
						log.error(result.getTaskName() + " exception.", e);
					}
					return result;
				}
			};
			results.add(worker);
		}
		return results;
	}
	
	private void prepareDbFromTableViewSp(CodeGenContext codeGenCtx, 
			List<GenTaskByTableViewSp> tableViewSps, List<GenTaskBySqlBuilder> sqlBuilders) {
		for (GenTaskByTableViewSp task : tableViewSps) {
			addDatabaseSet(codeGenCtx, task.getDatabaseSetName());
		}
		for (GenTaskBySqlBuilder task : sqlBuilders) {
			addDatabaseSet(codeGenCtx, task.getDatabaseSetName());
		}
	}
	
	private ViewHost buildViewHost(CodeGenContext codeGenCtx, GenTaskByTableViewSp tableViewSp,
			String viewName) throws Exception {
		JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
		if (!DbUtils.viewExists(tableViewSp.getAllInOneName(), viewName)) {
			log.error(String.format(
					"The view[%s] doesn't exist, pls check", viewName));
			return null;
		}

		ViewHost vhost = new ViewHost();
		String className = viewName.replace("_", "");
		className = getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), className);

		vhost.setPackageName(ctx.getNamespace());
		vhost.setDatabaseCategory(getDatabaseCategory(tableViewSp.getAllInOneName()));
		vhost.setDbSetName(tableViewSp.getDatabaseSetName());
		vhost.setPojoClassName(className);
		vhost.setViewName(viewName);

		List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
				tableViewSp.getAllInOneName(), viewName);
		List<AbstractParameterHost> params = DbUtils.getAllColumnNames(
				tableViewSp.getAllInOneName(), viewName, 
				new JavaColumnNameResultSetExtractor(tableViewSp.getAllInOneName(), viewName));
		List<JavaParameterHost> realParams = new ArrayList<JavaParameterHost>();
		if(null == params){
			throw new Exception(String.format("The column names of view[%s, %s] is null", 
					tableViewSp.getAllInOneName(), viewName));
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
	
	private SpHost buildSpHost(CodeGenContext codeGenCtx, GenTaskByTableViewSp tableViewSp, String spName)
			throws Exception {
		JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
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
			throw new Exception(String.format("The store procedure[%s, %s] doesn't exist, pls check", 
					tableViewSp.getAllInOneName(),  currentSp.getName()));
		}
		
		SpHost spHost = new SpHost();
		String className = realSpName.replace("_", "");
		className = getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), className);

		spHost.setPackageName(ctx.getNamespace());
		spHost.setDatabaseCategory(getDatabaseCategory(tableViewSp.getAllInOneName()));
		spHost.setDbName(tableViewSp.getDatabaseSetName());
		spHost.setPojoClassName(className);
		spHost.setSpName(spName);
		List<AbstractParameterHost> params = DbUtils.getSpParams(
				tableViewSp.getAllInOneName(), currentSp, 
				new JavaSpParamResultSetExtractor(tableViewSp.getAllInOneName(), currentSp.getName()));
		List<JavaParameterHost> realParams = new ArrayList<JavaParameterHost>();
		String callParams = "";
		if(null == params){
			throw new Exception(String.format("The sp[%s, %s] parameters is null", 
					tableViewSp.getAllInOneName(), currentSp.getName()));
		}
		for (AbstractParameterHost p : params) {
			callParams += "?,";
			realParams.add((JavaParameterHost) p);
		}
		spHost.setCallParameters(StringUtils.removeEnd(callParams, ","));
		spHost.setFields(realParams);

		return spHost;
	}
	

}
