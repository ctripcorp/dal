package com.ctrip.platform.dal.daogen.java;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.daogen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.cs.CSharpMethodHost;
import com.ctrip.platform.dal.daogen.cs.CSharpParameterHost;
import com.ctrip.platform.dal.daogen.cs.CSharpTableHost;
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.pojo.DatabaseCategory;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.pojo.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.StoredProcedure;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
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
		
		List<CSharpTableHost> tableHosts = new ArrayList<CSharpTableHost>();

		List<GenTaskBySqlBuilder> sqlBuilders = daoBySqlBuilder
				.getTasksByProjectId(projectId);

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
			}
		}
	}

	@Override
	public void generateByFreeSql(List<GenTaskByFreeSql> tasks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateBySqlBuilder(List<GenTask> tasks) {
		// TODO Auto-generated method stub
		
	}
	
}