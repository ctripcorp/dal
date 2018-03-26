package com.ctrip.platform.dal.codegen;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import com.ctrip.platform.dal.dao.helper.*;

public class FreePersonDaoDao {

	private static final String DATA_BASE = "MySqlSimpleShardForDB";
	private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
	private DalQueryDao queryDao = null;

	private DalRowMapper<FreeEntityPojo> freeEntityPojoRowMapper = null;

	public FreePersonDaoDao() throws SQLException {
		this.freeEntityPojoRowMapper = new DalDefaultJpaMapper<>(FreeEntityPojo.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}

	public List<String> findWithError(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("SELECT aaaname FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		builder.simpleType();

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * find field list
	 **/
	public List<String> findFreeFieldList(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("SELECT name FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		builder.simpleType();

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * find free field list page
	 **/
	public List<String> findFreeFieldListPage(String name, List<Integer> cityIds, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("SELECT name FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		builder.simpleType().atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * find free field single
	 **/
	public String findFreeFieldSingle(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("SELECT name FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		builder.simpleType().requireSingle().nullable();

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * find free field first
	 **/
	public String findFreeFieldFirst(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("SELECT name FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		builder.simpleType().requireFirst().nullable();

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * select free list
	 **/
	public List<FreeEntityPojo> findFreeList(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<FreeEntityPojo>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		builder.mapWith(freeEntityPojoRowMapper);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * e
	 **/
	public List<FreeEntityPojo> findFreeListPage(String name, List<Integer> cityIds, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<FreeEntityPojo>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		builder.mapWith(freeEntityPojoRowMapper).atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * select free sinle
	 **/
	public FreeEntityPojo findFreeSingle(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<FreeEntityPojo> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		builder.mapWith(freeEntityPojoRowMapper).requireSingle().nullable();

		return (FreeEntityPojo)queryDao.query(builder, parameters, hints);
	}

	/**
	 * select free first
	 **/
	public FreeEntityPojo findFreeFirst(String name, List<Integer> cityIds, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<FreeEntityPojo> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityIds", Types.INTEGER, cityIds);
		builder.mapWith(freeEntityPojoRowMapper).requireFirst().nullable();

		return (FreeEntityPojo)queryDao.query(builder, parameters, hints);
	}

	/**
	 * free update
	 **/
	public int update (String name, List<Integer> cityId, List<Integer> countryID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("UPDATE Person SET `Name`=? WHERE `CityId`IN (?) AND `CountryId` IN (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "name", Types.VARCHAR, name);
		i = parameters.setSensitiveInParameter(i, "cityId", Types.INTEGER, cityId);
		i = parameters.setSensitiveInParameter(i, "countryID", Types.INTEGER, countryID);

		return queryDao.update(builder, parameters, hints);
	}
}
