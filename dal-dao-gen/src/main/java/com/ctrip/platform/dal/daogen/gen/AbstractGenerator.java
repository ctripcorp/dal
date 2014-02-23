package com.ctrip.platform.dal.daogen.gen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.velocity.app.Velocity;
import org.springframework.jdbc.support.JdbcUtils;

import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.FieldMeta;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.Project;
import com.ctrip.platform.dal.daogen.utils.DataSourceLRUCache;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public abstract class AbstractGenerator implements Generator {

	protected static DaoOfProject projectDao;

	protected static DaoBySqlBuilder autoTaskDao;

	protected static DaoByFreeSql sqlTaskDao;

	protected static DaoOfDbServer dbServerDao;
	
	protected static DaoByTableViewSp daoByTableViewSp;

	protected String namespace;

	protected int projectId;

	static {

		projectDao = SpringBeanGetter.getProjectDao();
		autoTaskDao = SpringBeanGetter.getDaoBySqlBuilder();
		sqlTaskDao = SpringBeanGetter.getDaoByFreeSql();
		dbServerDao = SpringBeanGetter.getDBServerDao();
		daoByTableViewSp = SpringBeanGetter.getDaoByTableViewSp();

		java.util.Properties pr = new java.util.Properties();
		pr.setProperty("resource.loader", "class");
		pr.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(pr);
	}

	@Override
	public boolean generateCode(int projectId) {

		Project proj = projectDao.getProjectByID(projectId);

		if (null != proj) {
			namespace = proj.getNamespace();
			this.projectId = projectId;
		}

		generateByTableView(daoByTableViewSp.getTasksByProjectId(projectId));

//		// 存储过程
//		List<GenTaskBySP> sp = spTaskDao.getTasksByProjectId(Integer
//				.valueOf(projectId));
//		List<GenTask> _sp = new ArrayList<GenTask>();
//		for (GenTaskBySP t : sp) {
//			_sp.add(t);
//		}
//		generateBySP(_sp);
//
//		// 手工编写的SQL
//		List<GenTaskByFreeSql> _freeSql = sqlTaskDao.getTasksByProjectId(Integer
//				.valueOf(projectId));
//		List<GenTask> freeSql = new ArrayList<GenTask>();
//		for (GenTaskByFreeSql t : _freeSql) {
//			freeSql.add(t);
//		}
//		generateByFreeSql(freeSql);

		return true;

	}

	protected List<FieldMeta> getMetaData(int server, String dbName,
			String tableName) {

		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(server);

		if (ds == null) {
			DbServer dbServer = dbServerDao.getDbServerByID(server);
			ds = DataSourceLRUCache.newInstance().putDataSource(dbServer);
		}

		Set<String> primaryKeys = new HashSet<String>();
		List<FieldMeta> fields = new ArrayList<FieldMeta>();

		Connection connection = null;
		try {
			connection = ds.getConnection();
			// 获取所有主键
			ResultSet primaryKeyRs = null;
			try {
				primaryKeyRs = connection.getMetaData().getPrimaryKeys(dbName,
						null, tableName);
				while (primaryKeyRs.next()) {
					primaryKeys.add(primaryKeyRs.getString("COLUMN_NAME"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.closeResultSet(primaryKeyRs);
			}

			// 获取所有列
			ResultSet allColumnsRs = null;
			try {
				allColumnsRs = connection.getMetaData().getColumns(dbName,
						null, tableName, null);
				
				

				while (allColumnsRs.next()) {
					FieldMeta meta = new FieldMeta();

					String columnName = allColumnsRs.getString("COLUMN_NAME");
					//获取出来的数据类型是varchar这一类的
					String columnType = allColumnsRs.getString("TYPE_NAME");
					int position = allColumnsRs.getInt("ORDINAL_POSITION");
					String isIdentity = allColumnsRs.getString("IS_AUTOINCREMENT");
					
					meta.setIdentity(isIdentity.equalsIgnoreCase("yes"));
					meta.setName(columnName);
					meta.setDbType(columnType);
					meta.setPosition(position);
					meta.setPrimary(primaryKeys.contains(columnName));
					// meta.setNullable(nullable.equalsIgnoreCase("yes")
					// && Consts.CSharpValueTypes.contains(columnType));
					// meta.setValueType(Consts.CSharpValueTypes.contains(columnType));

					fields.add(meta);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.closeResultSet(allColumnsRs);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			JdbcUtils.closeConnection(connection);
		}

		return fields;
	}

	/**
	 * For a list of DBObject, return a map group by a condition
	 * 
	 * @param tasks
	 * @param condition
	 * @return
	 */
	protected Map<String, List<GenTask>> groupByDbName(List<GenTask> tasks) {
		Map<String, List<GenTask>> groupBy = new HashMap<String, List<GenTask>>();

		for (GenTask t : tasks) {
			if (groupBy.containsKey(t.getDb_name())) {
				groupBy.get(t.getDb_name()).add(t);
			} else {
				List<GenTask> objs = new ArrayList<GenTask>();
				objs.add(t);
				groupBy.put(t.getDb_name(), objs);
			}
		}

		return groupBy;
	};

	protected Map<String, List<GenTask>> groupByTableName(List<GenTask> tasks) {
		Map<String, List<GenTask>> groupBy = new HashMap<String, List<GenTask>>();

		for (GenTask t : tasks) {
			if (groupBy.containsKey(t.getTable_name())) {
				groupBy.get(t.getTable_name()).add(t);
			} else {
				List<GenTask> objs = new ArrayList<GenTask>();
				objs.add(t);
				groupBy.put(t.getTable_name(), objs);
			}
		}

		return groupBy;
	};

	@Override
	public abstract void generateByTableView(List<GenTaskByTableViewSp> tasks);

	@Override
	public abstract void generateByFreeSql(List<GenTask> tasks);
	
	@Override
	public abstract void generateBySqlBuilder(List<GenTask> tasks);

}