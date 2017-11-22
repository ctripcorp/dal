package shardTest.oldVersionCodeTest;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class HintsOfCodeGenOnSqlServerDao {
    private static final String DATA_BASE = "SimpleShardByDBOnSqlServer";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from People WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM People WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "select * from People (nolock) order by PeopleID desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";	
	private DalParser<HintsOfCodeGenOnSqlServer> parser = null;	
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalTableDao<HintsOfCodeGenOnSqlServer> client;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;
	private DalRowMapper<HintsOfCodeGenOnSqlServer> peopleGenSimpleShardBySqlServerLljRowMapper = null;
	
	public HintsOfCodeGenOnSqlServerDao() throws SQLException {
		this.peopleGenSimpleShardBySqlServerLljRowMapper = new DalDefaultJpaMapper(HintsOfCodeGenOnSqlServer.class);
		parser = new DalDefaultJpaParser<>(HintsOfCodeGenOnSqlServer.class);
		this.client = new DalTableDao<HintsOfCodeGenOnSqlServer>(parser);
		dbCategory = this.client.getDatabaseCategory();
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public List<HintsOfCodeGenOnSqlServer> testBaseClient() throws SQLException{

		int list = 20;
//		list.add(20);
		String sql = "select * FROM people where cityid = ?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.INTEGER, list);
		//DalQueryDao dalQueryDao = new DalQueryDao(this.parser.getDatabaseName());
		List<HintsOfCodeGenOnSqlServer> ret =baseClient.query(sql, parameters, new DalHints().inShard(0), new DalRowMapperExtractor<>(this.parser));
		//List<PromotionPo> promotionPos = dalQueryDao.query(sql, parameters, new DalHints(), PromotionPo.class);
//		assertNotNull(promotionPos);
		return ret;
	}

	/**
	 * Query PeopleGen by the specified ID
	 * The ID must be a number
	**/
	public HintsOfCodeGenOnSqlServer queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
    /**
	 * Query PeopleGen by PeopleGen instance which the primary key is set
	**/
	public HintsOfCodeGenOnSqlServer queryByPk(HintsOfCodeGenOnSqlServer pk, DalHints hints)
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
	 * Query PeopleGen with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<HintsOfCodeGenOnSqlServer> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
        StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String sql = PAGE_SQL_PATTERN;
		int fromRownum = (pageNo - 1) * pageSize;
        int endRownum = pageSize;
		parameters.set(1, Types.INTEGER, fromRownum);
		parameters.set(2, Types.INTEGER, endRownum);
		return queryDao.query(sql, parameters, hints, parser);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<HintsOfCodeGenOnSqlServer> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<HintsOfCodeGenOnSqlServer> result = null;
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
	public int insert(DalHints hints, HintsOfCodeGenOnSqlServer daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, List<HintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, HintsOfCodeGenOnSqlServer daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<HintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int[] batchInsert(DalHints hints, List<HintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int delete(DalHints hints, HintsOfCodeGenOnSqlServer daoPojo) throws SQLException {
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
	public int[] delete(DalHints hints, List<HintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int[] batchDelete(DalHints hints, List<HintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int update(DalHints hints, HintsOfCodeGenOnSqlServer daoPojo) throws SQLException {
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
	public int[] update(DalHints hints, List<HintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int[] batchUpdate(DalHints hints, List<HintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}
	/**
	 * ��������ѯ
	**/
	public List<HintsOfCodeGenOnSqlServer> test_build_query_allShard_callback(Integer CityID, DalHints hints, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.callbackWith(callback);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory, false);
		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.equal("CityID", CityID, Types.INTEGER, false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}
	/**
	 * ��������ѯ
	**/
	public List<HintsOfCodeGenOnSqlServer> test_build_query_shards_async(Integer CityID, DalHints hints, Set<String> shards) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.asyncExecution();
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory, false);
		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.equal("CityID", CityID, Types.INTEGER, false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}
	/**
	 * ����������
	**/
	public int test_build_update_allShard_callback (String Name, Integer CityID, DalHints hints, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.callbackWith(callback);
		UpdateSqlBuilder builder = new UpdateSqlBuilder("People", dbCategory);
		builder.update("Name", Name, Types.VARCHAR);
		builder.equal("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	/**
	 * ����������
	**/
	public int test_build_update_shards_async (String Name, Integer CityID, DalHints hints, Set<String> shards) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.asyncExecution();
		UpdateSqlBuilder builder = new UpdateSqlBuilder("People", dbCategory);
		builder.update("Name", Name, Types.VARCHAR);
		builder.equal("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	/**
	 * ����������
	**/
	public int test_build_insert_allShard_callback (Integer CityID, String Name, Integer ProvinceID, Integer CountryID, DalHints hints, DalResultCallback callback) throws SQLException {
		String sql = SQLParser.parse("INSERT INTO People ([CityID],[Name],[ProvinceID],[CountryID]) VALUES ( ? , ? , ? , ? )");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.callbackWith(callback);
		int i = 1;
		parameters.set(i++, "CityID", Types.INTEGER, CityID);
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		parameters.set(i++, "ProvinceID", Types.INTEGER, ProvinceID);
		parameters.set(i++, "CountryID", Types.INTEGER, CountryID);
		return client.update(sql, parameters, hints);
	}
	/**
	 * ����������
	**/
	public int test_build_insert_shards_async (Integer CityID, String Name, Integer ProvinceID, Integer CountryID, DalHints hints, Set<String> shards) throws SQLException {
		String sql = SQLParser.parse("INSERT INTO People ([CityID],[Name],[ProvinceID],[CountryID]) VALUES ( ? , ? , ? , ? )");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.asyncExecution();
		int i = 1;
		parameters.set(i++, "CityID", Types.INTEGER, CityID);
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		parameters.set(i++, "ProvinceID", Types.INTEGER, ProvinceID);
		parameters.set(i++, "CountryID", Types.INTEGER, CountryID);
		return client.update(sql, parameters, hints);
	}
	/**
	 * ������ɾ��
	**/
	public int test_build_delete_allShard_callback (Integer CityID, DalHints hints, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.callbackWith(callback);
		DeleteSqlBuilder builder = new DeleteSqlBuilder("People", dbCategory);
		builder.equal("CityID", CityID, Types.INTEGER, false);
	    String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	/**
	 * ������ɾ��
	**/
	public int test_build_delete_shards_async (Integer CityID, DalHints hints, Set<String> shards) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.asyncExecution();
		DeleteSqlBuilder builder = new DeleteSqlBuilder("People", dbCategory);
		builder.equal("CityID", CityID, Types.INTEGER, false);
	    String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	
	/**
	 * 构建，查询
	**/
	public List<HintsOfCodeGenOnSqlServer> test_def_query_allShard_callback(Integer CityID, DalHints hints, DalResultCallback callback) throws SQLException {
		String sql = "select * from People with(nolock) where CityID=?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.callbackWith(callback);
		int i = 1;
		parameters.set(i++, "CityID", Types.INTEGER, CityID);
		return (List<HintsOfCodeGenOnSqlServer>)queryDao.query(sql, parameters, hints, peopleGenSimpleShardBySqlServerLljRowMapper);
	}
	/**
	 * 构建，查询
	**/
	public List<HintsOfCodeGenOnSqlServer> test_def_query_shards_async(Integer CityID, DalHints hints, Set<String> shards) throws SQLException {
		String sql = "select * from People with(nolock) where CityID=?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.asyncExecution();
		int i = 1;
		parameters.set(i++, "CityID", Types.INTEGER, CityID);
		return (List<HintsOfCodeGenOnSqlServer>)queryDao.query(sql, parameters, hints, peopleGenSimpleShardBySqlServerLljRowMapper);
	}
	/**
	 * 构建，删除
	**/
	public int test_def_update (DalHints hints) throws SQLException {
		String sql = SQLParser.parse("truncate table People");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		return baseClient.update(sql, parameters, hints);
	}

}