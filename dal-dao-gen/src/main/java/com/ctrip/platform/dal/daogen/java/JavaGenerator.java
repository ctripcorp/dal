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
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.GenUtils;

public class JavaGenerator extends AbstractGenerator {

	private JavaGenerator() {

	}

	private static JavaGenerator instance = new JavaGenerator();

	public static JavaGenerator getInstance() {
		return instance;
	}

	@Override
	public void generateByTableView(List<GenTaskByTableViewSp> tasks) throws Exception {
		
		prepareFolder(projectId, "java");
		
		List<JavaTableHost> tableHosts = new ArrayList<JavaTableHost>();
		List<SpHost> spHosts = new ArrayList<SpHost>();
		List<GenTaskBySqlBuilder> sqlBuilders = daoBySqlBuilder
				.getTasksByProjectId(projectId);

		// 首先为所有表/存储过程生成DAO
		for (GenTaskByTableViewSp tableViewSp : tasks) {

			String dbName = tableViewSp.getDb_name();
			String[] tableNames = StringUtils.split(
					tableViewSp.getTable_names(), ",");
			String[] spNames = StringUtils
					.split(tableViewSp.getSp_names(), ",");

			String prefix = tableViewSp.getPrefix();
			String suffix = tableViewSp.getSuffix();
			
			boolean cud_by_sp = tableViewSp.isCud_by_sp();

			DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
			if (!DbUtils.getDbType(tableViewSp.getDb_name()).equalsIgnoreCase(
					"Microsoft SQL Server")) {
				dbCategory = DatabaseCategory.MySql;
			}

			List<StoredProcedure> allSpNames = DbUtils.getAllSpNames( dbName);

			for (String table : tableNames) {
				JavaTableHost tableHost = new JavaTableHost();
				tableHost.setPackageName(super.namespace);
				tableHost.setDatabaseCategory(dbCategory);
				tableHost.setDbName(dbName);
				tableHost.setTableName(table);
				tableHost.setPojoClassName(getPojoClassName(prefix, suffix, table));
				tableHost.setSpa(cud_by_sp);

				// 主键及所有列
				List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(dbName, table);
				List<AbstractParameterHost> allColumnsAbstract = DbUtils
						.getAllColumnNames(dbName,
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
						sqlBuilders, dbName, table);

				List<JavaMethodHost> methods = buildMethodHosts(allColumns,
						currentTableBuilders);
				
				tableHost.setFields(allColumns);
				tableHost.setPrimaryKeys(primaryKeys);
				tableHost.setHasIdentity(hasIdentity);
				tableHost.setIdentityColumnName(identityColumnName);
				tableHost.setMethods(methods);

//				tableHost.setTable(true);
				// SP方式增删改
				if (tableHost.isSpa()) {
					tableHost.setSpaInsert(SpaOperationHost.getSpaOperation(dbName, table, allSpNames,"i"));
					tableHost.setSpaUpdate(SpaOperationHost.getSpaOperation(dbName, table, allSpNames,"u"));
					tableHost.setSpaDelete(SpaOperationHost.getSpaOperation(dbName, table, allSpNames,"d"));
				}
				
				tableHosts.add(tableHost);
			}
		
			for (String spName : spNames) {
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
				className = getPojoClassName(prefix, suffix, className);
				
				spHost.setPackageName(super.namespace);
				spHost.setDatabaseCategory(dbCategory);
				spHost.setDbName(dbName);
				spHost.setPojoClassName(className);
				spHost.setSpName(spName);
				List<AbstractParameterHost> params = DbUtils.getSpParams(dbName, currentSp, CurrentLanguage.Java);
				List<JavaParameterHost> realParams = new ArrayList<JavaParameterHost>();
				for (AbstractParameterHost p : params) {
					realParams.add((JavaParameterHost) p);
				}
				spHost.setFields(realParams);
	
				spHosts.add(spHost);
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

		generateTableDao(tableHosts, context, mavenLikeDir);
		generateSpDao(spHosts, context, mavenLikeDir);
	}

	private void generateTableDao(List<JavaTableHost> tableHosts,
			VelocityContext context, File mavenLikeDir) {
		for (JavaTableHost host : tableHosts) {
			context.put("host", host);
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Dao/%sDao.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/DAO.java.tpl");
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Entity/%s.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/Pojo.java.tpl");
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sTest.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/DAOTest.java.tpl");
		}
	}

	private void generateSpDao(List<SpHost> spHosts, VelocityContext context,
			File mavenLikeDir) {
		for (SpHost host : spHosts) {
			context.put("host", host);
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Dao/%sDao.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/DAOBySp.java.tpl");
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Entity/%s.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/Pojo.java.tpl");
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sTest.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/DAOBySpTest.java.tpl");
		}
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

	@Override
	public void generateByFreeSql(List<GenTaskByFreeSql> tasks) {

		// 首先按照ServerID, DbName以及ClassName做一次GroupBy
		Map<String, List<GenTaskByFreeSql>> groupBy = new HashMap<String, List<GenTaskByFreeSql>>();

		for (GenTaskByFreeSql task : tasks) {
			String key = String.format("%s_%s", 
					task.getDb_name(), task.getClass_name());
			if (groupBy.containsKey(key)) {
				groupBy.get(key).add(task);
			} else {
				groupBy.put(key, new ArrayList<GenTaskByFreeSql>());
				groupBy.get(key).add(task);
			}
		}

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
				method.setPojoClassName(WordUtils.capitalize(task.getMethod_name()) + "Pojo");
				List<JavaParameterHost> params = new ArrayList<JavaParameterHost>();
				for (String param : StringUtils
						.split(task.getParameters(), ";")) {
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
		for(JavaMethodHost host : pojoHosts.values()){
			context.put("host", host);

			GenUtils.mergeVelocityContext(context, String.format("%s/Entity/%s.java",
					mavenLikeDir.getAbsolutePath(), host.getPojoClassName()), "templates/java/Pojo.java.tpl");
		}

		for (FreeSqlHost host : hosts) {
			context.put("host", host);
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Dao/%sDao.java",
						mavenLikeDir.getAbsolutePath(), host.getClassName()), "templates/java/FreeSqlDAO.java.tpl");
			
			GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sTest.java",
					mavenLikeDir.getAbsolutePath(), host.getClassName()), "templates/java/FreeSqlDAOTest.java.tpl");
		}
	}
}
