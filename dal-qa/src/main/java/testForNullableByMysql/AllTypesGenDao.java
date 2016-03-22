package testForNullableByMysql;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.*;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

public class AllTypesGenDao {
    private static final String DATA_BASE = "testForNullableByMysql";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from all_types";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM all_types";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM all_types LIMIT ?, ?";
	private DalParser<AllTypesGen> parser = null;	
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalTableDao<AllTypesGen> client;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;
	
	public AllTypesGenDao() throws SQLException {
		parser = new DalDefaultJpaParser<>(AllTypesGen.class);
		this.client = new DalTableDao<AllTypesGen>(parser);
		dbCategory = this.client.getDatabaseCategory();
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
	/**
	 * Query AllTypesGen by the specified ID
	 * The ID must be a number
	**/
	public AllTypesGen queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
    /**
	 * Query AllTypesGen by AllTypesGen instance which the primary key is set
	**/
	public AllTypesGen queryByPk(AllTypesGen pk, DalHints hints)
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
	 * Query AllTypesGen with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<AllTypesGen> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
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
	public List<AllTypesGen> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<AllTypesGen> result = null;
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
	public int insert(DalHints hints, AllTypesGen daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, List<AllTypesGen> daoPojos) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, AllTypesGen daoPojo) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<AllTypesGen> daoPojos) throws SQLException {
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
	public int[] batchInsert(DalHints hints, List<AllTypesGen> daoPojos) throws SQLException {
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
	public int combinedInsert(DalHints hints, List<AllTypesGen> daoPojos) throws SQLException {
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
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<AllTypesGen> daoPojos) throws SQLException {
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
	public int delete(DalHints hints, AllTypesGen daoPojo) throws SQLException {
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
	public int[] delete(DalHints hints, List<AllTypesGen> daoPojos) throws SQLException {
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
	public int[] batchDelete(DalHints hints, List<AllTypesGen> daoPojos) throws SQLException {
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
	public int update(DalHints hints, AllTypesGen daoPojo) throws SQLException {
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
	public int[] update(DalHints hints, List<AllTypesGen> daoPojos) throws SQLException {
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
	public int[] batchUpdate(DalHints hints, List<AllTypesGen> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}
	/**
	 * query

	**/
	public List<AllTypesGen> test_Java_nullable(String CharCol, String SetCol, Boolean BitCol, Integer IntCol, Integer SmallIntCol, Integer MediumIntCol, BigDecimal DecimalCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarCharCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("all_types", dbCategory, false);
		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.likeNullable("CharCol", CharCol, Types.CHAR, true);
		builder.and();
		builder.leftBracket();
		builder.equalNullable("SetCol", SetCol, Types.CHAR, true);
		builder.or();
		builder.not();
		builder.notEqualNullable("BitCol", BitCol, Types.BIT, true);
		builder.rightBracket();
		builder.and();
		builder.greaterThanNullable("IntCol", IntCol, Types.INTEGER, true);
		builder.and();
		builder.lessThanNullable("SmallIntCol", SmallIntCol, Types.SMALLINT, true);
		builder.and();
		builder.greaterThanEqualsNullable("MediumIntCol", MediumIntCol, Types.INTEGER, true);
		builder.and();
		builder.lessThanEqualsNullable("DecimalCol", DecimalCol, Types.DECIMAL, true);
		builder.and();
		builder.betweenNullable("BigIntCol", BigIntCol_start, BigIntCol_end, Types.BIGINT, true);
		builder.and();
		builder.inNullable("VarCharCol", VarCharCol, Types.VARCHAR, true);
		builder.and();
		builder.isNull("FloatCol");
		builder.and();
		builder.isNotNull("DoubleCol");
		builder.orderBy("idAll_Types", false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}
	
	public List<AllTypesGen> test_Java(String CharCol, String SetCol, Boolean BitCol, Integer IntCol, Integer SmallIntCol, Integer MediumIntCol, BigDecimal DecimalCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarCharCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("all_types", dbCategory, false);
		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.like("CharCol", CharCol, Types.CHAR, false);
		builder.and();
		builder.leftBracket();
		builder.equal("SetCol", SetCol, Types.CHAR, false);
		builder.or();
		builder.not();
		builder.notEqual("BitCol", BitCol, Types.BIT, false);
		builder.rightBracket();
		builder.and();
		builder.greaterThan("IntCol", IntCol, Types.INTEGER, false);
		builder.and();
		builder.lessThan("SmallIntCol", SmallIntCol, Types.SMALLINT, false);
		builder.and();
		builder.greaterThanEquals("MediumIntCol", MediumIntCol, Types.INTEGER, false);
		builder.and();
		builder.lessThanEquals("DecimalCol", DecimalCol, Types.DECIMAL, false);
		builder.and();
		builder.between("BigIntCol", BigIntCol_start, BigIntCol_end, Types.BIGINT, false);
		builder.and();
		builder.in("VarCharCol", VarCharCol, Types.VARCHAR, false);
		builder.and();
		builder.isNull("FloatCol");
		builder.and();
		builder.isNotNull("DoubleCol");
		builder.orderBy("idAll_Types", false);
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}
	/**
	 * truncate
	**/
	public int def_truncate (DalHints hints) throws SQLException {
		String sql = SQLParser.parse("truncate all_types;");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		return baseClient.update(sql, parameters, hints);
	}
}