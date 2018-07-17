package dao.shard.oldVersionCode;


import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
import com.ctrip.platform.dal.dao.helper.SQLParser;
import com.ctrip.platform.dal.dao.sqlbuilder.DeleteSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.UpdateSqlBuilder;
import entity.MysqlPersonTable;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShardColModShardByDBOnMysqlGenDao {
    private static final String DATA_BASE = "ShardColModShardByDBOnMysql";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from person";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM person";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM person LIMIT ?, ?";
	private DalParser<MysqlPersonTable> parser = null;
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalTableDao<MysqlPersonTable> client;
	private DalRowMapper<MysqlPersonTable> personPojoRowMapper = null;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;
	
	public ShardColModShardByDBOnMysqlGenDao() throws SQLException {
		parser = new DalDefaultJpaParser<>(MysqlPersonTable.class,DATA_BASE);
		this.personPojoRowMapper = new DalDefaultJpaMapper(MysqlPersonTable.class);
		this.client = new DalTableDao<MysqlPersonTable>(parser);
		dbCategory = this.client.getDatabaseCategory();
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	private class MysqlPersonTableComparator implements Comparator<MysqlPersonTable> {
		@Override
		public int compare(MysqlPersonTable o1, MysqlPersonTable o2) {
			return new Integer(o1.getAge().compareTo(o2.getAge()));
		}
	}

	private class StringComparator implements Comparator<String>{
		@Override
		public int compare(String o1, String o2) {
			return new Integer( o1.compareTo(o2));
		}
	}
	
	/**
	 * Query PersonSimpleShardByDBOnMysqlGen by the specified ID
	 * The ID must be a number
	**/
	public MysqlPersonTable queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
    /**
	 * Query PersonSimpleShardByDBOnMysqlGen by PersonSimpleShardByDBOnMysqlGen instance which the primary key is set
	**/
	public MysqlPersonTable queryByPk(MysqlPersonTable pk, DalHints hints)
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
	 * Query PersonSimpleShardByDBOnMysqlGen with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<MysqlPersonTable> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
        StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String sql = PAGE_MYSQL_PATTERN;
		parameters.set(1, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(2, Types.INTEGER, pageSize);
		return queryDao.query(sql, parameters, hints, parser);
	}
	/**
	 * Get all records in the whole table
	**/
	public List<MysqlPersonTable> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<MysqlPersonTable> result = null;
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
	public int insert(DalHints hints, MysqlPersonTable daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, MysqlPersonTable daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int[] batchInsert(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int combinedInsert(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int delete(DalHints hints, MysqlPersonTable daoPojo) throws SQLException {
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
	public int[] delete(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int[] batchDelete(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int update(DalHints hints, MysqlPersonTable daoPojo) throws SQLException {
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
	public int[] update(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
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
	public int[] batchUpdate(DalHints hints, List<MysqlPersonTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}
	/**
	 * SimpleShardByDBOnMysql，构建，查询，field，list
	**/
	public List<String> test_build_query_field(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
        String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints.sortBy(new StringComparator()), String.class);
	}
	/**
	 * SimpleShardByDBOnMysql，构建，查询，field，listByPage
	**/
	public List<String> test_build_query_field_byPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, true);
		builder.select("Name");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
        String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		int index =  builder.getStatementParameterIndex();
		parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(index++, Types.INTEGER, pageSize);
		return queryDao.query(sql, parameters, hints, String.class);
	}
	/**
	 * SimpleShardByDBOnMysql，构建，查询，field，single
	**/
	public String test_build_query_field_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Name");
		builder.inNullable("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints, String.class);
	}
	/**
	 * SimpleShardByDBOnMysql，构建，查询，field，first
	**/
	public String test_build_query_field_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Name");
		builder.in("Age", Age, Types.INTEGER, false);
	    String sql = builder.buildFirst();
		return queryDao.queryFirstNullable(sql, builder.buildParameters(), hints.sortBy(new StringComparator()), String.class);
	}
	
	/**
	 * 构建，查询
	**/
	public List<MysqlPersonTable> test_ClientQueryFrom_list(List<Integer> Age, DalHints hints,int start,int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

		return client.queryFrom("Age in ?", parameters, hints.sortBy(new MysqlPersonTableComparator()), start, count);
	}
	
	/**
	 * 构建，查询
	**/
	public List<MysqlPersonTable> test_ClientQueryFromPartialFieldsSet_list(List<Integer> Age, DalHints hints,int start,int count) throws SQLException {
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
	public List<MysqlPersonTable> test_ClientQueryFromPartialFieldsStrings_list(List<Integer> Age, DalHints hints,int start,int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		
		return client.queryFrom("Age in ?", parameters, hints.partialQuery("Age","Name").sortBy(new MysqlPersonTableComparator()), start, count);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，查询部分字段，List
	**/
	public List<MysqlPersonTable> test_build_queryFrom(List<Integer> Age, DalHints hints,int start,int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
//		return queryDao.query(sql, parameters, hints.partialQuery("Name","Age"), parser);
		return queryDao.queryFrom(sql, parameters, hints.sortBy(new MysqlPersonTableComparator()), personPojoRowMapper, start, count);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，查询部分字段，List
	**/
	public List<MysqlPersonTable> test_build_queryFromPartialFieldsSet(List<Integer> Age, DalHints hints,int start,int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.selectAll();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
//		return queryDao.query(sql, parameters, hints.partialQuery("Name","Age"), parser);
		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		return queryDao.queryFrom(sql, parameters, hints.partialQuery(columns).sortBy(new MysqlPersonTableComparator()), personPojoRowMapper, start, count);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，查询部分字段，List
	**/
	public List<MysqlPersonTable> test_build_queryFromPartialFieldsStrings(List<Integer> Age, DalHints hints,int start,int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.selectAll();
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
//		return queryDao.query(sql, parameters, hints.partialQuery("Name","Age"), parser);
//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		return queryDao.queryFrom(sql, parameters, hints.partialQuery("Name","Age").sortBy(new MysqlPersonTableComparator()), personPojoRowMapper, start, count);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，查询部分字段，List
	**/
	public List<MysqlPersonTable> test_build_queryPartialFields(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints.partialQuery("Name","Age").sortBy(new MysqlPersonTableComparator()), parser);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，查询，List
	**/
	public List<MysqlPersonTable> test_build_query(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints.sortBy(new MysqlPersonTableComparator()), parser);
	}


	/**
	 * SimpleShardByDBOnMysql，构建，查询，ListByPage
	**/
	public List<MysqlPersonTable> test_build_query_byPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, true);
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("Age", true);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		int index =  builder.getStatementParameterIndex();
		parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(index++, Types.INTEGER, pageSize);
		return queryDao.query(sql, parameters, hints, parser);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，查询部分字段，ListByPage
	**/
	public List<MysqlPersonTable> test_build_queryPartialFields_byPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, true);
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("Age", true);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		int index =  builder.getStatementParameterIndex();
		parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(index++, Types.INTEGER, pageSize);
		return queryDao.query(sql, parameters, hints.partialQuery("Name","Age"), parser);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，查询，single
	**/
	public MysqlPersonTable test_build_query_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.in("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints, parser);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，查询部分字段，single
	**/
	public MysqlPersonTable test_build_queryPartialFields_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.in("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints.partialQuery("Name","Age"), parser);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，查询，first
	**/
	public MysqlPersonTable test_build_query_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.selectAll();
//		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
	    String sql = builder.buildFirst();
		return queryDao.queryFirstNullable(sql, builder.buildParameters(), hints.sortBy(new MysqlPersonTableComparator()), parser);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，查询部分字段，first
	**/
	public MysqlPersonTable test_build_queryPartialFields_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.selectAll();
//		builder.select("Name","Age");
		builder.inNullable("Age", Age, Types.INTEGER, false);
	    String sql = builder.buildFirst();
		return queryDao.queryFirstNullable(sql, builder.buildParameters(), hints.partialQuery("Name","Age").sortBy(new MysqlPersonTableComparator()), parser);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建，更新
	**/
	public int test_build_update (String Name, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		UpdateSqlBuilder builder = new UpdateSqlBuilder("person", dbCategory);
		builder.update("Name", Name, Types.VARCHAR);
		builder.in("Age", Age, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	
	/**
	 * 构建，更新
	**/
	public int test_build_update_new(String Name, List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
		builder.update("Name", Name, Types.VARCHAR);
		builder.in("Age", Age, Types.INTEGER, false);

		return client.update(builder, hints);
	}
	/**
	 * SimpleShardByDBOnMysql，构建，更新
	**/
	public int test_update_part(String Name, Integer Age, Integer ID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
		builder.update("Name", Name, Types.VARCHAR);
		builder.update("Age", Age, Types.INTEGER);
		builder.greaterThan("ID", ID, Types.INTEGER, false);

		return client.update(builder, hints);
	}
	/**
	 * SimpleShardByDBOnMysql，构建，新增
	**/
	public int test_build_insert (String Name, Integer Age, DalHints hints) throws SQLException {
		String sql = SQLParser.parse("INSERT INTO person (`Name`,`Age`) VALUES ( ? , ? )");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		parameters.set(i++, "Age", Types.INTEGER, Age);
		return client.update(sql, parameters, hints);
	}
	/**
	 * SimpleShardByDBOnMysql，构建，删除
	**/
	public int test_build_delete (List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		DeleteSqlBuilder builder = new DeleteSqlBuilder("person", dbCategory);
		builder.inNullable("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}

	/**
	 * SimpleShardByDBOnMysql，自定义sql，查询，field
	**/
	public List<String> test_def_query_field(List<Integer> Age, DalHints hints) throws SQLException {	
		String sql = "select name from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		return queryDao.query(sql, parameters, hints.sortBy(new StringComparator()), String.class);
	}
	/**
	 * SimpleShardByDBOnMysql，自定义sql，查询，field,bypage
	**/
	public List<String> test_def_query_field_byPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {	
		String sql = "SELECT name FROM person WHERE Age IN (?) limit ?, ?";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(i++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(i++, Types.INTEGER, pageSize);
		return queryDao.query(sql, parameters, hints, String.class);
	}
	/**
	 * SimpleShardByDBOnMysql，自定义sql，查询，field,single
	**/
	public String test_def_query_field_single(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select name from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return queryDao.queryForObjectNullable(sql, parameters, hints, String.class);
	}
	/**
	 * SimpleShardByDBOnMysql，自定义sql，查询，field,first
	**/
	public String test_def_query_field_first(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select name from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "Age", Types.INTEGER, Age);
		return queryDao.queryFirstNullable(sql, parameters, hints.sortBy(new StringComparator()), String.class);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义，查询
	**/
	public List<MysqlPersonTable> test_def_queryFrom_list(List<Integer> Age, DalHints hints,int start,int count) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
//		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints, personPojoRowMapper);
		return queryDao.queryFrom(sql, parameters, hints.sortBy(new MysqlPersonTableComparator()), personPojoRowMapper, start, count);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义，查询
	**/
	public List<MysqlPersonTable> test_def_queryFromPartialFieldsSet_list(List<Integer> Age, DalHints hints,int start,int count) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
//		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints, personPojoRowMapper);
		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		return queryDao.queryFrom(sql, parameters, hints.partialQuery(columns).sortBy(new MysqlPersonTableComparator()), personPojoRowMapper, start, count);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义，查询
	**/
	public List<MysqlPersonTable> test_def_queryFromPartialFieldsStrings_list(List<Integer> Age, DalHints hints,int start,int count) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
//		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints, personPojoRowMapper);
//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		return queryDao.queryFrom(sql, parameters, hints.partialQuery("Age","Name").sortBy(new MysqlPersonTableComparator()), personPojoRowMapper, start, count);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义，查询
	**/
	public List<MysqlPersonTable> test_def_query_list(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints.sortBy(new MysqlPersonTableComparator()), personPojoRowMapper);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义，查询部分字段
	**/
	public List<MysqlPersonTable> test_def_queryPartialSet_list(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints.partialQuery(columns).sortBy(new MysqlPersonTableComparator()), personPojoRowMapper);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义，查询部分字段
	**/
	public List<MysqlPersonTable> test_def_queryPartialStrings_list(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
//		Set<String> columns = new HashSet<>();
//		columns.add("Age");
//		columns.add("Name");
		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints.partialQuery("Age","Name").sortBy(new MysqlPersonTableComparator()), personPojoRowMapper);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义sql，查询，list，byPage
	**/
	public List<MysqlPersonTable> test_def_query_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM person WHERE Age IN (?) limit ?, ?";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(i++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(i++, Types.INTEGER, pageSize);
		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints, personPojoRowMapper);
	}
	
	public List<MysqlPersonTable> test_def_queryPartialSet_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM person WHERE Age IN (?) limit ?, ?";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(i++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(i++, Types.INTEGER, pageSize);
		
		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints.partialQuery(columns), personPojoRowMapper);
	}
	
	public List<MysqlPersonTable> test_def_queryPartialStrings_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM person WHERE Age IN (?) limit ?, ?";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(i++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(i++, Types.INTEGER, pageSize);
		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints.partialQuery("Age","Name"), personPojoRowMapper);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义sql，查询，list，single
	**/
	public MysqlPersonTable test_def_query_list_single(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return (MysqlPersonTable)queryDao.queryForObjectNullable(sql, parameters, hints, personPojoRowMapper);
	}
	
	public MysqlPersonTable test_def_queryPartialSet_list_single(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		
		return (MysqlPersonTable)queryDao.queryForObjectNullable(sql, parameters, hints.partialQuery(columns), personPojoRowMapper);
	}
	
	public MysqlPersonTable test_def_queryPartialStrings_list_single(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return (MysqlPersonTable)queryDao.queryForObjectNullable(sql, parameters, hints.partialQuery("Age","Name"), personPojoRowMapper);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义sql，查询，list，first
	**/
	public MysqlPersonTable test_def_query_list_first(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return (MysqlPersonTable)queryDao.queryFirstNullable(sql, parameters, hints.sortBy(new MysqlPersonTableComparator()), personPojoRowMapper);
	}
	
	public MysqlPersonTable test_def_queryPartialSet_list_first(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		Set<String> columns = new HashSet<>();
		columns.add("Age");
		columns.add("Name");
		return (MysqlPersonTable)queryDao.queryFirstNullable(sql, parameters, hints.partialQuery(columns).sortBy(new MysqlPersonTableComparator()), personPojoRowMapper);
	}
	
	public MysqlPersonTable test_def_queryPartialStrings_list_first(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return (MysqlPersonTable)queryDao.queryFirstNullable(sql, parameters, hints.partialQuery("Age","Name").sortBy(new MysqlPersonTableComparator()), personPojoRowMapper);
	}
	/**
	 * SimpleShardByDBOnMysql，自定义sql，删除
	**/
	public int test_def_update (DalHints hints) throws SQLException {
		String sql = SQLParser.parse("truncate Person");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		return baseClient.update(sql, parameters, hints);
	}
	
	/**
	 * 自定义，删除
	**/
	public int test_def_delete (List<Integer> Age, DalHints hints) throws SQLException {
		String sql = SQLParser.parse("delete from person where Age in (?)",Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return baseClient.update(sql, parameters, hints);
	}
	
	/**
	 * 自定义，删除
	**/
	public int test_def_delete_equal (Integer Age, DalHints hints) throws SQLException {
		String sql = SQLParser.parse("delete from Person where Age=?");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, "Age", Types.INTEGER, Age);
		return baseClient.update(sql, parameters, hints);
	}
	
	/**
	 * 自定义，更新
	**/
	public int test_def_update_in (String Name, List<Integer> Age, DalHints hints) throws SQLException {
		String sql = SQLParser.parse("update person set Name=? where Age in (?)",Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return baseClient.update(sql, parameters, hints);
	}
//	public List<PersonMysqlPersonTable> test_build_query_equal(Integer Age, DalHints hints) throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
//		builder.select("Birth","Name","Age","ID");
//		builder.equal("Age", Age, Types.INTEGER, false);
//	    String sql = builder.build();
//		StatementParameters parameters = builder.buildParameters();
//		return queryDao.query(sql, parameters, hints, parser);
//	}
	/**
	 * 构建，查询，equal
	**/
	public List<MysqlPersonTable> test_build_query_equal(Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.equal("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}
	/**
	 * 构建，更新，equal
	**/
	public int test_build_update_equal (String Name, Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		UpdateSqlBuilder builder = new UpdateSqlBuilder("person", dbCategory);
		builder.update("Name", Name, Types.VARCHAR);
		builder.equal("Age", Age, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	/**
	 * 构建，新增，equal
	**/
	public int test_build_insert_equal (String Name, Integer Age, DalHints hints) throws SQLException {
		String sql = SQLParser.parse("INSERT INTO person (`Name`,`Age`) VALUES ( ? , ? )");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		parameters.set(i++, "Age", Types.INTEGER, Age);
		return client.update(sql, parameters, hints);
	}
	/**
	 * 构建，删除，equal
	**/
	public int test_build_delete_equal (Integer param1, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		DeleteSqlBuilder builder = new DeleteSqlBuilder("person", dbCategory);
		builder.equal("Age", param1, Types.INTEGER, false);
	    String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}

	/**
	 * test
	**/
	public List<MysqlPersonTable> test(String param1, Integer param2, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.equal("Name", param1, Types.VARCHAR, false);
		builder.and();
		builder.equal("Age", param2, Types.INTEGER, false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}
	
	/**
	 * test
	**/
	public List<MysqlPersonTable> test2(Integer param1, String param2, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.equal("Age", param1, Types.INTEGER, false);
		builder.and();
		builder.equal("Name", param2, Types.VARCHAR, false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}
}