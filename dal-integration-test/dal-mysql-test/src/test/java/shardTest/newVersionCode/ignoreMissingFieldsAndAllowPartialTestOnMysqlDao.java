package shardTest.newVersionCode;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;


public class ignoreMissingFieldsAndAllowPartialTestOnMysqlDao {
	private static final boolean ASC = true;
	private DalTableDao<ignoreMissingFieldsAndAllowPartialTestOnMysql> client;
	private DalRowMapper<ignoreMissingFieldsAndAllowPartialTestOnMysql> personShardColModShardByDBOnMysqlPojoRowMapper = null;
	private static final String DATA_BASE = "ShardColModShardByDBOnMysql";
	private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
	private DalQueryDao queryDao = null;
	private static final String TABLE_NAME="person";


	private String sqlList = "select * from " + TABLE_NAME;
	private String sqlFieldList = "select Name from " + TABLE_NAME;
	private String sqlCount = "select count(*) from " + TABLE_NAME;
	private String sqlObject = "select * from " + TABLE_NAME + " where ID = ? and Age=21";
	private String sqlFirst = "select * from " + TABLE_NAME + " where ID = ?";
	private String sqlNoResult = "select * from " + TABLE_NAME + " where ID = 4";
	private String sqlInParam = "select * from " + TABLE_NAME + " where Age in (?)";


	public ignoreMissingFieldsAndAllowPartialTestOnMysqlDao() throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(ignoreMissingFieldsAndAllowPartialTestOnMysql.class));
		this.personShardColModShardByDBOnMysqlPojoRowMapper = new DalDefaultJpaMapper<>(ignoreMissingFieldsAndAllowPartialTestOnMysql.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}
	
	
	private class PersonShardColModShardByDBOnMysqlComparator implements Comparator<ignoreMissingFieldsAndAllowPartialTestOnMysql>{
		@Override
		public int compare(ignoreMissingFieldsAndAllowPartialTestOnMysql o1, ignoreMissingFieldsAndAllowPartialTestOnMysql o2) {
			return new Integer(o1.getAge()).compareTo(o2.getAge());
		}
	}
	
	private class StringComparator implements Comparator<String>{
		@Override
		public int compare(String o1, String o2) {
			return new Integer( o2.compareTo(o1));
		}
	}
	
	private class IntegerComparator implements Comparator<Integer>{
		@Override
		public int compare(Integer o1, Integer o2) {
			return new Integer(o1.compareTo(o2));
		}
	}
	
	private class LongComparator implements Comparator<Long>{
		@Override
		public int compare(Long o1, Long o2) {
			return new Integer(o1.compareTo(o2));
		}
	}
	
//	private class InteregrComparator implements Comparator<Integer>{
//		@Override
//		public int compare(Integer o1, Integer o2) {
//			return o1.compareTo(o2);
//		}
//	}
//	
	

	private class TestDalRowCallback implements DalRowCallback {

		@Override
		public void process(ResultSet rs) throws SQLException {
		}
	}

	/**
	 * Query ignoreMissingFieldsAndAllowPartialTestOnMysql by the specified ID
	 * The ID must be a number
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query ignoreMissingFieldsAndAllowPartialTestOnMysql by ignoreMissingFieldsAndAllowPartialTestOnMysql instance which the primary key is set
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql queryByPk(ignoreMissingFieldsAndAllowPartialTestOnMysql pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> queryLike(ignoreMissingFieldsAndAllowPartialTestOnMysql sample, DalHints hints)
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
	 * Query ignoreMissingFieldsAndAllowPartialTestOnMysql with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll().atPage(pageNo, pageSize).orderBy("ID", ASC);

		return client.query(builder, hints);
	}
	
	/**
	 * Get all records from table
	 */
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> queryAll(DalHints hints) throws SQLException {
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
	public int insert(DalHints hints, ignoreMissingFieldsAndAllowPartialTestOnMysql daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, List<ignoreMissingFieldsAndAllowPartialTestOnMysql> daoPojos) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, ignoreMissingFieldsAndAllowPartialTestOnMysql daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<ignoreMissingFieldsAndAllowPartialTestOnMysql> daoPojos) throws SQLException {
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
	public int[] batchInsert(DalHints hints, List<ignoreMissingFieldsAndAllowPartialTestOnMysql> daoPojos) throws SQLException {
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
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, List<ignoreMissingFieldsAndAllowPartialTestOnMysql> daoPojos) throws SQLException {
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
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<ignoreMissingFieldsAndAllowPartialTestOnMysql> daoPojos) throws SQLException {
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
	public int delete(DalHints hints, ignoreMissingFieldsAndAllowPartialTestOnMysql daoPojo) throws SQLException {
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
	public int[] delete(DalHints hints, List<ignoreMissingFieldsAndAllowPartialTestOnMysql> daoPojos) throws SQLException {
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
	public int[] batchDelete(DalHints hints, List<ignoreMissingFieldsAndAllowPartialTestOnMysql> daoPojos) throws SQLException {
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
	public int update(DalHints hints, ignoreMissingFieldsAndAllowPartialTestOnMysql daoPojo) throws SQLException {
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
	public int[] update(DalHints hints, List<ignoreMissingFieldsAndAllowPartialTestOnMysql> daoPojos) throws SQLException {
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
	public int[] batchUpdate(DalHints hints, List<ignoreMissingFieldsAndAllowPartialTestOnMysql> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}

	/**
	 * 构建，查询
	**/
	public List<String> test_build_query_fieldList(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);

		return client.query(builder, hints, String.class);
	}

	/**
	 * 构建，查询
	**/
	public List<String> test_build_query_fieldListByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
		builder.atPage(pageNo, pageSize);

		return client.query(builder, hints, String.class);
	}

	/**
	 * 构建，查询
	**/
	public String test_build_query_field_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints, String.class);
	}

	/**
	 * 构建，查询
	**/
	public String test_build_query_field_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireFirst();

		return client.queryObject(builder, hints, String.class);
	}

	/**
	 * 构建，查询
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_build_query_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.selectAll();
		builder.inNullable("Age", Age, Types.INTEGER, false);

		return client.query(builder, hints.sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}
	
	/**
	 * 构建，查询部分字段
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_build_queryPartial_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name","ID");
//		builder.selectAll();
		builder.inNullable("Age", Age, Types.INTEGER, false);

		return client.query(builder, hints);
	}
	


	/**
	 * 构建，查询
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_build_query_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
//		builder.range(start, count)
		builder.atPage(pageNo, pageSize);
		return client.query(builder, hints.sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}
	
	/**
	 * 构建，查询部分字段
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_build_queryPartial_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
//		builder.range(start, count)
		builder.atPage(pageNo, pageSize);
		return client.query(builder, hints);
	}

	/**
	 * 构建，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_build_query_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints.sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}
	
	/**
	 * 构建，查询部分字段
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_build_queryPartial_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name","ID");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints);
	}

	/**
	 * 构建，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_build_query_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
	    builder.requireFirst();

		return client.queryObject(builder, hints.sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}
	
	/**
	 * 构建，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_build_queryPartial_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Age","ID");
//		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
	    builder.requireFirst();

		return client.queryObject(builder, hints);
	}
	

	
	/**
	 * testEqual
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test(Integer param1, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.notEqualNullable("Age", null,Types.INTEGER);
//		builder.and();
		builder.equal("Age", param1, Types.INTEGER, false);
//		builder.and();
		
//		builder.equalNullable(field, paramValue, sqlType)
		builder.orderBy("ID", true);

		return client.query(builder, hints);
	}
	

	/**
	 * 构建，更新
	**/
	public int test_build_update(String Name, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
		builder.update("Name", Name, Types.VARCHAR);
		builder.in("Age", Age, Types.INTEGER, false);

		return client.update(builder, hints);
	}
	/**
	 * 构建，新增
	**/
	public int test_build_insert(String Name, Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		InsertSqlBuilder builder = new InsertSqlBuilder();
		builder.set("Name", Name, Types.VARCHAR);
		builder.set("Age", Age, Types.INTEGER);

		return client.insert(builder, hints);
	}

	/**
	 * 构建，删除
	**/
	public int test_build_delete(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		DeleteSqlBuilder builder = new DeleteSqlBuilder();
		builder.inNullable("Age", Age, Types.INTEGER, false);

		return client.delete(builder, hints);
	}
	
	/**
	 * 自定义，查询
	**/
	public List<String> test_def_query_fieldList(List<Integer> Age, DalHints hints) throws SQLException {	
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select Name from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.simpleType();

		return queryDao.query(builder, parameters, hints.sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}

	/**
	 * 自定义，查询
	**/
	public List<String> test_def_query_fieldListByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {	
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select Name from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.simpleType().atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义，查询
	**/
	public String test_def_query_field_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select Name from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.simpleType().requireSingle().nullable();

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义，查询
	**/
	public String test_def_query_field_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select Name from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.simpleType().requireFirst().nullable();

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义，查询
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_query_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper);

		return queryDao.query(builder, parameters, hints);
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_queryPartialSet_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper);

		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return queryDao.query(builder, parameters, hints.partialQuery(columns) );
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_queryPartialStrings_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper);

//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		
		return queryDao.query(builder, parameters, hints.partialQuery("Age","Name") );
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_queryIgnoreMissingFields_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper);

//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		
		return queryDao.query(builder, parameters, hints.partialQuery("Age","Name","Birth","ID").sortBy(new PersonShardColModShardByDBOnMysqlComparator()) );
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_queryAllowPartial_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper);
		
		return queryDao.query(builder, parameters, hints.partialQuery("ID","Age","Name","IsCompany").sortBy(new PersonShardColModShardByDBOnMysqlComparator()) );
	}
	
	
	/**
	 * 自定义，查询，部分字段
	**/
	public List<Map<String, Object>> test_def_query_partlist(List<Integer> Age, DalHints hints) throws SQLException {
		
		DalCustomRowMapper mapper = new DalCustomRowMapper("name", "age");

		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<Map<String, Object>>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select name,age from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
		builder.mapWith(mapper);

		return queryDao.query(builder, parameters, hints);
	}
	


	/**
	 * 自定义，查询
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_query_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints);
	}
	
	
	
	/**
	 * 自定义，查询
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_queryPartialSet_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).atPage(pageNo, pageSize);

		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return queryDao.query(builder, parameters, hints.partialQuery(columns));
	}
	
	
	/**
	 * 自定义，查询部分字段
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_queryIgnoreMissingFields_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).atPage(pageNo, pageSize);

//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		
		return queryDao.query(builder, parameters, hints.partialQuery("Name", "ID", "Birth", "Age").sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_queryAllowPartial_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints.sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_queryPartialStrings_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).atPage(pageNo, pageSize);

//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		
		return queryDao.query(builder, parameters, hints.partialQuery("Age","Name"));
	}

	/**
	 * 自定义，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_queryPartialSet_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<ignoreMissingFieldsAndAllowPartialTestOnMysql> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireSingle().nullable();

		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints.partialQuery(columns));
	}
	
	/**
	 * 自定义，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_queryPartialStrings_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<ignoreMissingFieldsAndAllowPartialTestOnMysql> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireSingle().nullable();

		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints.partialQuery("Age","Name"));
	}
	
	/**
	 * 自定义，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_query_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<ignoreMissingFieldsAndAllowPartialTestOnMysql> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireSingle().nullable();

		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints);
	}
	
	/**
	 * 自定义，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_queryIgnoreMissingFields_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<ignoreMissingFieldsAndAllowPartialTestOnMysql> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireSingle().nullable();
		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints.partialQuery("Name", "ID", "Birth", "Age").sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}
	
	/**
	 * 自定义，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_queryAllowPartial_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<ignoreMissingFieldsAndAllowPartialTestOnMysql> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireSingle().nullable();
		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints.sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}

	/**
	 * 自定义，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_query_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<ignoreMissingFieldsAndAllowPartialTestOnMysql> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireFirst().nullable();
		
		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints);
	}
	
	/**
	 * 自定义，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_queryIgnoreMissingFields_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<ignoreMissingFieldsAndAllowPartialTestOnMysql> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireFirst().nullable();
//		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints);
		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints.partialQuery("Name", "ID", "Birth", "Age").sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}
	
	/**
	 * 自定义，查询
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_queryAllowPartial_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<ignoreMissingFieldsAndAllowPartialTestOnMysql> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireFirst().nullable();
		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints.sortBy(new PersonShardColModShardByDBOnMysqlComparator()));
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_queryPartialSet_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<ignoreMissingFieldsAndAllowPartialTestOnMysql> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireFirst().nullable();
		
		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints.partialQuery(columns));
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_queryPartialStrings_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<ignoreMissingFieldsAndAllowPartialTestOnMysql> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select Age, name from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireFirst().nullable();
		
//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
//		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints);
		return (ignoreMissingFieldsAndAllowPartialTestOnMysql)queryDao.query(builder, parameters, hints.partialQuery("Age","Name"));
	}

	/**
	 * 自定义，更新
	**/
	public int test_def_update_in (String Name, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("update person set Name=? where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return queryDao.update(builder, parameters, hints);
	}

	/**
	 * 自定义，删除
	**/
	public int test_def_truncate (DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("truncate person");
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}
	
	/**
	 * 自定义 ，查询，list<string>
	**/
	public List<ignoreMissingFieldsAndAllowPartialTestOnMysql> test_def_query_in_string(List<String> name, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<ignoreMissingFieldsAndAllowPartialTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where name in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "name", Types.VARCHAR, name);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper);

		return queryDao.query(builder, parameters, hints);
	}
	
	/**
	 * count 
	**/
	public Long test_def_count(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<Long> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select count(*) from person");
		StatementParameters parameters = new StatementParameters();
		builder.simpleType().requireSingle().nullable();

		return queryDao.query(builder, parameters, hints);
	}

	/**
	 * min
	**/
	public Integer test_def_min(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<Integer> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select min(Age) from person");
		StatementParameters parameters = new StatementParameters();
		builder.simpleType().requireFirst().nullable();

		return queryDao.query(builder, parameters, hints);
	}
	
	private class IntegerRowMapper implements DalRowMapper<Integer> {

		@Override
		public Integer map(ResultSet rs, int rowNum) throws SQLException {
			return rs.getInt(1);
		}
	}
	
	private class StringRowMapper implements DalRowMapper<String> {

		@Override
		public String map(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString(1);
		}
	}

	public List queryListMultipleAllShards(DalHints hints) throws SQLException {
		
	    MultipleSqlBuilder builder = new MultipleSqlBuilder();

	    builder.addQuery(sqlList, new StatementParameters(),personShardColModShardByDBOnMysqlPojoRowMapper);//ListMapper
	    builder.addQuery(sqlList, new StatementParameters(),personShardColModShardByDBOnMysqlPojoRowMapper,new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());//ListMerger
	    builder.addQuery(sqlList, new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class);
	    builder.addQuery(sqlList, new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
//	    builder.addQuery(sqlList, new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new DalRangedResultMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>(1,2));
//	    builder.addQuery(sqlList, new StatementParameters(), new DalRowMapperExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,1,1), new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlList, new StatementParameters(), new DalSingleResultExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,false), new DalFirstResultMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlList, new StatementParameters(), new TestDalRowCallback());
	    builder.addQuery(sqlList, new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new PersonShardColModShardByDBOnMysqlComparator());
	    builder.addQuery(sqlList, new StatementParameters(), personShardColModShardByDBOnMysqlPojoRowMapper, new PersonShardColModShardByDBOnMysqlComparator());
	   
	    builder.addQuery(sqlCount, new StatementParameters(),new IntegerRowMapper());
	    builder.addQuery(sqlCount, new StatementParameters(),new IntegerRowMapper(),new DalListMerger<Integer>());//ListMerger
	    builder.addQuery(sqlCount, new StatementParameters(), Integer.class);
	    builder.addQuery(sqlCount, new StatementParameters(), Integer.class, new DalListMerger<Integer>());
	    builder.addQuery(sqlCount, new StatementParameters(), new DalRowMapperExtractor(new IntegerRowMapper()), new DalListMerger<Integer>());
	    builder.addQuery(sqlCount, new StatementParameters(), new TestDalRowCallback());
//	    builder.addQuery(sqlCount, new StatementParameters(), Integer.class, new IntegerComparator());
	    builder.addQuery(sqlCount, new StatementParameters(), new IntegerRowMapper(), new IntegerComparator());
	    
	    builder.addQuery(sqlFieldList, new StatementParameters(), String.class);
	    builder.addQuery(sqlFieldList, new StatementParameters(),String.class,new DalListMerger<String>());
	    builder.addQuery(sqlFieldList, new StatementParameters(), new StringRowMapper());
	    builder.addQuery(sqlFieldList, new StatementParameters(), new StringRowMapper(),new DalListMerger<String>());
	    builder.addQuery(sqlFieldList, new StatementParameters(), new DalSingleResultExtractor(new StringRowMapper(),false), new DalFirstResultMerger<String>());
	    builder.addQuery(sqlFieldList, new StatementParameters(), new TestDalRowCallback());
	    builder.addQuery(sqlFieldList, new StatementParameters(), String.class, new StringComparator());
	    builder.addQuery(sqlFieldList, new StatementParameters(), new StringRowMapper(), new StringComparator());
	    
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1),personShardColModShardByDBOnMysqlPojoRowMapper);//ListMapper
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1),personShardColModShardByDBOnMysqlPojoRowMapper,new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());//ListMerger
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), ignoreMissingFieldsAndAllowPartialTestOnMysql.class);
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
//	    builder.addQuery(sqlList, new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new DalRangedResultMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>(1,2));
//	    builder.addQuery(sqlList, new StatementParameters(), new DalRowMapperExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,1,1), new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), new DalSingleResultExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,false), new DalFirstResultMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), new TestDalRowCallback());
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new PersonShardColModShardByDBOnMysqlComparator());
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), personShardColModShardByDBOnMysqlPojoRowMapper, new PersonShardColModShardByDBOnMysqlComparator());
	   
	    
//	    builder.addQuery(sqlObject, parameters, Integer.class);
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), personShardColModShardByDBOnMysqlPojoRowMapper);	    
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1),personShardColModShardByDBOnMysqlPojoRowMapper,new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), ignoreMissingFieldsAndAllowPartialTestOnMysql.class);
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), ignoreMissingFieldsAndAllowPartialTestOnMysql.class,new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), new DalSingleResultExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,false), new DalFirstResultMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), new TestDalRowCallback());
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new PersonShardColModShardByDBOnMysqlComparator());
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), personShardColModShardByDBOnMysqlPojoRowMapper, new PersonShardColModShardByDBOnMysqlComparator());
//	    builder.addQuery(sqlFieldList, new StatementParameters(),String.class, new DalListMerger<String>());//merger
//	    builder.addQuery(sqlListCount,new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new PersonShardColModShardByDBOnMysqlComparator());//sorter
//	    builder.addQuery(sqlListCount, new StatementParameters(),Integer.class, new DalListMerger<Integer>());//merger
//	    builder.addQuery(sqlObject, parameters,Integer.class, new InteregrComparator());//soter
//	    builder.addQuery(sqlNoResult, new StatementParameters(),new TestDalRowCallback3());//callback
	    StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, new ArrayList<Integer>(){{add(20); add(21);add(23);}});
		
	    builder.addQuery(sqlInParam, parameters, personShardColModShardByDBOnMysqlPojoRowMapper);	    
	    builder.addQuery(sqlInParam, parameters,personShardColModShardByDBOnMysqlPojoRowMapper,new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlInParam, parameters, ignoreMissingFieldsAndAllowPartialTestOnMysql.class);
	    builder.addQuery(sqlInParam, parameters, ignoreMissingFieldsAndAllowPartialTestOnMysql.class,new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlInParam, parameters, new DalSingleResultExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,false), new DalFirstResultMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlInParam, parameters, new TestDalRowCallback());
	    builder.addQuery(sqlInParam, parameters, ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new PersonShardColModShardByDBOnMysqlComparator());
	    builder.addQuery(sqlInParam, parameters, personShardColModShardByDBOnMysqlPojoRowMapper, new PersonShardColModShardByDBOnMysqlComparator());

	    builder.addQuery(sqlNoResult, new StatementParameters(), personShardColModShardByDBOnMysqlPojoRowMapper);	    
	    builder.addQuery(sqlNoResult, new StatementParameters(),personShardColModShardByDBOnMysqlPojoRowMapper,new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlNoResult, new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class);
	    builder.addQuery(sqlNoResult, new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class,new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlNoResult, new StatementParameters(), new DalSingleResultExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,false), new DalFirstResultMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlNoResult, new StatementParameters(), new TestDalRowCallback());
	    builder.addQuery(sqlNoResult, new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new PersonShardColModShardByDBOnMysqlComparator());
	    builder.addQuery(sqlNoResult, new StatementParameters(), personShardColModShardByDBOnMysqlPojoRowMapper, new PersonShardColModShardByDBOnMysqlComparator());
	    		
	    builder.addQuery(sqlCount, new StatementParameters(), Long.class, new LongComparator());
	    
	    return queryDao.query(builder, hints);
	}
	
//	public List queryInListMultipleAllShards(DalHints hints,List<Integer> Age) throws SQLException {
//		MultipleSqlBuilder builder = new MultipleSqlBuilder();
//		StatementParameters parameters = new StatementParameters();
//		
//		int i = 1;
//		i=parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
//
//	    builder.addQuery(sqlInParam, parameters, personShardColModShardByDBOnMysqlPojoRowMapper);
//	    return queryDao.query(builder, hints);
//	}
//	
//	public List queryNoRetMultipleAllShards(DalHints hints) throws SQLException {
//		 MultipleSqlBuilder builder = new MultipleSqlBuilder();
//		 builder.addQuery(sqlNoResult, new StatementParameters(), personShardColModShardByDBOnMysqlPojoRowMapper);	
//		 return queryDao.query(builder, hints);
//	}
	
	public List queryCountMultipleAllShards(DalHints hints) throws SQLException {
		MultipleSqlBuilder builder = new MultipleSqlBuilder();
	    builder.addQuery(sqlCount, new StatementParameters(),new IntegerRowMapper());
	    builder.addQuery(sqlCount, new StatementParameters(),new IntegerRowMapper(),new DalListMerger<Integer>());//ListMerger
	    builder.addQuery(sqlCount, new StatementParameters(), Long.class);
	    builder.addQuery(sqlCount, new StatementParameters(), Long.class, new DalListMerger<Long>());
	    builder.addQuery(sqlCount, new StatementParameters(), new DalRowMapperExtractor(new IntegerRowMapper()), new DalListMerger<Integer>());
	    builder.addQuery(sqlCount, new StatementParameters(), new TestDalRowCallback());
	    builder.addQuery(sqlCount, new StatementParameters(), Long.class, new LongComparator());
	    builder.addQuery(sqlCount, new StatementParameters(), new IntegerRowMapper(),new IntegerComparator());
//	    builder.addQuery(sqlCount, new StatementParameters(), Integer.class, new IntegerComparator());
	    return queryDao.query(builder, hints);
	}
}