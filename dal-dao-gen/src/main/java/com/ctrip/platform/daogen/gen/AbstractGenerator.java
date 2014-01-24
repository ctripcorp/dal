package com.ctrip.platform.daogen.gen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.Velocity;

import com.ctrip.platform.daogen.Consts;
import com.ctrip.platform.daogen.dao.AutoTaskDAO;
import com.ctrip.platform.daogen.dao.MasterDAO;
import com.ctrip.platform.daogen.dao.ProjectDAO;
import com.ctrip.platform.daogen.dao.SPTaskDAO;
import com.ctrip.platform.daogen.dao.SqlTaskDAO;
import com.ctrip.platform.daogen.pojo.AutoTask;
import com.ctrip.platform.daogen.pojo.FieldMeta;
import com.ctrip.platform.daogen.pojo.Project;
import com.ctrip.platform.daogen.pojo.SpTask;
import com.ctrip.platform.daogen.pojo.SqlTask;
import com.ctrip.platform.daogen.pojo.Task;

public abstract class AbstractGenerator implements Generator {

	protected static MasterDAO masterDao;

	protected static ProjectDAO projectDao;

	protected static AutoTaskDAO autoTaskDao;

	protected static SPTaskDAO spTaskDao;

	protected static SqlTaskDAO sqlTaskDao;

	protected String namespace;

	protected String projectId;

	static {

		masterDao = new MasterDAO();
		projectDao = new ProjectDAO();
		autoTaskDao = new AutoTaskDAO();
		spTaskDao = new SPTaskDAO();
		sqlTaskDao = new SqlTaskDAO();

		java.util.Properties pr = new java.util.Properties();
		pr.setProperty("resource.loader", "class");
		pr.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(pr);
	}

	@Override
	public boolean generateCode(String projectId) {

		ResultSet projectsResultSet = projectDao.getProjectByID(Integer
				.valueOf(projectId));
		Project proj = new Project();
		try {
			if (projectsResultSet.next()) {
				proj.setId(projectsResultSet.getInt(1));
				proj.setUser_id(projectsResultSet.getInt(2));
				proj.setName(projectsResultSet.getString(3));
				proj.setNamespace(projectsResultSet.getString(4));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (null != proj) {
			namespace = proj.getNamespace();
			this.projectId = projectId;
		}

		List<Task> autoTasks = new ArrayList<Task>();
		ResultSet autoSqlResultSet = autoTaskDao.getTasksByProjectId(proj
				.getId());
		try {
			while (autoSqlResultSet.next()) {
				AutoTask task = new AutoTask();
				task.setId(autoSqlResultSet.getInt(1));
				task.setProject_id(autoSqlResultSet.getInt(2));
				task.setDb_name(autoSqlResultSet.getString(3));
				task.setTable_name(autoSqlResultSet.getString(4));
				task.setClass_name(autoSqlResultSet.getString(5));
				task.setMethod_name(autoSqlResultSet.getString(6));
				task.setSql_style(autoSqlResultSet.getString(7));
				task.setSql_type(autoSqlResultSet.getString(8));
				task.setCrud_type(autoSqlResultSet.getString(9));
				task.setFields(autoSqlResultSet.getString(10));
				task.setCondition(autoSqlResultSet.getString(11));
				task.setSql_content(autoSqlResultSet.getString(12));
				autoTasks.add(task);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		generateAutoSqlCode(autoTasks);

		// 存储过程
		List<Task> sp = new ArrayList<Task>();
		ResultSet spResultSet = spTaskDao.getTasksByProjectId(proj
				.getId());
		try {
			while (spResultSet.next()) {
				SpTask task = new SpTask();
				task.setId(spResultSet.getInt(1));
				task.setProject_id(spResultSet.getInt(2));
				task.setDb_name(spResultSet.getString(3));
				task.setClass_name(spResultSet.getString(4));
				task.setSp_schema(spResultSet.getString(5));
				task.setSp_name(spResultSet.getString(6));
				task.setSql_style(spResultSet.getString(7));
				task.setCrud_type(spResultSet.getString(8));
				task.setSp_content(spResultSet.getString(9));
				sp.add(task);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		generateSPCode(sp);

		// 手工编写的SQL
		List<Task> freeSql = new ArrayList<Task>();
		generateFreeSqlCode(freeSql);

		return true;

	}

	protected List<FieldMeta> getMetaData(String dbName, String tableName) {
		ResultSet allColumns = masterDao.getAllColumns(dbName, tableName);
		ResultSet primaryKeyResultSet = masterDao.getPrimaryKey(dbName,
				tableName);
		String primaryKey = "";
		try {
			if (primaryKeyResultSet.next()) {
				primaryKey = primaryKeyResultSet.getString(1);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<FieldMeta> fields = new ArrayList<FieldMeta>();
		try {
			while (allColumns.next()) {
				FieldMeta meta = new FieldMeta();

				String columnName = allColumns.getString(1);
				String columnType = allColumns.getString(2);
				int position = allColumns.getInt(3);
				String nullable = allColumns.getString(4);

				meta.setName(columnName);
				meta.setType(columnType);
				meta.setPosition(position);
				meta.setPrimary(columnName.equals(primaryKey));
				meta.setNullable(nullable.equalsIgnoreCase("yes") && Consts.CSharpValueTypes.contains(columnType));
				meta.setValueType(Consts.CSharpValueTypes.contains(columnType));

				fields.add(meta);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
