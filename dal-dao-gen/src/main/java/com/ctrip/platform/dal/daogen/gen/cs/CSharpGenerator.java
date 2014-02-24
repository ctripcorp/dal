package com.ctrip.platform.dal.daogen.gen.cs;

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
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.gen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.gen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.pojo.DatabaseCategory;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.pojo.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.StoredProcedure;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.JavaIOUtils;

public class CSharpGenerator extends AbstractGenerator {

	private CSharpGenerator() {

	}

	private static CSharpGenerator instance = new CSharpGenerator();

	public static CSharpGenerator getInstance() {
		return instance;
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

		List<CSharpFreeSqlHost> hosts = new ArrayList<CSharpFreeSqlHost>();
		Map<String, CSharpFreeSqlPojoHost> pojoHosts = new HashMap<String, CSharpFreeSqlPojoHost>();

		// 随后，以ServerID, DbName以及ClassName为维度，为每个维度生成一个DAO类
		for (Map.Entry<String, List<GenTaskByFreeSql>> entry : groupBy
				.entrySet()) {

			List<GenTaskByFreeSql> currentTasks = entry.getValue();

			if (currentTasks.size() < 1)
				continue;

			CSharpFreeSqlHost host = new CSharpFreeSqlHost();
			host.setDbSetName(currentTasks.get(0).getDb_name());
			host.setClassName(currentTasks.get(0).getClass_name());
			host.setNameSpaceEntity(String.format("%s.Entity.DataModel",
					super.namespace));
			host.setNameSpaceDao(String.format("%s.Dao", super.namespace));

			List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();
			// 每个Method可能就有一个Pojo
			for (GenTaskByFreeSql task : currentTasks) {
				CSharpMethodHost method = new CSharpMethodHost();
				method.setSql(task.getSql_content());
				method.setName(task.getMethod_name());
				List<CSharpParameterHost> params = new ArrayList<CSharpParameterHost>();
				for (String param : StringUtils
						.split(task.getParameters(), ",")) {
					String[] splitedParam = StringUtils.split(param, "_");
					CSharpParameterHost p = new CSharpParameterHost();
					p.setName(splitedParam[0]);
					p.setDbType(DbType.getDbTypeFromJdbcType(Integer
							.valueOf(splitedParam[1])));
					p.setType(DbType.getCSharpType(p.getDbType()));
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
							List<CSharpParameterHost> pHosts = new ArrayList<CSharpParameterHost>();
							for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
								CSharpParameterHost pHost = new CSharpParameterHost();
								pHost.setName(rsMeta.getColumnName(i));
								pHost.setDbType(DbType
										.getDbTypeFromJdbcType(rsMeta
												.getColumnType(i)));
								pHost.setType(DbType.getCSharpType(pHost
										.getDbType()));
								pHost.setIdentity(false);
								pHost.setNullable(false);
								pHost.setPrimary(false);
								pHost.setLength(rsMeta.getColumnDisplaySize(i));
								pHosts.add(pHost);
							}
							CSharpFreeSqlPojoHost freeSqlHost = new CSharpFreeSqlPojoHost();
							freeSqlHost.setColumns(pHosts);
							freeSqlHost.setTableName("");
							freeSqlHost.setClassName(task.getClass_name());
							freeSqlHost.setNameSpaceDao(host.getNameSpaceDao());

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
		
		for(CSharpFreeSqlPojoHost host : pojoHosts.values()){
			context.put("host", host);

			FileWriter pojoWriter = null;
			try {
				File mavenLikeDir = new File(String.format("gen/%s/cs",
						projectId));
				FileUtils.forceMkdir(mavenLikeDir);

				pojoWriter = new FileWriter(String.format("%s/%s.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));

				Velocity.mergeTemplate("templates/Pojo.cs.tpl", "UTF-8",
						context, pojoWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(pojoWriter);
			}
		}

		for (CSharpFreeSqlHost host : hosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			try {
				File mavenLikeDir = new File(String.format("gen/%s/cs",
						projectId));
				FileUtils.forceMkdir(mavenLikeDir);

				daoWriter = new FileWriter(String.format("%s/%sDao.cs",
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

	@Override
	public void generateByTableView(List<GenTaskByTableViewSp> tasks) {

		List<CSharpTableHost> tableHosts = new ArrayList<CSharpTableHost>();
		List<CSharpTableHost> spHosts = new ArrayList<CSharpTableHost>();

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

				// 主键及所有列
				List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
						tableViewSp.getServer_id(), dbName, table);
				List<CSharpParameterHost> allColumns = DbUtils
						.getAllColumnNames(tableViewSp.getServer_id(), dbName,
								table);

				List<CSharpParameterHost> primaryKeys = new ArrayList<CSharpParameterHost>();
				for (CSharpParameterHost h : allColumns) {
					if (h.isNullable()
							&& Consts.CSharpValueTypes.contains(h.getType())) {
						h.setNullable(true);
					} else {
						h.setNullable(false);
					}
					if (primaryKeyNames.contains(h.getName())) {
						h.setPrimary(true);
						primaryKeys.add(h);
					}
				}

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

				List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();

				for (GenTaskBySqlBuilder builder : currentTableBuilders) {
					CSharpMethodHost method = new CSharpMethodHost();
					method.setCrud_type(builder.getCrud_type());
					method.setName(builder.getMethod_name());
					method.setSql(builder.getSql_content());
					List<CSharpParameterHost> parameters = new ArrayList<CSharpParameterHost>();
					if (method.getCrud_type().equals("select")
							|| method.getCrud_type().equals("delete")) {
						String[] conditions = StringUtils.split(
								builder.getCondition(), ",");
						for (String condition : conditions) {
							String name = StringUtils.split(condition, "_")[0];
							for (CSharpParameterHost pHost : allColumns) {
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
							for (CSharpParameterHost pHost : allColumns) {
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
						for (CSharpParameterHost pHost : allColumns) {
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

				CSharpTableHost tableHost = new CSharpTableHost();

				tableHost.setExtraMethods(methods);

				String className = table;
				if (null != prefix && !prefix.isEmpty()) {
					className = className.substring(prefix.length());
				}
				if (null != suffix && !suffix.isEmpty()) {
					className = className + suffix;
				}

				tableHost.setNameSpaceEntity(String.format(
						"%s.Entity.DataModel", super.namespace));
				tableHost.setNameSpaceIDao(String.format("%s.Interface.IDao",
						super.namespace));
				tableHost.setNameSpaceDao(String.format("%s.Dao",
						super.namespace));
				tableHost.setDatabaseCategory(dbCategory);
				tableHost.setDbSetName(dbName);
				tableHost.setTableName(table);
				tableHost.setClassName(className);
				tableHost.setTable(true);
				tableHost.setSpa(cud_by_sp);
				// SP方式增删改
				if (tableHost.isSpa()) {
					CSharpSpInsertHost insertHost = CSharpSpInsertHost
							.getInsertSp(tableViewSp.getServer_id(), dbName,
									table, allSpNames);
					tableHost
							.setHasInsertMethod(insertHost.isHasInsertMethod());
					if (tableHost.isHasInsertMethod()) {
						tableHost.setInsertMethodName(insertHost
								.getInsertMethodName());
						tableHost.setInsertParameterList(insertHost
								.getInsertParameterList());
					}

					CSharpSpUpdateHost updateHost = CSharpSpUpdateHost
							.getUpdateSp(tableViewSp.getServer_id(), dbName,
									table, allSpNames);
					tableHost
							.setHasUpdateMethod(updateHost.isHasUpdateMethod());
					if (tableHost.isHasUpdateMethod()) {
						tableHost.setUpdateMethodName(updateHost
								.getUpdateMethodName());
						tableHost.setUpdateParameterList(updateHost
								.getUpdateParameterList());
					}

					CSharpSpDeleteHost deleteHost = CSharpSpDeleteHost
							.getDeleteSp(tableViewSp.getServer_id(), dbName,
									table, allSpNames);
					tableHost
							.setHasDeleteMethod(deleteHost.isHasDeleteMethod());
					if (tableHost.isHasDeleteMethod()) {
						tableHost.setDeleteMethodName(deleteHost
								.getDeleteMethodName());
						tableHost.setDeleteParameterList(deleteHost
								.getDeleteParameterList());
					}
				}

				tableHost.setPrimaryKeys(primaryKeys);
				tableHost.setColumns(allColumns);

				tableHost.setHasPagination(pagination);

				StoredProcedure expectSptI = new StoredProcedure();
				expectSptI.setName(String.format("spT_%s_i", table));

				StoredProcedure expectSptU = new StoredProcedure();
				expectSptU.setName(String.format("spT_%s_u", table));

				StoredProcedure expectSptD = new StoredProcedure();
				expectSptD.setName(String.format("spT_%s_d", table));

				tableHost.setHasSptI(allSpNames.contains(expectSptI));
				tableHost.setHasSptU(allSpNames.contains(expectSptU));
				tableHost.setHasSptD(allSpNames.contains(expectSptD));
				tableHost.setHasSpt(tableHost.isHasSptI()
						|| tableHost.isHasSptU() || tableHost.isHasSptD());

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

				CSharpTableHost tableHost = new CSharpTableHost();
				String className = realSpName.replace("_", "");
				if (null != prefix && !prefix.isEmpty()) {
					className = className.substring(prefix.length());
				}
				if (null != suffix && !suffix.isEmpty()) {
					className = className + suffix;
				}

				tableHost.setNameSpaceEntity(String.format(
						"%s.Entity.DataModel", super.namespace));
				tableHost.setNameSpaceDao(String.format("%s.Dao",
						super.namespace));
				tableHost.setDatabaseCategory(dbCategory);
				tableHost.setDbSetName(dbName);
				tableHost.setClassName(className);
				tableHost.setTable(false);
				tableHost.setSpName(spName);
				List<AbstractParameterHost> params = DbUtils.getSpParams(
						tableViewSp.getServer_id(), dbName, currentSp, 0);
				List<CSharpParameterHost> realParams = new ArrayList<CSharpParameterHost>();
				for (AbstractParameterHost p : params) {
					realParams.add((CSharpParameterHost) p);
				}
				tableHost.setSpParams(realParams);

				spHosts.add(tableHost);
			}

		}

		VelocityContext context = new VelocityContext();
		context.put("WordUtils", WordUtils.class);
		context.put("StringUtils", StringUtils.class);

		for (CSharpTableHost host : tableHosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			FileWriter iDaoWriter = null;
			FileWriter pojoWriter = null;
			try {
				File mavenLikeDir = new File(String.format("gen/%s/cs",
						projectId));
				FileUtils.forceMkdir(mavenLikeDir);

				daoWriter = new FileWriter(String.format("%s/%sDao.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));
				pojoWriter = new FileWriter(String.format("%s/%s.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));
				iDaoWriter = new FileWriter(String.format("%s/I%sDao.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));

				Velocity.mergeTemplate("templates/DAO.cs.tpl", "UTF-8",
						context, daoWriter);
				Velocity.mergeTemplate("templates/Pojo.cs.tpl", "UTF-8",
						context, pojoWriter);
				Velocity.mergeTemplate("templates/IDAO.cs.tpl", "UTF-8",
						context, iDaoWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(daoWriter);
				JavaIOUtils.closeWriter(pojoWriter);
				JavaIOUtils.closeWriter(iDaoWriter);
			}
		}

		for (CSharpTableHost host : spHosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			FileWriter pojoWriter = null;
			try {
				File mavenLikeDir = new File(String.format("gen/%s/cs",
						projectId));
				FileUtils.forceMkdir(mavenLikeDir);

				daoWriter = new FileWriter(String.format("%s/%sDao.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));
				pojoWriter = new FileWriter(String.format("%s/%s.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));

				Velocity.mergeTemplate("templates/SpDAO.cs.tpl", "UTF-8",
						context, daoWriter);
				Velocity.mergeTemplate("templates/SpPojo.cs.tpl", "UTF-8",
						context, pojoWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(daoWriter);
				JavaIOUtils.closeWriter(pojoWriter);
			}
		}

	}

}
