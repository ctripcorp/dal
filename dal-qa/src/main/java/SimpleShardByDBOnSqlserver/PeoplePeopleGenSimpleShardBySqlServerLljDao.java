package SimpleShardByDBOnSqlserver;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
//import com.trip.platform.SimpleShardByDBOnMysql_test.PersonGenSimpleShardByDbOnMySqlLlj;

public class PeoplePeopleGenSimpleShardBySqlServerLljDao {
    private static final String DATA_BASE = "SimpleShardByDBOnSqlserver";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from People WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM People WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by PeopleID desc ) as rownum" 
			+" from People (nolock)) select * from CTE where rownum between ? and ?";
	private DalParser<PeoplePeopleGenSimpleShardBySqlServerLlj> parser = null;	
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<PeoplePeopleGenSimpleShardBySqlServerLlj> rowextractor = null;
	private DalTableDao<PeoplePeopleGenSimpleShardBySqlServerLlj> client;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;
	private DalRowMapper<PeoplePeopleGenSimpleShardBySqlServerLlj> peoplePeopleGenSimpleShardBySqlServerLljRowMapper = null;
	
	public PeoplePeopleGenSimpleShardBySqlServerLljDao() throws SQLException {
		this.peoplePeopleGenSimpleShardBySqlServerLljRowMapper = new DalDefaultJpaMapper(PeoplePeopleGenSimpleShardBySqlServerLlj.class);
		parser = new DalDefaultJpaParser(PeoplePeopleGenSimpleShardBySqlServerLlj.class);
		this.client = new DalTableDao<PeoplePeopleGenSimpleShardBySqlServerLlj>(parser);
		dbCategory = this.client.getDatabaseCategory();
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.rowextractor = new DalRowMapperExtractor<PeoplePeopleGenSimpleShardBySqlServerLlj>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query PeoplePeopleGenSimpleShardBySqlServerLlj by the specified ID
	 * The ID must be a number
	**/
	public PeoplePeopleGenSimpleShardBySqlServerLlj queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
    /**
	 * Query PeoplePeopleGenSimpleShardBySqlServerLlj by PeoplePeopleGenSimpleShardBySqlServerLlj instance which the primary key is set
	**/
	public PeoplePeopleGenSimpleShardBySqlServerLlj queryByPk(PeoplePeopleGenSimpleShardBySqlServerLlj pk, DalHints hints)
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
	 * Query PeoplePeopleGenSimpleShardBySqlServerLlj with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<PeoplePeopleGenSimpleShardBySqlServerLlj> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
        StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String sql = PAGE_SQL_PATTERN;
		int fromRownum = (pageNo - 1) * pageSize + 1;
        int endRownum = pageSize * pageNo;
		parameters.set(1, Types.INTEGER, fromRownum);
		parameters.set(2, Types.INTEGER, endRownum);
		return this.baseClient.query(sql, parameters, hints, rowextractor);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<PeoplePeopleGenSimpleShardBySqlServerLlj> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<PeoplePeopleGenSimpleShardBySqlServerLlj> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
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
	public int insert(DalHints hints, PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos) throws SQLException {
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
	public int[] batchInsert(DalHints hints, List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}
	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder holder for generated primary keys
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, daoPojos);
	}
	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder holder for generated primary keys
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, keyHolder, daoPojos);
	}
	/**
	 * Delete the given pojo.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojo pojo to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(DalHints hints, PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo) throws SQLException {
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
	public int[] delete(DalHints hints, List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos) throws SQLException {
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
	public int[] batchDelete(DalHints hints, List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos) throws SQLException {
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
	public int update(DalHints hints, PeoplePeopleGenSimpleShardBySqlServerLlj daoPojo) throws SQLException {
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
	public int[] update(DalHints hints, List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos) throws SQLException {
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
	public int[] batchUpdate(DalHints hints, List<PeoplePeopleGenSimpleShardBySqlServerLlj> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}
	/**
	 * 查询

	**/
	public List<PeoplePeopleGenSimpleShardBySqlServerLlj> test_build_query(Integer CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory, false);
		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.equal("CityID", CityID, Types.INTEGER, false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}
	/**
	 * 更新

	**/
	public int test_build_update (String Name, Integer CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		UpdateSqlBuilder builder = new UpdateSqlBuilder("People", dbCategory);
		builder.update("Name", Name, Types.VARCHAR);
		builder.equal("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	/**
	 * 新增

	**/
	public int test_build_insert (Integer CityID, String Name, Integer ProvinceID, Integer CountryID, DalHints hints) throws SQLException {
		String sql = SQLParser.parse("INSERT INTO People ([CityID],[Name],[ProvinceID],[CountryID]) VALUES ( ? , ? , ? , ? )");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		
		int i = 1;
		parameters.set(i++, "CityID", Types.INTEGER, CityID);
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		parameters.set(i++, "ProvinceID", Types.INTEGER, ProvinceID);
		parameters.set(i++, "CountryID", Types.INTEGER, CountryID);
		return client.update(sql, parameters, hints);
	}
	/**
	 * 删除

	**/
	public int test_build_delete (Integer CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		DeleteSqlBuilder builder = new DeleteSqlBuilder("People", dbCategory);
		builder.equal("CityID", CityID, Types.INTEGER, false);
	    String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}

	/**
	 * 查询
	**/
	public List<PeoplePeopleGenSimpleShardBySqlServerLlj> test_def_query(Integer CityID, DalHints hints) throws SQLException {
		String sql = "select * from People with(nolock) where CityID=?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, "CityID", Types.INTEGER, CityID);
		return (List<PeoplePeopleGenSimpleShardBySqlServerLlj>)queryDao.query(sql, parameters, hints, peoplePeopleGenSimpleShardBySqlServerLljRowMapper);
	}
	/**
	 * 增删改
	**/
	public int test_def_update (DalHints hints) throws SQLException {
		String sql = SQLParser.parse("truncate table People");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		
		int i = 1;
		return baseClient.update(sql, parameters, hints);
	}


}