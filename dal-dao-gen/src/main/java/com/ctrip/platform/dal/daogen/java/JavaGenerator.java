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

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.daogen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.pojo.CurrentLanguage;
import com.ctrip.platform.dal.daogen.pojo.DatabaseCategory;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByFreeSql;
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

		// 首先为所有表/存储过程生成DAO
		for (GenTaskByTableViewSp tableViewSp : tasks) {

			String dbName = tableViewSp.getDb_name();
			String[] tableNames = StringUtils.split(
					tableViewSp.getTable_names(), ",");

			String prefix = tableViewSp.getPrefix();
			String suffix = tableViewSp.getSuffix();
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

				String className = table;
				if (null != prefix && !prefix.isEmpty()) {
					className = className.substring(prefix.length());
				}
				if (null != suffix && !suffix.isEmpty()) {
					className = className + suffix;
				}

				JavaTableHost tableHost = new JavaTableHost();
				tableHost.setNamespace(super.namespace);
				tableHost.setDbName(dbName);
				tableHost.setTableName(table);
				tableHost.setPojoClassName(className);
				tableHost.setFields(allColumns);
				tableHost.setHasIdentity(hasIdentity);
				tableHost.setIdentityColumnName(identityColumnName);
				tableHost.setSpa(cud_by_sp);

				tableHosts.add(tableHost);
			}
		}
		
		VelocityContext context = new VelocityContext();
		context.put("WordUtils", WordUtils.class);
		context.put("StringUtils", StringUtils.class);

		for (JavaTableHost host : tableHosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			try {
				File mavenLikeDir = new File(String.format("gen/%s/java",
						projectId));
				FileUtils.forceMkdir(mavenLikeDir);

				daoWriter = new FileWriter(String.format("%s/%sDao.java",
						mavenLikeDir.getAbsolutePath(), host.getPojoClassName()));

				Velocity.mergeTemplate("templates/DAO.java.tpl", "UTF-8",
						context, daoWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(daoWriter);
			}
		}
		
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
		Map<String, FreeSqlPojoHost> pojoHosts = new HashMap<String, FreeSqlPojoHost>();

		// 随后，以ServerID, DbName以及ClassName为维度，为每个维度生成一个DAO类
		for (Map.Entry<String, List<GenTaskByFreeSql>> entry : groupBy
				.entrySet()) {

			List<GenTaskByFreeSql> currentTasks = entry.getValue();

			if (currentTasks.size() < 1)
				continue;

			FreeSqlHost host = new FreeSqlHost();
			host.setDbSetName(currentTasks.get(0).getDb_name());
			host.setClassName(currentTasks.get(0).getClass_name());
			host.setNameSpaceEntity(String.format("%s.Entity.DataModel",
					super.namespace));
			host.setNameSpaceDao(String.format("%s.Dao", super.namespace));

			List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();
			// 每个Method可能就有一个Pojo
			for (GenTaskByFreeSql task : currentTasks) {
				JavaMethodHost method = new JavaMethodHost();
				method.setSql(task.getSql_content());
				method.setName(task.getMethod_name());
				List<JavaParameterHost> params = new ArrayList<JavaParameterHost>();
				for (String param : StringUtils
						.split(task.getParameters(), ",")) {
					String[] splitedParam = StringUtils.split(param, "_");
					JavaParameterHost p = new JavaParameterHost();
					p.setName(splitedParam[0]);
					p.setSqlType(Integer.valueOf(splitedParam[1]));
					p.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(p.getSqlType()));
					params.add(p);
				}
				method.setParameters(params);
				methods.add(method);

				if (!pojoHosts.containsKey(task.getClass_name())) {
					ResultSetMetaData rsMeta = DbUtils.testAQuerySql(
							task.getServer_id(), task.getDb_name(),
							task.getSql_content(), task.getParameters());

					if (rsMeta != null) {
						try {
							List<JavaParameterHost> pHosts = new ArrayList<JavaParameterHost>();
							for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
								JavaParameterHost pHost = new JavaParameterHost();
								pHost.setName(rsMeta.getColumnName(i));
								pHost.setSqlType(rsMeta.getColumnType(i));
								pHost.setJavaClass(Consts.jdbcSqlTypeToJavaClass.get(pHost.getSqlType()));
								pHost.setIdentity(false);
								pHost.setNullable(false);
								pHost.setPrimary(false);
								pHost.setLength(rsMeta.getColumnDisplaySize(i));
								pHosts.add(pHost);
							}
							FreeSqlPojoHost freeSqlHost = new FreeSqlPojoHost();
							freeSqlHost.setColumns(pHosts);
							freeSqlHost.setTableName("");
							freeSqlHost.setClassName(task.getClass_name());
							freeSqlHost.setNameSpaceDao(host.getNameSpaceDao());
							freeSqlHost.setNameSpaceEntity(host.getNameSpaceEntity());

							pojoHosts.put(task.getClass_name(), freeSqlHost);
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
		
		File mavenLikeDir = new File(String.format("gen/%s/cs",
				projectId));
		
		for(FreeSqlPojoHost host : pojoHosts.values()){
			context.put("host", host);

			FileWriter pojoWriter = null;
			try {
				pojoWriter = new FileWriter(String.format("%s/Entity/%s.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));

				Velocity.mergeTemplate("templates/Pojo.cs.tpl", "UTF-8",
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
			try {

				daoWriter = new FileWriter(String.format("%s/Dao/%sDao.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));

				Velocity.mergeTemplate("templates/FreeSqlDAO.cs.tpl", "UTF-8",
						context, daoWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(daoWriter);
			}
		}
	}

	@Override
	public void generateBySqlBuilder(List<GenTask> tasks) {
		// TODO Auto-generated method stub
		
	}
	
}