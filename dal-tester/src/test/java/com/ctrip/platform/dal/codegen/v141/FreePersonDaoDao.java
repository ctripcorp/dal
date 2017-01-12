package com.ctrip.platform.dal.codegen.v141;


import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import com.ctrip.platform.dal.dao.helper.*;

public class FreePersonDaoDao {

	private static final String DATA_BASE = "MySqlSimpleShard";
	
	private DalQueryDao queryDao = null;
	private DalClient baseClient = null;

	private DalRowMapper<FreeEntityPojo> freeEntityPojoRowMapper = null;









	public FreePersonDaoDao() throws SQLException {
		this.freeEntityPojoRowMapper = new DalDefaultJpaMapper(FreeEntityPojo.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * free field list
	**/
	public List<String> findFreeFieldList(String name, List<Integer> cityIds, DalHints hints) throws SQLException {	
		String sql = "SELECT name FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name";
		sql = SQLParser.parse(sql, cityIds);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		return queryDao.query(sql, parameters, hints, String.class);
	}
	/**
	 * free field list page
	**/
	public List<String> findFreeFieldListPage(String name, List<Integer> cityIds, int pageNo, int pageSize, DalHints hints) throws SQLException {	
		String sql = "SELECT name FROM Person WHERE name LIKE ? AND CityId IN (?) ORDER BY name limit ?, ?";
		sql = SQLParser.parse(sql, cityIds);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		parameters.set(i++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(i++, Types.INTEGER, pageSize);
		return queryDao.query(sql, parameters, hints, String.class);
	}
	/**
	 * free field single
	**/
	public String findFreeFieldSingle(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		String sql = "SELECT name FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name";
		sql = SQLParser.parse(sql, cityIds);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		return queryDao.queryForObjectNullable(sql, parameters, hints, String.class);
	}
	/**
	 * free field first
	**/
	public String findFreeFieldFirst(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		String sql = "SELECT name FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name";
		sql = SQLParser.parse(sql, cityIds);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		return queryDao.queryFirstNullable(sql, parameters, hints, String.class);
	}
	/**
	 * free select list
	**/
	public List<FreeEntityPojo> findFreeList(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name";
		sql = SQLParser.parse(sql, cityIds);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		return (List<FreeEntityPojo>)queryDao.query(sql, parameters, hints, freeEntityPojoRowMapper);
	}
	/**
	 * free select first
	**/
	public List<FreeEntityPojo> findFreeListPage(String name, List<Integer> cityIds, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM Person WHERE name LIKE ? AND CityId IN (?) ORDER BY name limit ?, ?";
		sql = SQLParser.parse(sql, cityIds);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		parameters.set(i++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(i++, Types.INTEGER, pageSize);
		return (List<FreeEntityPojo>)queryDao.query(sql, parameters, hints, freeEntityPojoRowMapper);
	}
	/**
	 * free select sinle
	**/
	public FreeEntityPojo findFreeSingle(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name";
		sql = SQLParser.parse(sql, cityIds);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		return (FreeEntityPojo)queryDao.queryForObjectNullable(sql, parameters, hints, freeEntityPojoRowMapper);
	}
	/**
	 * free select first
	**/
	public FreeEntityPojo findFreeFirst(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name";
		sql = SQLParser.parse(sql, cityIds);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		return (FreeEntityPojo)queryDao.queryFirstNullable(sql, parameters, hints, freeEntityPojoRowMapper);
	}
	/**
	 * free update
	**/
	public int update (String name, List<Integer> cityId, List<Integer> countryId, DalHints hints) throws SQLException {
		String sql = SQLParser.parse("UPDATE Person SET `Name`=? WHERE `CityId`IN (?) AND `CountryId` IN (?)",cityId,countryId);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityId", Types.INTEGER, cityId);
		i = parameters.setSensitiveInParameter(i, "countryId", Types.INTEGER, countryId);
		return baseClient.update(sql, parameters, hints);
	}

}
