package com.ctrip.platform.dal.daogen.generator.csharp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.dao.DaoOfDatabaseSet;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.enums.ConditionType;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlPojoHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpMethodHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpParameterHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpSpaOperationHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpTableHost;
import com.ctrip.platform.dal.daogen.host.csharp.DatabaseHost;
import com.ctrip.platform.dal.daogen.resource.ProgressResource;
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;

public class CSharpDalGenerator implements DalGenerator {
	
	private Logger log = Logger.getLogger(CSharpDalGenerator.class);
	
	private static ExecutorService executor = Executors.newCachedThreadPool();
	
	private static DaoOfProject daoOfProject;
	private static DaoBySqlBuilder daoBySqlBuilder;
	private static DaoByFreeSql daoByFreeSql;
	private static DaoByTableViewSp daoByTableViewSp;
	private static DaoOfDatabaseSet daoOfDatabaseSet;
	
	static {
		daoOfProject = SpringBeanGetter.getDaoOfProject();
		daoBySqlBuilder = SpringBeanGetter.getDaoBySqlBuilder();
		daoByFreeSql = SpringBeanGetter.getDaoByFreeSql();
		daoByTableViewSp = SpringBeanGetter.getDaoByTableViewSp();
		daoOfDatabaseSet = SpringBeanGetter.getDaoOfDatabaseSet();
	}

	@Override
	public CodeGenContext createContext(int projectId, boolean regenerate,
			Progress progress, Map<String, ?> hints) throws Exception {
		return new CSharpCodeGenContext(projectId, regenerate, progress, hints);
	}

	@Override
	public boolean prepareDirectory(CodeGenContext codeGenCtx) throws Exception {
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		int projectId = ctx.getProjectId();
		boolean regenerate = ctx.isRegenerate();
		File mavenLikeDir = new File(String.format("%s/%s/cs", CodeGenContext.generatePath, projectId));

		if (mavenLikeDir.exists() && regenerate)
			FileUtils.forceDelete(mavenLikeDir);
		
		log.info("The maven like directory: " + mavenLikeDir.getAbsolutePath());
		
		File idaoMavenLike = new File(mavenLikeDir, "IDao");
		File daoMavenLike = new File(mavenLikeDir, "Dao");
		File entityMavenLike = new File(mavenLikeDir, "Entity");
		File testMavenLike = new File(mavenLikeDir, "Test");
		File configMavenLike = new File(mavenLikeDir, "Config");

		if (!idaoMavenLike.exists()) {
			FileUtils.forceMkdir(idaoMavenLike);
		}
		if (!daoMavenLike.exists()) {
			FileUtils.forceMkdir(daoMavenLike);
		}
		if (!entityMavenLike.exists()) {
			FileUtils.forceMkdir(entityMavenLike);
		}
		if (!testMavenLike.exists()) {
			FileUtils.forceMkdir(testMavenLike);
		}
		if (!configMavenLike.exists()) {
			FileUtils.forceMkdir(configMavenLike);
		}

		return true;
	}

	@Override
	public boolean prepareData(CodeGenContext codeGenCtx) throws Exception {
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		Project project = daoOfProject.getProjectByID(ctx.getProjectId());
		DalConfigHost dalConfigHost = null;
		if(project.getDal_config_name() != null && !project.getDal_config_name().isEmpty())
			dalConfigHost = new DalConfigHost(project.getDal_config_name());
		else if(project.getNamespace() != null && !project.getNamespace().isEmpty())
			dalConfigHost = new DalConfigHost(project.getNamespace());
		else 
			dalConfigHost = new DalConfigHost("");
		
		ctx.setDalConfigHost(dalConfigHost);
		
		List<Callable<ExecuteResult>> _freeSqlCallables = prepareFreeSql(ctx);

		List<Callable<ExecuteResult>> _tableViewSpCallables = prepareTableViewSp(ctx);

		@SuppressWarnings("unchecked")
		List<Callable<ExecuteResult>> allResults = ListUtils.union(
				_freeSqlCallables, _tableViewSpCallables);

		if (allResults.size() > 0) {
			TaskUtils.invokeBatch(log, executor, allResults);
		}

		List<Callable<ExecuteResult>> _sqlBuilderCallables = prepareSqlBuilder(ctx);

		if (_sqlBuilderCallables.size() > 0) {
			TaskUtils.invokeBatch(log, executor, _sqlBuilderCallables);
		}

		return true;
	}

	@Override
	public boolean generateCode(CodeGenContext codeGenCtx) throws Exception{
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		int projectId = ctx.getProjectId();
		Progress progress = ctx.getProgress();
		Map<String,?> hints = ctx.getHints();
		
		final File dir = new File(String.format("%s/%s/cs", CodeGenContext.generatePath, projectId));
		
		if(null != hints && hints.containsKey("newPojo")){
			Object _newPojo = hints.get("newPojo");
			boolean newPojo = Boolean.parseBoolean(_newPojo.toString());
			ctx.setNewPojo(newPojo);
		}

		List<Callable<ExecuteResult>> tableCallables = generateTableDao(ctx, dir);
		
		ProgressResource.addDoneFiles(progress, ctx.get_tableViewHosts().size());

		List<Callable<ExecuteResult>> spCallables = generateSpDao(ctx, dir);
		
		ProgressResource.addDoneFiles(progress, ctx.get_spHosts().size());

		List<Callable<ExecuteResult>> freeCallables = generateFreeSqlDao(ctx,dir);

		@SuppressWarnings("unchecked")
		List<Callable<ExecuteResult>> allResults = ListUtils.union(
				ListUtils.union(tableCallables, spCallables), freeCallables);

		if (allResults.size() > 0) {
			try {
				TaskUtils.invokeBatch(log, executor, allResults);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		log.info("Generate common code...");
		generateCommonCode(ctx);
		log.info("Generate common code completed.");
		
		return true;
	}
	
	private List<Callable<ExecuteResult>> prepareFreeSql(CodeGenContext codeGenCtx) throws Exception {
		final CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		int projectId = ctx.getProjectId();
		boolean regenerate = ctx.isRegenerate();
		final Progress progress = ctx.getProgress();
		final String namespace = ctx.getNamespace();
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
		final Map<String, CSharpFreeSqlPojoHost> _freeSqlPojoHosts = ctx.get_freeSqlPojoHosts();
		final Queue<CSharpFreeSqlHost> _freeSqlHosts = ctx.get_freeSqlHosts();
		// 随后，以ServerID, DbName以及ClassName为维度，为每个维度生成一个DAO类
		for (final Map.Entry<String, List<GenTaskByFreeSql>> entry : groupBy
				.entrySet()) {
			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() throws Exception {
					ExecuteResult result  = new ExecuteResult("Build  Free SQL[" + entry.getKey() + "] Host");				
					progress.setOtherMessage(result.getTaskName());
					
					try{
						List<GenTaskByFreeSql> currentTasks = entry.getValue();
						if (currentTasks.size() < 1)
							return result;
	
						CSharpFreeSqlHost host = new CSharpFreeSqlHost();
						host.setDbSetName(currentTasks.get(0).getDb_name());
						host.setClassName(CommonUtils.normalizeVariable(WordUtils
								.capitalize(currentTasks.get(0).getClass_name())));
						host.setNameSpace(namespace);
	
						List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();
						// 每个Method可能就有一个Pojo
						for (GenTaskByFreeSql task : currentTasks) {
							methods.add(buildFreeSqlMethodHost(task));
							if (!_freeSqlPojoHosts.containsKey(task.getPojo_name())) {
								CSharpFreeSqlPojoHost freeSqlPojoHost = buildFreeSqlPojoHost(ctx, task);
								if (null != freeSqlPojoHost) {
									_freeSqlPojoHosts.put(task.getPojo_name(),
											freeSqlPojoHost);
								}
							}
						}
						host.setMethods(methods);
						_freeSqlHosts.add(host);
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + "exception", e);
					}
					return result;
				}
			};
			results.add(worker);
		}

		return results;
	}
	
	private void prepareDbFromFreeSql(CodeGenContext codeGenCtx, List<GenTaskByFreeSql> freeSqls) throws Exception {
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		Map<String, DatabaseHost> _dbHosts = ctx.get_dbHosts();
		Set<String> _freeDaos = ctx.get_freeDaos();
		for (GenTaskByFreeSql task : freeSqls) {
			addDatabaseSet(ctx, task.getDatabaseSetName());
			_freeDaos.add(WordUtils.capitalize(task.getClass_name()));
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
	
	private void addDatabaseSet(CodeGenContext codeGenCtx, String databaseSetName){
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		List<DatabaseSet> sets = daoOfDatabaseSet.getAllDatabaseSetByName(databaseSetName);
		if(null == sets || sets.isEmpty()){
			log.error(String.format("The databaseSet name[%s] does not exist", databaseSetName));
			return;
		}
		DalConfigHost dalConfigHost = ctx.getDalConfigHost();
		dalConfigHost.addDatabaseSet(sets);
		for (DatabaseSet databaseSet : sets) {
			List<DatabaseSetEntry> entries = daoOfDatabaseSet.getAllDatabaseSetEntryByDbsetid(databaseSet.getId());
			if(null == entries || entries.isEmpty()){
				log.error(String.format("The databaseSet[%s] does't contain any entries", databaseSet.getId()));
				continue;
			}
			dalConfigHost.addDatabaseSetEntry(entries);
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
	
	private CSharpMethodHost buildFreeSqlMethodHost(GenTaskByFreeSql task) {
		CSharpMethodHost method = new CSharpMethodHost();
		List<String> inParams = new ArrayList<String>();
		Matcher m = CSharpCodeGenContext.inRegxPattern.matcher(task.getSql_content());
		String temp=task.getSql_content();
		int index = 0;
		while(m.find())
    	{
			String paramName = m.group(1);
			inParams.add(paramName.substring(1));
			temp = temp.replace(paramName, String.format("({%d}) ", index++));
    	}
		method.setSql(temp);
		method.setName(task.getMethod_name());
		method.setPojoName(CommonUtils.normalizeVariable(WordUtils
				.capitalize(task.getPojo_name())));
		List<CSharpParameterHost> params = new ArrayList<CSharpParameterHost>();
		for (String param : StringUtils.split(task.getParameters(), ";")) {
			String[] splitedParam = StringUtils.split(param, ",");
			CSharpParameterHost p = new CSharpParameterHost();
			p.setName(splitedParam[0]);
			p.setInParameter(inParams.contains(p.getName()));
			p.setDbType(DbType.getDbTypeFromJdbcType(Integer
					.valueOf(splitedParam[1])));
			p.setType(DbType.getCSharpType(p.getDbType()));
			Object mockValue = DbUtils.mockATest(Integer
					.valueOf(splitedParam[1]));
			if (p.getType().equals("string") || p.getType().equals("DateTime")) {
				p.setValue("\"" + mockValue + "\"");
			} else {
				p.setValue(mockValue);
			}
			params.add(p);
		}
		method.setParameters(params);
		return method;
	}
	
	private CSharpFreeSqlPojoHost buildFreeSqlPojoHost(CodeGenContext codeGenCtx, GenTaskByFreeSql task) {

		CSharpFreeSqlPojoHost freeSqlHost = new CSharpFreeSqlPojoHost();

		List<CSharpParameterHost> pHosts = new ArrayList<CSharpParameterHost>();

		for (AbstractParameterHost _ahost : DbUtils.testAQuerySql(
				task.getDb_name(), task.getSql_content(), task.getParameters(),
				CurrentLanguage.CSharp, false)) {
			pHosts.add((CSharpParameterHost) _ahost);
		}

		freeSqlHost.setColumns(pHosts);
		freeSqlHost.setTableName("");
		freeSqlHost.setClassName(CommonUtils.normalizeVariable(WordUtils
				.capitalize(task.getPojo_name())));
		freeSqlHost.setNameSpace(codeGenCtx.getNamespace());

		return freeSqlHost;
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
			final String[] viewNames = StringUtils.split(
					tableViewSp.getView_names(), ",");
			final String[] tableNames = StringUtils.split(
					tableViewSp.getTable_names(), ",");
			final String[] spNames = StringUtils.split(
					tableViewSp.getSp_names(), ",");

			final DatabaseCategory dbCategory;
			String dbType = DbUtils.getDbType(tableViewSp.getDb_name());
			if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
				dbCategory = DatabaseCategory.MySql;
			} else {
				dbCategory = DatabaseCategory.SqlServer;
			}

			final List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(tableViewSp.getDb_name());

			final Queue<CSharpTableHost> _tableViewHosts = ctx.get_tableViewHosts();
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

			for (final String view : viewNames) {
				Callable<ExecuteResult> viewWorker = new Callable<ExecuteResult>() {
					@Override
					public ExecuteResult call() throws Exception {
						//progress.setOtherMessage("正在整理视图 " + view);
						ExecuteResult result = new ExecuteResult("Build View[" + tableViewSp.getDb_name() + "." + view + "] Host");
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
	
	protected String getPojoClassName(String prefix, String suffix, String table) {
		String className = table;
		if (null != prefix && !prefix.isEmpty()) {
			className = className.substring(prefix.length());
		}
		if (null != suffix && !suffix.isEmpty()) {
			className = className + WordUtils.capitalize(suffix);
		}

		StringBuilder result = new StringBuilder();
		for (String str : StringUtils.split(className, "_")) {
			result.append(WordUtils.capitalize(str));
		}

		return WordUtils.capitalize(result.toString());
	}
	
	private CSharpTableHost buildTableHost(CodeGenContext codeGenCtx, 
			GenTaskByTableViewSp tableViewSp,
			String table, DatabaseCategory dbCategory,
			List<StoredProcedure> allSpNames) throws Exception {

		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		if (!DbUtils.tableExists(tableViewSp.getDb_name(), table)) {
			throw new Exception(String.format("表 %s 不存在，请编辑DAO再生成", table));
		}

		// 主键及所有列
		List<AbstractParameterHost> allColumnsAbstract = DbUtils
				.getAllColumnNames(tableViewSp.getDb_name(), table,
						CurrentLanguage.CSharp);

		List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
				tableViewSp.getDb_name(), table);

		List<CSharpParameterHost> allColumns = new ArrayList<CSharpParameterHost>();
		for (AbstractParameterHost h : allColumnsAbstract) {
			allColumns.add((CSharpParameterHost) h);
		}

		List<CSharpParameterHost> primaryKeys = new ArrayList<CSharpParameterHost>();
		for (CSharpParameterHost h : allColumns) {
			if (primaryKeyNames.contains(h.getName())) {
				h.setPrimary(true);
				primaryKeys.add(h);
			}
		}

		Queue<GenTaskBySqlBuilder> _sqlBuilders = ctx.get_sqlBuilders();
		List<GenTaskBySqlBuilder> currentTableBuilders = filterExtraMethods(
				_sqlBuilders, tableViewSp.getDb_name(), table);

		List<CSharpMethodHost> methods = buildMethodHosts(allColumns,
				currentTableBuilders);

		CSharpTableHost tableHost = new CSharpTableHost();
		tableHost.setExtraMethods(methods);
		tableHost.setNameSpace(ctx.getNamespace());
		tableHost.setDatabaseCategory(dbCategory);
		tableHost.setDbSetName(tableViewSp.getDb_name());
		tableHost.setTableName(table);
		tableHost.setClassName(CommonUtils.normalizeVariable(getPojoClassName(
				tableViewSp.getPrefix(), tableViewSp.getSuffix(), table)));
		tableHost.setTable(true);
		tableHost.setSpa(tableViewSp.isCud_by_sp());
		// SP方式增删改
		if (tableHost.isSpa()) {
			tableHost.setSpaInsert(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getDb_name(), table, allSpNames, "i"));
			tableHost.setSpaUpdate(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getDb_name(), table, allSpNames, "u"));
			tableHost.setSpaDelete(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getDb_name(), table, allSpNames, "d"));
		}

		tableHost.setPrimaryKeys(primaryKeys);
		tableHost.setColumns(allColumns);

		tableHost.setHasPagination(tableViewSp.isPagination());

		StoredProcedure expectSptI = new StoredProcedure();
		expectSptI.setName(String.format("spT_%s_i", table));

		StoredProcedure expectSptU = new StoredProcedure();
		expectSptU.setName(String.format("spT_%s_u", table));

		StoredProcedure expectSptD = new StoredProcedure();
		expectSptD.setName(String.format("spT_%s_d", table));

		tableHost.setHasSptI(allSpNames.contains(expectSptI));
		tableHost.setHasSptU(allSpNames.contains(expectSptU));
		tableHost.setHasSptD(allSpNames.contains(expectSptD));
		tableHost.setHasSpt(tableHost.isHasSptI() || tableHost.isHasSptU()
				|| tableHost.isHasSptD());

		return tableHost;
	}
	
	private List<GenTaskBySqlBuilder> filterExtraMethods(
			Queue<GenTaskBySqlBuilder> sqlBuilders, String dbName, String table) {
		List<GenTaskBySqlBuilder> currentTableBuilders = new ArrayList<GenTaskBySqlBuilder>();

		Iterator<GenTaskBySqlBuilder> iter = sqlBuilders.iterator();
		while (iter.hasNext()) {
			GenTaskBySqlBuilder currentSqlBuilder = iter.next();
			if (currentSqlBuilder.getDb_name().equals(dbName)
					&& currentSqlBuilder.getTable_name().equals(table)) {
				currentTableBuilders.add(currentSqlBuilder);
				iter.remove();
			}
		}

		return currentTableBuilders;
	}
	
	private List<CSharpMethodHost> buildMethodHosts(
			List<CSharpParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) {
		List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			CSharpMethodHost method = new CSharpMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			
			Matcher m = CSharpCodeGenContext.inRegxPattern.matcher(builder.getSql_content());
			int index = 0;
			String temp=builder.getSql_content();
			while(m.find())
	    	{
				temp = temp.replace(m.group(1), String.format("({%d}) ", index));
				index ++;
	    	}
			method.setSql(temp);
			
			List<CSharpParameterHost> parameters = new ArrayList<CSharpParameterHost>();
			if (method.getCrud_type().equals("select")
					|| method.getCrud_type().equals("delete")) {
				String[] conditions = StringUtils.split(builder.getCondition(),
						";");
				
				for (String condition : conditions) {
					String[] tokens = StringUtils.split(condition, ",");
					String name = tokens[0];
					int type = tokens.length >= 2 ? Integer.parseInt(tokens[1])
							: -1;
					String alias = "";
					if (tokens.length >= 3)
						alias = tokens[2];
					for (CSharpParameterHost pHost : allColumns) {
						if (pHost.getName().equals(name)) {
							CSharpParameterHost host_al = new CSharpParameterHost(
									pHost);
							host_al.setAlias(alias);
							host_al.setInParameter(ConditionType.In == ConditionType
									.valueOf(type));
							parameters.add(host_al);
							// Between need an extra parameter
							if (ConditionType.Between == ConditionType
									.valueOf(type)) {
								CSharpParameterHost host_bw = new CSharpParameterHost(
										pHost);
								String alias_bw = tokens.length >= 4 ? tokens[3]
										: "";
								host_bw.setAlias(alias_bw);
								parameters.add(host_bw);
							}
							break;
						}
					}
				}
			} else if (method.getCrud_type().equals("insert")) {
				String[] fields = StringUtils.split(builder.getFields(), ",");
				for (String field : fields) {
					for (CSharpParameterHost pHost : allColumns) {
						if (pHost.getName().equals(field)) {
							parameters.add(pHost);
							break;
						}
					}
				}
			} else {
				String[] fields = StringUtils.split(builder.getFields(), ",");
				String[] conditions = StringUtils.split(builder.getCondition(),
						";");
				for (String field : fields) {
					for (CSharpParameterHost pHost : allColumns) {
						if (pHost.getName().equals(field)) {
							parameters.add(pHost);
							break;
						}
					}
				}
				for (String condition : conditions) {
					for (CSharpParameterHost pHost : allColumns) {
						String[] tokens = StringUtils.split(condition, ",");
						String name = tokens[0];
						int type = tokens.length >= 2 ? Integer.parseInt(tokens[1])
								: -1;
						String alias = "";
						if (tokens.length >= 3)
							alias = tokens[2];
						if (pHost.getName().equals(name)) {
							CSharpParameterHost host_al = new CSharpParameterHost(
									pHost);
							host_al.setAlias(alias);
							parameters.add(host_al);
							if (ConditionType.Between == ConditionType
									.valueOf(type)) {
								CSharpParameterHost host_bw = new CSharpParameterHost(
										pHost);
								String alias_bw = tokens.length >= 4 ? tokens[3]
										: "";
								host_bw.setAlias(alias_bw);
								parameters.add(host_bw);
							}
							break;
						}
					}
				}
			}
			method.setParameters(parameters);
			methods.add(method);
		}
		return methods;
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
	
	private List<Callable<ExecuteResult>> prepareSqlBuilder(CodeGenContext codeGenCtx) {
		
		final CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		final Progress progress = ctx.getProgress();
		
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		Queue<GenTaskBySqlBuilder> _sqlBuilders = ctx.get_sqlBuilders();
		final Queue<CSharpTableHost> _tableViewHosts = ctx.get_tableViewHosts();
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
						CSharpTableHost extraTableHost;
						try {
							extraTableHost = buildExtraSqlBuilderHost(ctx, _table
									.getValue());
							if (null != extraTableHost) {
								_tableViewHosts.add(extraTableHost);
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
	
	private CSharpTableHost buildExtraSqlBuilderHost(CodeGenContext codeGenCtx,
			GenTaskBySqlBuilder sqlBuilder) throws Exception {
		GenTaskByTableViewSp tableViewSp = new GenTaskByTableViewSp();
		tableViewSp.setCud_by_sp(false);
		tableViewSp.setPagination(false);
		tableViewSp.setDb_name(sqlBuilder.getDb_name());
		tableViewSp.setPrefix("");
		tableViewSp.setSuffix("Gen");

		DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
		String dbType = DbUtils.getDbType(sqlBuilder.getDb_name());
		if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
			dbCategory = DatabaseCategory.MySql;
		}

		List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(sqlBuilder
				.getDb_name());

		return buildTableHost(codeGenCtx, tableViewSp, sqlBuilder.getTable_name(),
				dbCategory, allSpNames);
	}
	
	private List<Callable<ExecuteResult>> generateTableDao(CodeGenContext codeGenCtx,
			final File mavenLikeDir) {

		final CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		final Progress progress = ctx.getProgress();
		
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		Queue<CSharpTableHost> _tableViewHosts = ctx.get_tableViewHosts();
		
		for (final CSharpTableHost host : _tableViewHosts) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() {
					
					//progress.setOtherMessage("正在生成 " + host.getClassName());
					ExecuteResult result = new ExecuteResult("Generate Table[" + host.getTableName() + "] Dao");
					progress.setOtherMessage(result.getTaskName());
					try{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Dao/%sDao.cs",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
								"templates/csharp/DAO.cs.tpl");
	
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Entity/%s.cs",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
										ctx.isNewPojo() ? "templates/csharp/PojoNew.cs.tpl" : "templates/csharp/Pojo.cs.tpl");
	
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/IDao/I%sDao.cs",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
								"templates/csharp/IDAO.cs.tpl");
	
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Test/%sTest.cs",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
								"templates/csharp/DAOTest.cs.tpl");
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + "exception", e);
					}
					return result;
				}
			};
			results.add(worker);
		}

		return results;
	}
	
	private List<Callable<ExecuteResult>> generateSpDao(CodeGenContext codeGenCtx,
			final File mavenLikeDir) {

		final CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		final Progress progress = ctx.getProgress();
		
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		Queue<CSharpTableHost> _spHosts = ctx.get_spHosts();
		
		for (final CSharpTableHost host : _spHosts) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() {
					//progress.setOtherMessage("正在生成 " + host.getClassName());
					ExecuteResult result = new ExecuteResult("Generate SP[" + host.getClassName() + "] Dao");
					progress.setOtherMessage(result.getTaskName());
					try{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Dao/%sDao.cs",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
								"templates/csharp/DAOBySp.cs.tpl");
	
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Entity/%s.cs",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
								"templates/csharp/PojoBySp.cs.tpl");
	
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Test/%sTest.cs",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
								"templates/csharp/SpTest.cs.tpl");
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + "exception", e);
					}
					return result;
				}
			};
			results.add(worker);
		}

		return results;
	}
	
	private List<Callable<ExecuteResult>> generateFreeSqlDao(CodeGenContext codeGenCtx,
			final File mavenLikeDir) {
		
		final CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		final Progress progress = ctx.getProgress();

		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		Map<String, CSharpFreeSqlPojoHost> _freeSqlPojoHosts = ctx.get_freeSqlPojoHosts();
		
		for (final CSharpFreeSqlPojoHost host : _freeSqlPojoHosts.values()) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() {
					//progress.setOtherMessage("正在生成 " + host.getClassName());
					ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getClassName() + "] Pojo");
					progress.setOtherMessage(result.getTaskName());
					try{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						GenUtils.mergeVelocityContext(context,
								String.format("%s/Entity/%s.cs", mavenLikeDir
										.getAbsolutePath(), CommonUtils
										.normalizeVariable(host.getClassName())),
										ctx.isNewPojo() ? "templates/csharp/PojoNew.cs.tpl" : "templates/csharp/Pojo.cs.tpl");
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + "exception", e);
					}
					return result;
				}
			};
			results.add(worker);
		}
		ProgressResource.addDoneFiles(progress, _freeSqlPojoHosts.size());

		Queue<CSharpFreeSqlHost> _freeSqlHosts = ctx.get_freeSqlHosts();
		for (final CSharpFreeSqlHost host : _freeSqlHosts) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() {
					//progress.setOtherMessage("正在生成 " + host.getClassName());
					ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getClassName() + "] Dap, Test");
					progress.setOtherMessage(result.getTaskName());
					try
					{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						GenUtils.mergeVelocityContext(context,
								String.format("%s/Dao/%sDao.cs", mavenLikeDir
										.getAbsolutePath(), CommonUtils
										.normalizeVariable(host.getClassName())),
								"templates/csharp/FreeSqlDAO.cs.tpl");
	
						GenUtils.mergeVelocityContext(context,
								String.format("%s/Test/%sTest.cs", mavenLikeDir
										.getAbsolutePath(), CommonUtils
										.normalizeVariable(host.getClassName())),
								"templates/csharp/FreeSqlTest.cs.tpl");
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + "exception", e);
					}
					return result;
				}
			};
			results.add(worker);

		}

		return results;
	}
	
	/**
	 * 生成C#的公共部分，如Dal.config，Program.cs以及DALFactory.cs
	 */
	private void generateCommonCode(CodeGenContext codeGenCtx) {
		
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		final int id = ctx.getProjectId();

		final VelocityContext context = GenUtils.buildDefaultVelocityContext();

		final File csMavenLikeDir = new File(String.format("%s/%s/cs",
				CodeGenContext.generatePath, id));
		context.put("host", ctx.getDalConfigHost());
		context.put("dbs", ctx.get_dbHosts().values());
		context.put("namespace", ctx.getNamespace());
		context.put("freeSqlHosts", ctx.get_freeDaos());
		context.put("tableHosts", ctx.get_tableDaos());
		context.put("spHosts", ctx.get_spDaos());

		GenUtils.mergeVelocityContext(
				context,
				String.format("%s/Config/Dal.config",
						csMavenLikeDir.getAbsolutePath()),
				"templates/csharp/DalConfig.cs.tpl");
		
		GenUtils.mergeVelocityContext(
				context,
				String.format("%s/DalFactory.cs",
						csMavenLikeDir.getAbsolutePath()),
				"templates/csharp/DalFactory.cs.tpl");
	}

}
