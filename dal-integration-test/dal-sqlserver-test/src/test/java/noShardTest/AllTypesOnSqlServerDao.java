package noShardTest;




import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class AllTypesOnSqlServerDao {
	private static final boolean ASC = true;
	private DalTableDao<AllTypesOnSqlServer> client;
	private static final String DATA_BASE = "noShardTestOnSqlServer";
	private static final DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
	private DalQueryDao queryDao = null;

	private DalRowMapper<AllTypesOnSqlServer> AllTypesRowMapper = null;

	public AllTypesOnSqlServerDao() throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(AllTypesOnSqlServer.class));
		this.AllTypesRowMapper = new DalDefaultJpaMapper<>(AllTypesOnSqlServer.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}

    /**
	 * 自定义，查询，pojoList
	**/
	public List<AllTypesOnSqlServer> testDefQueryPojoList(List<Integer> intcol) throws SQLException {
		return testDefQueryPojoList(intcol, null);
	}

	/**
	 * 自定义，查询，pojoList
	**/
	public List<AllTypesOnSqlServer> testDefQueryPojoList(List<Integer> intcol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<AllTypesOnSqlServer>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from all_types with (nolock) where intcol in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "intcol", Types.INTEGER, intcol);
		builder.mapWith(AllTypesRowMapper);

		return queryDao.query(builder, parameters, hints);
	}

    /**
	 * 自定义，查询，pojoListByPage
	**/
	public List<AllTypesOnSqlServer> testDefQueryPojoListByPage(Integer id, int pageNo, int pageSize) throws SQLException {
		return testDefQueryPojoListByPage(id, pageNo, pageSize, null);
	}

	/**
	 * 自定义，查询，pojoListByPage
	**/
	public List<AllTypesOnSqlServer> testDefQueryPojoListByPage(Integer id, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<AllTypesOnSqlServer>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from all_types with (nolock) where id>? order by id");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.mapWith(AllTypesRowMapper).atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义，查询，filedList
	**/
	public List<Integer> testDefQueryFieldList(Integer id) throws SQLException {
		return testDefQueryFieldList(id, null);
	}

	/**
	 * 自定义，查询，filedList
	**/
	public List<Integer> testDefQueryFieldList(Integer id, DalHints hints) throws SQLException {	
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<Integer>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select intCol from all_types with (nolock) where id>?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.simpleType();

		return queryDao.query(builder, parameters, hints);
	}

    /**
	 * 自定义，查询，filedListByPage
	**/
	public List<Integer> testDefQueryFieldListByPage(Integer id, int pageNo, int pageSize) throws SQLException {
		return testDefQueryFieldListByPage(id, pageNo, pageSize, null);
	}

	/**
	 * 自定义，查询，filedListByPage
	**/
	public List<Integer> testDefQueryFieldListByPage(Integer id, int pageNo, int pageSize, DalHints hints) throws SQLException {	
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<Integer>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select intCol from all_types with (nolock) where id>? order by id");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.simpleType().atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints);
	}

    /**
	 * 自定义，查询，fieldSingle
	**/
	public Integer testDefQueryFieldSingle(Integer id) throws SQLException {
		return testDefQueryFieldSingle(id, null);
	}

	/**
	 * 自定义，查询，fieldSingle
	**/
	public Integer testDefQueryFieldSingle(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<Integer> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select intcol from all_types with (nolock) where id=?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.simpleType().requireSingle().nullable();

		return queryDao.query(builder, parameters, hints);
	}

    /**
	 * 自定义，查询，fieldFirst
	**/
	public Integer testDefQueryFieldFirst(Integer id) throws SQLException {
		return testDefQueryFieldFirst(id, null);
	}

	/**
	 * 自定义，查询，fieldFirst
	**/
	public Integer testDefQueryFieldFirst(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<Integer> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select intcol from all_types with (nolock) where id>? order by id");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.simpleType().requireFirst().nullable();

		return queryDao.query(builder, parameters, hints);
	}

    /**
	 * 自定义，查询，pojoSingle
	**/
	public AllTypesOnSqlServer testDefQueryPojoSingle(Integer id) throws SQLException {
		return testDefQueryPojoSingle(id, null);
	}

	/**
	 * 自定义，查询，pojoSingle
	**/
	public AllTypesOnSqlServer testDefQueryPojoSingle(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<AllTypesOnSqlServer> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from all_types with (nolock) where id=?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.mapWith(AllTypesRowMapper).requireSingle().nullable();

		return (AllTypesOnSqlServer)queryDao.query(builder, parameters, hints);
	}

   

	/**
	 * 自定义，查询，pojoFirst
	**/
	public AllTypesOnSqlServer testDefQueryPojoFirst(Integer id) throws SQLException {
		return testDefQueryPojoFirst(id, null);
	}

	/**
	 * 自定义，查询，pojoFirst
	**/
	public AllTypesOnSqlServer testDefQueryPojoFirst(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<AllTypesOnSqlServer> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from all_types with (nolock) where id>? order by id");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.mapWith(AllTypesRowMapper).requireFirst().nullable();
		
		return (AllTypesOnSqlServer)queryDao.query(builder, parameters, hints);
	}

	

	/**
	 * 自定义，update
	**/
	public int testDefUpdate (String varcharcol, String charcol) throws SQLException {
		return testDefUpdate(varcharcol, charcol, null);
	}

	/**
	 * 自定义，update
	**/
	public int testDefUpdate (String varcharcol, String charcol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("update all_types set varcharcol=? where charcol like ?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "varcharcol", Types.VARCHAR, varcharcol);
		parameters.setSensitive(i++, "charcol", Types.CHAR, charcol);

		return queryDao.update(builder, parameters, hints);
	}

	/**
	 * 自定义，删除
	**/
	public int testDefDelete (Integer id) throws SQLException {
		return testDefDelete(id, null);
	}

	/**
	 * 自定义，删除
	**/
	public int testDefDelete (Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("delete from all_types where id=?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);

		return queryDao.update(builder, parameters, hints);
	}
	
	/**
	 * truncate
	**/
	public int testDefTruncate () throws SQLException {
		return testDefTruncate(null);
	}

	/**
	 * truncate
	**/
	public int testDefTruncate (DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("truncate table all_types");
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}

	/**
	 * 自定义，insert
	**/
	public int testDefInsert (String varcharcol, Integer intcol) throws SQLException {
		return testDefInsert(varcharcol, intcol, null);
	}

	/**
	 * 自定义，insert
	**/
	public int testDefInsert (String varcharcol, Integer intcol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("insert into all_types (varcharcol,intcol) values (?,?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "varcharcol", Types.VARCHAR, varcharcol);
		parameters.setSensitive(i++, "intcol", Types.INTEGER, intcol);

		return queryDao.update(builder, parameters, hints);
	}
	
	

	/**
	 * Query AllTypes by the specified ID
	 * The ID must be a number
	**/
	public AllTypesOnSqlServer queryByPk(Number id)
			throws SQLException {
		return queryByPk(id, null);
	}

	/**
	 * Query AllTypes by the specified ID
	 * The ID must be a number
	**/
	public AllTypesOnSqlServer queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query AllTypes by AllTypes instance which the primary key is set
	**/
	public AllTypesOnSqlServer queryByPk(AllTypesOnSqlServer pk)
			throws SQLException {
		return queryByPk(pk, null);
	}

	/**
	 * Query AllTypes by AllTypes instance which the primary key is set
	**/
	public AllTypesOnSqlServer queryByPk(AllTypesOnSqlServer pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<AllTypesOnSqlServer> queryLike(AllTypesOnSqlServer sample)
			throws SQLException {
		return queryLike(sample, null);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<AllTypesOnSqlServer> queryLike(AllTypesOnSqlServer sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryLike(sample, hints);
	}

	/**
	 * Get the all records count
	 */
	public int count() throws SQLException {
		return count(null);
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
	 * Query AllTypes with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<AllTypesOnSqlServer> queryAllByPage(int pageNo, int pageSize)  throws SQLException {
		return queryAllByPage(pageNo, pageSize, null);
	}

	/**
	 * Query AllTypes with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<AllTypesOnSqlServer> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll().atPage(pageNo, pageSize).orderBy("id", ASC);

		return client.query(builder, hints);
	}

	/**
	 * Get all records from table
	 */
	public List<AllTypesOnSqlServer> queryAll() throws SQLException {
		return queryAll(null);
	}

	/**
	 * Get all records from table
	 */
	public List<AllTypesOnSqlServer> queryAll(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("id", ASC);
		
		return client.query(builder, hints);
	}

	/**
	 * Insert single pojo
	 *
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(AllTypesOnSqlServer daoPojo) throws SQLException {
		return insert(null, daoPojo);
	}

	/**
	 * Insert single pojo
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, AllTypesOnSqlServer daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojo);
	}

	/**
	 * Insert pojos one by one. If you want to inert them in the batch mode,
	 * user batchInsert instead. You can also use the combinedInsert.
	 *
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 */
	public int[] insert(List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		return insert(null, daoPojos);
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
	public int[] insert(DalHints hints, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojos);
	}

	/**
	 * Insert pojo and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set, the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
	 *
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insertWithKeyHolder(KeyHolder keyHolder, AllTypesOnSqlServer daoPojo) throws SQLException {
		return insert(null, keyHolder, daoPojo);
	}

	/**
	 * Insert pojo and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set, the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
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
	public int insert(DalHints hints, KeyHolder keyHolder, AllTypesOnSqlServer daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojo);
	}

	/**
	 * Insert pojos and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set, the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
	 *
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] insertWithKeyHolder(KeyHolder keyHolder, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		return insert(null, keyHolder, daoPojos);
	}

	/**
	 * Insert pojos and get the generated PK back in keyHolder. 
	 * If the "set no count on" for MS SqlServer is set, the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojos);
	}

	/**
	 * Insert pojos in batch mode.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 *
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchInsert(List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		return batchInsert(null, daoPojos);
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
	public int[] batchInsert(DalHints hints, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 *
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		return combinedInsert(null, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set, the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 *
	 * @param keyHolder holder for generated primary keys
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsertWithKeyHolder(KeyHolder keyHolder, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		return combinedInsert(null, keyHolder, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set, the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder holder for generated primary keys
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, keyHolder, daoPojos);
	}

	/**
	 * Delete the given pojo.
	 *
	 * @param daoPojo pojo to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(AllTypesOnSqlServer daoPojo) throws SQLException {
		return delete(null, daoPojo);
	}

	/**
	 * Delete the given pojo.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojo pojo to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(DalHints hints, AllTypesOnSqlServer daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojo);
	}

	/**
	 * Delete the given pojos list one by one.
	 *
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] delete(List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		return delete(null, daoPojos);
	}

	/**
	 * Delete the given pojos list one by one.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] delete(DalHints hints, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojos);
	}

	/**
	 * Delete the given pojo list in batch.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
	 *
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchDelete(List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		return batchDelete(null, daoPojos);
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
	public int[] batchDelete(DalHints hints, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
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
	 * @param daoPojo pojo to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(AllTypesOnSqlServer daoPojo) throws SQLException {
		return update(null, daoPojo);
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
	public int update(DalHints hints, AllTypesOnSqlServer daoPojo) throws SQLException {
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
	 * @param daoPojos list of pojos to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] update(List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		return update(null, daoPojos);
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
	public int[] update(DalHints hints, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
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
	public int[] batchUpdate(List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		return batchUpdate(null, daoPojos);
	}

	/**
	 * Update the given pojo list in batch. 
	 * 
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] batchUpdate(DalHints hints, List<AllTypesOnSqlServer> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}

	/**
	 * ��������ѯ��nullable
	**/
	public List<AllTypesOnSqlServer> test_build_query_nullable(String CharCol, String NcharCol, Boolean BitCol, Integer IntCol, Short SmallIntCol, Short TinyIntCol, BigDecimal NumericCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarcharCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
//		SelectSqlBuilder builder = new SelectSqlBuilder("All_Types", dbCategory, false);
		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("TextCol","DatetimeCol","DecimalCol","TimeCol","XmlCol","VarcharmaxCol","FloatCol","VarcharCol","BinaryCol","BitCol","SmallDatetimeCol","HierarchidCol","CharCol","ID","DatetimeOffsetCol","TinyIntCol","SmallIntCol","NvarcharMaxCol","NumericCol","NvarcharCol","BigIntCol","GeographyCol","MoneyCol","Datetime2Col","GeometryCol","UniqueidentifierCol","TimestampCol","IntCol","ImageCol","NtextCol","NcharCol","RealCol","SmallMoneyCol","DateCol","VarBinaryMaxCol","VarBinaryCol");
		builder.likeNullable("CharCol", CharCol, Types.CHAR, false);
		builder.and();
		builder.leftBracket();
		builder.equalNullable("NcharCol", NcharCol, Types.NCHAR, false);
		builder.or();
		builder.not();
		builder.notEqualNullable("BitCol", BitCol, Types.BIT, false);
		builder.rightBracket();
		builder.and();
		builder.greaterThanNullable("IntCol", IntCol, Types.INTEGER, false);
		builder.and();
		builder.lessThanNullable("SmallIntCol", SmallIntCol, Types.SMALLINT, false);
		builder.and();
		builder.greaterThanEqualsNullable("TinyIntCol", TinyIntCol, Types.TINYINT, false);
		builder.and();
		builder.lessThanEqualsNullable("NumericCol", NumericCol, Types.NUMERIC, false);
		builder.and();
		builder.betweenNullable("BigIntCol", BigIntCol_start, BigIntCol_end, Types.BIGINT, false);
		builder.and();
		builder.inNullable("VarcharCol", VarcharCol, Types.VARCHAR, false);
		builder.and();
		builder.isNull("FloatCol");
		builder.and();
		builder.isNotNull("RealCol");
		builder.orderBy("ID", false);
//	    String sql = builder.build();
//		StatementParameters parameters = builder.buildParameters();
//		return queryDao.query(sql, parameters, hints, parser);
		return client.query(builder, hints);
	}
	
	public List<AllTypesOnSqlServer> test_build_query(Timestamp datatimecol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder =  new SelectSqlBuilder();
		builder.select("TextCol","DatetimeCol","DecimalCol","TimeCol","XmlCol","VarcharmaxCol","FloatCol","VarcharCol","BinaryCol","BitCol","SmallDatetimeCol","HierarchidCol","CharCol","ID","DatetimeOffsetCol","TinyIntCol","SmallIntCol","NvarcharMaxCol","NumericCol","NvarcharCol","BigIntCol","GeographyCol","MoneyCol","Datetime2Col","GeometryCol","UniqueidentifierCol","TimestampCol","IntCol","ImageCol","NtextCol","NcharCol","RealCol","SmallMoneyCol","DateCol","VarBinaryMaxCol","VarBinaryCol");
//		builder.likeNullable("CharCol", CharCol, Types.CHAR, false);
//		builder.and();
//		builder.leftBracket();
//		builder.equalNullable("NcharCol", NcharCol, Types.NCHAR, false);
//		builder.or();
//		builder.not();
//		builder.notEqualNullable("BitCol", BitCol, Types.BIT, false);
//		builder.rightBracket();
//		builder.and();
//		builder.greaterThanNullable("IntCol", IntCol, Types.INTEGER, false);
//		builder.and();
//		builder.lessThanNullable("SmallIntCol", SmallIntCol, Types.SMALLINT, false);
//		builder.and();
//		builder.greaterThanEqualsNullable("TinyIntCol", TinyIntCol, Types.TINYINT, false);
//		builder.and();
//		builder.lessThanEqualsNullable("NumericCol", NumericCol, Types.NUMERIC, false);
//		builder.and();
//		builder.betweenNullable("BigIntCol", BigIntCol_start, BigIntCol_end, Types.BIGINT, false);
//		builder.and();
//		builder.inNullable("VarcharCol", VarcharCol, Types.VARCHAR, false);
//		builder.and();
//		builder.isNull("FloatCol");
//		builder.and();
//		builder.isNotNull("RealCol");
//		builder.orderBy("ID", false);
		builder.lessThanEqualsNullable("DatetimeCol", datatimecol, Types.TIMESTAMP, false);
//	    String sql = builder.build();
//		StatementParameters parameters = builder.buildParameters();
		return client.query(builder, hints);
	}
	/**
	 * ��������ѯ��notnull
	**/
	public List<AllTypesOnSqlServer> test_build_query_notnull(String CharCol, String NcharCol, Boolean BitCol, Integer IntCol, Short SmallIntCol, Short TinyIntCol, BigDecimal NumericCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarcharCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
//		SelectSqlBuilder builder = new SelectSqlBuilder("All_Types", dbCategory, false);
		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("TextCol","DatetimeCol","DecimalCol","TimeCol","XmlCol","VarcharmaxCol","FloatCol","VarcharCol","BinaryCol","BitCol","SmallDatetimeCol","HierarchidCol","CharCol","ID","DatetimeOffsetCol","TinyIntCol","SmallIntCol","NvarcharMaxCol","NumericCol","NvarcharCol","BigIntCol","GeographyCol","MoneyCol","Datetime2Col","GeometryCol","UniqueidentifierCol","TimestampCol","IntCol","ImageCol","NtextCol","NcharCol","RealCol","SmallMoneyCol","DateCol","VarBinaryMaxCol","VarBinaryCol");
		builder.like("CharCol", CharCol, Types.CHAR, false);
		builder.and();
		builder.leftBracket();
		builder.equal("NcharCol", NcharCol, Types.NCHAR, false);
		builder.or();
		builder.not();
		builder.notEqual("BitCol", BitCol, Types.BIT, false);
		builder.rightBracket();
		builder.and();
		builder.greaterThanEquals("IntCol", IntCol, Types.INTEGER, false);
		builder.and();
		builder.lessThanEquals("SmallIntCol", SmallIntCol, Types.SMALLINT, false);
		builder.and();
		builder.greaterThanEquals("TinyIntCol", TinyIntCol, Types.TINYINT, false);
		builder.and();
		builder.lessThanEquals("NumericCol", NumericCol, Types.NUMERIC, false);
		builder.and();
		builder.between("BigIntCol", BigIntCol_start, BigIntCol_end, Types.BIGINT, false);
		builder.and();
		builder.in("VarcharCol", VarcharCol, Types.VARCHAR, false);
		builder.and();
		builder.isNull("FloatCol");
		builder.and();
		builder.isNotNull("RealCol");
		builder.orderBy("ID", false);
//	    String sql = builder.build();
//		StatementParameters parameters = builder.buildParameters();
//		return queryDao.query(sql, parameters, hints, parser);
		return client.query(builder, hints);
	}
	
    /**
	 * 构建，查询，filedList
	**/
	public List<Integer> testBuildQueryFieldList(Integer id) throws SQLException {
		return testBuildQueryFieldList(id, null);
	}

	/**
	 * 构建，查询，filedList
	**/
	public List<Integer> testBuildQueryFieldList(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("IntCol");
		builder.greaterThan("id", id, Types.INTEGER, false);

		return client.query(builder, hints, Integer.class);
	}

    /**
	 * 构建，查询，fieldListByPage
	**/
	public List<Integer> testBuildQueryFieldListByPage(Integer id, int pageNo, int pageSize) throws SQLException {
		return testBuildQueryFieldListByPage(id, pageNo, pageSize, null);
	}

	/**
	 * 构建，查询，fieldListByPage
	**/
	public List<Integer> testBuildQueryFieldListByPage(Integer id, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("IntCol");
		builder.greaterThan("id", id, Types.INTEGER, false);
		builder.orderBy("id", false);
		builder.atPage(pageNo, pageSize);

		return client.query(builder, hints, Integer.class);
	}

    /**
	 * 构建，查询，fieldsingle
	**/
	public Integer testBuildQueryFieldSingle(Integer id) throws SQLException {
		return testBuildQueryFieldSingle(id, null);
	}

	/**
	 * 构建，查询，fieldsingle
	**/
	public Integer testBuildQueryFieldSingle(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("IntCol");
		builder.equal("id", id, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints, Integer.class);
	}

    /**
	 * 构建，查询，fieldFirst
	**/
	public Integer testBuildQueryFieldFirst(Integer id) throws SQLException {
		return testBuildQueryFieldFirst(id, null);
	}

	/**
	 * 构建，查询，fieldFirst
	**/
	public Integer testBuildQueryFieldFirst(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("IntCol");
		builder.greaterThan("id", id, Types.INTEGER, false);
		builder.orderBy("id", false);
		builder.requireFirst();

		return client.queryObject(builder, hints, Integer.class);
	}

    /**
	 * 构建，查询，pojoList
	**/
	public List<AllTypesOnSqlServer> testBuildQueryPojoList(Integer id) throws SQLException {
		return testBuildQueryPojoList(id, null);
	}

	/**
	 * 构建，查询，pojoList
	**/
	public List<AllTypesOnSqlServer> testBuildQueryPojoList(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll();
		builder.greaterThanNullable("id", id, Types.INTEGER, true);

		return client.query(builder, hints);
	}

    /**
	 * 构建，查询，pojoListByPage
	**/
	public List<AllTypesOnSqlServer> testBuildQueryPojoListByPage(Integer id, int pageNo, int pageSize) throws SQLException {
		return testBuildQueryPojoListByPage(id, pageNo, pageSize, null);
	}

	/**
	 * 构建，查询，pojoListByPage
	**/
	public List<AllTypesOnSqlServer> testBuildQueryPojoListByPage(Integer id, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll();
		builder.greaterThan("id", id, Types.INTEGER, false);
		builder.orderBy("id", false);
		builder.atPage(pageNo, pageSize);

		return client.query(builder, hints);
	}

    /**
	 * 构建，查询，pojoSingle
	**/
	public AllTypesOnSqlServer testBuildQueryPojoSingle(Integer id) throws SQLException {
		return testBuildQueryPojoSingle(id, null);
	}

	/**
	 * 构建，查询，pojoSingle
	**/
	public AllTypesOnSqlServer testBuildQueryPojoSingle(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll();
		builder.equal("id", id, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints);
	}

    /**
	 * 构建，查询，pojoFirst
	**/
	public AllTypesOnSqlServer testBuildQueryPojoFirst(Integer id) throws SQLException {
		return testBuildQueryPojoFirst(id, null);
	}

	/**
	 * 构建，查询，pojoFirst
	**/
	public AllTypesOnSqlServer testBuildQueryPojoFirst(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll();
		builder.greaterThan("id", id, Types.INTEGER, false);
		builder.orderBy("id", false);
	    builder.requireFirst();

		return client.queryObject(builder, hints);
	}

    /**
	 * 构建，update
	**/
	public int testBuildUpdate(String VarCharCol, Integer intcol) throws SQLException {
		return testBuildUpdate(VarCharCol, intcol, null);
	}

	/**
	 * 构建，update
	**/
	public int testBuildUpdate(String VarCharCol, Integer intcol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
		builder.update("VarCharCol", VarCharCol, Types.VARCHAR);
		builder.equal("id", intcol, Types.INTEGER, false);

		return client.update(builder, hints);
	}

    /**
	 * 构建，insert
	**/
	public int testBuildInsert(String VarCharCol, Integer TinyIntCol) throws SQLException {
		return testBuildInsert(VarCharCol, TinyIntCol, null);
	}

	/**
	 * 构建，insert
	**/
	public int testBuildInsert(String VarCharCol, Integer TinyIntCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		InsertSqlBuilder builder = new InsertSqlBuilder();
		builder.set("VarCharCol", VarCharCol, Types.VARCHAR);
		builder.setSensitive("TinyIntCol", TinyIntCol, Types.TINYINT);

		return client.insert(builder, hints);
	}

    /**
	 * 构建，delete
	**/
	public int testBuildDelete(Integer id) throws SQLException {
		return testBuildDelete(id, null);
	}

	/**
	 * 构建，delete
	**/
	public int testBuildDelete(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		DeleteSqlBuilder builder = new DeleteSqlBuilder();
		builder.equalNullable("id", id, Types.INTEGER, true);

		return client.delete(builder, hints);
	}
}
