package com.ctrip.platform.daogen.domain;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;
import com.ctrip.platform.daogen.pojo.AutoTask;
import com.ctrip.platform.daogen.pojo.SqlTask;

public class SqlTaskDAO extends AbstractDAO {
	
	public SqlTaskDAO() {
		logicDbName = "daogen";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}
	
	public ResultSet getAllTasks() {
		return this.fetch("select id, project_id, db_name,class_name,method_name,crud_type,sql_content from task_sql",
				null, null);
	}

	public ResultSet getTasksByProjectId(int iD) {

		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false).setValue(iD)
				.build());

		return this.fetch(
				"select id, project_id, db_name,class_name,method_name,crud_type,sql_content from task_sql where project_id=?",
				parameters, null);
	}

	public int insertTask(SqlTask task) {

		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false)
				.setValue(task.getProject_id()).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(2).setName("").setSensitive(false)
				.setValue(task.getDb_name()).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(3).setName("").setSensitive(false)
				.setValue(task.getClass_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(4).setName("").setSensitive(false)
				.setValue(task.getMethod_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(5).setName("").setSensitive(false)
				.setValue(task.getCrud_type()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(6).setName("").setSensitive(false)
				.setValue(task.getSql_content()).build());

		return this
				.execute(
						"insert into task_sql (project_id, db_name,class_name,method_name,crud_type,sql_content) values (,?,?,?,?,?,?)",
						parameters, null);

	}

	public int updateTask(SqlTask task) {

		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		
		

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false)
				.setValue(task.getProject_id()).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(2).setName("").setSensitive(false)
				.setValue(task.getDb_name()).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(3).setName("").setSensitive(false)
				.setValue(task.getClass_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(4).setName("").setSensitive(false)
				.setValue(task.getMethod_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(5).setName("").setSensitive(false)
				.setValue(task.getCrud_type()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(6).setName("").setSensitive(false)
				.setValue(task.getSql_content()).build());
		
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(7).setName("").setSensitive(false)
				.setValue(task.getId()).build());

		return this
				.execute(
						"udpate task_sql set project_id=?, db_name=?,class_name=?,method_name=?,crud_type=?,sql_content=? where id=?",
						parameters, null);

	}

	public int deleteTask(SqlTask task) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false)
				.setValue(task.getId()).build());

		return this.execute("delete from task_sql where id=?", parameters, null);
	}

}
