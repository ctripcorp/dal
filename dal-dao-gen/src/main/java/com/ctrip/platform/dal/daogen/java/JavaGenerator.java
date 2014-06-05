package com.ctrip.platform.dal.daogen.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.DalConfigHost;
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
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;

public class JavaGenerator extends AbstractGenerator {
	
	private Queue<JavaTableHost> _tableHosts = new ConcurrentLinkedQueue<JavaTableHost>();
	private Queue<ViewHost> _viewHosts = new ConcurrentLinkedQueue<ViewHost>();
	private Map<String, SpDbHost> _spHostMaps = new ConcurrentHashMap<String, SpDbHost>();
	private Queue<SpHost> _spHosts = new ConcurrentLinkedQueue<SpHost>();
	private DalConfigHost dalConfigHost = null;
	private Map<String, String> dbs = new ConcurrentHashMap<String, String>();
	private Queue<FreeSqlHost> _freeSqlHosts = new ConcurrentLinkedQueue<FreeSqlHost>();
	private Map<String, JavaMethodHost> _freeSqlPojoHosts = new ConcurrentHashMap<String, JavaMethodHost>();
	private Queue<GenTaskBySqlBuilder> _sqlBuilders = new ConcurrentLinkedQueue<GenTaskBySqlBuilder>();

	private List<Callable<ExecuteResult>> generateTableDao( final File mavenLikeDir,
			final Progress progress) {

		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		for (final JavaTableHost host : _tableHosts) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

				@Override
				public ExecuteResult call() throws Exception {
					ExecuteResult result = new ExecuteResult("Generate Table[" + host.getDbName() + "." + host.getTableName() + "] Dao, Pojo, Test");
					progress.setOtherMessage(result.getTaskName());
					try
					{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Dao/%sDao.java",
										mavenLikeDir.getAbsolutePath(),
										host.getPojoClassName()),
								"templates/java/DAO.java.tpl");
	
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Entity/%s.java",
										mavenLikeDir.getAbsolutePath(),
										host.getPojoClassName()),
								"templates/java/Pojo.java.tpl");
	
						GenUtils.mergeVelocityContext(context, String.format(
								"%s/Test/%sDaoTest.java",
								mavenLikeDir.getAbsolutePath(),
								host.getPojoClassName()),
								"templates/java/DAOTest.java.tpl");
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + " exception", e);
					}
					return result;
				}
			};
			results.add(worker);

		}
		return results;
	}

	private List<Callable<ExecuteResult>> generateSpDao(final File mavenLikeDir,
			final Progress progress) {

		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		for (final SpDbHost host : _spHostMaps.values()) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

				@Override
				public ExecuteResult call() throws Exception {
					ExecuteResult result = new ExecuteResult("Generate SP[" + host.getDbName() + "] Dao, Pojo, Test");
					progress.setOtherMessage(result.getTaskName());
					try
					{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Dao/%sSpDao.java",
										mavenLikeDir.getAbsolutePath(),
										host.getDbName()),
								"templates/java/DAOBySp.java.tpl");
	
						GenUtils.mergeVelocityContext(context, String.format(
								"%s/Test/%sSpDaoTest.java",
								mavenLikeDir.getAbsolutePath(), host.getDbName()),
								"templates/java/DAOBySpTest.java.tpl");
	
						for (SpHost sp : host.getSpHosts()) {
							context.put("host", sp);
							GenUtils.mergeVelocityContext(
									context,
									String.format("%s/Entity/%s.java",
											mavenLikeDir.getAbsolutePath(),
											sp.getPojoClassName()),
									"templates/java/Pojo.java.tpl");
						}
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + " exception", e);
					}
					return result;
				}
			};
			results.add(worker);
		}
		return results;

	}

	private List<Callable<ExecuteResult>> generateViewDao( 
			final File mavenLikeDir, final Progress progress) {

		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		for (final ViewHost host : _viewHosts) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

				@Override
				public ExecuteResult call() throws Exception {					
					ExecuteResult result = new ExecuteResult("Generate View[" + host.getDbName() + "." + host.getViewName() + "] Dao");
					progress.setOtherMessage(result.getTaskName());
					try
					{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Dao/%sDao.java",
										mavenLikeDir.getAbsolutePath(),
										host.getPojoClassName()),
								"templates/java/ViewDAO.java.tpl");
	
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Entity/%s.java",
										mavenLikeDir.getAbsolutePath(),
										host.getPojoClassName()),
								"templates/java/Pojo.java.tpl");
	
						GenUtils.mergeVelocityContext(context, String.format(
								"%s/Test/%sDaoTest.java",
								mavenLikeDir.getAbsolutePath(),
								host.getPojoClassName()),
								"templates/java/DAOByViewTest.java.tpl");
						
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + " exception", e);
					}
					return result;
				}
			};
			results.add(worker);
		}
		return results;
	}

	private List<Callable<ExecuteResult>> generateFreeSqlDao(final File mavenLikeDir,
			final Progress progress) {

		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		for (final JavaMethodHost host : _freeSqlPojoHosts.values()) {
			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() throws Exception {
					ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getPojoClassName() + "] Pojo");
					progress.setOtherMessage(result.getTaskName());
					try
					{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Entity/%s.java",
										mavenLikeDir.getAbsolutePath(),
										host.getPojoClassName()),
								"templates/java/Pojo.java.tpl");
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + " exception", e);
					}
					return result;
				}
			};
			results.add(worker);
		}

		for (final FreeSqlHost host : _freeSqlHosts) {
			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

				@Override
				public ExecuteResult call() throws Exception {
					ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getClassName() + "] Dap, Test");
					progress.setOtherMessage(result.getTaskName());
					try
					{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Dao/%sDao.java",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
								"templates/java/FreeSqlDAO.java.tpl");
	
						GenUtils.mergeVelocityContext(context,
								String.format("%s/Test/%sDaoTest.java",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
								"templates/java/FreeSqlDAOTest.java.tpl");
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + " exception", e);
					}
					return result;
				}
			};
			results.add(worker);
		}
		return results;
	}

	private JavaTableHost buildExtraSqlBuilderHost(
			GenTaskBySqlBuilder sqlBuilder) throws Exception {
		GenTaskByTableViewSp tableViewSp = new GenTaskByTableViewSp();
		tableViewSp.setCud_by_sp(false);
		tableViewSp.setPagination(false);
		tableViewSp.setDb_name(sqlBuilder.getDb_name());
		tableViewSp.setPrefix("");
		tableViewSp.setSuffix("Gen");

		return buildTableHost(tableViewSp, sqlBuilder.getTable_name());
	}

	private JavaTableHost buildTableHost(GenTaskByTableViewSp tableViewSp,
			String table) throws Exception {
		if(!DbUtils.tableExists(tableViewSp.getDb_name(), table)){
			throw new Exception(String.format("The table doesn't exist, pls check", tableViewSp.getDb_name(), table));
		}
		JavaTableHost tableHost = new JavaTableHost();
		tableHost.setPackageName(super.namespace);
		tableHost.setDatabaseCategory(this.getDatabaseCategory(tableViewSp));
		tableHost.setDbName(tableViewSp.getDb_name());
		tableHost.setTableName(table);
		tableHost.setPojoClassName(getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), table));
		tableHost.setSp(tableViewSp.isCud_by_sp());

		// 主键及所有列
		List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
				tableViewSp.getDb_name(), table);
		List<AbstractParameterHost> allColumnsAbstract = DbUtils
				.getAllColumnNames(tableViewSp.getDb_name(), table,
						CurrentLanguage.Java);
		if(null == allColumnsAbstract){
			throw new Exception(String.format("The column names of tabel[%s, %s] is null", 
					tableViewSp.getDb_name(), table));
		}
		List<JavaParameterHost> allColumns = new ArrayList<JavaParameterHost>();
		for (AbstractParameterHost h : allColumnsAbstract) {
			allColumns.add((JavaParameterHost) h);
		}

		List<JavaParameterHost> primaryKeys = new ArrayList<JavaParameterHost>();
		boolean hasIdentity = false;
		String identityColumnName = null;
		for (JavaParameterHost h : allColumns) {
			if (!hasIdentity && h.isIdentity()) {
				hasIdentity = true;
				identityColumnName = h.getName();
			}
			if (primaryKeyNames.contains(h.getName())) {
				h.setPrimary(true);
				primaryKeys.add(h);
			}
		}

		List<GenTaskBySqlBuilder> currentTableBuilders = filterExtraMethods(tableViewSp.getDb_name(), table);

		List<JavaMethodHost> methods = buildMethodHosts(allColumns,
				currentTableBuilders);

		tableHost.setFields(allColumns);
		tableHost.setPrimaryKeys(primaryKeys);
		tableHost.setHasIdentity(hasIdentity);
		tableHost.setIdentityColumnName(identityColumnName);
		tableHost.setMethods(methods);

		if (tableHost.isSp()) {
			tableHost.setSpInsert(this.getSpaOperation(
					tableViewSp.getDb_name(), table, "i"));
			tableHost.setSpUpdate(this.getSpaOperation(
					tableViewSp.getDb_name(), table, "u"));
			tableHost.setSpDelete(this.getSpaOperation(
					tableViewSp.getDb_name(), table, "d"));
		}
		return tableHost;
	}

	private ViewHost buildViewHost(GenTaskByTableViewSp tableViewSp,
			String viewName) throws Exception {
		if (!DbUtils.viewExists(tableViewSp.getDb_name(), viewName)) {
			log.error(String.format(
					"The view[%s] doesn't exist, pls check", viewName));
			return null;
		}

		ViewHost vhost = new ViewHost();
		String className = viewName.replace("_", "");
		className = getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), className);

		vhost.setPackageName(super.namespace);
		vhost.setDatabaseCategory(this.getDatabaseCategory(tableViewSp));
		vhost.setDbName(tableViewSp.getDb_name());
		vhost.setPojoClassName(className);
		vhost.setViewName(viewName);

		List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
				tableViewSp.getDb_name(), viewName);
		List<AbstractParameterHost> params = DbUtils.getAllColumnNames(
				tableViewSp.getDb_name(), viewName, CurrentLanguage.Java);
		List<JavaParameterHost> realParams = new ArrayList<JavaParameterHost>();
		if(null == params){
			throw new Exception(String.format("The column names of view[%s, %s] is null", 
					tableViewSp.getDb_name(), viewName));
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

	private SpHost buildSpHost(GenTaskByTableViewSp tableViewSp, String spName)
			throws Exception {
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
			throw new Exception(String.format("The store procedure[%s, %s] doesn't exist, pls check", 
					tableViewSp.getDb_name(),  currentSp.getName()));
		}
		
		SpHost spHost = new SpHost();
		String className = realSpName.replace("_", "");
		className = getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), className);

		spHost.setPackageName(super.namespace);
		spHost.setDatabaseCategory(this.getDatabaseCategory(tableViewSp));
		spHost.setDbName(tableViewSp.getDb_name());
		spHost.setPojoClassName(className);
		spHost.setSpName(spName);
		List<AbstractParameterHost> params = DbUtils.getSpParams(
				tableViewSp.getDb_name(), currentSp, CurrentLanguage.Java);
		List<JavaParameterHost> realParams = new ArrayList<JavaParameterHost>();
		String callParams = "";
		if(null == params){
			throw new Exception(String.format("The sp[%s, %s] parameters is null", 
					tableViewSp.getDb_name(), currentSp.getName()));
		}
		for (AbstractParameterHost p : params) {
			callParams += "?,";
			realParams.add((JavaParameterHost) p);
		}
		spHost.setCallParameters(StringUtils.removeEnd(callParams, ","));
		spHost.setFields(realParams);

		return spHost;
	}

	private List<GenTaskBySqlBuilder> filterExtraMethods( String dbName, String table) {
		List<GenTaskBySqlBuilder> currentTableBuilders = new ArrayList<GenTaskBySqlBuilder>();

		Iterator<GenTaskBySqlBuilder> iter = _sqlBuilders.iterator();
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
			// Only have condition clause
			if (method.getCrud_type().equals("select")
					|| method.getCrud_type().equals("delete")) {
				String[] conditions = StringUtils.split(builder.getCondition(),
						";");
				for (String condition : conditions) {
					String[] tokens = StringUtils.split(condition, ",");
					String name = tokens[0];
					int type = tokens.length >= 2 ? CommonUtils.tryParse(tokens[1], -1) : -1;
					String alias = tokens.length >= 3 ? tokens[2] : "";
					for (JavaParameterHost pHost : allColumns) {
						if (pHost.getName().equals(name)) {
							JavaParameterHost host_ls = new JavaParameterHost(
									pHost);
							host_ls.setAlias(alias);
							host_ls.setConditional(true);
							if (-1 != type)
								host_ls.setConditionType(ConditionType
										.valueOf(type));
							parameters.add(host_ls);
							// Between need an extra parameter
							if (ConditionType.Between == host_ls
									.getConditionType()) {
								JavaParameterHost host_bw = new JavaParameterHost(
										host_ls);
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
			// Have no condition
			else if (method.getCrud_type().equals("insert")) {
				String[] fields = StringUtils.split(builder.getFields(), ",");
				for (String field : fields) {
					for (JavaParameterHost pHost : allColumns) {
						if (pHost.getName().equals(field)) {
							parameters.add(pHost);
							break;
						}
					}
				}
			}
			// Have both set and condition clause
			else {
				String[] fields = StringUtils.split(builder.getFields(), ",");
				String[] conditions = StringUtils.split(builder.getCondition(),
						";");
				for (String field : fields) {
					for (JavaParameterHost pHost : allColumns) {
						if (pHost.getName().equals(field)) {
							JavaParameterHost host_ls = new JavaParameterHost(
									pHost);
							parameters.add(host_ls);
							break;
						}
					}
				}

				for (String condition : conditions) {
					String[] tokens = StringUtils.split(condition, ",");
					String name = tokens[0];
					int type = tokens.length >= 2 ? CommonUtils.tryParse(tokens[1], -1) : -1;
					String alias = tokens.length >= 3 ? tokens[2] : "";
					for (JavaParameterHost pHost : allColumns) {
						if (pHost.getName().equals(name)) {
							JavaParameterHost host_ls = new JavaParameterHost(
									pHost);
							host_ls.setAlias(alias);
							host_ls.setConditional(true);
							if (-1 != type)
								host_ls.setConditionType(ConditionType
										.valueOf(type));
							if (ConditionType.In == host_ls.getConditionType()) {

							}
							parameters.add(host_ls);
							// Between need an extra parameter
							if (ConditionType.Between == host_ls
									.getConditionType()) {
								JavaParameterHost host_bw = new JavaParameterHost(
										host_ls);
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

	private DatabaseCategory getDatabaseCategory(
			GenTaskByTableViewSp tableViewSp) {
		DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
		String dbType = DbUtils.getDbType(tableViewSp.getDb_name());
		if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
			dbCategory = DatabaseCategory.MySql;
		}
		return dbCategory;
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

	private SpOperationHost getSpaOperation(String dbName, String tableName,
			String operation) throws Exception {
		List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(dbName);
		return SpOperationHost.getSpaOperation(dbName, tableName, allSpNames,
				operation);
	}

	private void prepareDbFromFreeSql(List<GenTaskByFreeSql> freeSqls) {
		for (GenTaskByFreeSql task : freeSqls) {		
			this.addDatabaseSet(task.getDatabaseSetName());
			/*if (!dbs.containsKey(task.getDb_name())) {

				String provider = "sqlProvider";
				String dbType = DbUtils.getDbType(task.getDb_name());
				if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				dbs.put(task.getDb_name(), provider);
			}*/
		}
	}

	private void addDatabaseSet(String databaseSetName){
		List<DatabaseSet> sets = daoOfDatabaseSet.getAllDatabaseSetByName(databaseSetName);
		if(null == sets || sets.isEmpty()){
			log.error(String.format("The databaseSet name[%s] does not exist", databaseSetName));
			return;
		}
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
	
	private void prepareDbFromTableViewSp(
			List<GenTaskByTableViewSp> tableViewSps,
			List<GenTaskBySqlBuilder> sqlBuilders) {
		for (GenTaskByTableViewSp task : tableViewSps) {
			
			this.addDatabaseSet(task.getDatabaseSetName());
			/*if (!dbs.containsKey(task.getDb_name())) {
				String provider = "sqlProvider";
				String dbType = DbUtils.getDbType(task.getDb_name());
				if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				dbs.put(task.getDb_name(), provider);
			}*/
		}

		for (GenTaskBySqlBuilder task : sqlBuilders) {
			
			this.addDatabaseSet(task.getDatabaseSetName());
			/*if (!dbs.containsKey(task.getDb_name())) {
				String provider = "sqlProvider";
				String dbType = DbUtils.getDbType(task.getDb_name());
				if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				dbs.put(task.getDb_name(), provider);
			}*/
		}
	}

	private List<Callable<ExecuteResult>> prepareFreeSql(int projectId,
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
					host.setDbName(currentTasks.get(0).getDb_name());
					host.setClassName(currentTasks.get(0).getClass_name());
					host.setPackageName(namespace);

					List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();
					// 每个Method可能就有一个Pojo
					for (GenTaskByFreeSql task : currentTasks) {
						JavaMethodHost method = new JavaMethodHost();
						method.setSql(task.getSql_content());
						method.setName(task.getMethod_name());
						method.setPackageName(namespace);
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

	private List<Callable<ExecuteResult>> prepareTableViewSp(int projectId,
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

		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();
		for (final GenTaskByTableViewSp tableViewSp : _tableViewSps) {
			final String[] viewNames = StringUtils.split(
					tableViewSp.getView_names(), ",");
			final String[] tableNames = StringUtils.split(
					tableViewSp.getTable_names(), ",");
			final String[] spNames = StringUtils.split(
					tableViewSp.getSp_names(), ",");

			for (final String table : tableNames) {
				Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
					@Override
					public ExecuteResult call() throws Exception {
						/*progress.setOtherMessage("正在为所有表/存储过程生成DAO准备数据.<br/>buildTable:"
								+ table);*/
						ExecuteResult result = new ExecuteResult("Build Table[" + tableViewSp.getDb_name() + "." + table + "] Host");
						progress.setOtherMessage(result.getTaskName());
						try{
							JavaTableHost tableHost = buildTableHost(tableViewSp, table);
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

			for (final String view : viewNames) {
				Callable<ExecuteResult> viewWorker = new Callable<ExecuteResult>() {
					@Override
					public ExecuteResult call() throws Exception {
						/*progress.setOtherMessage("正在为所有表/存储过程生成DAO准备数据.<br/>buildView:"
								+ view);*/
						ExecuteResult result = new ExecuteResult("Build View[" + tableViewSp.getDb_name() + "." + view + "] Host");
						progress.setOtherMessage(result.getTaskName());
						try{
							ViewHost vhost = buildViewHost(tableViewSp, view);
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

			for (final String spName : spNames) {
				Callable<ExecuteResult> spWorker = new Callable<ExecuteResult>() {
					@Override
					public ExecuteResult call() throws Exception {
					/*	progress.setOtherMessage("正在为所有表/存储过程生成DAO准备数据.<br/>buildSp:"
								+ spName);*/
						ExecuteResult result = new ExecuteResult("Build SP[" + tableViewSp.getDb_name() + "." + spName + "] Host");
						progress.setOtherMessage(result.getTaskName());
						try{
							SpHost spHost = buildSpHost(tableViewSp, spName);
							if (null != spHost) {
								if (!_spHostMaps.containsKey(spHost.getDbName())) {
									SpDbHost spDbHost = new SpDbHost(
											spHost.getDbName(),
											spHost.getPackageName());
									_spHostMaps.put(spHost.getDbName(), spDbHost);
								}
								_spHostMaps.get(spHost.getDbName()).addSpHost(
										spHost);
								_spHosts.add(spHost);
							}
							result.setSuccessal(true);
						}catch(Exception e){
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

	private List<Callable<ExecuteResult>> prepareSqlBuilder(final Progress progress) {
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

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
							JavaTableHost extraTableHost = buildExtraSqlBuilderHost(_table
								.getValue());
							if (null != extraTableHost) {
								_tableHosts.add(extraTableHost);
							}
							result.setSuccessal(true);
						}catch(Exception e){
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

	@Override
	public boolean prepareDirectory(int projectId, boolean regenerate) {
		File mavenLikeDir = new File(String.format("%s/%s/java", generatePath,
				projectId));
		try {
			if (mavenLikeDir.exists() && regenerate)
				FileUtils.forceDelete(mavenLikeDir);

			log.info("The maven like directory: " + mavenLikeDir.getAbsolutePath());
			
			File daoMavenLike = new File(mavenLikeDir, "Dao");
			File entityMavenLike = new File(mavenLikeDir, "Entity");
			File testMavenLike = new File(mavenLikeDir, "Test");

			if (!daoMavenLike.exists()) {
				FileUtils.forceMkdir(daoMavenLike);
			}
			if (!entityMavenLike.exists()) {
				FileUtils.forceMkdir(entityMavenLike);
			}
			if (!testMavenLike.exists()) {
				FileUtils.forceMkdir(testMavenLike);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean prepareData(int projectId, boolean regenerate,
			Progress progress) {
		
		Project project = daoOfProject.getProjectByID(projectId);
		if(project.getDal_config_name() != null && !project.getDal_config_name().isEmpty())
			dalConfigHost = new DalConfigHost(project.getDal_config_name());
		else if(project.getNamespace() != null && !project.getNamespace().isEmpty())
			dalConfigHost = new DalConfigHost(project.getNamespace());
		else 
			dalConfigHost = new DalConfigHost("");
		
		List<Callable<ExecuteResult>> _freeSqlCallables = prepareFreeSql(projectId,
				regenerate, progress);

		try {
			List<Callable<ExecuteResult>> _tableViewSpCallables = prepareTableViewSp(
					projectId, regenerate, progress);

			@SuppressWarnings("unchecked")
			List<Callable<ExecuteResult>> allResults = ListUtils.union(
					_freeSqlCallables, _tableViewSpCallables);

			if (allResults.size() > 0) {
				TaskUtils.invokeBatch(log, executor, allResults);
			}

			List<Callable<ExecuteResult>> _sqlBuilderCallables = prepareSqlBuilder(progress);

			if (_sqlBuilderCallables.size() > 0) {
				TaskUtils.invokeBatch(log, executor, _sqlBuilderCallables);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean clearResource() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean generateCode(int id, Progress progress, Map hints) {

		VelocityContext context = GenUtils.buildDefaultVelocityContext();
		File mavenLikeDir = new File(String.format("%s/%s/java", generatePath,
				id));

		List<Callable<ExecuteResult>> tableCallables = generateTableDao(
				 mavenLikeDir, progress);

		List<Callable<ExecuteResult>> viewCallables = generateViewDao(
				 mavenLikeDir, progress);

		List<Callable<ExecuteResult>> spCallables = generateSpDao(
				 mavenLikeDir, progress);

		List<Callable<ExecuteResult>> freeCallables = generateFreeSqlDao(
				mavenLikeDir, progress);

		@SuppressWarnings("unchecked")
		List<Callable<ExecuteResult>> allResults = ListUtils.union(ListUtils.union(
				ListUtils.union(tableCallables, viewCallables), spCallables),
				freeCallables);

		if (allResults.size() > 0) {
			try {
				TaskUtils.invokeBatch(log, executor,allResults);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/*context.put("dbs", dbs);
		GenUtils.mergeVelocityContext(context,
				String.format("%s/Dal.config", mavenLikeDir.getAbsolutePath()),
				"templates/java/Dal.config.tpl");*/
		
		context.put("host", dalConfigHost);
		GenUtils.mergeVelocityContext(context,
				String.format("%s/Dal.config", mavenLikeDir.getAbsolutePath()),
				"templates/java/DalConfig.java.tpl");
		return false;
	}
}
