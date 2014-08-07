package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpParameterHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpTableHost;
import com.ctrip.platform.dal.daogen.host.csharp.DatabaseHost;
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class CSharpDataPreparerOfTableViewSpProcessor extends AbstractCSharpDataPreparer implements Processor {

	private static Logger log = Logger.getLogger(CSharpDataPreparerOfTableViewSpProcessor.class);
	
	private static DaoBySqlBuilder daoBySqlBuilder;
	private static DaoByTableViewSp daoByTableViewSp;
	
	static {
		daoBySqlBuilder = SpringBeanGetter.getDaoBySqlBuilder();
		daoByTableViewSp = SpringBeanGetter.getDaoByTableViewSp();
	}
	
	@Override
	public void process(Context context) {
		List<Callable<ExecuteResult>> _tableViewSpCallables;
		try {
			_tableViewSpCallables = prepareTableViewSp((CodeGenContext )context);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		TaskUtils.invokeBatch(log, _tableViewSpCallables);
	}
	
	private List<Callable<ExecuteResult>> prepareTableViewSp(CodeGenContext codeGenCtx) throws Exception {
		final CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		int projectId = ctx.getProjectId();
		boolean regenerate = ctx.isRegenerate();
		final Progress progress = ctx.getProgress();
		List<GenTaskByTableViewSp> _tableViewSps;
		List<GenTaskBySqlBuilder> _tempSqlBuilders;
		if (regenerate) {
			_tableViewSps = daoByTableViewSp.updateAndGetAllTasks(projectId);
			_tempSqlBuilders = daoBySqlBuilder.updateAndGetAllTasks(projectId);
			prepareDbFromTableViewSp(ctx, _tableViewSps, _tempSqlBuilders);
		} else {
			_tableViewSps = daoByTableViewSp.updateAndGetTasks(projectId);
			_tempSqlBuilders = daoBySqlBuilder.updateAndGetTasks(projectId);
			prepareDbFromTableViewSp(ctx, 
					daoByTableViewSp.getTasksByProjectId(projectId),
					daoBySqlBuilder.getTasksByProjectId(projectId));
		}
		Queue<GenTaskBySqlBuilder> _sqlBuilders = ctx.get_sqlBuilders();
		for (GenTaskBySqlBuilder _t : _tempSqlBuilders) {
			_sqlBuilders.add(_t);
		}

		final Queue<CSharpTableHost> _spHosts = ctx.get_spHosts();
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		for (final GenTaskByTableViewSp tableViewSp : _tableViewSps) {
			final String[] viewNames = StringUtils.split(tableViewSp.getView_names(), ",");
			final String[] tableNames = StringUtils.split(tableViewSp.getTable_names(), ",");
			final String[] spNames = StringUtils.split(tableViewSp.getSp_names(), ",");

			final DatabaseCategory dbCategory;
			String dbType = DbUtils.getDbType(tableViewSp.getDb_name());
			if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
				dbCategory = DatabaseCategory.MySql;
			} else {
				dbCategory = DatabaseCategory.SqlServer;
			}

			final Queue<CSharpTableHost> _tableViewHosts = ctx.get_tableViewHosts();
			
			results.addAll(prepareTable(ctx, progress, tableViewSp, tableNames,
					dbCategory, _tableViewHosts));

			results.addAll(prepareView(ctx, progress, tableViewSp, viewNames,
					dbCategory, _tableViewHosts));

			results.addAll(prepareSp(ctx, progress, _spHosts, tableViewSp, spNames, dbCategory));
		}

		return results;
	}

	private List<Callable<ExecuteResult>> prepareSp(final CSharpCodeGenContext ctx,
			final Progress progress, final Queue<CSharpTableHost> _spHosts,
			final GenTaskByTableViewSp tableViewSp, final String[] spNames,
			final DatabaseCategory dbCategory) {
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		for (final String spName : spNames) {
			Callable<ExecuteResult> spWorker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() throws Exception {
					ExecuteResult result = new ExecuteResult("Build SP[" + tableViewSp.getDb_name() + "." + spName + "] Host");
					//progress.setOtherMessage("正在整理存储过程 " + spName);
					progress.setOtherMessage(result.getTaskName());
					try {
						CSharpTableHost currentSpHost = buildSpHost(ctx,
								tableViewSp, dbCategory, spName);
						if (null != currentSpHost) {
							_spHosts.add(currentSpHost);
						}
						result.setSuccessal(true);
					} catch (Exception e) {
						log.error(result.getTaskName() + " exception.", e);
					}
					return result;
				}
			};
			results.add(spWorker);
		}
		return results;
	}

	private List<Callable<ExecuteResult>> prepareView(final CSharpCodeGenContext ctx,
			final Progress progress, final GenTaskByTableViewSp tableViewSp,
			final String[] viewNames, final DatabaseCategory dbCategory,
			final Queue<CSharpTableHost> _tableViewHosts) {
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		for (final String view : viewNames) {
			Callable<ExecuteResult> viewWorker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() throws Exception {
					// progress.setOtherMessage("正在整理视图 " + view);
					ExecuteResult result = new ExecuteResult("Build View["
							+ tableViewSp.getDb_name() + "." + view + "] Host");
					progress.setOtherMessage(result.getTaskName());
					try {
						CSharpTableHost currentViewHost = buildViewHost(ctx,
								tableViewSp, dbCategory, view);
						if (null != currentViewHost) {
							_tableViewHosts.add(currentViewHost);
						}
						result.setSuccessal(true);
					} catch (Exception e) {
						log.error(result.getTaskName() + " exception.", e);
					}
					return result;
				}
			};
			results.add(viewWorker);
		}
		return results;
	}

	private List<Callable<ExecuteResult>> prepareTable(final CSharpCodeGenContext ctx,
			final Progress progress, final GenTaskByTableViewSp tableViewSp, 
			final String[] tableNames, final DatabaseCategory dbCategory,
			final Queue<CSharpTableHost> _tableViewHosts) throws Exception {
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		final List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(tableViewSp.getDb_name());
		for (final String table : tableNames) {
			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() throws Exception {
					//progress.setOtherMessage("正在整理表 " + table);
					ExecuteResult result = new ExecuteResult("Build Table[" + tableViewSp.getDb_name() + "." + table + "] Host");
					progress.setOtherMessage(result.getTaskName());
					CSharpTableHost currentTableHost;
					try {
						currentTableHost = buildTableHost(ctx, tableViewSp,
								table, dbCategory, allSpNames);
						if (null != currentTableHost) {
							_tableViewHosts.add(currentTableHost);
						}
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
			List<GenTaskByTableViewSp> tableViewSps,
			List<GenTaskBySqlBuilder> sqlBuilders) throws Exception {
		
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		Set<String> existsTable = new HashSet<String>();

		Set<String> _tableDaos = ctx.get_tableDaos();
		Set<String> _spDaos = ctx.get_spDaos();
		Map<String, DatabaseHost> _dbHosts = ctx.get_dbHosts();
		for (GenTaskByTableViewSp task : tableViewSps) {
			for (String table : StringUtils.split(task.getTable_names(), ",")) {
				_tableDaos.add(getPojoClassName(task.getPrefix(),
						task.getSuffix(), table));
				existsTable.add(table);
			}
			for (String table : StringUtils.split(task.getView_names(), ",")) {
				_tableDaos.add(getPojoClassName(task.getPrefix(),
						task.getSuffix(), table));
			}
			for (String table : StringUtils.split(task.getSp_names(), ",")) {
				String realSpName = table;
				if (table.contains(".")) {
					String[] splitSp = StringUtils.split(table, '.');
					realSpName = splitSp[1];
				}
				_spDaos.add(getPojoClassName(task.getPrefix(),
						task.getSuffix(), realSpName.replace("_", "")));
			}
			
			addDatabaseSet(ctx,task.getDatabaseSetName());
			
			if (!_dbHosts.containsKey(task.getDb_name())) {
				String provider = "sqlProvider";
				String dbType = DbUtils.getDbType(task.getDb_name());
				if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				DatabaseHost host = new DatabaseHost();
				host.setAllInOneName(task.getDb_name());
				host.setProviderType(provider);
				host.setDatasetName(host.getAllInOneName());
				_dbHosts.put(task.getDb_name(), host);
			}
		}

		for (GenTaskBySqlBuilder task : sqlBuilders) {

			if (!existsTable.contains(task.getTable_name())) {
				_tableDaos.add(getPojoClassName("", "Gen", task.getTable_name()));
			}
			
			addDatabaseSet(ctx, task.getDatabaseSetName());
			
			if (!_dbHosts.containsKey(task.getDb_name())) {
				String provider = "sqlProvider";
				String dbType = DbUtils.getDbType(task.getDb_name());
				if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				DatabaseHost host = new DatabaseHost();
				host.setAllInOneName(task.getDb_name());
				host.setProviderType(provider);
				host.setDatasetName(host.getAllInOneName());
				_dbHosts.put(task.getDb_name(), host);
			}
		}
	}
	
	
	private CSharpTableHost buildViewHost(CodeGenContext codeGenCtx, 
			GenTaskByTableViewSp tableViewSp,
			DatabaseCategory dbCategory, String view) throws Exception {

		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		if (!DbUtils.viewExists(tableViewSp.getDb_name(), view)) {
			throw new Exception(String.format("视图 %s 不存在，请编辑DAO再生成", view));
		}

		List<AbstractParameterHost> allColumnsAbstract = DbUtils
				.getAllColumnNames(tableViewSp.getDb_name(), view,
						CurrentLanguage.CSharp);

		List<CSharpParameterHost> allColumns = new ArrayList<CSharpParameterHost>();
		for (AbstractParameterHost h : allColumnsAbstract) {
			allColumns.add((CSharpParameterHost) h);
		}

		CSharpTableHost tableHost = new CSharpTableHost();
		tableHost.setNameSpace(ctx.getNamespace());
		tableHost.setDatabaseCategory(dbCategory);
		tableHost.setDbSetName(tableViewSp.getDb_name());
		tableHost.setTableName(view);
		tableHost.setClassName(CommonUtils.normalizeVariable(getPojoClassName(
				tableViewSp.getPrefix(), tableViewSp.getSuffix(), view)));
		tableHost.setTable(false);
		tableHost.setSpa(false);
		tableHost.setColumns(allColumns);
		tableHost.setHasPagination(tableViewSp.isPagination());
		return tableHost;
	}
	
	private CSharpTableHost buildSpHost(CodeGenContext codeGenCtx, 
			GenTaskByTableViewSp tableViewSp,
			DatabaseCategory dbCategory, String spName) throws Exception {

		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
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

		if (!DbUtils.spExists(tableViewSp.getDb_name(), currentSp)) {
			throw new Exception(String.format("存储过程 %s 不存在，请修改DAO后再试！", currentSp.getName()));
		}

		List<AbstractParameterHost> params = DbUtils.getSpParams(
				tableViewSp.getDb_name(), currentSp, CurrentLanguage.CSharp);

		List<CSharpParameterHost> realParams = new ArrayList<CSharpParameterHost>();
		for (AbstractParameterHost p : params) {
			realParams.add((CSharpParameterHost) p);
		}

		CSharpTableHost tableHost = new CSharpTableHost();
		tableHost.setNameSpace(ctx.getNamespace());
		tableHost.setDatabaseCategory(dbCategory);
		tableHost.setDbSetName(tableViewSp.getDb_name());
		tableHost.setClassName(getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), realSpName.replace("_", "")));
		tableHost.setTable(false);
		tableHost.setSpName(spName);
		tableHost.setSpParams(realParams);

		return tableHost;
	}
	
}
