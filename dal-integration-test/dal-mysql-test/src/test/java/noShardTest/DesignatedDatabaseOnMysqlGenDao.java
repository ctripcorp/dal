package noShardTest;


import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class DesignatedDatabaseOnMysqlGenDao {
    private static final String DATA_BASE = "testDesignatedDatabaseOnMysql";
//    private DalParser<Integer> parser1 = null;	
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from person";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM person";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM person LIMIT ?, ?";
	private DalParser<DesignatedDatabaseOnMysqlGen> parser = null;	
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalTableDao<DesignatedDatabaseOnMysqlGen> client;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;
	private DalRowMapper<DesignatedDatabaseOnMysqlGen> personSimpleShardByDBOnMysqlGenRowMapper = null;
	
	public DesignatedDatabaseOnMysqlGenDao() throws SQLException {
		parser = new DalDefaultJpaParser<>(DesignatedDatabaseOnMysqlGen.class);
		this.client = new DalTableDao<DesignatedDatabaseOnMysqlGen>(parser);
		this.personSimpleShardByDBOnMysqlGenRowMapper = new DalDefaultJpaMapper(DesignatedDatabaseOnMysqlGen.class);
		dbCategory = this.client.getDatabaseCategory();
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query PersonSimpleShardByDBOnMysqlGen by the specified ID
	 * The ID must be a number
	**/
	public DesignatedDatabaseOnMysqlGen queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
    /**
	 * Query PersonSimpleShardByDBOnMysqlGen by PersonSimpleShardByDBOnMysqlGen instance which the primary key is set
	**/
	public DesignatedDatabaseOnMysqlGen queryByPk(DesignatedDatabaseOnMysqlGen pk, DalHints hints)
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
	public List<DesignatedDatabaseOnMysqlGen> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
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
	public List<DesignatedDatabaseOnMysqlGen> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<DesignatedDatabaseOnMysqlGen> result = null;
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
	public int insert(DalHints hints, DesignatedDatabaseOnMysqlGen daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, List<DesignatedDatabaseOnMysqlGen> daoPojos) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, DesignatedDatabaseOnMysqlGen daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<DesignatedDatabaseOnMysqlGen> daoPojos) throws SQLException {
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
	public int[] batchInsert(DalHints hints, List<DesignatedDatabaseOnMysqlGen> daoPojos) throws SQLException {
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
	public int combinedInsert(DalHints hints, List<DesignatedDatabaseOnMysqlGen> daoPojos) throws SQLException {
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
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<DesignatedDatabaseOnMysqlGen> daoPojos) throws SQLException {
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
	public int delete(DalHints hints, DesignatedDatabaseOnMysqlGen daoPojo) throws SQLException {
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
	public int[] delete(DalHints hints, List<DesignatedDatabaseOnMysqlGen> daoPojos) throws SQLException {
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
	public int[] batchDelete(DalHints hints, List<DesignatedDatabaseOnMysqlGen> daoPojos) throws SQLException {
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
	public int update(DalHints hints, DesignatedDatabaseOnMysqlGen daoPojo) throws SQLException {
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
	public int[] update(DalHints hints, List<DesignatedDatabaseOnMysqlGen> daoPojos) throws SQLException {
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
	public int[] batchUpdate(DalHints hints, List<DesignatedDatabaseOnMysqlGen> daoPojos) throws SQLException {
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
		return queryDao.query(sql, parameters, hints, String.class);
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
		return queryDao.queryFirstNullable(sql, builder.buildParameters(), hints, String.class);
	}
	/**
	 * SimpleShardByDBOnMysql，构建，查询，List
	**/
	public List<DesignatedDatabaseOnMysqlGen> test_build_query(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}
	/**
	 * SimpleShardByDBOnMysql，构建，查询，ListByPage
	**/
	public List<DesignatedDatabaseOnMysqlGen> test_build_query_byPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
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
	 * SimpleShardByDBOnMysql，构建，查询，single
	**/
	public DesignatedDatabaseOnMysqlGen test_build_query_single(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.in("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints, parser);
	}
	/**
	 * SimpleShardByDBOnMysql，构建，查询，first
	**/
	public DesignatedDatabaseOnMysqlGen test_build_query_first(List<Integer> Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.inNullable("Age", Age, Types.INTEGER, false);
	    String sql = builder.buildFirst();
		return queryDao.queryFirstNullable(sql, builder.buildParameters(), hints, parser);
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
	 * SimpleShardByDBOnMysql，自定义，查询
	**/
	public List<DesignatedDatabaseOnMysqlGen> test_def_query_list(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return (List<DesignatedDatabaseOnMysqlGen>)queryDao.query(sql, parameters, hints, personSimpleShardByDBOnMysqlGenRowMapper);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义sql，查询，list，byPage
	**/
	public List<DesignatedDatabaseOnMysqlGen> test_def_query_listByPage(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM person WHERE Age IN (?) limit ?, ?";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		parameters.set(i++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(i++, Types.INTEGER, pageSize);
		return (List<DesignatedDatabaseOnMysqlGen>)queryDao.query(sql, parameters, hints, personSimpleShardByDBOnMysqlGenRowMapper);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义sql，查询，list，single
	**/
	public DesignatedDatabaseOnMysqlGen test_def_query_list_single(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return (DesignatedDatabaseOnMysqlGen)queryDao.queryForObjectNullable(sql, parameters, hints, personSimpleShardByDBOnMysqlGenRowMapper);
	}
	
	/**
	 * SimpleShardByDBOnMysql，自定义sql，查询，list，first
	**/
	public DesignatedDatabaseOnMysqlGen test_def_query_list_first(List<Integer> Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age in (?)";
		sql = SQLParser.parse(sql, Age);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);
		return (DesignatedDatabaseOnMysqlGen)queryDao.queryFirstNullable(sql, parameters, hints, personSimpleShardByDBOnMysqlGenRowMapper);
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
		return queryDao.query(sql, parameters, hints, String.class);
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
		return queryDao.queryFirstNullable(sql, parameters, hints, String.class);
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
	 * SimpleShardByDBOnMysql，构建查询，
	**/
	public List<DesignatedDatabaseOnMysqlGen> test_build_query_equal(Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.equal("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建查询,分页
	**/
	public List<DesignatedDatabaseOnMysqlGen> test_build_query_page(Integer Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, true);
		builder.select("Birth","Name","Age","ID");
		builder.greaterThan("Age", Age, Types.INTEGER, false);
		builder.orderBy("ID", true);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		int index =  builder.getStatementParameterIndex();
		parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(index++, Types.INTEGER, pageSize);
		return queryDao.query(sql, parameters, hints, parser);
	}
	
//	/**
//	 * SimpleShardByDBOnMysql，构建查询，nullable
//	**/
//	public Integer test_build_query_nullable(Integer Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, true);
//		builder.select("count(*) as Age");
//		builder.greaterThan("Age", Age, Types.INTEGER, false);
//		builder.orderBy("ID", true);
//	    String sql = builder.build();
//		StatementParameters parameters = builder.buildParameters();
//		int index =  builder.getStatementParameterIndex();
//		parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize);
//		parameters.set(index++, Types.INTEGER, pageSize);
//		return queryDao.queryFirst(sql, parameters, hints, parser1);
//	}
	
	/**
	 * SimpleShardByDBOnMysql,构建更新，equal
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
	 * SimpleShardByDBOnMysql,构建新增，equal
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
	 * SimpleShardByDBOnMysql，构建查询,single,greaterThan
	**/
	public DesignatedDatabaseOnMysqlGen test_build_query_single_greaterThan(Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.greaterThan("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints, parser);
	}
	
	/**
	 * SimpleShardByDBOnMysql，构建查询,first,greaterThan
	**/
	public DesignatedDatabaseOnMysqlGen test_build_query_first_greaterThan(Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, false);
		builder.select("Birth","Name","Age","ID");
		builder.greaterThan("Age", Age, Types.INTEGER, false);
	    String sql = builder.buildFirst();
		return queryDao.queryFirstNullable(sql, builder.buildParameters(), hints, parser);
	}
	
	/**
	 * SimpleShardByDBOnMysql,构建删除，equal
	**/
	public int test_build_delete_equal (Integer Age, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		DeleteSqlBuilder builder = new DeleteSqlBuilder("person", dbCategory);
		builder.equal("Age", Age, Types.INTEGER, false);
	    String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
	
	/**
	 * SimpleShardByDBOnMysql,自定义查询，equal
	**/
	public List<DesignatedDatabaseOnMysqlGen> test_def_query_equal(Integer Age, DalHints hints) throws SQLException {
		String sql = "select * from person where Age=?";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.setSensitive(i++, "Age", Types.INTEGER, Age);
		return (List<DesignatedDatabaseOnMysqlGen>)queryDao.query(sql, parameters, hints, personSimpleShardByDBOnMysqlGenRowMapper);
	}
	
	/**
	 * SimpleShardByDBOnMysql,自定义查询，equal
	**/
	public List<DesignatedDatabaseOnMysqlGen> test_def_query_nonexistentColumn(DalHints hints) throws SQLException {
		String sql = "select * from person where name in (select Name from people where Age1=3)";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
//		parameters.setSensitive(i++, "Age", Types.INTEGER, Age);
		return (List<DesignatedDatabaseOnMysqlGen>)queryDao.query(sql, parameters, hints, parser);
	}

	/**
	 * SimpleShardByDBTableOnMysql，自定义增删改
	 * 自定义sql，不支持分表，如要分表效果，可将tableID作为参数传入api，前提是shard策略中只配置分库而不分表
	**/
	public int test_def_update (DalHints hints,String tableShardID) throws SQLException {
		String sql = SQLParser.parse("truncate People_"+tableShardID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		return baseClient.update(sql, parameters, hints);
	}

}