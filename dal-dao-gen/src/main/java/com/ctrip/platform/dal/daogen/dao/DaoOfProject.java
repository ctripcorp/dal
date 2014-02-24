
package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.pojo.Project;

public class DaoOfProject {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Project> getAllProjects() {
		try {
			return this.jdbcTemplate.query(
					"select id, user_id, name, namespace from project",
					new RowMapper<Project>() {
						public Project mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return Project.visitRow(rs);
						}
					});
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public List<Project> getProjectsByUserID(String userID) {
		try {
			return this.jdbcTemplate
					.query("select id, user_id, name, namespace from project where user_id=?",
							new Object[] { userID }, new RowMapper<Project>() {
								public Project mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									return Project.visitRow(rs);
								}
							});
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Project getProjectByID(int iD) {
		try {
			return this.jdbcTemplate
					.queryForObject(
							"select id, user_id, name, namespace from project where id=?",
							new Object[] { iD }, new RowMapper<Project>() {
								public Project mapRow(ResultSet rs, int rowNum)
										throws SQLException {
									return Project.visitRow(rs);
								}
							});
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public int insertProject(Project project) {
		try {
			return this.jdbcTemplate
					.update("insert into project (user_id, name, namespace) values (?,?,?)",
							project.getUser_id(), project.getName(),
							project.getNamespace());
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public int updateProject(Project project) {
		try {
			return this.jdbcTemplate
					.update("update project set user_id=?, name=?, namespace=? where id=?",
							project.getUser_id(), project.getName(),
							project.getNamespace(), project.getId());
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public int deleteProject(Project project) {
		try {
			return this.jdbcTemplate.update("delete from project where id=?",
					project.getId());
		} catch (DataAccessException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
}