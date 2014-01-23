package com.ctrip.platform.daogen.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.StatementParameter;
import com.ctrip.platform.dao.client.AbstractDAO;
import com.ctrip.platform.daogen.pojo.AutoTask;
import com.ctrip.sysdev.das.common.enums.DbType;
import com.ctrip.sysdev.das.common.enums.ParameterDirection;

public class AutoTaskDAO extends AbstractDAO {
	
	public AutoTaskDAO() {
		logicDbName = "daogen";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}
	
	public ResultSet getAllTasks() {
		return this.fetch("select id, project_id, db_name, table_name,class_name,method_name,sql_style,sql_type,crud_type,fields,condition,sql_content from task_auto",
				null, null);
	}

	public ResultSet getTasksByProjectId(int iD) {

		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false).setValue(iD)
				.build());

		return this.fetch(
				"select id, project_id, db_name, table_name,class_name,method_name,sql_style,sql_type,crud_type,fields,where_condition,sql_content from task_auto where project_id=?",
				parameters, null);
	}

	public int insertTask(AutoTask task) {

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
				.setValue(task.getTable_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(4).setName("").setSensitive(false)
				.setValue(task.getClass_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(5).setName("").setSensitive(false)
				.setValue(task.getMethod_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(6).setName("").setSensitive(false)
				.setValue(task.getSql_style()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(7).setName("").setSensitive(false)
				.setValue(task.getSql_type()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(8).setName("").setSensitive(false)
				.setValue(task.getCrud_type()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(9).setName("").setSensitive(false)
				.setValue(task.getFields()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(10).setName("").setSensitive(false)
				.setValue(task.getCondition()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(11).setName("").setSensitive(false)
				.setValue(task.getSql_content()).build());

		return this
				.execute(
						"insert into task_auto (project_id, db_name, table_name,class_name,method_name,sql_style,sql_type,crud_type,fields,where_condition,sql_content) values (?,?,?,?,?,?,?,?,?,?,?)",
						parameters, null);

	}

	public int updateTask(AutoTask task) {

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
				.setValue(task.getTable_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(4).setName("").setSensitive(false)
				.setValue(task.getClass_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(5).setName("").setSensitive(false)
				.setValue(task.getMethod_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(6).setName("").setSensitive(false)
				.setValue(task.getSql_style()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(7).setName("").setSensitive(false)
				.setValue(task.getSql_type()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(8).setName("").setSensitive(false)
				.setValue(task.getCrud_type()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(9).setName("").setSensitive(false)
				.setValue(task.getFields()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(10).setName("").setSensitive(false)
				.setValue(task.getCondition()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(11).setName("").setSensitive(false)
				.setValue(task.getSql_content()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(12).setName("").setSensitive(false)
				.setValue(task.getId()).build());

		return this
				.execute(
						"update task_auto set project_id=?, db_name=?, table_name=?,class_name=?,method_name=?,sql_style=?,sql_type=?,crud_type=?,fields=?,where_condition=?,sql_content=? where id=?",
						parameters, null);

	}

	public int deleteTask(AutoTask task) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false)
				.setValue(task.getId()).build());

		return this.execute("delete from task_auto where id=?", parameters, null);
	}


}