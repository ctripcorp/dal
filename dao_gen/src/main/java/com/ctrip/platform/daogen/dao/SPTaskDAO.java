<<<<<<< HEAD:dao_gen/src/main/java/com/ctrip/platform/daogen/domain/SPTaskDAO.java
package com.ctrip.platform.daogen.domain;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.param.StatementParameter;
import com.ctrip.platform.daogen.pojo.SpTask;
import com.ctrip.sysdev.das.common.enums.DbType;
import com.ctrip.sysdev.das.common.enums.ParameterDirection;

public class SPTaskDAO extends AbstractDAO {

	public SPTaskDAO() {
		logicDbName = "daogen";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}
	
	public ResultSet getAllTasks() {
		return this.fetch("select id, project_id, db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content from task_sp",
				null, null);
	}

	public ResultSet getTasksByProjectId(int iD) {

		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false).setValue(iD)
				.build());

		return this.fetch(
				"select id, project_id, db_name, table_name,class_name,method_name,sql_style,sql_type,crud_type from task_auto where project_id=?",
				parameters, null);
	}

	public int insertTask(SpTask task) {

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
				.setValue(task.getSp_schema()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(5).setName("").setSensitive(false)
				.setValue(task.getSp_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(6).setName("").setSensitive(false)
				.setValue(task.getSql_style()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(7).setName("").setSensitive(false)
				.setValue(task.getCrud_type()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(8).setName("").setSensitive(false)
				.setValue(task.getSp_content()).build());

		return this
				.execute(
						"insert into task_sp ( project_id, db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content) values (?,?,?,?,?,?,?,?)",
						parameters, null);

	}

	public int updateTask(SpTask task) {

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
				.setValue(task.getSp_schema()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(5).setName("").setSensitive(false)
				.setValue(task.getSp_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(6).setName("").setSensitive(false)
				.setValue(task.getSql_style()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(7).setName("").setSensitive(false)
				.setValue(task.getCrud_type()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(8).setName("").setSensitive(false)
				.setValue(task.getSp_content()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(9).setName("").setSensitive(false)
				.setValue(task.getId()).build());

		return this
				.execute(
						"udpate task_sp set project_id=?, db_name=?, class_name=?,sp_schema=?,sp_name=?,sql_style=?,crud_type=?,sp_content=? where id=?",
						parameters, null);

	}

	public int deleteTask(SpTask task) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false)
				.setValue(task.getId()).build());

		return this.execute("delete from task_sp where id=?", parameters, null);
	}
	
}
=======
package com.ctrip.platform.daogen.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;
import com.ctrip.platform.daogen.pojo.AutoTask;
import com.ctrip.platform.daogen.pojo.SpTask;

public class SPTaskDAO extends AbstractDAO {

	public SPTaskDAO() {
		logicDbName = "daogen";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}
	
	public ResultSet getAllTasks() {
		return this.fetch("select id, project_id, db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content from task_sp",
				null, null);
	}

	public ResultSet getTasksByProjectId(int iD) {

		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false).setValue(iD)
				.build());

		return this.fetch(
				"select id, project_id, db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content from task_sp where project_id=?",
				parameters, null);
	}

	public int insertTask(SpTask task) {

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
				.setValue(task.getSp_schema()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(5).setName("").setSensitive(false)
				.setValue(task.getSp_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(6).setName("").setSensitive(false)
				.setValue(task.getSql_style()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(7).setName("").setSensitive(false)
				.setValue(task.getCrud_type()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(8).setName("").setSensitive(false)
				.setValue(task.getSp_content()).build());

		return this
				.execute(
						"insert into task_sp ( project_id, db_name,class_name,sp_schema,sp_name,sql_style,crud_type,sp_content) values (?,?,?,?,?,?,?,?)",
						parameters, null);

	}

	public int updateTask(SpTask task) {

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
				.setValue(task.getSp_schema()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(5).setName("").setSensitive(false)
				.setValue(task.getSp_name()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(6).setName("").setSensitive(false)
				.setValue(task.getSql_style()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(7).setName("").setSensitive(false)
				.setValue(task.getCrud_type()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(8).setName("").setSensitive(false)
				.setValue(task.getSp_content()).build());
		
		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(9).setName("").setSensitive(false)
				.setValue(task.getId()).build());

		return this
				.execute(
						"update task_sp set project_id=?, db_name=?, class_name=?,sp_schema=?,sp_name=?,sql_style=?,crud_type=?,sp_content=? where id=?",
						parameters, null);

	}

	public int deleteTask(SpTask task) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false)
				.setValue(task.getId()).build());

		return this.execute("delete from task_sp where id=?", parameters, null);
	}
	
}
>>>>>>> 8af442caeb5f075c39df89b88a7d4b283ade2270:dao_gen/src/main/java/com/ctrip/platform/daogen/dao/SPTaskDAO.java
