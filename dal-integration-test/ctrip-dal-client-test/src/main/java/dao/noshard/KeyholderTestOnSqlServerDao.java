package dao.noshard;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import java.sql.SQLException;
import java.util.List;


import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import entity.SqlServerKeyholderTestTable;
import entity.SqlServerPeopleTable;

/**
 * @author llj李龙姣
 * @date 2018-08-13
 */
public class KeyholderTestOnSqlServerDao {
	private static final boolean ASC = true;
	private DalTableDao<SqlServerKeyholderTestTable> client;

	private static final String DATA_BASE = "noShardTestOnSqlServer";
	private static final DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
	private DalQueryDao queryDao = null;
	private DalRowMapper<SqlServerKeyholderTestTable> noShardOnSqlServerGenRowMapper = null;


	public KeyholderTestOnSqlServerDao() throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(SqlServerKeyholderTestTable.class));
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.noShardOnSqlServerGenRowMapper = new DalDefaultJpaMapper<>(SqlServerKeyholderTestTable.class);
	}

	/**
	 * Query SqlServerKeyholderTestTable by the specified ID
	 * The ID must be a number
	 */
	public SqlServerKeyholderTestTable queryByPk(Number id)
			throws SQLException {
		return queryByPk(id, null);
	}

	/**
	 * Query SqlServerKeyholderTestTable by the specified ID
	 * The ID must be a number
	 */
	public SqlServerKeyholderTestTable queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query SqlServerKeyholderTestTable by SqlServerKeyholderTestTable instance which the primary key is set
	 */
	public SqlServerKeyholderTestTable queryByPk(SqlServerKeyholderTestTable pk)
			throws SQLException {
		return queryByPk(pk, null);
	}

	/**
	 * Query SqlServerKeyholderTestTable by SqlServerKeyholderTestTable instance which the primary key is set
	 */
	public SqlServerKeyholderTestTable queryByPk(SqlServerKeyholderTestTable pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	 */
	public List<SqlServerKeyholderTestTable> queryBy(SqlServerKeyholderTestTable sample)
			throws SQLException {
		return queryBy(sample, null);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	 */
	public List<SqlServerKeyholderTestTable> queryBy(SqlServerKeyholderTestTable sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryBy(sample, hints);
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
	 * Query SqlServerKeyholderTestTable with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<SqlServerKeyholderTestTable> queryAllByPage(int pageNo, int pageSize)  throws SQLException {
		return queryAllByPage(pageNo, pageSize, null);
	}

	/**
	 * Query SqlServerKeyholderTestTable with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<SqlServerKeyholderTestTable> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll().atPage(pageNo, pageSize).orderBy("PeopleID", ASC);

		return client.query(builder, hints);
	}

	/**
	 * Get all records from table
	 */
	public List<SqlServerKeyholderTestTable> queryAll() throws SQLException {
		return queryAll(null);
	}

	/**
	 * Get all records from table
	 */
	public List<SqlServerKeyholderTestTable> queryAll(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("PeopleID", ASC);
		
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
	public int insert(SqlServerKeyholderTestTable daoPojo) throws SQLException {
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
	public int insert(DalHints hints, SqlServerKeyholderTestTable daoPojo) throws SQLException {
		if (null == daoPojo) {
			return 0;
		}
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
	public int[] insert(List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
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
	public int[] insert(DalHints hints, List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0) {
			return new int[0];
		}
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
	public int insertWithKeyHolder(KeyHolder keyHolder, SqlServerKeyholderTestTable daoPojo) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, SqlServerKeyholderTestTable daoPojo) throws SQLException {
		if (null == daoPojo) {
			return 0;
		}
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
	public int[] insertWithKeyHolder(KeyHolder keyHolder, List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0) {
			return new int[0];
		}
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
	public int[] batchInsert(List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
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
	public int[] batchInsert(DalHints hints, List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0) {
			return new int[0];
		}
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}

	/**
	 * Delete the given pojo.
	 *
	 * @param daoPojo pojo to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(SqlServerKeyholderTestTable daoPojo) throws SQLException {
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
	public int delete(DalHints hints, SqlServerKeyholderTestTable daoPojo) throws SQLException {
		if (null == daoPojo) {
			return 0;
		}
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
	public int[] delete(List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
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
	public int[] delete(DalHints hints, List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0) {
			return new int[0];
		}
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
	public int[] batchDelete(List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
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
	public int[] batchDelete(DalHints hints, List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0) {
			return new int[0];
		}
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
	public int update(SqlServerKeyholderTestTable daoPojo) throws SQLException {
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
	public int update(DalHints hints, SqlServerKeyholderTestTable daoPojo) throws SQLException {
		if (null == daoPojo) {
			return 0;
		}
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
	public int[] update(List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
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
	public int[] update(DalHints hints, List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0) {
			return new int[0];
		}
		hints = DalHints.createIfAbsent(hints);
		return client.update(hints, daoPojos);
	}

	/**
	 * Update the given pojo list in batch.
	 *
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] batchUpdate(List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
		return batchUpdate(null, daoPojos);
	}

	/**
	 * Update the given pojo list in batch. 
	 * 
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] batchUpdate(DalHints hints, List<SqlServerKeyholderTestTable> daoPojos) throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0) {
			return new int[0];
		}
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}

	/**
	 * ss
	 **/
	public int test_def_update (DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.setTemplate("truncate table keyholdertest");
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}
}