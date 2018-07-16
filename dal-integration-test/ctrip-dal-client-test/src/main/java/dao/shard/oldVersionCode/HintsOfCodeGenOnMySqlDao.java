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
import java.util.List;
import java.util.Set;

public class HintsOfCodeGenOnMySqlDao {
    private static final String DATA_BASE = "ShardColModShardByDBOnMysql";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from person";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM person";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM person LIMIT ?, ?";
	private DalParser<MysqlPersonTable> parser = null;
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalTableDao<MysqlPersonTable> client;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;
	private DalRowMapper<MysqlPersonTable> personGenSimpleShardByDbOnMySqlLljRowMapper = null;
	
	public HintsOfCodeGenOnMySqlDao() throws SQLException {
		parser = new DalDefaultJpaParser<>(MysqlPersonTable.class,DATA_BASE);
		this.client = new DalTableDao<MysqlPersonTable>(parser);
		dbCategory = this.client.getDatabaseCategory();
		this.personGenSimpleShardByDbOnMySqlLljRowMapper = new DalDefaultJpaMapper(MysqlPersonTable.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	private class MysqlPersonTableComparator implements Comparator<MysqlPersonTable> {
		@Override
		public int compare(MysqlPersonTable o1, MysqlPersonTable o2) {
			return new Integer(o1.getName().compareTo(o2.getName()));
		}
	}

//	private class StringComparator implements Comparator<String>{
//		@Override
//		public int compare(String o1, String o2) {
//			return new Integer( o2.compareTo(o1));
//		}
//	}
	
	/**
	 * Query PersonGen by the specified ID
	 * The ID must be a number
	**/
	public MysqlPersonTable queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
    /**
	 * Query PersonGen by PersonGen instance which the primary key is set
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
	 * Query PersonGen with paging function
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
	 * ��������ѯ
	**/
	public List<MysqlPersonTable> test_build_query_allShard_async(Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.asyncExecution();
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.equal("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints.sortBy(new MysqlPersonTableComparator()), parser);
	}
	/**
	 * ��������ѯ
	**/
	public List<MysqlPersonTable> test_build_query_shards_callback(Integer Age, DalHints hints, Set<String> shards, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.callbackWith(callback);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.equal("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints.sortBy(new MysqlPersonTableComparator()), parser);
	}
	/**
	 * 构建，更新
	**/
	public int test_build_update_shards_callback (String Name, Integer Age, DalHints hints, Set<String> shards, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.callbackWith(callback);
		UpdateSqlBuilder builder = new UpdateSqlBuilder("person", dbCategory);
		builder.update("Name", Name, Types.VARCHAR);
		builder.equal("Age", Age, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	/**
	 * ����������
	**/
	public int test_build_update_allShard_async (String Name, Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.asyncExecution();
		UpdateSqlBuilder builder = new UpdateSqlBuilder("person", dbCategory);
		builder.update("Name", Name, Types.VARCHAR);
		builder.equal("Age", Age, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	/**
	 * ����������
	**/
	public int test_build_insert_shards_callback (String Name, Integer Age, DalHints hints, Set<String> shards, DalResultCallback callback) throws SQLException {
		String sql = SQLParser.parse("INSERT INTO person (`Name`,`Age`) VALUES ( ? , ? )");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.callbackWith(callback);
		int i = 1;
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		parameters.set(i++, "Age", Types.INTEGER, Age);
		return client.update(sql, parameters, hints);
	}
	/**
	 * ����������
	**/
	public int test_build_insert_allShard_async (String Name, Integer Age, DalHints hints) throws SQLException {
		String sql = SQLParser.parse("INSERT INTO person (`Name`,`Age`) VALUES ( ? , ? )");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.asyncExecution();
		int i = 1;
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		parameters.set(i++, "Age", Types.INTEGER, Age);
		return client.update(sql, parameters, hints);
	}
	/**
	 * ������ɾ��
	**/
	public int test_build_delete_shards_callback (Integer Age, DalHints hints, Set<String> shards, DalResultCallback callback) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.callbackWith(callback);
		DeleteSqlBuilder builder = new DeleteSqlBuilder("person", dbCategory);
		builder.equal("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	/**
	 * ������ɾ��
	**/
	public int test_build_delete_allShard_async (Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.asyncExecution();
		DeleteSqlBuilder builder = new DeleteSqlBuilder("person", dbCategory);
		builder.equal("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	
	/**
	 * 自定义，查询
	**/
	public List<MysqlPersonTable> test_def_query_shards_callback(String Name, DalHints hints, Set<String> shards, DalResultCallback callback) throws SQLException {
		String sql = "SELECT * FROM person WHERE `Name` like ?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		hints.inShards(shards);
		hints.callbackWith(callback);
		int i = 1;
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints, personGenSimpleShardByDbOnMySqlLljRowMapper);
	}
	/**
	 * 自定义，查询
	**/
	public List<MysqlPersonTable> test_def_query_allShard_async(String Name, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM person WHERE `Name` like ?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		hints.inAllShards();
		hints.asyncExecution();
		int i = 1;
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		return (List<MysqlPersonTable>)queryDao.query(sql, parameters, hints, personGenSimpleShardByDbOnMySqlLljRowMapper);
	}
	/**
	 * 自定义，增删改
	**/
	public int test_def_update (DalHints hints) throws SQLException {
		String sql = SQLParser.parse("truncate Person");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		return baseClient.update(sql, parameters, hints);
	}

}