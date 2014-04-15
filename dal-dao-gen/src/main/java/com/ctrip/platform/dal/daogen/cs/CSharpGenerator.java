package com.ctrip.platform.dal.daogen.cs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.daogen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.resource.ProgressResource;
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.GenUtils;

public class CSharpGenerator extends AbstractGenerator {

	private Map<String, DatabaseHost> _dbHosts = new ConcurrentHashMap<String, DatabaseHost>();
	private Queue<CSharpFreeSqlHost> _freeSqlHosts = new ConcurrentLinkedQueue<CSharpFreeSqlHost>();
	private Map<String, CSharpFreeSqlPojoHost> _freeSqlPojoHosts = new ConcurrentHashMap<String, CSharpFreeSqlPojoHost>();
	private Set<String> _freeDaos = Collections
			.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	private Set<String> _tableDaos = Collections
			.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	private Set<String> _spDaos = Collections
			.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	private Queue<CSharpTableHost> _tableViewHosts = new ConcurrentLinkedQueue<CSharpTableHost>();
	private Queue<CSharpTableHost> _spHosts = new ConcurrentLinkedQueue<CSharpTableHost>();
	private Queue<GenTaskBySqlBuilder> _sqlBuilders = new ConcurrentLinkedQueue<GenTaskBySqlBuilder>();

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

	private CSharpMethodHost buildFreeSqlMethodHost(GenTaskByFreeSql task) {
		CSharpMethodHost method = new CSharpMethodHost();
		method.setSql(task.getSql_content());
		method.setName(task.getMethod_name());
		method.setPojoName(CommonUtils.normalizeVariable(WordUtils
				.capitalize(task.getPojo_name())));
		List<CSharpParameterHost> params = new ArrayList<CSharpParameterHost>();
		for (String param : StringUtils.split(task.getParameters(), ";")) {
			String[] splitedParam = StringUtils.split(param, ",");
			CSharpParameterHost p = new CSharpParameterHost();
			p.setName(splitedParam[0]);
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

	private CSharpFreeSqlPojoHost buildFreeSqlPojoHost(GenTaskByFreeSql task) {

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
		freeSqlHost.setNameSpace(namespace);

		return freeSqlHost;
	}

	private CSharpTableHost buildTableHost(GenTaskByTableViewSp tableViewSp,
			String table, DatabaseCategory dbCategory,
			List<StoredProcedure> allSpNames) throws Exception {

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

		List<GenTaskBySqlBuilder> currentTableBuilders = filterExtraMethods(
				_sqlBuilders, tableViewSp.getDb_name(), table);

		List<CSharpMethodHost> methods = buildMethodHosts(allColumns,
				currentTableBuilders);

		CSharpTableHost tableHost = new CSharpTableHost();
		tableHost.setExtraMethods(methods);
		tableHost.setNameSpace(namespace);
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

	private CSharpTableHost buildSpHost(GenTaskByTableViewSp tableViewSp,
			DatabaseCategory dbCategory, String spName) throws Exception {

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
			throw new Exception(String.format("视图 %s 不存在，请修改DAO后再试！"));
		}

		List<AbstractParameterHost> params = DbUtils.getSpParams(
				tableViewSp.getDb_name(), currentSp, CurrentLanguage.CSharp);

		List<CSharpParameterHost> realParams = new ArrayList<CSharpParameterHost>();
		for (AbstractParameterHost p : params) {
			realParams.add((CSharpParameterHost) p);
		}

		CSharpTableHost tableHost = new CSharpTableHost();
		tableHost.setNameSpace(namespace);
		tableHost.setDatabaseCategory(dbCategory);
		tableHost.setDbSetName(tableViewSp.getDb_name());
		tableHost.setClassName(getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), realSpName.replace("_", "")));
		tableHost.setTable(false);
		tableHost.setSpName(spName);
		tableHost.setSpParams(realParams);

		return tableHost;
	}

	private CSharpTableHost buildViewHost(GenTaskByTableViewSp tableViewSp,
			DatabaseCategory dbCategory, String view) throws Exception {

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
		tableHost.setNameSpace(namespace);
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

	private CSharpTableHost buildExtraSqlBuilderHost(
			GenTaskBySqlBuilder sqlBuilder) throws Exception {
		GenTaskByTableViewSp tableViewSp = new GenTaskByTableViewSp();
		tableViewSp.setCud_by_sp(false);
		tableViewSp.setPagination(false);
		tableViewSp.setDb_name(sqlBuilder.getDb_name());
		tableViewSp.setPrefix("");
		tableViewSp.setSuffix("Gen");

		DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
		if (!DbUtils.getDbType(sqlBuilder.getDb_name()).equalsIgnoreCase(
				"Microsoft SQL Server")) {
			dbCategory = DatabaseCategory.MySql;
		}

		List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(sqlBuilder
				.getDb_name());

		return buildTableHost(tableViewSp, sqlBuilder.getTable_name(),
				dbCategory, allSpNames);
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
			method.setSql(builder.getSql_content());
			List<CSharpParameterHost> parameters = new ArrayList<CSharpParameterHost>();
			if (method.getCrud_type().equals("select")
					|| method.getCrud_type().equals("delete")) {
				String[] conditions = StringUtils.split(builder.getCondition(),
						";");
				for (String condition : conditions) {
					String[] tokens = StringUtils.split(condition, ",");
					String name = tokens[0];
					String alias = "";
					if (tokens.length == 3)
						alias = tokens[2];
					for (CSharpParameterHost pHost : allColumns) {
						if (pHost.getName().equals(name)) {
							CSharpParameterHost host_al = new CSharpParameterHost(
									pHost);
							host_al.setAlias(alias);
							parameters.add(host_al);
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
						String alias = "";
						if (tokens.length == 3)
							alias = tokens[2];
						if (pHost.getName().equals(name)) {
							CSharpParameterHost host_al = new CSharpParameterHost(
									pHost);
							host_al.setAlias(alias);
							parameters.add(host_al);
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

	private List<Callable<Boolean>> generateTableDao(
			final Queue<CSharpTableHost> tableHosts,
			final VelocityContext context, final File mavenLikeDir,
			final Progress progress) {

		List<Callable<Boolean>> results = new ArrayList<Callable<Boolean>>();

		for (final CSharpTableHost host : tableHosts) {

			context.put("host", host);

			Callable<Boolean> worker = new Callable<Boolean>() {
				@Override
				public Boolean call() {

					progress.setOtherMessage("正在生成 " + host.getClassName());

					GenUtils.mergeVelocityContext(
							context,
							String.format("%s/Dao/%sDao.cs",
									mavenLikeDir.getAbsolutePath(),
									host.getClassName()),
							"templates/DAO.cs.tpl");

					GenUtils.mergeVelocityContext(
							context,
							String.format("%s/Entity/%s.cs",
									mavenLikeDir.getAbsolutePath(),
									host.getClassName()),
							"templates/Pojo.cs.tpl");

					GenUtils.mergeVelocityContext(
							context,
							String.format("%s/IDao/I%sDao.cs",
									mavenLikeDir.getAbsolutePath(),
									host.getClassName()),
							"templates/IDAO.cs.tpl");

					GenUtils.mergeVelocityContext(
							context,
							String.format("%s/Test/%sTest.cs",
									mavenLikeDir.getAbsolutePath(),
									host.getClassName()),
							"templates/DAOTest.cs.tpl");
					return true;
				}
			};
			results.add(worker);
		}

		return results;
	}

	private List<Callable<Boolean>> generateSpDao(
			final Queue<CSharpTableHost> spHosts,
			final VelocityContext context, final File mavenLikeDir,
			final Progress progress) {

		List<Callable<Boolean>> results = new ArrayList<Callable<Boolean>>();

		for (final CSharpTableHost host : spHosts) {
			context.put("host", host);

			Callable<Boolean> worker = new Callable<Boolean>() {
				@Override
				public Boolean call() {
					progress.setOtherMessage("正在生成 " + host.getClassName());

					GenUtils.mergeVelocityContext(
							context,
							String.format("%s/Dao/%sDao.cs",
									mavenLikeDir.getAbsolutePath(),
									host.getClassName()),
							"templates/DAOBySp.cs.tpl");

					GenUtils.mergeVelocityContext(
							context,
							String.format("%s/Entity/%s.cs",
									mavenLikeDir.getAbsolutePath(),
									host.getClassName()),
							"templates/PojoBySp.cs.tpl");

					GenUtils.mergeVelocityContext(
							context,
							String.format("%s/Test/%sTest.cs",
									mavenLikeDir.getAbsolutePath(),
									host.getClassName()),
							"templates/SpTest.cs.tpl");
					return true;
				}
			};
			results.add(worker);
		}

		return results;
	}

	private List<Callable<Boolean>> generateFreeSqlDao(
			final VelocityContext context, final File mavenLikeDir,
			final Progress progress) {

		List<Callable<Boolean>> results = new ArrayList<Callable<Boolean>>();

		for (final CSharpFreeSqlPojoHost host : _freeSqlPojoHosts.values()) {
			context.put("host", host);

			Callable<Boolean> worker = new Callable<Boolean>() {
				@Override
				public Boolean call() {
					progress.setOtherMessage("正在生成 " + host.getClassName());
					GenUtils.mergeVelocityContext(context,
							String.format("%s/Entity/%s.cs", mavenLikeDir
									.getAbsolutePath(), CommonUtils
									.normalizeVariable(host.getClassName())),
							"templates/Pojo.cs.tpl");
					return true;
				}
			};
			results.add(worker);
		}
		ProgressResource.addDoneFiles(progress, _freeSqlPojoHosts.size());

		for (final CSharpFreeSqlHost host : _freeSqlHosts) {
			context.put("host", host);

			Callable<Boolean> worker = new Callable<Boolean>() {
				@Override
				public Boolean call() {
					progress.setOtherMessage("正在生成 " + host.getClassName());

					GenUtils.mergeVelocityContext(context,
							String.format("%s/Dao/%sDao.cs", mavenLikeDir
									.getAbsolutePath(), CommonUtils
									.normalizeVariable(host.getClassName())),
							"templates/FreeSqlDAO.cs.tpl");

					GenUtils.mergeVelocityContext(context,
							String.format("%s/Test/%sTest.cs", mavenLikeDir
									.getAbsolutePath(), CommonUtils
									.normalizeVariable(host.getClassName())),
							"templates/FreeSqlTest.cs.tpl");
					return true;
				}
			};
			results.add(worker);

		}

		return results;
	}

	/**
	 * 生成C#的公共部分，如Dal.config，Program.cs以及DALFactory.cs
	 */
	private void generateCommonCode(final int id, final Progress progress) {

		final VelocityContext context = GenUtils.buildDefaultVelocityContext();

		final File csMavenLikeDir = new File(String.format("%s/%s/cs",
				generatePath, id));

		context.put("dbs", _dbHosts.values());
		context.put("namespace", namespace);
		context.put("freeSqlHosts", _freeDaos);
		context.put("tableHosts", _tableDaos);
		context.put("spHosts", _spDaos);

		GenUtils.mergeVelocityContext(
				context,
				String.format("%s/Config/Dal.config",
						csMavenLikeDir.getAbsolutePath()),
				"templates/Dal.config.tpl");

		GenUtils.mergeVelocityContext(
				context,
				String.format("%s/DalFactory.cs",
						csMavenLikeDir.getAbsolutePath()),
				"templates/DalFactory.cs.tpl");
	}

	private void prepareDbFromFreeSql(List<GenTaskByFreeSql> freeSqls) {
		for (GenTaskByFreeSql task : freeSqls) {
			_freeDaos.add(WordUtils.capitalize(task.getClass_name()));
			if (!_dbHosts.containsKey(task.getDb_name())) {

				String provider = "sqlProvider";
				if (!DbUtils.getDbType(task.getDb_name()).equalsIgnoreCase(
						"Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				DatabaseHost host = new DatabaseHost();
				host.setAllInOneName(task.getDb_name());
				host.setProviderType(provider);
				// int index = host.getAllInOneName().indexOf("_");
				// host.setDatasetName(host.getAllInOneName().substring(0,
				// index > -1 ? index : host.getAllInOneName().length()));
				host.setDatasetName(host.getAllInOneName());
				_dbHosts.put(task.getDb_name(), host);
			}
		}
	}

	private List<Callable<Boolean>> prepareFreeSql(int projectId,
			boolean regenerate, final Progress progress) {

		List<GenTaskByFreeSql> _freeSqls;
		if (regenerate) {
			_freeSqls = daoByFreeSql.updateAndGetAllTasks(projectId);
			prepareDbFromFreeSql(_freeSqls);
		} else {
			_freeSqls = daoByFreeSql.updateAndGetTasks(projectId);
			prepareDbFromFreeSql(daoByFreeSql.getTasksByProjectId(projectId));
		}

		// 首先按照ServerID, DbName以及ClassName做一次GroupBy，但是ClassName不区分大小写
		final Map<String, List<GenTaskByFreeSql>> groupBy = freeSqlGroupBy(_freeSqls);

		List<Callable<Boolean>> results = new ArrayList<Callable<Boolean>>();
		// 随后，以ServerID, DbName以及ClassName为维度，为每个维度生成一个DAO类
		for (final Map.Entry<String, List<GenTaskByFreeSql>> entry : groupBy
				.entrySet()) {
			Callable<Boolean> worker = new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {

					List<GenTaskByFreeSql> currentTasks = entry.getValue();
					if (currentTasks.size() < 1)
						return false;

					CSharpFreeSqlHost host = new CSharpFreeSqlHost();
					host.setDbSetName(currentTasks.get(0).getDb_name());
					host.setClassName(CommonUtils.normalizeVariable(WordUtils
							.capitalize(currentTasks.get(0).getClass_name())));
					host.setNameSpace(namespace);

					progress.setOtherMessage("正在整理表 " + host.getClassName());

					List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();
					// 每个Method可能就有一个Pojo
					for (GenTaskByFreeSql task : currentTasks) {
						methods.add(buildFreeSqlMethodHost(task));
						if (!_freeSqlPojoHosts.containsKey(task.getPojo_name())) {
							CSharpFreeSqlPojoHost freeSqlPojoHost = buildFreeSqlPojoHost(task);
							if (null != freeSqlPojoHost) {
								_freeSqlPojoHosts.put(task.getPojo_name(),
										freeSqlPojoHost);
							}
						}
					}
					host.setMethods(methods);
					_freeSqlHosts.add(host);

					return true;
				}
			};
			results.add(worker);
		}

		return results;
	}

	private void prepareDbFromTableViewSp(
			List<GenTaskByTableViewSp> tableViewSps,
			List<GenTaskBySqlBuilder> sqlBuilders) {
		Set<String> existsTable = new HashSet<String>();

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

			if (!_dbHosts.containsKey(task.getDb_name())) {

				String provider = "sqlProvider";
				if (!DbUtils.getDbType(task.getDb_name()).equalsIgnoreCase(
						"Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				DatabaseHost host = new DatabaseHost();
				host.setAllInOneName(task.getDb_name());
				host.setProviderType(provider);
				// int index = host.getAllInOneName().indexOf("_");
				// host.setDatasetName(host.getAllInOneName().substring(0,
				// index > -1 ? index : host.getAllInOneName().length()));
				host.setDatasetName(host.getAllInOneName());
				_dbHosts.put(task.getDb_name(), host);
			}
		}

		for (GenTaskBySqlBuilder task : sqlBuilders) {

			if (!existsTable.contains(task.getTable_name())) {
				_tableDaos
						.add(getPojoClassName("", "Gen", task.getTable_name()));
			}

			if (!_dbHosts.containsKey(task.getDb_name())) {
				String provider = "sqlProvider";
				if (!DbUtils.getDbType(task.getDb_name()).equalsIgnoreCase(
						"Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				DatabaseHost host = new DatabaseHost();
				host.setAllInOneName(task.getDb_name());
				host.setProviderType(provider);
				// int index = host.getAllInOneName().indexOf("_");
				// host.setDatasetName(host.getAllInOneName().substring(0,
				// index > -1 ? index : host.getAllInOneName().length()));
				host.setDatasetName(host.getAllInOneName());
				_dbHosts.put(task.getDb_name(), host);
			}
		}
	}

	private List<Callable<Boolean>> prepareTableViewSp(int projectId,
			boolean regenerate, final Progress progress) throws Exception {
		List<GenTaskByTableViewSp> _tableViewSps;
		List<GenTaskBySqlBuilder> _tempSqlBuilders;
		if (regenerate) {
			_tableViewSps = daoByTableViewSp.updateAndGetAllTasks(projectId);
			_tempSqlBuilders = daoBySqlBuilder.updateAndGetAllTasks(projectId);
			prepareDbFromTableViewSp(_tableViewSps, _tempSqlBuilders);
		} else {
			_tableViewSps = daoByTableViewSp.updateAndGetTasks(projectId);
			_tempSqlBuilders = daoBySqlBuilder.updateAndGetTasks(projectId);
			prepareDbFromTableViewSp(
					daoByTableViewSp.getTasksByProjectId(projectId),
					daoBySqlBuilder.getTasksByProjectId(projectId));
		}
		for (GenTaskBySqlBuilder _t : _tempSqlBuilders) {
			_sqlBuilders.add(_t);
		}

		List<Callable<Boolean>> results = new ArrayList<Callable<Boolean>>();
		for (final GenTaskByTableViewSp tableViewSp : _tableViewSps) {
			final String[] viewNames = StringUtils.split(
					tableViewSp.getView_names(), ",");
			final String[] tableNames = StringUtils.split(
					tableViewSp.getTable_names(), ",");
			final String[] spNames = StringUtils.split(
					tableViewSp.getSp_names(), ",");

			final DatabaseCategory dbCategory;
			if (!DbUtils.getDbType(tableViewSp.getDb_name()).equalsIgnoreCase(
					"Microsoft SQL Server")) {
				dbCategory = DatabaseCategory.MySql;
			} else {
				dbCategory = DatabaseCategory.SqlServer;
			}

			final List<StoredProcedure> allSpNames = DbUtils
					.getAllSpNames(tableViewSp.getDb_name());

			for (final String table : tableNames) {
				Callable<Boolean> worker = new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						progress.setOtherMessage("正在整理表 " + table);
						CSharpTableHost currentTableHost;
						try {
							currentTableHost = buildTableHost(tableViewSp,
									table, dbCategory, allSpNames);
							if (null != currentTableHost) {
								_tableViewHosts.add(currentTableHost);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}
				};
				results.add(worker);
			}

			for (final String view : viewNames) {
				Callable<Boolean> viewWorker = new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						progress.setOtherMessage("正在整理视图 " + view);
						try {
							CSharpTableHost currentViewHost = buildViewHost(
									tableViewSp, dbCategory, view);
							if (null != currentViewHost) {
								_tableViewHosts.add(currentViewHost);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}
				};
				results.add(viewWorker);
			}

			for (final String spName : spNames) {
				Callable<Boolean> spWorker = new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						progress.setOtherMessage("正在整理存储过程 " + spName);
						try {
							CSharpTableHost currentSpHost = buildSpHost(
									tableViewSp, dbCategory, spName);
							if (null != currentSpHost) {
								_spHosts.add(currentSpHost);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}
				};
				results.add(spWorker);
			}
		}

		return results;
	}

	private List<Callable<Boolean>> prepareSqlBuilder(final Progress progress) {
		List<Callable<Boolean>> results = new ArrayList<Callable<Boolean>>();

		if (_sqlBuilders.size() > 0) {
			Map<String, GenTaskBySqlBuilder> _TempSqlBuildres = sqlBuilderBroupBy(_sqlBuilders);

			for (final Map.Entry<String, GenTaskBySqlBuilder> _table : _TempSqlBuildres
					.entrySet()) {
				Callable<Boolean> worker = new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						progress.setOtherMessage("正在整理表 "
								+ _table.getValue().getClass_name());
						CSharpTableHost extraTableHost;
						try {
							extraTableHost = buildExtraSqlBuilderHost(_table
									.getValue());
							if (null != extraTableHost) {
								_tableViewHosts.add(extraTableHost);
							}
							return true;
						} catch (Exception e) {
							e.printStackTrace();
						}
						return false;
					}
				};
				results.add(worker);
			}
		}
		return results;
	}

	@Override
	public boolean prepareDirectory(int projectId, boolean regenerate) {
		File mavenLikeDir = new File(String.format("%s/%s/cs", generatePath,
				projectId));

		try {
			if (mavenLikeDir.exists() && regenerate)
				FileUtils.forceDelete(mavenLikeDir);

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
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean prepareData(int projectId, boolean regenerate,
			Progress progress) {

		List<Callable<Boolean>> _freeSqlCallables = prepareFreeSql(projectId,
				regenerate, progress);

		try {
			List<Callable<Boolean>> _tableViewSpCallables = prepareTableViewSp(
					projectId, regenerate, progress);

			List<Callable<Boolean>> allResults = ListUtils.union(
					_freeSqlCallables, _tableViewSpCallables);

			if (allResults.size() > 0) {
				executor.invokeAll(allResults);
			}

			List<Callable<Boolean>> _sqlBuilderCallables = prepareSqlBuilder(progress);

			if (_sqlBuilderCallables.size() > 0) {
				executor.invokeAll(_sqlBuilderCallables);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean generateCode(int id, Progress progress) {
		final VelocityContext context = GenUtils.buildDefaultVelocityContext();

		final File csMavenLikeDir = new File(String.format("%s/%s/cs",
				generatePath, id));

		List<Callable<Boolean>> tableCallables = generateTableDao(
				_tableViewHosts, context, csMavenLikeDir, progress);
		ProgressResource.addDoneFiles(progress, _tableViewHosts.size());

		List<Callable<Boolean>> spCallables = generateSpDao(_spHosts, context,
				csMavenLikeDir, progress);
		ProgressResource.addDoneFiles(progress, _spHosts.size());

		List<Callable<Boolean>> freeCallables = generateFreeSqlDao(context,
				csMavenLikeDir, progress);

		List<Callable<Boolean>> allResults = ListUtils.union(
				ListUtils.union(tableCallables, spCallables), freeCallables);

		if (allResults.size() > 0) {
			try {
				executor.invokeAll(allResults);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		generateCommonCode(id, progress);

		return false;
	}

	@Override
	public boolean clearResource() {
		// TODO Auto-generated method stub
		return false;
	}

}