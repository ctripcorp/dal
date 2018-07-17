package dao.noshard;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import entity.SqlServerPeopleTable;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;


public class MasterOnlyOnSqlServerDao {
	private static final boolean ASC = true;
	private DalTableDao<SqlServerPeopleTable> client;
	private static final String DATA_BASE = "testMaterOnlyOnSqlServer";
	private static final DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
	private DalQueryDao queryDao = null;

	private DalRowMapper<SqlServerPeopleTable> peopleSqlServerPeopleTableRowMapper = null;
	
	public MasterOnlyOnSqlServerDao() throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(SqlServerPeopleTable.class,DATA_BASE));
		this.peopleSqlServerPeopleTableRowMapper = new DalDefaultJpaMapper<>(SqlServerPeopleTable.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}

	public MasterOnlyOnSqlServerDao(String DATA_BASE) throws SQLException {
		this.client = new DalTableDao<>(SqlServerPeopleTable.class, DATA_BASE);
		this.peopleSqlServerPeopleTableRowMapper = new DalDefaultJpaMapper<>(SqlServerPeopleTable.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}

	/**
	 * Query SqlServerPeopleTable by the specified ID
	 * The ID must be a number
	**/
	public SqlServerPeopleTable queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query SqlServerPeopleTable by SqlServerPeopleTable instance which the primary key is set
	**/
	public SqlServerPeopleTable queryByPk(SqlServerPeopleTable pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
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

	/**
	 * Get the all records count
	 */
	public int count(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder().selectCount();
		return client.count(builder, hints).intValue();
	}

	/**
	 * Query SqlServerPeopleTable with paging function
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
	 * 构建，查询，first
	 **/
	public SqlServerPeopleTable test_build_queryByPK(Integer PeopleID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.equal("PeopleID", PeopleID, Types.INTEGER, false);
		builder.orderBy("PeopleID", true);
		builder.requireFirst();

		return client.queryObject(builder, hints);
	}

	/**
	 * 构建，查询，first
	 **/
	public List<SqlServerPeopleTable> test_build_queryByName(String name, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.equal("Name", name, Types.VARCHAR, false);
		builder.orderBy("PeopleID", true);

		return client.query(builder, hints);
	}

	/**
	 * 构建，查询
	**/
	public List<String> test_build_queryFieldList(List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.orderBy("PeopleID", true);

		return client.query(builder, hints, String.class);
	}

	/**
	 * 构建，查询
	**/
	public String test_build_queryFieldSingle(List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints, String.class);
	}

	/**
	 * 构建，查询
	**/
	public String test_build_queryFieldFirst(List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.orderBy("PeopleID", true);
		builder.requireFirst();

		return client.queryObject(builder, hints, String.class);
	}

	/**
	 * 构建，查询
	**/
	public List<SqlServerPeopleTable> test_build_queryList(List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.orderBy("PeopleID", true);

		return client.query(builder, hints);
	}

	/**
	 * 构建，查询
	**/
	public SqlServerPeopleTable test_build_querySingle(List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints);
	}

	/**
	 * 构建，查询
	**/
	public SqlServerPeopleTable test_build_queryFirst(List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.orderBy("PeopleID", true);
	    builder.requireFirst();

		return client.queryObject(builder, hints);
	}

	/**
	 * 构建，更新
	**/
	public int test_build_update(String Name, List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
		builder.update("Name", Name, Types.VARCHAR);
		builder.in("CityID", CityID, Types.INTEGER, false);

		return client.update(builder, hints);
	}
	/**
	 * 构建，新增
	**/
	public int test_build_insert(Integer CityID, String Name, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		InsertSqlBuilder builder = new InsertSqlBuilder();
		builder.set("CityID", CityID, Types.INTEGER);
		builder.set("Name", Name, Types.VARCHAR);

		return client.insert(builder, hints);
	}

	/**
	 * 构建，删除
	**/
	public int test_build_delete(List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		DeleteSqlBuilder builder = new DeleteSqlBuilder();
		builder.in("CityID", CityID, Types.INTEGER, false);

		return client.delete(builder, hints);
	}
	
	/**
	 * 自定义，查询
	**/
	public List<SqlServerPeopleTable> test_def_queryList(List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<SqlServerPeopleTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People with (nolock) where CityID in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		builder.mapWith(peopleSqlServerPeopleTableRowMapper);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义，查询
	 **/
	public List<SqlServerPeopleTable> test_def_queryByPK(Integer ID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<SqlServerPeopleTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from people where PeopleID = ?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.set(i, "PeopleID", Types.INTEGER, ID);
		builder.mapWith(peopleSqlServerPeopleTableRowMapper);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义，增删改
	**/
	public int test_def_truncate (DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("truncate table People");
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}
}