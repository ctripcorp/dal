package com.ctrip.platform.dal.daogen.cs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.daogen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.Consts;
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
			host.setClassName(WordUtils.capitalize(currentTasks.get(0).getClass_name()));
			host.setNameSpace(super.namespace);

			List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();
			// 每个Method可能就有一个Pojo
			for (GenTaskByFreeSql task : currentTasks) {
				CSharpMethodHost method = new CSharpMethodHost();
				method.setSql(task.getSql_content());
				method.setName(task.getMethod_name());
				method.setPojoName(task.getPojo_name());
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
							freeSqlHost.setClassName(WordUtils.capitalize(task.getClass_name()));
							freeSqlHost.setNameSpace(host
									.getNameSpace());

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

		VelocityContext context = buildDefaultVelocityContext();

		File mavenLikeDir = new File(String.format("gen/%s/cs", projectId));

		for (CSharpFreeSqlPojoHost host : pojoHosts.values()) {
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

		for (CSharpFreeSqlHost host : hosts) {
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

	@Override
	public void generateByTableView(List<GenTaskByTableViewSp> tasks) {

		List<CSharpTableHost> tableHosts = new ArrayList<CSharpTableHost>();
		List<CSharpTableHost> spHosts = new ArrayList<CSharpTableHost>();

		// 首先为所有表/存储过程生成DAO
		for (GenTaskByTableViewSp tableViewSp : tasks) {

			String dbName = tableViewSp.getDb_name();
			String[] viewNames = StringUtils.split(tableViewSp.getView_names(),
					",");
			String[] tableNames = StringUtils.split(
					tableViewSp.getTable_names(), ",");
			String[] spNames = StringUtils
					.split(tableViewSp.getSp_names(), ",");

			String prefix = tableViewSp.getPrefix();
			String suffix = tableViewSp.getSuffix();
			boolean pagination = tableViewSp.isPagination();
			DbServer dbServer = daoOfDbServer.getDbServerByID(tableViewSp
					.getServer_id());
			DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
			if (dbServer.getDb_type().equalsIgnoreCase("mysql")) {
				dbCategory = DatabaseCategory.MySql;
			}

			List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(
					tableViewSp.getServer_id(), dbName);

			for (String table : tableNames) {
				tableHosts.add(buildTableHost(tableViewSp, table, dbCategory,
						allSpNames));
			}

			for (String view : viewNames) {
				List<AbstractParameterHost> allColumnsAbstract = DbUtils
						.getAllColumnNames(tableViewSp.getServer_id(), dbName,
								view, CurrentLanguage.CSharp);

				List<CSharpParameterHost> allColumns = new ArrayList<CSharpParameterHost>();
				for (AbstractParameterHost h : allColumnsAbstract) {
					allColumns.add((CSharpParameterHost) h);
				}

				for (CSharpParameterHost h : allColumns) {
					if (h.isNullable()
							&& Consts.CSharpValueTypes.contains(h.getType())) {
						h.setNullable(true);
					} else {
						h.setNullable(false);
					}
				}

				CSharpTableHost tableHost = new CSharpTableHost();
				tableHost.setNameSpace(super.namespace);
				tableHost.setDatabaseCategory(dbCategory);
				tableHost.setDbSetName(dbName);
				tableHost.setTableName(view);
				tableHost.setClassName(getPojoClassName(prefix, suffix, view));
				tableHost.setTable(false);
				tableHost.setSpa(false);
				tableHost.setColumns(allColumns);
				tableHost.setHasPagination(pagination);
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

				List<AbstractParameterHost> params = DbUtils.getSpParams(
						tableViewSp.getServer_id(), dbName, currentSp,CurrentLanguage.CSharp);
				List<CSharpParameterHost> realParams = new ArrayList<CSharpParameterHost>();
				for (AbstractParameterHost p : params) {
					realParams.add((CSharpParameterHost) p);
				}

				CSharpTableHost tableHost = new CSharpTableHost();
				tableHost.setNameSpace(super.namespace);
				tableHost.setDatabaseCategory(dbCategory);
				tableHost.setDbSetName(dbName);
				tableHost.setClassName(getPojoClassName(prefix, suffix,
						realSpName.replace("_", "")));
				tableHost.setTable(false);
				tableHost.setSpName(spName);
				tableHost.setSpParams(realParams);
				spHosts.add(tableHost);
			}

		}

		if (sqlBuilders.size() > 0) {
			List<GenTaskBySqlBuilder> _tableNames = new ArrayList<GenTaskBySqlBuilder>();
			for (GenTaskBySqlBuilder sqlBuilder : sqlBuilders) {
				_tableNames.add(sqlBuilder);

			}
			for (GenTaskBySqlBuilder _table : _tableNames) {
				GenTaskByTableViewSp tableViewSp = new GenTaskByTableViewSp();
				tableViewSp.setCud_by_sp(false);
				tableViewSp.setPagination(false);
				tableViewSp.setDb_name(_table.getDb_name());
				tableViewSp.setServer_id(_table.getServer_id());
				tableViewSp.setPrefix("");
				tableViewSp.setSuffix("_gen");

				DbServer dbServer = daoOfDbServer.getDbServerByID(tableViewSp
						.getServer_id());
				DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
				if (dbServer.getDb_type().equalsIgnoreCase("mysql")) {
					dbCategory = DatabaseCategory.MySql;
				}

				List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(
						tableViewSp.getServer_id(), _table.getDb_name());

				tableHosts.add(buildTableHost(tableViewSp,
						_table.getTable_name(), dbCategory, allSpNames));
			}
		}

		VelocityContext context = buildDefaultVelocityContext();

		File mavenLikeDir = new File(String.format("gen/%s/cs", projectId));

		generateTableDao(tableHosts, context, mavenLikeDir);

		generateSpDao(spHosts, context, mavenLikeDir);

	}

	private CSharpTableHost buildTableHost(GenTaskByTableViewSp tableViewSp,
			String table, DatabaseCategory dbCategory,
			List<StoredProcedure> allSpNames) {
		// 主键及所有列
		List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
				tableViewSp.getServer_id(), tableViewSp.getDb_name(), table);
		List<AbstractParameterHost> allColumnsAbstract = DbUtils
				.getAllColumnNames(tableViewSp.getServer_id(),
						tableViewSp.getDb_name(), table, CurrentLanguage.CSharp);

		List<CSharpParameterHost> allColumns = new ArrayList<CSharpParameterHost>();
		for (AbstractParameterHost h : allColumnsAbstract) {
			allColumns.add((CSharpParameterHost) h);
		}

		List<CSharpParameterHost> primaryKeys = new ArrayList<CSharpParameterHost>();
		for (CSharpParameterHost h : allColumns) {
			if (h.isNullable() && Consts.CSharpValueTypes.contains(h.getType())) {
				h.setNullable(true);
			} else {
				h.setNullable(false);
			}
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
		tableHost.setClassName(getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), table));
		tableHost.setTable(true);
		tableHost.setSpa(tableViewSp.isCud_by_sp());
		// SP方式增删改
		if (tableHost.isSpa()) {
			tableHost.setSpaInsert(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getServer_id(), tableViewSp.getDb_name(),
					table, allSpNames, "i"));
			tableHost.setSpaUpdate(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getServer_id(), tableViewSp.getDb_name(),
					table, allSpNames, "u"));
			tableHost.setSpaDelete(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getServer_id(), tableViewSp.getDb_name(),
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
						",");
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
						",");
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
		return methods;
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

	private void generateTableDao(List<CSharpTableHost> tableHosts,
			VelocityContext context, File mavenLikeDir) {
		for (CSharpTableHost host : tableHosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			FileWriter iDaoWriter = null;
			FileWriter pojoWriter = null;
			try {

				daoWriter = new FileWriter(String.format("%s/Dao/%sDao.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));
				pojoWriter = new FileWriter(String.format("%s/Entity/%s.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));
				iDaoWriter = new FileWriter(String.format("%s/IDao/I%sDao.cs",
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
	}

	private void generateSpDao(List<CSharpTableHost> spHosts,
			VelocityContext context, File mavenLikeDir) {
		for (CSharpTableHost host : spHosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			FileWriter pojoWriter = null;
			try {

				daoWriter = new FileWriter(String.format("%s/Dao/%sDao.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));
				pojoWriter = new FileWriter(String.format("%s/Entity/%s.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));

				Velocity.mergeTemplate("templates/DAOBySp.cs.tpl", "UTF-8",
						context, daoWriter);
				Velocity.mergeTemplate("templates/PojoBySp.cs.tpl", "UTF-8",
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
