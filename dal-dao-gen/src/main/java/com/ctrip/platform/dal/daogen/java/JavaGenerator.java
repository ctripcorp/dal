package com.ctrip.platform.dal.daogen.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.ctrip.platform.dal.daogen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.pojo.CurrentLanguage;
import com.ctrip.platform.dal.daogen.pojo.DatabaseCategory;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.pojo.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.StoredProcedure;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.JavaIOUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public class JavaGenerator extends AbstractGenerator {

	private JavaGenerator() {

	}

	private static JavaGenerator instance = new JavaGenerator();
	private static DaoOfDbServer dbServerDao;

	static {
		dbServerDao = SpringBeanGetter.getDaoOfDbServer();
	}

	public static JavaGenerator getInstance() {
		return instance;
	}

	@Override
	public void generateByTableView(List<GenTaskByTableViewSp> tasks) {
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
			boolean pagination = tableViewSp.isPagination();
			boolean cud_by_sp = tableViewSp.isCud_by_sp();
			DbServer dbServer = daoOfDbServer.getDbServerByID(tableViewSp
					.getServer_id());
			DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
			if (dbServer.getDb_type().equalsIgnoreCase("mysql")) {
				dbCategory = DatabaseCategory.MySql;
			}

			List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(
					tableViewSp.getServer_id(), dbName);

			for (String table : tableNames) {
				JavaTableHost tableHost = new JavaTableHost();
				tableHost.setPackageName(super.namespace);
				tableHost.setDatabaseCategory(dbCategory);
				tableHost.setDbName(dbName);
				tableHost.setTableName(table);
				tableHost.setPojoClassName(getPojoClassName(prefix, suffix, table));
				tableHost.setSpa(cud_by_sp);

				// 主键及所有列
				List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
						tableViewSp.getServer_id(), dbName, table);
				List<AbstractParameterHost> allColumnsAbstract = DbUtils
						.getAllColumnNames(tableViewSp.getServer_id(), dbName,
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
				tableHost.setHasIdentity(hasIdentity);
				tableHost.setIdentityColumnName(identityColumnName);
				tableHost.setMethods(methods);

//				tableHost.setTable(true);
				// SP方式增删改
				if (tableHost.isSpa()) {
					tableHost.setSpaInsert(SpaOperationHost.getSpaOperation(
							tableViewSp.getServer_id(), dbName, table, allSpNames,"i"));
					tableHost.setSpaUpdate(SpaOperationHost.getSpaOperation(
							tableViewSp.getServer_id(), dbName, table, allSpNames,"u"));
					tableHost.setSpaDelete(SpaOperationHost.getSpaOperation(
							tableViewSp.getServer_id(), dbName, table, allSpNames,"d"));
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
				List<AbstractParameterHost> params = DbUtils.getSpParams(
						tableViewSp.getServer_id(), dbName, currentSp, CurrentLanguage.Java);
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
		File mavenLikeDir = new File(String.format("gen/%s/java",
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

			FileWriter daoWriter = null;
			FileWriter pojoWriter = null;
			FileWriter testWriter = null;
			
			try {
				FileUtils.forceMkdir(mavenLikeDir);

				daoWriter = new FileWriter(String.format("%s/Dao/%sDao.java",
						mavenLikeDir.getAbsolutePath(), host.getPojoClassName()));
				pojoWriter = new FileWriter(String.format("%s/Entity/%s.java",
						mavenLikeDir.getAbsolutePath(), host.getPojoClassName()));
				testWriter = new FileWriter(String.format("%s/Test/%sTest.java",
						mavenLikeDir.getAbsolutePath(), host.getPojoClassName()));

				Velocity.mergeTemplate("templates/java/DAO.java.tpl", "UTF-8",
						context, daoWriter);
				Velocity.mergeTemplate("templates/java/Pojo.java.tpl", "UTF-8",
						context, pojoWriter);
				Velocity.mergeTemplate("templates/java/DAOTest.java.tpl", "UTF-8",
						context, testWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(daoWriter);
				JavaIOUtils.closeWriter(pojoWriter);
				JavaIOUtils.closeWriter(testWriter);
			}
		}
	}

	private void generateSpDao(List<SpHost> spHosts, VelocityContext context,
			File mavenLikeDir) {
		for (SpHost host : spHosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			FileWriter pojoWriter = null;
			FileWriter testWriter = null;
			
			try {

				daoWriter = new FileWriter(String.format("%s/Dao/%sDao.java",
						mavenLikeDir.getAbsolutePath(), host.getPojoClassName()));
				pojoWriter = new FileWriter(String.format("%s/Entity/%s.java",
						mavenLikeDir.getAbsolutePath(), host.getPojoClassName()));
				testWriter = new FileWriter(String.format("%s/Test/%sTest.java",
						mavenLikeDir.getAbsolutePath(), host.getPojoClassName()));

				Velocity.mergeTemplate("templates/java/DAOBySp.java.tpl", "UTF-8",
						context, daoWriter);
				Velocity.mergeTemplate("templates/java/Pojo.java.tpl", "UTF-8",
						context, pojoWriter);
				Velocity.mergeTemplate("templates/java/DAOBySpTest.java.tpl", "UTF-8",
						context, testWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(daoWriter);
				JavaIOUtils.closeWriter(pojoWriter);
				JavaIOUtils.closeWriter(testWriter);
			}
		}
	}

	private String getPojoClassName(String prefix, String suffix, String table) {
		String className = table;
		if (null != prefix && !prefix.isEmpty()) {
			className = className.substring(prefix.length());
		}
		if (null != suffix && !suffix.isEmpty()) {
			className = className + suffix;
		}
		return WordUtils.capitalize(className);
	}

	private List<GenTaskBySqlBuilder> filterExtraMethods(
			List<GenTaskBySqlBuilder> sqlBuilders, String dbName, String table) {
		List<GenTaskBySqlBuilder> currentTableBuilders = new ArrayList<GenTaskBySqlBuilder>();

		// 首先设置SqlBuilder的所有方法
		// size每次都会进行一次计算
		int wholeSize = sqlBuilders.size();
		List<Integer> itemsToRemove = new ArrayList<Integer>();
		for (int i = 0; i < wholeSize; i++) {
			GenTaskBySqlBuilder currentSqlBuilder = sqlBuilders.get(i);
			if (currentSqlBuilder.getDb_name().equals(dbName)
					&& currentSqlBuilder.getTable_name().equals(table)) {
				currentTableBuilders.add(currentSqlBuilder);
				itemsToRemove.add(i);
			}
		}

		for (Integer i : itemsToRemove) {
			sqlBuilders.remove(i);
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
						builder.getCondition(), ",");
				for (String condition : conditions) {
					String name = StringUtils.split(condition, "_")[0];
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
						builder.getCondition(), ",");
				for (JavaParameterHost pHost : allColumns) {
					for (String field : fields) {
						if (pHost.getName().equals(field)) {
							parameters.add(pHost);
							break;
						}
					}
					for (String condition : conditions) {
						String name = StringUtils.split(condition, "_")[0];
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
			String key = String.format("%s_%s_%s", task.getServer_id(),
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
						.split(task.getParameters(), ",")) {
					String[] splitedParam = StringUtils.split(param, "_");
					JavaParameterHost p = new JavaParameterHost();
					p.setName(splitedParam[0]);
					p.setSqlType(Integer.valueOf(splitedParam[1]));
					p.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(p.getSqlType()));
					p.setValidationValue(splitedParam[2]);
					params.add(p);
				}
				method.setParameters(params);
				methods.add(method);

				// Need to specify Pojo class name for each method. or allow Map<Sting, Object> as row
//				if (!pojoHosts.containsKey(task.getClass_name())) {
				if (!pojoHosts.containsKey(method.getPojoClassName())) {
					ResultSetMetaData rsMeta = DbUtils.testAQuerySql(
							task.getServer_id(), task.getDb_name(),
							task.getSql_content(), task.getParameters());

					if (rsMeta != null) {
						try {
							List<JavaParameterHost> paramHosts = new ArrayList<JavaParameterHost>();
							for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
								JavaParameterHost paramHost = new JavaParameterHost();
								paramHost.setName(rsMeta.getColumnName(i));
								paramHost.setSqlType(rsMeta.getColumnType(i));
								paramHost.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(paramHost.getSqlType()));
								paramHost.setIdentity(false);
								paramHost.setNullable(false);
								paramHost.setPrimary(false);
								paramHost.setLength(rsMeta.getColumnDisplaySize(i));
								paramHosts.add(paramHost);
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
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}

			}
			host.setMethods(methods);
			hosts.add(host);

		}

		VelocityContext context = new VelocityContext();
		context.put("WordUtils", WordUtils.class);
		context.put("StringUtils", StringUtils.class);
		
		File mavenLikeDir = new File(String.format("gen/%s/java",
				projectId));
		
//		for(FreeSqlPojoHost host : pojoHosts.values()){
		for(JavaMethodHost host : pojoHosts.values()){
			context.put("host", host);

			FileWriter pojoWriter = null;
			try {
				pojoWriter = new FileWriter(String.format("%s/Entity/%s.java",
						mavenLikeDir.getAbsolutePath(), host.getPojoClassName()));

				Velocity.mergeTemplate("templates/java/Pojo.java.tpl", "UTF-8",
						context, pojoWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(pojoWriter);
			}
		}

		for (FreeSqlHost host : hosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			FileWriter testWriter = null;
			
			try {

				daoWriter = new FileWriter(String.format("%s/Dao/%sDao.java",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));
				testWriter = new FileWriter(String.format("%s/Test/%sTest.java",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));

				Velocity.mergeTemplate("templates/java/FreeSqlDAO.java.tpl", "UTF-8",
						context, daoWriter);
				Velocity.mergeTemplate("templates/java/FreeSqlDAOTest.java.tpl", "UTF-8",
						context, testWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(daoWriter);
				JavaIOUtils.closeWriter(testWriter);
			}
		}
	}

	@Override
	public void generateBySqlBuilder(List<GenTask> tasks) {
		// TODO Auto-generated method stub
		
	}
	
}
