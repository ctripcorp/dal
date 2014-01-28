package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.pojo.Project;

public class ProjectDAO {
	
	private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	public List<Project> getAllProjects() {
		
		return this.jdbcTemplate.query(
		        "select id, user_id, name, namespace from project",
		        new RowMapper<Project>() {
		            public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	Project project = new Project();
		               project.setId(rs.getInt(1));
		               project.setUser_id(rs.getString(2));
		               project.setName(rs.getString(3));
		               project.setNamespace(rs.getString(4));
		                return project;
		            }
		        });
	}
	
	public List<Project> getProjectsByUserID(String userID){
		return this.jdbcTemplate.query(
		        "select id, user_id, name, namespace from project where user_id=?",
		        new Object[]{userID},
		        new RowMapper<Project>() {
		            public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	Project project = new Project();
		               project.setId(rs.getInt(1));
		               project.setUser_id(rs.getString(2));
		               project.setName(rs.getString(3));
		               project.setNamespace(rs.getString(4));
		                return project;
		            }
		        });
	}

	public Project getProjectByID(int iD) {
		
		return this.jdbcTemplate.queryForObject(
		        "select id, user_id, name, namespace from project where id=?",
		        new Object[]{iD},
		        new RowMapper<Project>() {
		            public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
		            	Project project = new Project();
		               project.setId(rs.getInt(1));
		               project.setUser_id(rs.getString(2));
		               project.setName(rs.getString(3));
		               project.setNamespace(rs.getString(4));
		                return project;
		            }
		        });
	}

	public int insertProject(Project project) {
		
		return this.jdbcTemplate.update("insert into project (user_id, name, namespace) values (?,?,?)", 
				project.getUser_id(), project.getName(), project.getNamespace());

	}

	public int updateProject(Project project) {
		
		return this.jdbcTemplate.update("update project set user_id=?, name=?, namespace=? where id=?",
				project.getUser_id(), project.getName(), project.getNamespace(), project.getId());

	}

	public int deleteProject(Project project) {
		
		return this.jdbcTemplate.update("delete from project where id=?", project.getId());
	}


}