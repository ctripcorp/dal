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

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.dao.AutoTaskDAO;
import com.ctrip.platform.dal.daogen.dao.DbServerDAO;
import com.ctrip.platform.dal.daogen.dao.ProjectDAO;
import com.ctrip.platform.dal.daogen.dao.SpTaskDAO;
import com.ctrip.platform.dal.daogen.dao.SqlTaskDAO;
import com.ctrip.platform.dal.daogen.pojo.AutoTask;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.FieldMeta;
import com.ctrip.platform.dal.daogen.pojo.Project;
import com.ctrip.platform.dal.daogen.pojo.SpTask;
import com.ctrip.platform.dal.daogen.pojo.SqlTask;
import com.ctrip.platform.dal.daogen.pojo.Task;
import com.ctrip.platform.dal.daogen.utils.DataSourceLRUCache;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public abstract class AbstractGenerator implements Generator {

	protected static ProjectDAO projectDao;

	protected static AutoTaskDAO autoTaskDao;

	protected static SpTaskDAO spTaskDao;

	protected static SqlTaskDAO sqlTaskDao;

	private static DbServerDAO dbServerDao;

	protected String namespace;

	protected String projectId;

	static {

		projectDao = SpringBeanGetter.getProjectDao();
		autoTaskDao = SpringBeanGetter.getAutoTaskDao();
		spTaskDao = SpringBeanGetter.getSpTaskDao();
		sqlTaskDao = SpringBeanGetter.getSqlTaskDao();
		dbServerDao = SpringBeanGetter.getDBServerDao();

		java.util.Properties pr = new java.util.Properties();
		pr.setProperty("resource.loader", "class");
		pr.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(pr);
	}

	@Override
	public boolean generateCode(String projectId) {

		Project proj = projectDao.getProjectByID(Integer.valueOf(projectId));

		if (null != proj) {
			namespace = proj.getNamespace();
			this.projectId = projectId;
		}

		List<AutoTask> autoTasks = autoTaskDao.getTasksByProjectId(Integer
				.valueOf(projectId));
		List<Task> _autoTasks = new ArrayList<Task>();
		for (AutoTask t : autoTasks) {
			_autoTasks.add(t);
		}
		generateAutoSqlCode(_autoTasks);

		// 存储过程
		List<SpTask> sp = spTaskDao.getTasksByProjectId(Integer
				.valueOf(projectId));
		List<Task> _sp = new ArrayList<Task>();
		for (SpTask t : sp) {
			_sp.add(t);
		}
		generateSPCode(_sp);

		// 手工编写的SQL
		List<SqlTask> _freeSql = sqlTaskDao.getTasksByProjectId(Integer
				.valueOf(projectId));
		List<Task> freeSql = new ArrayList<Task>();
		for (SqlTask t : _freeSql) {
			freeSql.add(t);
		}
		generateFreeSqlCode(freeSql);

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

					int dataType = allColumnsRs.getInt("DATA_TYPE"); 
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
					meta.setDataType(dataType);
					meta.setJavaClass(Consts.JavaSqlTypeMap.get(dataType));
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
	protected Map<String, List<Task>> groupByDbName(List<Task> tasks) {
		Map<String, List<Task>> groupBy = new HashMap<String, List<Task>>();

		for (Task t : tasks) {
			if (groupBy.containsKey(t.getDb_name())) {
				groupBy.get(t.getDb_name()).add(t);
			} else {
				List<Task> objs = new ArrayList<Task>();
				objs.add(t);
				groupBy.put(t.getDb_name(), objs);
			}
		}

		return groupBy;
	};

	protected Map<String, List<Task>> groupByTableName(List<Task> tasks) {
		Map<String, List<Task>> groupBy = new HashMap<String, List<Task>>();

		for (Task t : tasks) {
			if (groupBy.containsKey(t.getTable_name())) {
				groupBy.get(t.getTable_name()).add(t);
			} else {
				List<Task> objs = new ArrayList<Task>();
				objs.add(t);
				groupBy.put(t.getTable_name(), objs);
			}
		}

		return groupBy;
	};

	@Override
	public abstract void generateAutoSqlCode(List<Task> tasks);

	@Override
	public abstract void generateSPCode(List<Task> tasks);

	@Override
	public abstract void generateFreeSqlCode(List<Task> tasks);

}