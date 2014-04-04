package com.ctrip.platform.dal.daogen.cs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	private CSharpGenerator() {

	}

	private static CSharpGenerator instance = new CSharpGenerator();

	public static CSharpGenerator getInstance() {
		return instance;
	}

	private List<CSharpTableHost> tableHosts;
	private List<CSharpTableHost> spHosts;
	private List<CSharpFreeSqlHost> freeSqlHosts;
	private List<CSharpFreeSqlPojoHost> freeSqlPojoHosts;

	/**
	 * 生成C#的公共部分，如Dal.config，Program.cs以及DALFactory.cs
	 */
	private void generateCSharpCode(Progress progress) {
		progress.setOtherMessage("正在生成C#的公共部分");
		ProgressResource.addTotalFiles(progress, freeSqlPojoHosts.size()+
				freeSqlHosts.size()+tableHosts.size()+spHosts.size()+2);
		
		VelocityContext context = GenUtils.buildDefaultVelocityContext();

		File csMavenLikeDir = new File(String.format("%s/%s/cs",generatePath ,projectId));

		for (CSharpFreeSqlPojoHost host : freeSqlPojoHosts) {
			context.put("host", host);
			GenUtils.mergeVelocityContext(
					context,
					String.format("%s/Entity/%s.cs",
							csMavenLikeDir.getAbsolutePath(),
							CommonUtils.normalizeVariable(host.getClassName())), "templates/Pojo.cs.tpl");
		}
		ProgressResource.addDoneFiles(progress, freeSqlPojoHosts.size());

		for (CSharpFreeSqlHost host : freeSqlHosts) {
			context.put("host", host);
			GenUtils.mergeVelocityContext(
					context,
					String.format("%s/Dao/%sDao.cs",
							csMavenLikeDir.getAbsolutePath(),
							CommonUtils.normalizeVariable(host.getClassName())), "templates/FreeSqlDAO.cs.tpl");

			GenUtils.mergeVelocityContext(
					context,
					String.format("%s/Test/%sTest.cs",
							csMavenLikeDir.getAbsolutePath(),
							CommonUtils.normalizeVariable(host.getClassName())),
					"templates/FreeSqlTest.cs.tpl");
		}
		ProgressResource.addDoneFiles(progress, freeSqlHosts.size());
		
		context.put("dbs", dbHosts.values());
		context.put("namespace", namespace);
		context.put("freeSqlHosts", freeDaos);
		context.put("tableHosts", tableDaos);
		context.put("spHosts", spDaos);

		GenUtils.mergeVelocityContext(context, String.format("%s/Dal.config",
				csMavenLikeDir.getAbsolutePath()), "templates/Dal.config.tpl");

		GenUtils.mergeVelocityContext(
				context,
				String.format("%s/DalFactory.cs",
						csMavenLikeDir.getAbsolutePath()),
				"templates/DalFactory.cs.tpl");
		
		ProgressResource.addDoneFiles(progress, 2);

		progress.setOtherMessage("正在生成TableDao");
		generateTableDao(tableHosts, context, csMavenLikeDir);
		ProgressResource.addDoneFiles(progress, tableHosts.size());
		
		progress.setOtherMessage("正在生成SpDao");
		generateSpDao(spHosts, context, csMavenLikeDir);
		ProgressResource.addDoneFiles(progress, spHosts.size());
	}

	// -----------------------------------------------Free Sql generate
	// begin---------------------------------------------------
	@Override
	public void generateByFreeSql(List<GenTaskByFreeSql> tasks,Progress progress) {

		progress.setOtherMessage("正在生成FreeSql的代码");
		
		// 首先按照ServerID, DbName以及ClassName做一次GroupBy，但是ClassName不区分大小写
		Map<String, List<GenTaskByFreeSql>> groupBy = freeSqlGroupBy(tasks);

		freeSqlHosts = new ArrayList<CSharpFreeSqlHost>();
		freeSqlPojoHosts = new ArrayList<CSharpFreeSqlPojoHost>();
		Map<String, CSharpFreeSqlPojoHost> pojoHosts = new HashMap<String, CSharpFreeSqlPojoHost>();

		// 随后，以ServerID, DbName以及ClassName为维度，为每个维度生成一个DAO类
		for (Map.Entry<String, List<GenTaskByFreeSql>> entry : groupBy
				.entrySet()) {
			List<GenTaskByFreeSql> currentTasks = entry.getValue();
			if (currentTasks.size() < 1)
				continue;

			CSharpFreeSqlHost host = new CSharpFreeSqlHost();
			host.setDbSetName(currentTasks.get(0).getDb_name());
			host.setClassName(CommonUtils.normalizeVariable(WordUtils.capitalize(currentTasks.get(0)
					.getClass_name())));
			host.setNameSpace(super.namespace);

			List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();
			// 每个Method可能就有一个Pojo
			for (GenTaskByFreeSql task : currentTasks) {
				methods.add(buildFreeSqlMethodHost(task));
				if (!pojoHosts.containsKey(task.getPojo_name())) {
					CSharpFreeSqlPojoHost freeSqlPojoHost = buildFreeSqlPojoHost(task);
					if (null != freeSqlPojoHost) {
						pojoHosts.put(task.getPojo_name(), freeSqlPojoHost);
					}
				}
			}
			host.setMethods(methods);
			freeSqlHosts.add(host);
		}

		freeSqlPojoHosts.addAll(pojoHosts.values());

		generateCSharpCode(progress);
		
	}

	private Map<String, List<GenTaskByFreeSql>> freeSqlGroupBy(
			List<GenTaskByFreeSql> tasks) {
		Map<String, List<GenTaskByFreeSql>> groupBy = new HashMap<String, List<GenTaskByFreeSql>>();

		for (GenTaskByFreeSql task : tasks) {
			String key = String.format("%s_%s", 
					task.getDb_name(), task.getClass_name().toLowerCase());
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
			List<GenTaskBySqlBuilder> builders) {
		Map<String, GenTaskBySqlBuilder> groupBy = new HashMap<String, GenTaskBySqlBuilder>();

		for (GenTaskBySqlBuilder task : builders) {
			String key = String.format("%s_%s", 
					task.getDb_name(), task.getTable_name());

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
		method.setPojoName(CommonUtils.normalizeVariable(WordUtils.capitalize(task.getPojo_name())));
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
		freeSqlHost.setClassName(CommonUtils.normalizeVariable(WordUtils.capitalize(task.getPojo_name())));
		freeSqlHost.setNameSpace(super.namespace);

		return freeSqlHost;
	}

	// -----------------------------------------------Free Sql generate
	// end---------------------------------------------------

	@Override
	public void generateByTableView(List<GenTaskByTableViewSp> tasks,Progress progress)
			throws Exception {

		prepareFolder(projectId, "cs");

		tableHosts = new ArrayList<CSharpTableHost>();
		spHosts = new ArrayList<CSharpTableHost>();

		progress.setOtherMessage("正在为所有表/存储过程生成DAO.");
		// 首先为所有表/存储过程生成DAO
		for (GenTaskByTableViewSp tableViewSp : tasks) {
			String[] viewNames = StringUtils.split(tableViewSp.getView_names(),
					",");
			String[] tableNames = StringUtils.split(
					tableViewSp.getTable_names(), ",");
			String[] spNames = StringUtils
					.split(tableViewSp.getSp_names(), ",");

			DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
			if (!DbUtils.getDbType(tableViewSp.getDb_name()).equalsIgnoreCase(
					"Microsoft SQL Server")) {
				dbCategory = DatabaseCategory.MySql;
			}

			List<StoredProcedure> allSpNames = DbUtils
					.getAllSpNames(tableViewSp.getDb_name());
			for (String table : tableNames) {
				CSharpTableHost currentTableHost = buildTableHost(tableViewSp,
						table, dbCategory, allSpNames);
				if (null != currentTableHost) {
					tableHosts.add(currentTableHost);
				}
			}
			for (String view : viewNames) {
				CSharpTableHost currentViewHost = buildViewHost(tableViewSp,
						dbCategory, view);
				if (null != currentViewHost) {
					tableHosts.add(currentViewHost);
				}
			}
			for (String spName : spNames) {
				CSharpTableHost currentSpHost = buildSpHost(tableViewSp,
						dbCategory, spName);
				if (null != currentSpHost) {
					spHosts.add(currentSpHost);
				}
			}
		}

		if (sqlBuilders.size() > 0) {
			Map<String, GenTaskBySqlBuilder> _sqlBuildres = sqlBuilderBroupBy(sqlBuilders);

			for (Map.Entry<String, GenTaskBySqlBuilder> _table : _sqlBuildres
					.entrySet()) {
				CSharpTableHost extraTableHost = buildExtraSqlBuilderHost(_table
						.getValue());
				if (null != extraTableHost) {
					tableHosts.add(extraTableHost);
				}
			}
		}
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
				sqlBuilders, tableViewSp.getDb_name(), table);

		List<CSharpMethodHost> methods = buildMethodHosts(allColumns,
				currentTableBuilders);

		CSharpTableHost tableHost = new CSharpTableHost();
		tableHost.setExtraMethods(methods);
		tableHost.setNameSpace(super.namespace);
		tableHost.setDatabaseCategory(dbCategory);
		tableHost.setDbSetName(tableViewSp.getDb_name());
		tableHost.setTableName(table);
		tableHost.setClassName(CommonUtils.normalizeVariable(getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), table)));
		tableHost.setTable(true);
		tableHost.setSpa(tableViewSp.isCud_by_sp());
		// SP方式增删改
		if (tableHost.isSpa()) {
			tableHost.setSpaInsert(CSharpSpaOperationHost.getSpaOperation(tableViewSp.getDb_name(),
					table, allSpNames, "i"));
			tableHost.setSpaUpdate(CSharpSpaOperationHost.getSpaOperation(tableViewSp.getDb_name(),
					table, allSpNames, "u"));
			tableHost.setSpaDelete(CSharpSpaOperationHost.getSpaOperation(tableViewSp.getDb_name(),
					table, allSpNames, "d"));
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
		tableHost.setNameSpace(super.namespace);
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
		tableHost.setNameSpace(super.namespace);
		tableHost.setDatabaseCategory(dbCategory);
		tableHost.setDbSetName(tableViewSp.getDb_name());
		tableHost.setTableName(view);
		tableHost.setClassName(CommonUtils.normalizeVariable(getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), view)));
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
			List<GenTaskBySqlBuilder> sqlBuilders, String dbName, String table) {
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
					if(tokens.length == 3) alias = tokens[2];
					for (CSharpParameterHost pHost : allColumns) {
						if (pHost.getName().equals(name)) {
							pHost.setAlias(alias);
							parameters.add(pHost);
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
				for (CSharpParameterHost pHost : allColumns) {
					for (String field : fields) {
						if (pHost.getName().equals(field)) {
							parameters.add(pHost);
							break;
						}
					}
					for (String condition : conditions) {
						String[] tokens = StringUtils.split(condition, ",");
						String name = tokens[0];
						String alias = "";
						if(tokens.length == 3) alias = tokens[2];
						if (pHost.getName().equals(name)) {
							pHost.setAlias(alias);
							parameters.add(pHost);
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

	

	private void generateTableDao(List<CSharpTableHost> tableHosts,
			VelocityContext context, File mavenLikeDir) {
		for (CSharpTableHost host : tableHosts) {
			context.put("host", host);

			GenUtils.mergeVelocityContext(context, String.format(
					"%s/Dao/%sDao.cs", mavenLikeDir.getAbsolutePath(),
					host.getClassName()), "templates/DAO.cs.tpl");

			GenUtils.mergeVelocityContext(context, String.format(
					"%s/Entity/%s.cs", mavenLikeDir.getAbsolutePath(),
					host.getClassName()), "templates/Pojo.cs.tpl");

			GenUtils.mergeVelocityContext(context, String.format(
					"%s/IDao/I%sDao.cs", mavenLikeDir.getAbsolutePath(),
					host.getClassName()), "templates/IDAO.cs.tpl");

			GenUtils.mergeVelocityContext(context, String.format(
					"%s/Test/%sTest.cs", mavenLikeDir.getAbsolutePath(),
					host.getClassName()), "templates/DAOTest.cs.tpl");
		}
	}

	private void generateSpDao(List<CSharpTableHost> spHosts,
			VelocityContext context, File mavenLikeDir) {
		for (CSharpTableHost host : spHosts) {
			context.put("host", host);

			GenUtils.mergeVelocityContext(context, String.format(
					"%s/Dao/%sDao.cs", mavenLikeDir.getAbsolutePath(),
					host.getClassName()), "templates/DAOBySp.cs.tpl");

			GenUtils.mergeVelocityContext(context, String.format(
					"%s/Entity/%s.cs", mavenLikeDir.getAbsolutePath(),
					host.getClassName()), "templates/PojoBySp.cs.tpl");

			GenUtils.mergeVelocityContext(context, String.format(
					"%s/Test/%sTest.cs", mavenLikeDir.getAbsolutePath(),
					host.getClassName()), "templates/SpTest.cs.tpl");
		}
	}

}