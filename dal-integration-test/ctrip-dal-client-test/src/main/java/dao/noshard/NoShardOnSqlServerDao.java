package dao.noshard;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import entity.SqlServerPeopleTable;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;


public class NoShardOnSqlServerDao {
	private static final boolean ASC = true;
	private DalTableDao<SqlServerPeopleTable> client;
	private static final String DATA_BASE = "noShardTestOnSqlServer";
	private static final DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
	private DalQueryDao queryDao = null;
	private DalRowMapper<SqlServerPeopleTable> noShardOnSqlServerGenRowMapper = null;

	public NoShardOnSqlServerDao() throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(SqlServerPeopleTable.class,DATA_BASE));
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.noShardOnSqlServerGenRowMapper = new DalDefaultJpaMapper<>(SqlServerPeopleTable.class);
	}

	/**
	 * Query PeopleGen by the specified ID
	 * The ID must be a number
	**/
	public SqlServerPeopleTable queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query PeopleGen by PeopleGen instance which the primary key is set
	**/
	public SqlServerPeopleTable queryByPk(SqlServerPeopleTable pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}


	public List<SqlServerPeopleTable> queryTop(Integer cityId, Integer count,DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		StatementParameters statementParameters=new StatementParameters();
		statementParameters.set(1,"cityId",Types.INTEGER,cityId);
		return client.queryTop("cityid>?",statementParameters,hints,count);
	}

	public List<SqlServerPeopleTable> queryFromWithOrderby(Integer cityId, Integer start,Integer count,DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		StatementParameters statementParameters=new StatementParameters();
		statementParameters.set(1,"cityId",Types.INTEGER,cityId);
		return client.queryFrom("cityid>? order by peopleid",statementParameters,hints,start,count);
	}

	public List<SqlServerPeopleTable> queryFromWithoutOrderby(Integer cityId, Integer start,Integer count,DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		StatementParameters statementParameters=new StatementParameters();
		statementParameters.set(1,"cityId",Types.INTEGER,cityId);
		return client.queryFrom("cityid>?",statementParameters,hints,start,count);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<SqlServerPeopleTable> queryLike(SqlServerPeopleTable sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryLike(sample, hints);
	}

	public List<SqlServerPeopleTable> queryBy(SqlServerPeopleTable sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryBy(sample, hints);
	}

	/**
	 * Get the all records count
	 */
	public int count(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder().selectCount();
		return client.count(builder, hints).intValue();
	}

	/**
	 * Query PeopleGen with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<SqlServerPeopleTable> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll().atPage(pageNo, pageSize).orderBy("PeopleID", ASC);

		return client.query(builder, hints);
	}
	
	/**
	 * Get all records from table
	 */
	public List<SqlServerPeopleTable> queryAll(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("PeopleID", ASC);
		
		return client.query(builder, hints);
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
	public int insert(DalHints hints, SqlServerPeopleTable daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, SqlServerPeopleTable daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<SqlServerPeopleTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojos);
	}

	/**
	 * Insert pojos in batch mode. 
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchInsert(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
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
	public int delete(DalHints hints, SqlServerPeopleTable daoPojo) throws SQLException {
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
	public int[] delete(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojos);
	}

	/**
	 * Delete the given pojo list in batch. 
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchDelete(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
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
	public int update(DalHints hints, SqlServerPeopleTable daoPojo) throws SQLException {
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
	public int[] update(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
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
	public int[] batchUpdate(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}
	
	/**
	 * ss
	**/
	public int test_def_update (DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("truncate table People");
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}



	/**
	 * 自定义，查询
	 **/
	public List<SqlServerPeopleTable> test_timeout(int delay,DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<SqlServerPeopleTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select top 1 * from People with (nolock) waitfor delay '00:00:" + delay + "'");
		StatementParameters parameters = new StatementParameters();

//		int i = 1;
//		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		builder.mapWith(noShardOnSqlServerGenRowMapper);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义，查询
	 **/
	public String select_servername_timeout(int delay,DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select @@SERVERNAME waitfor delay '00:00:" + delay + "'");
		StatementParameters parameters = new StatementParameters();

//		int i = 1;
//		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		builder.simpleType().requireSingle().nullable();

		return queryDao.query(builder, parameters, hints);
	}

	public List<SqlServerPeopleTable> testFreeSqlBuilderParameterIndex(String Name, List<Integer> CityID, Long ID, DalHints hints) throws Exception{
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<SqlServerPeopleTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People where Name=? and CityID in (?) and PeopleID=?");
		StatementParameters parameters = new StatementParameters();
		int i = 2;
		i = parameters.setInParameter(i, Types.BIGINT, CityID);
		parameters.set(1,Types.VARCHAR,Name);
		parameters.set(i++,Types.BIGINT,ID);
		builder.mapWith(noShardOnSqlServerGenRowMapper);
		return queryDao.query(builder, parameters, hints);
	}

	public List<SqlServerPeopleTable> testFreeSqlBuilderWithDiscontinuedParameterIndex(String Name, List<Integer> CityID, Long ID,  DalHints hints) throws Exception{
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<SqlServerPeopleTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People where Name=? and CityID in (?) and PeopleID=?");
		StatementParameters parameters = new StatementParameters();
		int i = 5;
		i = parameters.setInParameter(i, Types.INTEGER, CityID);
		parameters.set(2,Types.VARCHAR,Name);
		parameters.set(7,Types.BIGINT,ID);
		builder.mapWith(noShardOnSqlServerGenRowMapper);
		return queryDao.query(builder, parameters, hints);
	}


	public List<SqlServerPeopleTable> testFreeSqlBuilderParameterIndexNotIn(String Name,Integer CityID ,Long ID, DalHints hints) throws Exception{
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<SqlServerPeopleTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People where Name=? and CityID = ? and PeopleID=?");
		StatementParameters parameters = new StatementParameters();

		parameters.set(2,Types.BIGINT, CityID);
		parameters.set(1,Types.VARCHAR,Name);
		parameters.set(3,Types.INTEGER,ID);
		builder.mapWith(noShardOnSqlServerGenRowMapper);
		return queryDao.query(builder, parameters, hints);
	}

	public List<Map<String,Object>> testDalColumnMapRowMapper(DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		String sql="SELECT CityID , Name FROM people with (nolock)";
		return queryDao.query(sql, new StatementParameters(), hints, new DalColumnMapRowMapper());
	}

	public List<Map<String,Object>> testDalColumnMapRowMapperWithAlias(DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		String sql="SELECT CityID as c, Name as n FROM people";
		return queryDao.query(sql, new StatementParameters(), hints, new DalColumnMapRowMapper());
	}
}
