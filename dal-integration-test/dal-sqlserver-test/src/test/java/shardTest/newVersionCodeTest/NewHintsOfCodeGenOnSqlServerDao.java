package shardTest.newVersionCodeTest;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
//import com.ctrip.platform.dal.daogen.enums.ParameterDirection;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Set;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class NewHintsOfCodeGenOnSqlServerDao {
	private static final boolean ASC = true;
	private DalTableDao<NewHintsOfCodeGenOnSqlServer> client;
	private static final String DATA_BASE = "SimpleShardByDBOnSqlServer";
	private static final DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
	private DalQueryDao queryDao = null;

	private DalRowMapper<NewHintsOfCodeGenOnSqlServer> pojoPojoRowMapper = null;

	
	public NewHintsOfCodeGenOnSqlServerDao() throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(NewHintsOfCodeGenOnSqlServer.class));
		this.pojoPojoRowMapper = new DalDefaultJpaMapper<>(NewHintsOfCodeGenOnSqlServer.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}

	/**
	 * Query PeopleGen by the specified ID
	 * The ID must be a number
	**/
	public NewHintsOfCodeGenOnSqlServer queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query PeopleGen by PeopleGen instance which the primary key is set
	**/
	public NewHintsOfCodeGenOnSqlServer queryByPk(NewHintsOfCodeGenOnSqlServer pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<NewHintsOfCodeGenOnSqlServer> queryLike(NewHintsOfCodeGenOnSqlServer sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryLike(sample, hints);
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
	public List<NewHintsOfCodeGenOnSqlServer> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll().atPage(pageNo, pageSize).orderBy("PeopleID", ASC);

		return client.query(builder, hints);
	}
	
	/**
	 * Get all records from table
	 */
	public List<NewHintsOfCodeGenOnSqlServer> queryAll(DalHints hints) throws SQLException {
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
	public int insert(DalHints hints, NewHintsOfCodeGenOnSqlServer daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, List<NewHintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, NewHintsOfCodeGenOnSqlServer daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<NewHintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int[] batchInsert(DalHints hints, List<NewHintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int delete(DalHints hints, NewHintsOfCodeGenOnSqlServer daoPojo) throws SQLException {
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
	public int[] delete(DalHints hints, List<NewHintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int[] batchDelete(DalHints hints, List<NewHintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int update(DalHints hints, NewHintsOfCodeGenOnSqlServer daoPojo) throws SQLException {
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
	public int[] update(DalHints hints, List<NewHintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
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
	public int[] batchUpdate(DalHints hints, List<NewHintsOfCodeGenOnSqlServer> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}

	/**
	 * 构建，查询
	**/
	public List<NewHintsOfCodeGenOnSqlServer> test_build_query_allShard_callback(Integer CityID, DalHints hints, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.callbackWith(callback);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.equal("CityID", CityID, Types.INTEGER, false);

		return client.query(builder, hints);
	}

	/**
	 * 构建，查询
	**/
	public List<NewHintsOfCodeGenOnSqlServer> test_build_query_shards_async(Integer CityID, DalHints hints, Set<String> shards) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.asyncExecution();

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.equal("CityID", CityID, Types.INTEGER, false);

		return client.query(builder, hints);
	}

	/**
	 * 构建，更新
	**/
	public int test_build_update_allShard_callback(String Name, Integer CityID, DalHints hints, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.callbackWith(callback);

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
		builder.update("Name", Name, Types.VARCHAR);
		builder.equal("CityID", CityID, Types.INTEGER, false);

		return client.update(builder, hints);
	}

	/**
	 * 构建，更新
	**/
	public int test_build_update_shards_async(String Name, Integer CityID, DalHints hints, Set<String> shards) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.asyncExecution();

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
		builder.update("Name", Name, Types.VARCHAR);
		builder.equal("CityID", CityID, Types.INTEGER, false);

		return client.update(builder, hints);
	}
	/**
	 * 构建，新增
	**/
	public int test_build_insert_allShard_callback(Integer CityID, String Name, Integer ProvinceID, Integer CountryID, DalHints hints, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
				hints.inAllShards();
		hints.callbackWith(callback);

		InsertSqlBuilder builder = new InsertSqlBuilder();
		builder.set("CityID", CityID, Types.INTEGER);
		builder.set("Name", Name, Types.VARCHAR);
		builder.set("ProvinceID", ProvinceID, Types.INTEGER);
		builder.set("CountryID", CountryID, Types.INTEGER);

		return client.insert(builder, hints);
	}
	/**
	 * 构建，新增
	**/
	public int test_build_insert_shards_async(Integer CityID, String Name, Integer ProvinceID, Integer CountryID, DalHints hints, Set<String> shards) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
				hints.inShards(shards);
		hints.asyncExecution();

		InsertSqlBuilder builder = new InsertSqlBuilder();
		builder.set("CityID", CityID, Types.INTEGER);
		builder.set("Name", Name, Types.VARCHAR);
		builder.set("ProvinceID", ProvinceID, Types.INTEGER);
		builder.set("CountryID", CountryID, Types.INTEGER);

		return client.insert(builder, hints);
	}

	/**
	 * 构建，删除
	**/
	public int test_build_delete_allShard_callback(Integer CityID, DalHints hints, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.callbackWith(callback);

		DeleteSqlBuilder builder = new DeleteSqlBuilder();
		builder.equal("CityID", CityID, Types.INTEGER, false);

		return client.delete(builder, hints);
	}

	/**
	 * 构建，删除
	**/
	public int test_build_delete_shards_async(Integer CityID, DalHints hints, Set<String> shards) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.asyncExecution();

		DeleteSqlBuilder builder = new DeleteSqlBuilder();
		builder.equal("CityID", CityID, Types.INTEGER, false);

		return client.delete(builder, hints);
	}
	
	/**
	 * 构建，查询
	**/
	public List<NewHintsOfCodeGenOnSqlServer> test_def_query_allShard_callback(Integer CityID, DalHints hints, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.callbackWith(callback);

		FreeSelectSqlBuilder<List<NewHintsOfCodeGenOnSqlServer>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People with(nolock) where CityID=?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.set(i++, "CityID", Types.INTEGER, CityID);
		builder.mapWith(pojoPojoRowMapper);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 构建，查询
	**/
	public List<NewHintsOfCodeGenOnSqlServer> test_def_query_shards_async(Integer CityID, DalHints hints, Set<String> shards) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.asyncExecution();

		FreeSelectSqlBuilder<List<NewHintsOfCodeGenOnSqlServer>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People with(nolock) where CityID=?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.set(i++, "CityID", Types.INTEGER, CityID);
		builder.mapWith(pojoPojoRowMapper);

		return queryDao.query(builder, parameters, hints);
	}
	
	/**
	 * 构建，查询
	**/
	public List<NewHintsOfCodeGenOnSqlServer> test_def_query_shards_varchar(String name, DalHints hints, Set<String> shards) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.asyncExecution();

		FreeSelectSqlBuilder<List<NewHintsOfCodeGenOnSqlServer>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People with(nolock) where name=?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.set(i++, "CityID", Types.VARCHAR, name);
		builder.mapWith(pojoPojoRowMapper);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 构建，删除
	**/
	public int test_def_update (DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("truncate table People");
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}
}