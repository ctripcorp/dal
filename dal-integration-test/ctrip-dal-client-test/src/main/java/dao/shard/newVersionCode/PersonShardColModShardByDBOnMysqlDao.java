package dao.shard.newVersionCode;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import entity.MysqlPersonTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;


public class PersonShardColModShardByDBOnMysqlDao {
	private static final boolean ASC = true;
	private DalTableDao<MysqlPersonTable> client;
	private DalRowMapper<MysqlPersonTable> personShardColModShardByDBOnMysqlPojoRowMapper = null;
	private static final String DATA_BASE = "ShardColModShardByDBOnMysql";
	private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
	private DalQueryDao queryDao = null;
	private static final String TABLE_NAME="person";
	private DalParser<MysqlPersonTable> parser = null;
	
	private String sqlList = "select * from " + TABLE_NAME;
	private String sqlFieldList = "select Name from " + TABLE_NAME;
	private String sqlCount = "select count(*) from " + TABLE_NAME;
	private String sqlObject = "select * from " + TABLE_NAME + " where ID = ? and Age=21";
	private String sqlFirst = "select * from " + TABLE_NAME + " where ID = ?";
	private String sqlNoResult = "select * from " + TABLE_NAME + " where ID = 4";
	private String sqlInParam = "select * from " + TABLE_NAME + " where Age in (?)";
	
	
	public PersonShardColModShardByDBOnMysqlDao() throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(MysqlPersonTable.class,DATA_BASE));
		this.personShardColModShardByDBOnMysqlPojoRowMapper = new DalDefaultJpaMapper<>(MysqlPersonTable.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
//		this.parser = new DalDefaultJpaParser<>(MysqlPersonTable.class);
	}

	public PersonShardColModShardByDBOnMysqlDao(String DATA_BASE) throws SQLException {
		this.client = new DalTableDao<>(MysqlPersonTable.class,DATA_BASE);
		this.personShardColModShardByDBOnMysqlPojoRowMapper = new DalDefaultJpaMapper<>(MysqlPersonTable.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
//		this.parser = new DalDefaultJpaParser<>(MysqlPersonTable.class);
	}
	
	
	private class MysqlPersonTableComparator implements Comparator<MysqlPersonTable>{
		@Override
		public int compare(MysqlPersonTable o1, MysqlPersonTable o2) {
			return new Integer(o1.getAge()).compareTo(o2.getAge());
		}
	}
	
	private class StringComparator implements Comparator<String>{
		@Override
		public int compare(String o1, String o2) {
			return new Integer( o1.compareTo(o2));
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

	public MysqlPersonTable queryByPk(Number id)
			throws SQLException {
		return queryByPk(id, null);
	}
	
	/**
	 * Query ignoreMissingFieldsAndAllowPartialTestOnMysql by the specified ID
	 * The ID must be a number
	**/
	public MysqlPersonTable queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	public MysqlPersonTable queryByPk(MysqlPersonTable pk)
			throws SQLException {
		return queryByPk(pk, null);
	}
	
	/**
	 * Query ignoreMissingFieldsAndAllowPartialTestOnMysql by ignoreMissingFieldsAndAllowPartialTestOnMysql instance which the primary key is set
	**/
	public MysqlPersonTable queryByPk(MysqlPersonTable pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}

	public List<MysqlPersonTable> queryLike(MysqlPersonTable sample)
			throws SQLException {
		return queryLike(sample, null);
	}
	
	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<MysqlPersonTable> queryLike(MysqlPersonTable sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryLike(sample, hints);
	}

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

	public List<MysqlPersonTable> queryAllByPage(int pageNo, int pageSize)  throws SQLException {
		return queryAllByPage(pageNo, pageSize, null);
	}
	/**
	 * Query ignoreMissingFieldsAndAllowPartialTestOnMysql with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<MysqlPersonTable> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAllColumns().atPage(pageNo, pageSize).orderBy("ID", ASC);

		return client.query(builder, hints);
	}
	
	public List<MysqlPersonTable> queryAll() throws SQLException {
		return queryAll(null);
	}
	/**
	 * Get all records from table
	 */
	public List<MysqlPersonTable> queryAll(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		SelectSqlBuilder builder = new SelectSqlBuilder().selectAllColumns().orderBy("ID", ASC);
		
		return client.query(builder, hints);
	}

	public int insert(MysqlPersonTable daoPojo) throws SQLException {
		return insert(null, daoPojo);
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
	public int insert(DalHints hints, MysqlPersonTable daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojo);
	}

	
	public int[] insert(List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int[] insert(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojos);
	}

	public int insertWithKeyHolder(KeyHolder keyHolder, MysqlPersonTable daoPojo) throws SQLException {
		return insert(null, keyHolder, daoPojo);
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
	public int insert(DalHints hints, KeyHolder keyHolder, MysqlPersonTable daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojo);
	}

	
	public int[] insertWithKeyHolder(KeyHolder keyHolder, List<MysqlPersonTable> daoPojos) throws SQLException {
		return insert(null, keyHolder, daoPojos);
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<MysqlPersonTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojos);
	}

	
	public int[] batchInsert(List<MysqlPersonTable> daoPojos) throws SQLException {
		return batchInsert(null, daoPojos);
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
	public int[] batchInsert(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}

	public int combinedInsert(List<MysqlPersonTable> daoPojos) throws SQLException {
		return combinedInsert(null, daoPojos);
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
	public int combinedInsert(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, daoPojos);
	}

	public int combinedInsertWithKeyHolder(KeyHolder keyHolder, List<MysqlPersonTable> daoPojos) throws SQLException {
		return combinedInsert(null, keyHolder, daoPojos);
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
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<MysqlPersonTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, keyHolder, daoPojos);
	}

	public int delete(MysqlPersonTable daoPojo) throws SQLException {
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
	public int delete(DalHints hints, MysqlPersonTable daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojo);
	}

	public int[] delete(List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int[] delete(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojos);
	}

	public int[] batchDelete(List<MysqlPersonTable> daoPojos) throws SQLException {
		return batchDelete(null, daoPojos);
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
	public int[] batchDelete(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchDelete(hints, daoPojos);
	}

	public int update(MysqlPersonTable daoPojo) throws SQLException {
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
	public int update(DalHints hints, MysqlPersonTable daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.update(hints, daoPojo);
	}

	public int[] update(List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int[] update(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.update(hints, daoPojos);
	}

	public int[] batchUpdate(List<MysqlPersonTable> daoPojos) throws SQLException {
		return batchUpdate(null, daoPojos);
	}
	/**
	 * Update the given pojo list in batch. 
	 * 
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] batchUpdate(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}

	/**
	 * 构建，查询
	 **/
	public List<MysqlPersonTable> testBuildQueryLikeWithMatchPattern(String Name, MatchPattern pattern, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.like("Name",Name,pattern,Types.VARCHAR,false);
		builder.like("Name",Name,pattern,Types.VARCHAR);
		builder.orderBy("ID", true);

		return client.query(builder, hints);

	}

	/**
	 * 构建，查询
	 **/
	public List<MysqlPersonTable> testBuildQueryLikeNullableWithMatchPattern(String Name, MatchPattern pattern, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.likeNullable("Name",Name,pattern,Types.VARCHAR,false);
		builder.likeNullable("Name",Name,pattern,Types.VARCHAR);
		builder.orderBy("ID", true);

		return client.query(builder, hints);

	}

	public List<String> testBuildQueryField(List<Integer> age) throws SQLException {
		return test_build_query_fieldList(age, null);
	}

	/**
	 * 构建，查询
	**/
	public List<String> test_build_query_fieldList(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);

		return client.query(builder, hints.sortBy(new StringComparator()), String.class);
	}

	public List<String> testBuildQueryFieldByPage(List<Integer> age, int pageNo, int pageSize) throws SQLException {
		return test_build_query_fieldListByPage(age, pageNo, pageSize, null);
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

		return client.query(builder, hints.sortBy(new StringComparator()), String.class);
	}

	public String testBuildQueryFieldSingle(List<Integer> age) throws SQLException {
		return test_build_query_field_single(age, null);
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

	public String testBuildQueryFieldFirst(List<Integer> age) throws SQLException {
		return test_build_query_field_first(age, null);
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

		return client.queryObject(builder, hints.sortBy(new StringComparator()), String.class);
	}
	
	/**
	 * 构建，查询
	**/
	public List<MysqlPersonTable> test_ClientQueryFrom_list(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return client.queryFrom("Age in ?", parameters, hints.sortBy(new MysqlPersonTableComparator()), start, count);
	}

	/**
	 * 构建，查询
	 **/
	public List<MysqlPersonTable> test_ClientQueryTop_list(Integer Age, DalHints hints, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.set(i++, "Age", Types.INTEGER, Age);

		return client.queryTop("Age = ?", parameters, hints, count);
	}
	
	/**
	 * 构建，查询
	**/
	public List<MysqlPersonTable> test_ClientQueryFromPartialFieldsSet_list(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return client.queryFrom("Age in ?", parameters, hints.partialQuery(columns).sortBy(new MysqlPersonTableComparator()), start, count);
	}
	
	/**
	 * 构建，查询
	**/
	public List<MysqlPersonTable> test_ClientQueryFromPartialFieldsStrings_list(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		
		return client.queryFrom("Age in ?", parameters, hints.partialQuery("Age","Name").sortBy(new MysqlPersonTableComparator()), start, count);
	}

	public List<MysqlPersonTable> testBuildQuery(List<Integer> age) throws SQLException {
		return test_build_query_list(age, null);
	}
	/**
	 * 构建，查询
	**/
	public List<MysqlPersonTable> test_build_query_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.selectAllColumns();
//		builder.equalNullable("1", 1, Types.INTEGER, false);
//		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
//		builder.and();
		return client.query(builder, hints);
	}
	
	/**
	 * 构建，查询部分字段
	**/
	public List<MysqlPersonTable> test_build_queryPartial_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Age","Name","ID");
//		builder.selectAllColumns();
		builder.inNullable("Age", Age, Types.INTEGER, false);

		return client.query(builder, hints.sortBy(new MysqlPersonTableComparator()));
	}

	public List<MysqlPersonTable> testBuildQueryByPage(List<Integer> age, int pageNo, int pageSize) throws SQLException {
		return test_build_query_listByPage(age, pageNo, pageSize, null);
	}
	/**
	 * 构建，查询
	**/
	public List<MysqlPersonTable> test_build_query_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
//		builder.range(start, count)
		builder.atPage(pageNo, pageSize);
		return client.query(builder, hints.sortBy(new MysqlPersonTableComparator()));
	}

	/**
	 * 构建，查询部分字段
	**/
	public List<MysqlPersonTable> test_build_queryPartial_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
//		builder.range(start, count)
		builder.atPage(pageNo, pageSize);
		return client.query(builder, hints.sortBy(new MysqlPersonTableComparator()));
	}
	
	public MysqlPersonTable testBuildQuerySingle(List<Integer> age) throws SQLException {
		return test_build_query_single(age, null);
	}
	/**
	 * 构建，查询
	**/
	public MysqlPersonTable test_build_query_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints);
	}
	
	/**
	 * 构建，查询部分字段
	**/
	public MysqlPersonTable test_build_queryPartial_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Name","ID");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints);
	}

	public MysqlPersonTable testBuildQueryFirst(List<Integer> age) throws SQLException {
		return test_build_query_first(age, null);
	}
	/**
	 * 构建，查询
	**/
	public MysqlPersonTable test_build_query_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
	    builder.requireFirst();

		return client.queryObject(builder, hints.sortBy(new MysqlPersonTableComparator()));
	}

	public MysqlPersonTable test_query_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Birth","Name","Age","ID");
//		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.requireFirst();

		return client.queryFirst(builder, hints.sortBy(new MysqlPersonTableComparator()));
	}

    public MysqlPersonTable test_query_first_noRequireFirst(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Birth","Name","Age","ID");
//		builder.and();
        builder.inNullable("Age", Age, Types.INTEGER, false);
//        builder.requireFirst();

        return client.queryFirst(builder, hints.sortBy(new MysqlPersonTableComparator()));
    }
	
	/**
	 * 构建，查询
	**/
	public MysqlPersonTable test_build_queryPartial_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("Age","ID");
//		builder.and();
		builder.inNullable("Age", Age, Types.INTEGER, false);
	    builder.requireFirst();

		return client.queryObject(builder, hints.sortBy(new MysqlPersonTableComparator()));
	}
	
	/**
	 * testEqual
	**/
	public List<MysqlPersonTable> test(Integer param1, DalHints hints) throws SQLException {
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
	
	public int testBuildUpdate(String Name, List<Integer> age) throws SQLException {
		return test_build_update(Name, age, null);
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
	 * 更新多个字段
	**/
	public int test_build_multiColums_update(String Name, Integer Age, Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
		builder.update("Name", Name, Types.VARCHAR);
		builder.update("Age", Age, Types.INTEGER);
		builder.equal("ID", id, Types.INTEGER, false);

		return client.update(builder, hints);
	}
	
	public int testBuildInsert(String Name, Integer Age) throws SQLException {
		return test_build_insert(Name, Age, null);
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

	public int testBuildDelete(List<Integer> age) throws SQLException {
		return test_build_delete(age, null);
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
	
	
	public List<String> testDefQueryField(List<Integer> param1) throws SQLException {
		return test_def_query_fieldList(param1, null);
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

		return queryDao.query(builder, parameters, hints.sortBy(new StringComparator()));
	}

	public List<String> testDefQueryFieldByPage(List<Integer> param1, int pageNo, int pageSize) throws SQLException {
		return test_def_query_fieldListByPage(param1, pageNo, pageSize, null);
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

		return queryDao.query(builder, parameters, hints.sortBy(new StringComparator()));
	}

	public String testDefQueryFieldSingle(List<Integer> param1) throws SQLException {
		return test_def_query_field_single(param1, null);
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

	public String testDefQueryFieldFirst(List<Integer> param1) throws SQLException {
		return test_def_query_field_first(param1, null);
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

		return queryDao.query(builder, parameters, hints.sortBy(new StringComparator()));
	}

	
	public List<MysqlPersonTable> testDefQueryList(List<Integer> Age) throws SQLException {
		return test_def_query_list(Age, null);
	}
	/**
	 * 自定义，查询
	**/
	public List<MysqlPersonTable> test_def_query_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlPersonTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper);

		return queryDao.query(builder, parameters, hints.sortBy(new MysqlPersonTableComparator()));
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public List<MysqlPersonTable> test_def_queryPartialSet_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlPersonTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper);

		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return queryDao.query(builder, parameters, hints.partialQuery(columns).sortBy(new MysqlPersonTableComparator()) );
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public List<MysqlPersonTable> test_def_queryPartialStrings_list(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlPersonTable>> builder = new FreeSelectSqlBuilder<>();
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper);

//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		
		return queryDao.query(builder, parameters, hints.partialQuery("Age","Name").sortBy(new MysqlPersonTableComparator()) );
	}
	
	/**
	 * 自定义，查询，部分字段
	**/
	public List<Map<String, Object>> test_def_query_partlist(List<Integer> Age, DalHints hints) throws SQLException {
		
		DalCustomRowMapper mapper = new DalCustomRowMapper("myname", "age");

		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<Map<String, Object>>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select name as myname,age from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		
		builder.mapWith(mapper);

		return queryDao.query(builder, parameters, hints.sortBy(new MysqlPersonTableComparator()));
	}
	
//	 public List<pojo> queryVenuesInfoByKeyword(String sportType, String city, List<String> words, int pageSize, int current) {
//		      try {
//		    	
//		    	String sqlTemplate="select column1,clumn2,..., (POWER(venues_latitude - 11.000, 2) + POWER(venues_longitude - 10.000, 2)) AS distance" +
//		    	  		      "from tablename where venues_avaliable=true ";
//		       
//		    	if(StringUtils.isNotBlank(sportType) && sportType.length() < 64){
//		        	sqlTemplate += "and sport_catelog_names like ? ";
//		        }
//		        if(StringUtils.isNotBlank(city)){
//		        	sqlTemplate += "and city like ? ";		          
//		        }
//		       
//		        int size = words.size();
//		        for (int i = 0; i < size; i++){
//		        	sqlTemplate +="or venues_name like ? ";
//		        	sqlTemplate +="or venues_alias_name like ? ";
//		        	sqlTemplate +="or venues_address like ? ";
//		        	sqlTemplate +="or sport_catelog_names like ? ";
//		        }
//		        
//		        FreeSelectSqlBuilder<List<pojo>> builder = new FreeSelectSqlBuilder<>(dbCategory);
//				builder.setTemplate(sqlTemplate);
//		        
//		    	StatementParameters parameters = new StatementParameters();
//				int i = 1;
//				parameters.set(i++, "sport_catelog_names", Types.VARCHAR, sportType);
//				parameters.set(i++, "city", Types.VARCHAR, city);
//		        
//				 for (int j = 0; j < size; j++){
//					 String word = '%' + words.get(j) + '%';
//					 parameters.set(i++, "venues_name", Types.VARCHAR, word);
//				     parameters.set(i++, "venues_alias_name", Types.VARCHAR, word);
//				     parameters.set(i++, "venues_address", Types.VARCHAR, word);
//				     parameters.set(i++, "sport_catelog_names", Types.VARCHAR, word);
//				 }		       
//		        builder.mapWith(pojoRowMapper).atPage(current, pageSize);;
//				return queryDao.query(builder, parameters,null);
//
//		      } catch (SQLException e) {
//		        e.printStackTrace();
//		      }
//		      return new ArrayList<pojo>();
//		    } 

	public List<MysqlPersonTable> test_def_query_listByPage(List<Integer> Age, int pageNo, int pageSize) throws SQLException {
		return test_def_query_listByPage(Age, pageNo, pageSize, null);
	}

	/**
	 * 自定义，查询
	**/
	public List<MysqlPersonTable> test_def_query_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlPersonTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
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
	public List<MysqlPersonTable> test_def_queryPartialSet_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlPersonTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
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
	public List<MysqlPersonTable> test_def_queryPartialStrings_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlPersonTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
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

	public MysqlPersonTable testDefQueryListSingle(List<Integer> Age) throws SQLException {
		return test_def_query_single(Age, null);
	}
	/**
	 * 自定义，查询
	**/
	public MysqlPersonTable test_def_query_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<MysqlPersonTable> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireSingle().nullable();

        return (MysqlPersonTable)queryDao.query(builder, parameters, hints);
	}
	
	/**
	 * 自定义，查询
	**/
	public MysqlPersonTable test_def_queryPartialSet_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<MysqlPersonTable> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireSingle().nullable();

		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return (MysqlPersonTable)queryDao.query(builder, parameters, hints.partialQuery(columns));
	}
	
	/**
	 * 自定义，查询
	**/
	public MysqlPersonTable test_def_queryPartialStrings_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<MysqlPersonTable> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireSingle().nullable();
		return (MysqlPersonTable)queryDao.query(builder, parameters, hints.partialQuery("Age","Name"));
	}

	public MysqlPersonTable testDefQueryListFirst(List<Integer> param1) throws SQLException {
		return test_def_query_first(param1, null);
	}
	/**
	 * 自定义，查询
	**/
	public MysqlPersonTable test_def_query_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<MysqlPersonTable> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireFirst().nullable();
		
		return (MysqlPersonTable)queryDao.query(builder, parameters, hints.sortBy(new MysqlPersonTableComparator()));
	}
	
	/**
	 * 自定义，查询
	**/
//	public ignoreMissingFieldsAndAllowPartialTestOnMysql test_def_query_firstBySql(List<Integer> Age, DalHints hints) throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//
//
//		String sql="select * from person where Age in (?)";
//		StatementParameters parameters = new StatementParameters();
//		int i = 1;
//		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
//
//		System.out.println(queryDao.queryFirst(sql, parameters, hints, personShardColModShardByDBOnMysqlPojoRowMapper));
//		return queryDao.queryFirst(sql, parameters, hints, personShardColModShardByDBOnMysqlPojoRowMapper);
//
//	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public MysqlPersonTable test_def_queryPartialSet_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<MysqlPersonTable> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireFirst().nullable();
		
		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return (MysqlPersonTable)queryDao.query(builder, parameters, hints.partialQuery(columns).sortBy(new MysqlPersonTableComparator()));
	}
	
	/**
	 * 自定义，查询部分字段
	**/
	public MysqlPersonTable test_def_queryPartialStrings_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<MysqlPersonTable> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from person where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		builder.mapWith(personShardColModShardByDBOnMysqlPojoRowMapper).requireFirst().nullable();
		
//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		
		return (MysqlPersonTable)queryDao.query(builder, parameters, hints.partialQuery("Age","Name").sortBy(new MysqlPersonTableComparator()));
	}

	public int testDefUpdate (String Name, List<Integer> Age) throws SQLException {
		return test_def_update_in(Name,Age,null);
	}
	/**
	 * 自定义，更新
	**/
	public int test_def_update_in (String Name, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.setTemplate("update person set Name=? where Age in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return queryDao.update(builder, parameters, hints);
	}

	public int testDefTruncate () throws SQLException {
		return test_def_truncate(null);
	}
	/**
	 * 自定义，删除
	**/
	public int test_def_truncate (DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.setTemplate("truncate person");
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}
	
	/**
	 * 自定义 ，查询，list<string>
	**/
	public List<MysqlPersonTable> test_def_query_in_string(List<String> name, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlPersonTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
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
	    builder.addQuery(sqlList, new StatementParameters(),personShardColModShardByDBOnMysqlPojoRowMapper,new DalListMerger<MysqlPersonTable>());//ListMerger
	    builder.addQuery(sqlList, new StatementParameters(), MysqlPersonTable.class);
	    builder.addQuery(sqlList, new StatementParameters(), MysqlPersonTable.class, new DalListMerger<MysqlPersonTable>());
//	    builder.addQuery(sqlList, new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new DalRangedResultMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>(1,2));
//	    builder.addQuery(sqlList, new StatementParameters(), new DalRowMapperExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,1,1), new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlList, new StatementParameters(), new DalSingleResultExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,false), new DalFirstResultMerger<MysqlPersonTable>());
	    builder.addQuery(sqlList, new StatementParameters(), new TestDalRowCallback());
	    builder.addQuery(sqlList, new StatementParameters(), MysqlPersonTable.class, new MysqlPersonTableComparator());
	    builder.addQuery(sqlList, new StatementParameters(), personShardColModShardByDBOnMysqlPojoRowMapper, new MysqlPersonTableComparator());
	   
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
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1),personShardColModShardByDBOnMysqlPojoRowMapper,new DalListMerger<MysqlPersonTable>());//ListMerger
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), MysqlPersonTable.class);
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), MysqlPersonTable.class, new DalListMerger<MysqlPersonTable>());
//	    builder.addQuery(sqlList, new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new DalRangedResultMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>(1,2));
//	    builder.addQuery(sqlList, new StatementParameters(), new DalRowMapperExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,1,1), new DalListMerger<ignoreMissingFieldsAndAllowPartialTestOnMysql>());
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), new DalSingleResultExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,false), new DalFirstResultMerger<MysqlPersonTable>());
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), new TestDalRowCallback());
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), MysqlPersonTable.class, new MysqlPersonTableComparator());
	    builder.addQuery(sqlFirst, new StatementParameters().setSensitive(1, "ID", Types.INTEGER, 1), personShardColModShardByDBOnMysqlPojoRowMapper, new MysqlPersonTableComparator());
	   
	    
//	    builder.addQuery(sqlObject, parameters, Integer.class);
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), personShardColModShardByDBOnMysqlPojoRowMapper);	    
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1),personShardColModShardByDBOnMysqlPojoRowMapper,new DalListMerger<MysqlPersonTable>());
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), MysqlPersonTable.class);
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), MysqlPersonTable.class,new DalListMerger<MysqlPersonTable>());
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), new DalSingleResultExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,false), new DalFirstResultMerger<MysqlPersonTable>());
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), new TestDalRowCallback());
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), MysqlPersonTable.class, new MysqlPersonTableComparator());
	    builder.addQuery(sqlObject, new StatementParameters().set(1, "ID", Types.INTEGER, 1), personShardColModShardByDBOnMysqlPojoRowMapper, new MysqlPersonTableComparator());
//	    builder.addQuery(sqlFieldList, new StatementParameters(),String.class, new DalListMerger<String>());//merger
//	    builder.addQuery(sqlListCount,new StatementParameters(), ignoreMissingFieldsAndAllowPartialTestOnMysql.class, new MysqlPersonTableComparator());//sorter
//	    builder.addQuery(sqlListCount, new StatementParameters(),Integer.class, new DalListMerger<Integer>());//merger
//	    builder.addQuery(sqlObject, parameters,Integer.class, new InteregrComparator());//soter
//	    builder.addQuery(sqlNoResult, new StatementParameters(),new TestDalRowCallback3());//callback
	    StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, new ArrayList<Integer>(){{add(20); add(21);add(23);}});
		
	    builder.addQuery(sqlInParam, parameters, personShardColModShardByDBOnMysqlPojoRowMapper);	    
	    builder.addQuery(sqlInParam, parameters,personShardColModShardByDBOnMysqlPojoRowMapper,new DalListMerger<MysqlPersonTable>());
	    builder.addQuery(sqlInParam, parameters, MysqlPersonTable.class);
	    builder.addQuery(sqlInParam, parameters, MysqlPersonTable.class,new DalListMerger<MysqlPersonTable>());
	    builder.addQuery(sqlInParam, parameters, new DalSingleResultExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,false), new DalFirstResultMerger<MysqlPersonTable>());
	    builder.addQuery(sqlInParam, parameters, new TestDalRowCallback());
	    builder.addQuery(sqlInParam, parameters, MysqlPersonTable.class, new MysqlPersonTableComparator());
	    builder.addQuery(sqlInParam, parameters, personShardColModShardByDBOnMysqlPojoRowMapper, new MysqlPersonTableComparator());

	    builder.addQuery(sqlNoResult, new StatementParameters(), personShardColModShardByDBOnMysqlPojoRowMapper);	    
	    builder.addQuery(sqlNoResult, new StatementParameters(),personShardColModShardByDBOnMysqlPojoRowMapper,new DalListMerger<MysqlPersonTable>());
	    builder.addQuery(sqlNoResult, new StatementParameters(), MysqlPersonTable.class);
	    builder.addQuery(sqlNoResult, new StatementParameters(), MysqlPersonTable.class,new DalListMerger<MysqlPersonTable>());
	    builder.addQuery(sqlNoResult, new StatementParameters(), new DalSingleResultExtractor(personShardColModShardByDBOnMysqlPojoRowMapper,false), new DalFirstResultMerger<MysqlPersonTable>());
	    builder.addQuery(sqlNoResult, new StatementParameters(), new TestDalRowCallback());
	    builder.addQuery(sqlNoResult, new StatementParameters(), MysqlPersonTable.class, new MysqlPersonTableComparator());
	    builder.addQuery(sqlNoResult, new StatementParameters(), personShardColModShardByDBOnMysqlPojoRowMapper, new MysqlPersonTableComparator());
	    		
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
	
	public List queryTime(DalHints hints) throws SQLException {
		MultipleSqlBuilder builder = new MultipleSqlBuilder();
	    builder.addQuery("select now() as time", new StatementParameters(),Timestamp.class);
	    builder.addQuery("select now() as time", new StatementParameters(),Timestamp.class);
//	    builder.addQuery(sqlCount, new StatementParameters(), Long.class);
//	    builder.addQuery(sqlCount, new StatementParameters(), Long.class, new DalListMerger<Long>());
//	    builder.addQuery(sqlCount, new StatementParameters(), new DalRowMapperExtractor(new IntegerRowMapper()), new DalListMerger<Integer>());
//	    builder.addQuery(sqlCount, new StatementParameters(), new TestDalRowCallback());
//	    builder.addQuery(sqlCount, new StatementParameters(), Long.class, new LongComparator());
//	    builder.addQuery(sqlCount, new StatementParameters(), new IntegerRowMapper(),new IntegerComparator());
//	    builder.addQuery(sqlCount, new StatementParameters(), Integer.class, new IntegerComparator());
	    return queryDao.query(builder, hints);
	}

	public List<Map<String,Object>> testDalColumnMapRowMapper(DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		String sql="SELECT Age , Name FROM person";
		return queryDao.query(sql, new StatementParameters(), hints, new DalColumnMapRowMapper());
	}

	public List<Map<String,Object>> testDalColumnMapRowMapperWithAlias(DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		String sql="SELECT Age as age, Name name FROM person";
		return queryDao.query(sql, new StatementParameters(), hints, new DalColumnMapRowMapper());
	}
}