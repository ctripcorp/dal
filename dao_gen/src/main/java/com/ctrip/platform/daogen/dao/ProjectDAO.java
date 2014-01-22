package com.ctrip.platform.daogen.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.StatementParameter;
import com.ctrip.platform.daogen.pojo.Project;
import com.ctrip.sysdev.das.common.enums.DbType;
import com.ctrip.sysdev.das.common.enums.ParameterDirection;

public class ProjectDAO extends AbstractDAO {

	public ProjectDAO() {
		logicDbName = "daogen";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}

	public ResultSet getAllProjects() {
		return this.fetch("select id, user_id, name, namespace from project",
				null, null);
	}

	public ResultSet getProjectByID(int iD) {

		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false).setValue(iD)
				.build());

		return this.fetch(
				"select id, user_id, name, namespace from project where id=?",
				parameters, null);
	}

	public int insertProject(Project project) {

		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

//		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
//				.setDirection(ParameterDirection.Input).setNullable(false)
//				.setIndex(1).setName("").setSensitive(false)
//				.setValue(project.getId()).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false)
				.setValue(project.getUser_id()).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(2).setName("").setSensitive(false)
				.setValue(project.getName()).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(3).setName("").setSensitive(false)
				.setValue(project.getNamespace()).build());

		return this
				.execute(
						"insert into project (user_id, name, namespace) values (?,?,?)",
						parameters, null);

	}

	public int updateProject(Project project) {

		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(4).setName("").setSensitive(false)
				.setValue(project.getId()).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false)
				.setValue(project.getUser_id()).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(2).setName("").setSensitive(false)
				.setValue(project.getName()).build());

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(3).setName("").setSensitive(false)
				.setValue(project.getNamespace()).build());

		return this.execute(
				"update project set user_id=?, name=?, namespace=? where id=?",
				parameters, null);

	}

	public int deleteProject(Project project) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false)
				.setValue(project.getId()).build());

		return this.execute("delete from project where id=?", parameters, null);
	}

}