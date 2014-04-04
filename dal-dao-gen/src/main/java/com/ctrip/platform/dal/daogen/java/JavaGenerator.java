package com.ctrip.platform.dal.daogen.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.daogen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.resource.ProgressResource;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.GenUtils;

public class JavaGenerator extends AbstractGenerator {
	
	private JavaGenerator() { }

	private static JavaGenerator instance = new JavaGenerator();

	public static JavaGenerator getInstance() {
		return instance;
	}

	@Override

	public void generateByTableView(List<GenTaskByTableViewSp> tasks,Progress progress) throws Exception {
		
		prepareFolder(projectId, "java");
		
		Map<String, String> dbs = buildCommonVelocity(tasks);		
		List<JavaTableHost> tableHosts = new ArrayList<JavaTableHost>();
		HashMap<String, SpDbHost> spHostMaps = new HashMap<String, SpDbHost>();
		List<SpHost> spHosts = new ArrayList<SpHost>();
		List<ViewHost> viewHosts = new ArrayList<ViewHost>();

		progress.setOtherMessage("正在为所有表/存储过程生成DAO.");
		// 首先为所有表/存储过程生成DAO
		for (GenTaskByTableViewSp tableViewSp : tasks) {
			String[] tableNames = StringUtils.split(
					tableViewSp.getTable_names(), ",");
			String[] spNames = StringUtils
					.split(tableViewSp.getSp_names(), ",");
			String[] viewNames = StringUtils
					.split(tableViewSp.getView_names(), ",");

			for (String table : tableNames) {
				
				JavaTableHost tableHost = this.buildTableHost(tableViewSp, table);
				if(null != tableHost)
					tableHosts.add(tableHost);
			}
		
			for (String spName : spNames) {
				
				SpHost spHost = this.buildSpHost(tableViewSp, spName);
				if(null != spHost)
				{
					if(!spHostMaps.containsKey(spHost.getDbName()))
					{
						SpDbHost spDbHost = new SpDbHost(spHost.getDbName(), spHost.getPackageName());
						spHostMaps.put(spHost.getDbName(), spDbHost);
					}
					spHostMaps.get(spHost.getDbName()).addSpHost(spHost);
					spHosts.add(spHost);
				}
			}
			
			for (String viewName : viewNames)
			{
				ViewHost vhost = this.buildViewHost(tableViewSp, viewName);
				if(null != vhost)
					viewHosts.add(vhost);
			}
			
		}
		
		if (sqlBuilders.size() > 0) {
			Map<String, GenTaskBySqlBuilder> _sqlBuildres = sqlBuilderBroupBy(sqlBuilders);
			for (GenTaskBySqlBuilder _table : _sqlBuildres.values()) {
				JavaTableHost extraTableHost = buildExtraSqlBuilderHost(_table);
				if (null != extraTableHost) {
					tableHosts.add(extraTableHost);
				}
			}
		}
		
		
		VelocityContext context = new VelocityContext();
		context.put("WordUtils", WordUtils.class);
		context.put("StringUtils", StringUtils.class);
		File mavenLikeDir = new File(String.format("%s/%s/java",generatePath ,
				projectId));

		try {
			FileUtils.forceMkdir(mavenLikeDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		context.put("dbs", dbs);
		GenUtils.mergeVelocityContext(context, String.format("%s/Dal.config",
				mavenLikeDir.getAbsolutePath()), "templates/java/Dal.config.tpl");

		ProgressResource.addTotalFiles(progress, tableHosts.size()+spHostMaps.size()+viewHosts.size());
		
		progress.setOtherMessage("正在生成TableDao");
		generateTableDao(tableHosts, context, mavenLikeDir);
		ProgressResource.addDoneFiles(progress, tableHosts.size());
		
		progress.setOtherMessage("正在生成SpDao");
		generateSpDao(spHostMaps, context, mavenLikeDir);
		ProgressResource.addDoneFiles(progress, spHostMaps.size());
		
		progress.setOtherMessage("正在生成VeiwDao");
		generateVeiwDao(viewHosts, context, mavenLikeDir);
		ProgressResource.addDoneFiles(progress, viewHosts.size());
	}

	private Map<String, String> buildCommonVelocity(List<GenTaskByTableViewSp> tasks) {
		Map<String, String> dbs = new HashMap<String, String>();
		for (GenTaskByFreeSql task : freeSqls) {
			if (!dbs.containsKey(task.getDb_name())) {

				String provider = "sqlProvider";
				if (!DbUtils.getDbType(task.getDb_name()).equalsIgnoreCase(
						"Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				dbs.put(task.getDb_name(), provider);
			}
		}
		for (GenTaskByTableViewSp task : tableViewSps) {
			if (!dbs.containsKey(task.getDb_name())) {

				String provider = "sqlProvider";
				if (!DbUtils.getDbType(task.getDb_name()).equalsIgnoreCase(
						"Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				dbs.put(task.getDb_name(), provider);
			}
		}

		for (GenTaskBySqlBuilder task : sqlBuilders) {
			if (!dbs.containsKey(task.getDb_name())) {
				String provider = "sqlProvider";
				if (!DbUtils.getDbType(task.getDb_name()).equalsIgnoreCase(
						"Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				dbs.put(task.getDb_name(), provider);
			}
		}
		return dbs;
	}

	private void generateTableDao(List<JavaTableHost> tableHosts,
			VelocityContext context, File mavenLikeDir) {
		for (JavaTableHost host : tableHosts) {
			context.put("host", host);
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Dao/%sDao.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/DAO.java.tpl");
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Entity/%s.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/Pojo.java.tpl");
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sDaoTest.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/DAOTest.java.tpl");
		}
	}

	private void generateSpDao(HashMap<String, SpDbHost> spHostMaps,
			VelocityContext context, File mavenLikeDir) {
		for(SpDbHost host : spHostMaps.values())
		{
			context.put("host", host);
			GenUtils.mergeVelocityContext(context, String.format("%s/Dao/%sSpDao.java",
					mavenLikeDir.getAbsolutePath(), host.getDbName()), "templates/java/DAOBySp.java.tpl");

			GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sSpDaoTest.java",
					mavenLikeDir.getAbsolutePath(), host.getDbName()), "templates/java/DAOBySpTest.java.tpl");
			
			for(SpHost sp : host.getSpHosts())
			{
				context.put("host", sp);
				GenUtils.mergeVelocityContext(context, String.format("%s/Entity/%s.java",
						mavenLikeDir.getAbsolutePath(), sp.getPojoClassName()), "templates/java/Pojo.java.tpl");
			}
		}
		
	}
	
	private void generateVeiwDao(List<ViewHost> viewHosts, VelocityContext context,
			File mavenLikeDir)
	{
		for(ViewHost host : viewHosts)
		{
			context.put("host", host);
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Dao/%sDao.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/ViewDAO.java.tpl");
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Entity/%s.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/Pojo.java.tpl");
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sDaoTest.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/DAOByViewTest.java.tpl");
		}
	}
	
	private JavaTableHost buildExtraSqlBuilderHost(GenTaskBySqlBuilder sqlBuilder) throws Exception {
		GenTaskByTableViewSp tableViewSp = new GenTaskByTableViewSp();
		tableViewSp.setCud_by_sp(false);
		tableViewSp.setPagination(false);
		tableViewSp.setDb_name(sqlBuilder.getDb_name());
		tableViewSp.setPrefix("");
		tableViewSp.setSuffix("Gen");

		return buildTableHost(tableViewSp, sqlBuilder.getTable_name());
	}
	
	private JavaTableHost buildTableHost(GenTaskByTableViewSp tableViewSp, String table) throws Exception {
		JavaTableHost tableHost = new JavaTableHost();
		tableHost.setPackageName(super.namespace);
		tableHost.setDatabaseCategory(this.getDatabaseCategory(tableViewSp));
		tableHost.setDbName(tableViewSp.getDb_name());
		tableHost.setTableName(table);
		tableHost.setPojoClassName(getPojoClassName(tableViewSp.getPrefix(), 
				tableViewSp.getSuffix(), table));
		tableHost.setSp(tableViewSp.isCud_by_sp());

		// 主键及所有列
		List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(tableViewSp.getDb_name(), table);
		List<AbstractParameterHost> allColumnsAbstract = DbUtils
				.getAllColumnNames(tableViewSp.getDb_name(),
						table, CurrentLanguage.Java);
		
		List<JavaParameterHost> allColumns = new ArrayList<JavaParameterHost>();
		for(AbstractParameterHost h : allColumnsAbstract){
			allColumns.add((JavaParameterHost)h);
		}

		List<JavaParameterHost> primaryKeys = new ArrayList<JavaParameterHost>();
		boolean hasIdentity = false;
		String identityColumnName = null;
		for (JavaParameterHost h : allColumns) {
			if(!hasIdentity && h.isIdentity()){
				hasIdentity = true;
				identityColumnName = h.getName();
			}
			if (primaryKeyNames.contains(h.getName())) {
				h.setPrimary(true);
				primaryKeys.add(h);
			}
		}

		List<GenTaskBySqlBuilder> currentTableBuilders = filterExtraMethods(
				sqlBuilders, tableViewSp.getDb_name(), table);

		List<JavaMethodHost> methods = buildMethodHosts(allColumns,
				currentTableBuilders);
		
		tableHost.setFields(allColumns);
		tableHost.setPrimaryKeys(primaryKeys);
		tableHost.setHasIdentity(hasIdentity);
		tableHost.setIdentityColumnName(identityColumnName);
		tableHost.setMethods(methods);

		if (tableHost.isSp()) {
			tableHost.setSpInsert(this.getSpaOperation(tableViewSp.getDb_name(), table, "i"));
			tableHost.setSpUpdate(this.getSpaOperation(tableViewSp.getDb_name(), table, "u"));
			tableHost.setSpDelete(this.getSpaOperation(tableViewSp.getDb_name(), table, "d"));
		}
		return tableHost;
	}
	
	private ViewHost buildViewHost(GenTaskByTableViewSp tableViewSp, String viewName) throws Exception {
		if (!DbUtils.viewExists(tableViewSp.getDb_name(), viewName)) {
			throw new Exception(String.format("The view[%s] doesn't exist, pls check", viewName));
		}				
		
		ViewHost vhost = new ViewHost();
		String className = viewName.replace("_", "");
		className = getPojoClassName(tableViewSp.getPrefix(), tableViewSp.getSuffix(), className);
		
		vhost.setPackageName(super.namespace);
		vhost.setDatabaseCategory(this.getDatabaseCategory(tableViewSp));
		vhost.setDbName(tableViewSp.getDb_name());
		vhost.setPojoClassName(className);
		vhost.setViewName(viewName);
		
		List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(tableViewSp.getDb_name(), viewName);
		List<AbstractParameterHost> params = DbUtils
				.getAllColumnNames(tableViewSp.getDb_name(), viewName,
						CurrentLanguage.Java);
		List<JavaParameterHost> realParams = new ArrayList<JavaParameterHost>();
		for(AbstractParameterHost p : params)
		{
			JavaParameterHost jHost = (JavaParameterHost)p;
			if(primaryKeyNames.contains(jHost.getName()))
			{
				jHost.setPrimary(true);
			}
			realParams.add(jHost);
		}
		
		vhost.setFields(realParams);
		return vhost;
	}

	private SpHost buildSpHost(GenTaskByTableViewSp tableViewSp,  String spName) throws Exception {
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

		SpHost spHost = new SpHost();
		String className = realSpName.replace("_", "");
		className = getPojoClassName(tableViewSp.getPrefix(), tableViewSp.getSuffix(), className);
		
		spHost.setPackageName(super.namespace);
		spHost.setDatabaseCategory(this.getDatabaseCategory(tableViewSp));
		spHost.setDbName(tableViewSp.getDb_name());
		spHost.setPojoClassName(className);
		spHost.setSpName(spName);
		List<AbstractParameterHost> params = DbUtils.getSpParams(tableViewSp.getDb_name(), 
				currentSp, CurrentLanguage.Java);
		List<JavaParameterHost> realParams = new ArrayList<JavaParameterHost>();
		String callParams = "";
		for (AbstractParameterHost p : params) {
			callParams += "?,";
			realParams.add((JavaParameterHost) p);
		}
		spHost.setCallParameters(StringUtils.removeEnd(callParams, ","));
		spHost.setFields(realParams);
		
		return spHost;
	}
	
	private List<GenTaskBySqlBuilder> filterExtraMethods(
			List<GenTaskBySqlBuilder> sqlBuilders, String dbName, String table) {
		List<GenTaskBySqlBuilder> currentTableBuilders = new ArrayList<GenTaskBySqlBuilder>();

		Iterator<GenTaskBySqlBuilder> iter = sqlBuilders.iterator();
		while (iter.hasNext()) {
			GenTaskBySqlBuilder currentSqlBuilder =iter.next();
			if (currentSqlBuilder.getDb_name().equals(dbName)
					&& currentSqlBuilder.getTable_name().equals(table)) {
				currentTableBuilders.add(currentSqlBuilder);
				iter.remove();
			}
		}
		
		return currentTableBuilders;
	}
	
	private List<JavaMethodHost> buildMethodHosts(
			List<JavaParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) {
		List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			JavaMethodHost method = new JavaMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			method.setSql(builder.getSql_content());
			List<JavaParameterHost> parameters = new ArrayList<JavaParameterHost>();
			if (method.getCrud_type().equals("select")
					|| method.getCrud_type().equals("delete")) {
				String[] conditions = StringUtils.split(
						builder.getCondition(), ";");
				for (String condition : conditions) {
					String name = StringUtils.split(condition, ",")[0];
					for (JavaParameterHost pHost : allColumns) {
						if (pHost.getName().equals(name)) {
							parameters.add(pHost);
							break;
						}
					}
				}
			} else if (method.getCrud_type().equals("insert")) {
				String[] fields = StringUtils.split(
						builder.getFields(), ",");
				for (String field : fields) {
					for (JavaParameterHost pHost : allColumns) {
						if (pHost.getName().equals(field)) {
							parameters.add(pHost);
							break;
						}
					}
				}
			} else {
				String[] fields = StringUtils.split(
						builder.getFields(), ",");
				String[] conditions = StringUtils.split(
						builder.getCondition(), ";");
				for (JavaParameterHost pHost : allColumns) {
					for (String field : fields) {
						if (pHost.getName().equals(field)) {
							parameters.add(pHost);
							break;
						}
					}
					for (String condition : conditions) {
						String name = StringUtils.split(condition, ",")[0];
						if (pHost.getName().equals(name)) {
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

	private DatabaseCategory getDatabaseCategory(GenTaskByTableViewSp tableViewSp)
	{
		DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
		if (!DbUtils.getDbType(tableViewSp.getDb_name()).equalsIgnoreCase(
				"Microsoft SQL Server")) {
			dbCategory = DatabaseCategory.MySql;
		}
		return dbCategory;
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
	
	private SpOperationHost getSpaOperation(String dbName, String tableName, String operation) throws Exception
	{
		List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(dbName);
		return SpOperationHost.getSpaOperation(dbName, 
				tableName, allSpNames, operation);
	}
	
	@Override
	public void generateByFreeSql(List<GenTaskByFreeSql> tasks,Progress progress) {

		progress.setOtherMessage("正在生成FreeSql的代码");
		// 首先按照ServerID, DbName以及ClassName做一次GroupBy
		Map<String, List<GenTaskByFreeSql>> groupBy =  freeSqlGroupBy(tasks);

		List<FreeSqlHost> hosts = new ArrayList<FreeSqlHost>();
//		Map<String, FreeSqlPojoHost> pojoHosts = new HashMap<String, FreeSqlPojoHost>();
		Map<String, JavaMethodHost> pojoHosts = new HashMap<String, JavaMethodHost>();
		
		// 随后，以ServerID, DbName以及ClassName为维度，为每个维度生成一个DAO类
		for (Map.Entry<String, List<GenTaskByFreeSql>> entry : groupBy
				.entrySet()) {

			List<GenTaskByFreeSql> currentTasks = entry.getValue();

			if (currentTasks.size() < 1)
				continue;

			FreeSqlHost host = new FreeSqlHost();
			host.setDbName(currentTasks.get(0).getDb_name());
			host.setClassName(currentTasks.get(0).getClass_name());
			host.setPackageName(super.namespace);

			List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();
			// 每个Method可能就有一个Pojo
			for (GenTaskByFreeSql task : currentTasks) {
				JavaMethodHost method = new JavaMethodHost();
				method.setSql(task.getSql_content());
				method.setName(task.getMethod_name());
				method.setPackageName(namespace);
				method.setPojoClassName(WordUtils.capitalize(task.getMethod_name() + "Pojo"));
				List<JavaParameterHost> params = new ArrayList<JavaParameterHost>();
				for (String param : StringUtils
						.split(task.getParameters(), ";")) {
					if(param.contains("HotelAddress"))
					{
						System.out.println("");
					}
					String[] splitedParam = StringUtils.split(param, ",");
					JavaParameterHost p = new JavaParameterHost();
					p.setName(splitedParam[0]);
					p.setSqlType(Integer.valueOf(splitedParam[1]));
					p.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(p.getSqlType()));
					//p.setValidationValue(splitedParam[2]);
					p.setValidationValue(DbUtils.mockATest(p.getSqlType()));
					params.add(p);
				}
				method.setParameters(params);
				methods.add(method);

				// Need to specify Pojo class name for each method. or allow Map<Sting, Object> as row
//				if (!pojoHosts.containsKey(task.getClass_name())) {
				if (!pojoHosts.containsKey(method.getPojoClassName())) {

							List<JavaParameterHost> paramHosts = new ArrayList<JavaParameterHost>();
							
							for (AbstractParameterHost _ahost : DbUtils.testAQuerySql(
									task.getDb_name(), task.getSql_content(), task.getParameters(),
									CurrentLanguage.Java, false)) {
								paramHosts.add((JavaParameterHost) _ahost);
							}
							
							method.setFields(paramHosts);
							/*
							FreeSqlPojoHost pojoHost = new FreeSqlPojoHost();
							pojoHost.setColumns(paramHosts);
							pojoHost.setTableName("");
							pojoHost.setClassName(task.getClass_name());
							pojoHost.setPackageName(host.getPackageName());
							*/
							pojoHosts.put(method.getPojoClassName(), method);
				}
			}
			host.setMethods(methods);
			hosts.add(host);
		}

		VelocityContext context = new VelocityContext();
		context.put("WordUtils", WordUtils.class);
		context.put("StringUtils", StringUtils.class);
		
		File mavenLikeDir = new File(String.format("%s/%s/java",generatePath ,
				projectId));
		
//		for(FreeSqlPojoHost host : pojoHosts.values()){
		ProgressResource.addTotalFiles(progress, pojoHosts.size()+hosts.size());
		progress.setOtherMessage("正在生成FreeSql的POJO代码");
		for(JavaMethodHost host : pojoHosts.values()){
			context.put("host", host);

			GenUtils.mergeVelocityContext(context, String.format("%s/Entity/%s.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/Pojo.java.tpl");
		}
		ProgressResource.addDoneFiles(progress, pojoHosts.size());

		progress.setOtherMessage("正在生成FreeSql的Dao和Test代码");
		for (FreeSqlHost host : hosts) {
			context.put("host", host);
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Dao/%sDao.java",
						mavenLikeDir.getAbsolutePath(), host.getClassName()), "templates/java/FreeSqlDAO.java.tpl");
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sDaoTest.java",
					mavenLikeDir.getAbsolutePath(), host.getClassName()), "templates/java/FreeSqlDAOTest.java.tpl");
		}
		ProgressResource.addDoneFiles(progress, hosts.size());
	}
}