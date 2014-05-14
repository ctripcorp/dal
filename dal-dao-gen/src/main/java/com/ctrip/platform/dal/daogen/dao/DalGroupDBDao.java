package com.ctrip.platform.dal.daogen.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ctrip.platform.dal.daogen.entity.DalGroupDB;

public class DalGroupDBDao {

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<DalGroupDB> getGroupDBsByGroup(int groupId) {
		return this.jdbcTemplate.query(
				"select id, dbname, comment,dal_group_id from alldbs"
						+ " where dal_group_id=?", new Object[] { groupId },
				new RowMapper<DalGroupDB>() {
					public DalGroupDB mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return DalGroupDB.visitRow(rs);
					}
				});
	}

}
