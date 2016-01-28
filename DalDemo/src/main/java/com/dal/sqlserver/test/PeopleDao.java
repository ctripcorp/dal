package com.dal.sqlserver.test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class PeopleDao {
    private static final String DATA_BASE = "SqlServerSimpleShard";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from People WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM People WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by PeopleID desc ) as rownum" 
			+" from People (nolock)) select * from CTE where rownum between ? and ?";
	private DalParser<People> parser = null;	
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalTableDao<People> client;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;
	
	public PeopleDao() throws SQLException {
		parser = new DalDefaultJpaParser<>(People.class);
		this.client = new DalTableDao<People>(parser);
		dbCategory = this.client.getDatabaseCategory();
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query People by the specified ID
	 * The ID must be a number
	**/
	public People queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
    /**
	 * Query People by People instance which the primary key is set
	**/
	public People queryByPk(People pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}
	/**
	 * Get the records count
	**/
	public int count(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		Number result = (Number)this.baseClient.query(COUNT_SQL_PATTERN, parameters, hints, extractor);
		return result.intValue();
	}
	/**
	 * Query People with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<People> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
        StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String sql = PAGE_SQL_PATTERN;
		int fromRownum = (pageNo - 1) * pageSize + 1;
        int endRownum = pageSize * pageNo;
		parameters.set(1, Types.INTEGER, fromRownum);
		parameters.set(2, Types.INTEGER, endRownum);
		return queryDao.query(sql, parameters, hints, parser);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<People> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<People> result = null;
		result = queryDao.query(ALL_SQL_PATTERN, parameters, hints, parser);
		return result;
	}
	/**
	 * Insert pojo and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, People daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojo);
	}
	/**
	 * Insert pojos one by one. If you want to inert them in the batch mode,
	 * user batchInsert instead. You can also use the combinedInsert.
	 * 
	 * @param hints 
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 *            DalHintEnum.continueOnError can be used
	 *            to indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 */
	public int[] insert(DalHints hints, List<People> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojos);
	}
	/**
	 * Insert pojo and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, KeyHolder keyHolder, People daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojo);
	}
	/**
	 * Insert pojos and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 *            DalHintEnum.continueOnError can be used
	 *            to indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<People> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojos);
	}
	/**
	 * Insert pojos in batch mode. 
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchInsert(DalHints hints, List<People> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}
	/**
	 * Delete the given pojo.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojo pojo to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(DalHints hints, People daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojo);
	}
	/**
	 * Delete the given pojos list one by one.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] delete(DalHints hints, List<People> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojos);
	}
	/**
	 * Delete the given pojo list in batch. 
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchDelete(DalHints hints, List<People> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchDelete(hints, daoPojos);
	}
	/**
	 * Update the given pojo . By default, if a field of pojo is null value,
	 * that field will be ignored, so that it will not be updated. You can
	 * overwrite this by set updateNullField in hints.
	 * 
	 * @param hints
	 * 			Additional parameters that instruct how DAL Client perform database operation.
	 *          DalHintEnum.updateNullField can be used
	 *          to indicate that the field of pojo is null value will be update.
	 * @param daoPojo pojo to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(DalHints hints, People daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.update(hints, daoPojo);
	}
	/**
	 * Update the given pojo list one by one. By default, if a field of pojo is null value,
	 * that field will be ignored, so that it will not be updated. You can
	 * overwrite this by set updateNullField in hints.
	 * 
	 * @param hints
	 * 			Additional parameters that instruct how DAL Client perform database operation.
	 *          DalHintEnum.updateNullField can be used
	 *          to indicate that the field of pojo is null value will be update.
	 * @param daoPojos list of pojos to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] update(DalHints hints, List<People> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.update(hints, daoPojos);
	}
	/**
	 * Update the given pojo list in batch. 
	 * 
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] batchUpdate(DalHints hints, List<People> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}
	/**
	 * demo project
	**/
	public List<People> findPeople(String name, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory, true);
		builder.select("DataChange_LastTime","CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.like("Name", name, Types.VARCHAR, false);
		builder.orderBy("Name", true);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		int index =  builder.getStatementParameterIndex();
		parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize + 1);
		parameters.set(index++, Types.INTEGER, pageSize * pageNo);
		return queryDao.query(sql, parameters, hints, parser);
	}

}
