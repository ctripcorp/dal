package dao.shard.newVersionCode;


import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import entity.MysqlPeopleTable;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Comparator;
import java.util.List;

public class PeopleShardColModShardByDBTableOnMysqlDao {
	private static final boolean ASC = true;
	private DalTableDao<MysqlPeopleTable> client;
	private static final String DATA_BASE = "ShardColModShardByDBTableOnMysql";
	private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
	private DalQueryDao queryDao = null;

	private DalRowMapper<MysqlPeopleTable> peopleShardColModShardByDBTableOnMysqlRowMapper = null;
	
	public PeopleShardColModShardByDBTableOnMysqlDao() throws SQLException {
//		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(MysqlPeopleTable.class));
		this.client = new DalTableDao<>(MysqlPeopleTable.class,DATA_BASE);
		this.peopleShardColModShardByDBTableOnMysqlRowMapper = new DalDefaultJpaMapper<>(MysqlPeopleTable.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}

	public PeopleShardColModShardByDBTableOnMysqlDao(String DATA_BASE) throws SQLException {
//		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(MysqlPeopleTable.class));
		this.client = new DalTableDao<>(MysqlPeopleTable.class,DATA_BASE);
		this.peopleShardColModShardByDBTableOnMysqlRowMapper = new DalDefaultJpaMapper<>(MysqlPeopleTable.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}

	private class PeopleShardColModShardByDBTableOnMysqlComparator implements Comparator<MysqlPeopleTable> {
		@Override
		public int compare(MysqlPeopleTable o1, MysqlPeopleTable o2) {
			return new Integer(o1.getCityID()).compareTo(o2.getCityID());
		}
	}

	private class StringComparator implements Comparator<String>{
		@Override
		public int compare(String o1, String o2) {
			return new Integer( o1.compareTo(o2));
		}
	}

	/**
	 * Query MysqlPeopleTable by the specified ID
	 * The ID must be a number
	**/
	public MysqlPeopleTable queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query MysqlPeopleTable by MysqlPeopleTable instance which the primary key is set
	**/
	public MysqlPeopleTable queryByPk(MysqlPeopleTable pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<MysqlPeopleTable> queryLike(MysqlPeopleTable sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryLike(sample, hints);
	}

	public List<MysqlPeopleTable> queryBy(MysqlPeopleTable sample, DalHints hints)
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
	 * Query MysqlPeopleTable with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<MysqlPeopleTable> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll().atPage(pageNo, pageSize).orderBy("ID", ASC);

		return client.query(builder, hints);
	}
	
	/**
	 * Get all records from table
	 */
	public List<MysqlPeopleTable> queryAll(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("ID", ASC);
		
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
	public int insert(DalHints hints, MysqlPeopleTable daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, List<MysqlPeopleTable> daoPojos) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, MysqlPeopleTable daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<MysqlPeopleTable> daoPojos) throws SQLException {
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
	public int[] batchInsert(DalHints hints, List<MysqlPeopleTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, List<MysqlPeopleTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 * 
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder holder for generated primary keys
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<MysqlPeopleTable> daoPojos) throws SQLException {
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
	public int delete(DalHints hints, MysqlPeopleTable daoPojo) throws SQLException {
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
	public int[] delete(DalHints hints, List<MysqlPeopleTable> daoPojos) throws SQLException {
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
	public int[] batchDelete(DalHints hints, List<MysqlPeopleTable> daoPojos) throws SQLException {
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
	public int update(DalHints hints, MysqlPeopleTable daoPojo) throws SQLException {
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
	public int[] update(DalHints hints, List<MysqlPeopleTable> daoPojos) throws SQLException {
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
	public int[] batchUpdate(DalHints hints, List<MysqlPeopleTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}

	/**
	 * 构建
	**/
	public List<String> test_build_query_fieldList(List<Integer> CityID, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.and();
		builder.in("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);

		return client.query(builder, hints.sortBy(new StringComparator()), String.class);
	}

	/**
	 * 构建
	**/
	public List<String> test_build_query_fieldListByPage(List<Integer> CityID, List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.and();
		builder.in("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
		builder.atPage(pageNo, pageSize);

		return client.query(builder, hints, String.class);
	}

	/**
	 * 构建
	**/
	public String test_build_query_fieldSingle(List<Integer> CityID, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.and();
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints, String.class);
	}

	/**
	 * 构建
	**/
	public String test_build_query_fieldFirst(List<Integer> CityID, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.and();
		builder.in("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
		builder.requireFirst();

		return client.queryObject(builder, hints.sortBy(new StringComparator()), String.class);
	}

	/**
	 * 构建
	**/
	public List<MysqlPeopleTable> test_build_query_list(List<Integer> CityID, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","CityID","Age","ID");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);

		return client.query(builder, hints.sortBy(new PeopleShardColModShardByDBTableOnMysqlComparator()));
	}

	/**
	 * 构建
	**/
	public List<MysqlPeopleTable> test_build_query_listByPage(List<Integer> CityID, List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","CityID","Age","ID");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
		builder.atPage(pageNo, pageSize);

		return client.query(builder, hints);
	}

	/**
	 * 构建
	**/
	public MysqlPeopleTable test_build_query_listSingle(List<Integer> CityID, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","CityID","Age","ID");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.and();
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireSingle();
		return client.queryObject(builder, hints);
	}

	/**
	 * 构建
	**/
	public MysqlPeopleTable test_build_query_listFirst(List<Integer> CityID, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","CityID","Age","ID");
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.and();
		builder.in("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
	    builder.requireFirst();

		return client.queryObject(builder, hints.sortBy(new PeopleShardColModShardByDBTableOnMysqlComparator()));
	}

	/**
	 * 构建
	**/
	public int test_build_update(String Name, List<Integer> CityID, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
		builder.update("Name", Name, Types.VARCHAR);
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.and();
		builder.in("Age", Age, Types.INTEGER, false);

		return client.update(builder, hints);
	}
	/**
	 * 构建
	**/
	public int test_build_insert(String Name, Integer CityID, Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		InsertSqlBuilder builder = new InsertSqlBuilder();
		builder.set("Name", Name, Types.VARCHAR);
		builder.set("CityID", CityID, Types.INTEGER);
		builder.set("Age", Age, Types.INTEGER);

		return client.insert(builder, hints);
	}

	/**
	 * 构建
	**/
	public int test_build_delete(List<Integer> CityID, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		DeleteSqlBuilder builder = new DeleteSqlBuilder();
		builder.in("CityID", CityID, Types.INTEGER, false);
		builder.and();
		builder.in("Age", Age, Types.INTEGER, false);

		return client.delete(builder, hints);
	}
	
	/**
	 * 自定义
	**/
	public List<String> test_def_query_fieldList(List<Integer> CityID, List<Integer> Age, DalHints hints,String tableShardID) throws SQLException {	
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select Name from People"+tableShardID+" where CityID in (?) and Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.simpleType();

		return queryDao.query(builder, parameters, hints.sortBy(new StringComparator()));
	}

	/**
	 * 自定义
	**/
	public List<String> test_def_query_fieldListByPage(List<Integer> CityID, List<Integer> Age, int pageNo, int pageSize, DalHints hints,String tableShardID) throws SQLException {	
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select Name from People"+tableShardID+" where CityID in (?) and Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.simpleType().atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义
	**/
	public String test_def_query_fieldSingle(List<Integer> CityID, List<Integer> Age, DalHints hints,String tableShardID) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select Name from People"+tableShardID+" where CityID in (?) and Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.simpleType().requireSingle().nullable();

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义
	**/
	public String test_def_query_fieldFirst(List<Integer> CityID, List<Integer> Age, DalHints hints,String tableShardID) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select Name from People"+tableShardID+" where CityID in (?) and Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.simpleType().requireFirst().nullable();

		return queryDao.query(builder, parameters, hints.sortBy(new StringComparator() {
		}));
	}

	/**
	 * 自定义
	**/
	public List<MysqlPeopleTable> test_def_query_list(List<Integer> CityID, List<Integer> Age, DalHints hints, String tableShardID) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlPeopleTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People"+tableShardID+" where CityID in (?) and Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(peopleShardColModShardByDBTableOnMysqlRowMapper);

		return queryDao.query(builder, parameters, hints.sortBy(new PeopleShardColModShardByDBTableOnMysqlComparator()));
	}

	/**
	 * 自定义
	 **/
	public List<MysqlPeopleTable> testFreeSqlQueryList(List<Integer> CityID, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlPeopleTable>> builder = new FreeSelectSqlBuilder<>();
		builder.selectAll().from("People").where(Expressions.in("CityID",CityID, Types.INTEGER),Expressions.AND, Expressions.in("Age",Age, Types.INTEGER));

		builder.mapWith(peopleShardColModShardByDBTableOnMysqlRowMapper);

		return queryDao.query(builder, hints.sortBy(new PeopleShardColModShardByDBTableOnMysqlComparator()));
	}

	/**
	 * 自定义
	 **/
	public List<String> testFreeSqlQueryFieldList(Integer CityID, Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>();
		builder.select("Name").from("People").where(Expressions.equal("CityID",CityID, Types.INTEGER),Expressions.AND, Expressions.equal("Age",Age, Types.INTEGER));
		builder.simpleType();

		return queryDao.query(builder, hints.sortBy(new StringComparator()));
	}

	/**
	 * 自定义
	**/
	public List<MysqlPeopleTable> test_def_query_listByPage(List<Integer> CityID, List<Integer> Age, int pageNo, int pageSize, DalHints hints, String tableShardID) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlPeopleTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People"+tableShardID+" where CityID in (?) and Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(peopleShardColModShardByDBTableOnMysqlRowMapper).atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义
	**/
	public MysqlPeopleTable test_def_query_listSingle(List<Integer> CityID, List<Integer> Age, DalHints hints, String tableShardID) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<MysqlPeopleTable> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People"+tableShardID+" where CityID in (?) and Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(peopleShardColModShardByDBTableOnMysqlRowMapper).requireSingle().nullable();

		return (MysqlPeopleTable)queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义
	**/
	public MysqlPeopleTable test_def_query_listFirst(List<Integer> CityID, List<Integer> Age, DalHints hints, String tableShardID) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<MysqlPeopleTable> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from People"+tableShardID+" where CityID in (?) and Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(peopleShardColModShardByDBTableOnMysqlRowMapper).requireFirst().nullable();
		
		return (MysqlPeopleTable)queryDao.query(builder, parameters, hints.sortBy(new PeopleShardColModShardByDBTableOnMysqlComparator()));
	}

	/**
	 * 自定义
	**/
	public int test_def_update (String Name, List<Integer> CityID, List<Integer> Age, DalHints hints,String tableShardID) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("update People"+tableShardID+" set Name=? where CityID in (?) and Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "Name", Types.VARCHAR, Name);
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);

		return queryDao.update(builder, parameters, hints);
	}

	/**
	 * 自定义
	**/
	public int test_def_truncate (DalHints hints,String tableShardID) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("truncate people"+tableShardID);
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}
}