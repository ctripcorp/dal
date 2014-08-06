package com.ctrip.platform.dao.logstic;

import com.ctrip.platform.dal.dao.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

public class MySqlFreeDao {
	private static final String DATA_BASE = "dao_test";
	private DalQueryDao queryDao;

	private PeoplePojoRowMapper peoplePojoRowMapper = new PeoplePojoRowMapper();
	public MySqlFreeDao() {
		queryDao = new DalQueryDao(DATA_BASE);
	}
    
		/**
		 * MySql自定义SQL常规测试: List + Entity+Paging
		**/
	public List<PeoplePojo> fquery1(int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sqlPattern = "SELECT * FROM Person limit %s, %s";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		return queryDao.query(sql, parameters, hints, peoplePojoRowMapper);
	}

		/**
		 * MySql自定义SQL常规测试: List+Entity+No Paging
		**/
	public List<PeoplePojo> fquery2(DalHints hints) throws SQLException {
		String sql = "select * from Person";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		return queryDao.query(sql, parameters, hints, peoplePojoRowMapper);
	}

		/**
		 * MySql自定义SQL常规测试: Single+Simple+Paging
		**/
	public String fquery3(Integer id, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "select name, age from Person where id = ?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, id);
		return queryDao.queryForObjectNullable(sql, parameters, hints, String.class);
	}

		/**
		 * MySql自定义SQL常规测试: Single+Simple+no Paging
		**/
	public Integer fquery4(DalHints hints) throws SQLException {
		String sql = "select * from Person";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		return queryDao.queryForObjectNullable(sql, parameters, hints, Integer.class);
	}

		/**
		 * MySql自定义SQL常规测试: First+Simple+Paging
		**/
	public Integer fquery5(String name, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "select * from Person where name = ?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.VARCHAR, name);
		return queryDao.queryFirstNullable(sql, parameters, hints, Integer.class);
	}

		/**
		 * MySql自定义SQL常规测试: First+Simple+no Paging
		**/
	public Integer fquery6(Integer age, DalHints hints) throws SQLException {
		String sql = "select age from Person where age > ?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, age);
		return queryDao.queryFirstNullable(sql, parameters, hints, Integer.class);
	}

		/**
		 * MySql自定义SQL常规测试: List+Simple+Paging
		**/
	public List<String> fquery7(int pageNo, int pageSize, DalHints hints) throws SQLException {	
		String sqlPattern = "SELECT telephone, age FROM Person limit %s, %s";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		return queryDao.query(sql, parameters, hints, String.class);
	}

		/**
		 * MySql自定义SQL常规测试: List+Simple+no Paging
		**/
	public List<Integer> fquery8(Integer age, DalHints hints) throws SQLException {	
		String sql = "select * from Person where age < ?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, Types.INTEGER, age);
		return queryDao.query(sql, parameters, hints, Integer.class);
	}

		/**
		 * MySql自定义SQL常规测试: Singel+Entity+Paging
		**/
	public PeoplePojo fquery9(int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "select * from Person";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		return queryDao.queryForObjectNullable(sql, parameters, hints, peoplePojoRowMapper);
	}

		/**
		 * MySql自定义SQL常规测试: Singel+Entity+No Paging
		**/
	public PeoplePojo fquery10(DalHints hints) throws SQLException {
		String sql = "select * from Person";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		return queryDao.queryForObjectNullable(sql, parameters, hints, peoplePojoRowMapper);
	}

		/**
		 * MySql自定义SQL常规测试: First+Entity+Paging
		**/
	public PeoplePojo fquery11(int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "select * from Person";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		return queryDao.queryFirstNullable(sql, parameters, hints, peoplePojoRowMapper);
	}

		/**
		 * MySql自定义SQL常规测试: First+Entity+no Paging
		**/
	public PeoplePojo fquery12(DalHints hints) throws SQLException {
		String sql = "select * from Person";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		return queryDao.queryFirstNullable(sql, parameters, hints, peoplePojoRowMapper);
	}


	private class PeoplePojoRowMapper implements DalRowMapper<PeoplePojo> {

		@Override
		public PeoplePojo map(ResultSet rs, int rowNum) throws SQLException {
			PeoplePojo pojo = new PeoplePojo();
			
			pojo.setID((Integer)rs.getObject("ID"));
			pojo.setAddress((String)rs.getObject("Address"));
			pojo.setTelephone((String)rs.getObject("Telephone"));
			pojo.setName((String)rs.getObject("Name"));
			pojo.setAge((Integer)rs.getObject("Age"));
			pojo.setGender((Integer)rs.getObject("Gender"));
			pojo.setBirth((Timestamp)rs.getObject("Birth"));
			pojo.setPartmentID((Integer)rs.getObject("PartmentID"));
			pojo.setSpace((String)rs.getObject("space"));

			return pojo;
		}
	}
}
