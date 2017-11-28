package shardTest.oldVersionCodeTest;


import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
import com.ctrip.platform.dal.dao.helper.SQLParser;
import com.ctrip.platform.dal.dao.sqlbuilder.DeleteSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.UpdateSqlBuilder;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShardColModShardByDBOnSqlServerGenDao {
	private static final String DATA_BASE = "ShardColModShardByDBOnSqlServer";
	private static DatabaseCategory dbCategory = null;
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from People WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM People WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "select * from People (nolock) order by PeopleID desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
	private DalParser<ShardColModShardByDBOnSqlServerGen> parser = null;
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalTableDao<ShardColModShardByDBOnSqlServerGen> client;
	private DalRowMapper<ShardColModShardByDBOnSqlServerGen> PeopleSimpleShardByDBOnSqlServerGenRowMapper = null;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;

	public ShardColModShardByDBOnSqlServerGenDao() throws SQLException {
		this.PeopleSimpleShardByDBOnSqlServerGenRowMapper = new DalDefaultJpaMapper(
				ShardColModShardByDBOnSqlServerGen.class);
		parser = new DalDefaultJpaParser<>(
				ShardColModShardByDBOnSqlServerGen.class);
		this.client = new DalTableDao<ShardColModShardByDBOnSqlServerGen>(
				parser);
		dbCategory = this.client.getDatabaseCategory();
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	/**
	 * Query PeopleSimpleShardByDBOnSqlServerGen by the specified ID The ID must
	 * be a number
	 **/
	public ShardColModShardByDBOnSqlServerGen queryByPk(Number id,
                                                        DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query PeopleSimpleShardByDBOnSqlServerGen by
	 * PeopleSimpleShardByDBOnSqlServerGen instance which the primary key is set
	 **/
	public ShardColModShardByDBOnSqlServerGen queryByPk(
            ShardColModShardByDBOnSqlServerGen pk, DalHints hints)
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
		Number result = (Number) this.baseClient.query(COUNT_SQL_PATTERN,
				parameters, hints, extractor);
		return result.intValue();
	}

	/**
	 * Query PeopleSimpleShardByDBOnSqlServerGen with paging function The
	 * pageSize and pageNo must be greater than zero.
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> queryByPage(int pageSize,
                                                                int pageNo, DalHints hints) throws SQLException {
		if (pageNo < 1 || pageSize < 1)
			throw new SQLException("Illigal pagesize or pageNo, pls check");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String sql = PAGE_SQL_PATTERN;
		int fromRownum = (pageNo - 1) * pageSize;
		int endRownum = pageSize;
		parameters.set(1, Types.INTEGER, fromRownum);
		parameters.set(2, Types.INTEGER, endRownum);
		return queryDao.query(sql, parameters, hints, parser);
	}

	/**
	 * Get all records in the whole table
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> getAll(DalHints hints)
			throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<ShardColModShardByDBOnSqlServerGen> result = null;
		result = queryDao.query(ALL_SQL_PATTERN, parameters, hints, parser);
		return result;
	}

	/**
	 * Insert pojo and get the generated PK back in keyHolder. If the
	 * "set no count on" for MS SqlServer is set(currently set in Ctrip), the
	 * operation may fail. Please don't pass keyholder for MS SqlServer to avoid
	 * the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints,
			ShardColModShardByDBOnSqlServerGen daoPojo) throws SQLException {
		if (null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojo);
	}

	/**
	 * Insert pojos one by one. If you want to inert them in the batch mode,
	 * user batchInsert instead. You can also use the combinedInsert.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation. DalHintEnum.continueOnError can be used to
	 *            indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 */
	public int[] insert(DalHints hints,
			List<ShardColModShardByDBOnSqlServerGen> daoPojos)
			throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojos);
	}

	/**
	 * Insert pojo and get the generated PK back in keyHolder. If the
	 * "set no count on" for MS SqlServer is set(currently set in Ctrip), the
	 * operation may fail. Please don't pass keyholder for MS SqlServer to avoid
	 * the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int insert(DalHints hints, KeyHolder keyHolder,
			ShardColModShardByDBOnSqlServerGen daoPojo) throws SQLException {
		if (null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojo);
	}

	/**
	 * Insert pojos and get the generated PK back in keyHolder. If the
	 * "set no count on" for MS SqlServer is set(currently set in Ctrip), the
	 * operation may fail. Please don't pass keyholder for MS SqlServer to avoid
	 * the failure.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation. DalHintEnum.continueOnError can be used to
	 *            indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] insert(DalHints hints, KeyHolder keyHolder,
			List<ShardColModShardByDBOnSqlServerGen> daoPojos)
			throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojos);
	}

	/**
	 * Insert pojos in batch mode. The DalDetailResults will be set in hints to
	 * allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchInsert(DalHints hints,
			List<ShardColModShardByDBOnSqlServerGen> daoPojos)
			throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}

	/**
	 * Delete the given pojo.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param daoPojo
	 *            pojo to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(DalHints hints,
			ShardColModShardByDBOnSqlServerGen daoPojo) throws SQLException {
		if (null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojo);
	}

	/**
	 * Delete the given pojos list one by one.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param daoPojos
	 *            list of pojos to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] delete(DalHints hints,
			List<ShardColModShardByDBOnSqlServerGen> daoPojos)
			throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojos);
	}

	/**
	 * Delete the given pojo list in batch. The DalDetailResults will be set in
	 * hints to allow client know how the operation performed in each of the
	 * shard.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param daoPojos
	 *            list of pojos to be deleted
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchDelete(DalHints hints,
			List<ShardColModShardByDBOnSqlServerGen> daoPojos)
			throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0)
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
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation. DalHintEnum.updateNullField can be used to
	 *            indicate that the field of pojo is null value will be update.
	 * @param daoPojo
	 *            pojo to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(DalHints hints,
			ShardColModShardByDBOnSqlServerGen daoPojo) throws SQLException {
		if (null == daoPojo)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.update(hints, daoPojo);
	}

	/**
	 * Update the given pojo list one by one. By default, if a field of pojo is
	 * null value, that field will be ignored, so that it will not be updated.
	 * You can overwrite this by set updateNullField in hints.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation. DalHintEnum.updateNullField can be used to
	 *            indicate that the field of pojo is null value will be update.
	 * @param daoPojos
	 *            list of pojos to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] update(DalHints hints,
			List<ShardColModShardByDBOnSqlServerGen> daoPojos)
			throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0)
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
	public int[] batchUpdate(DalHints hints,
			List<ShardColModShardByDBOnSqlServerGen> daoPojos)
			throws SQLException {
		if (null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}

	/**
	 * 构建，查询，equal
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_build_query_equal(
			Integer CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				false);
		builder.select("CityID", "Name", "ProvinceID", "PeopleID", "CountryID");
		builder.equal("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints, parser);
	}

	/**
	 * 构建，更新，equal
	 **/
	public int test_build_update_equal(String Name, Integer CityID,
			DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		UpdateSqlBuilder builder = new UpdateSqlBuilder("People", dbCategory);
		builder.update("Name", Name, Types.VARCHAR);
		builder.equal("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}

	/**
	 * 构建，新增，equal
	 **/
	public int test_build_insert_equal(Integer CityID, String Name,
			Integer ProvinceID, Integer CountryID, DalHints hints)
			throws SQLException {
		String sql = SQLParser
				.parse("INSERT INTO People ([CityID],[Name],[ProvinceID],[CountryID]) VALUES ( ? , ? , ? , ? )");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, "CityID", Types.INTEGER, CityID);
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		parameters.set(i++, "ProvinceID", Types.INTEGER, ProvinceID);
		parameters.set(i++, "CountryID", Types.INTEGER, CountryID);
		return client.update(sql, parameters, hints);
	}

	/**
	 * 构建，删除，equal
	 **/
	public int test_build_delete_equal(Integer CityID, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		DeleteSqlBuilder builder = new DeleteSqlBuilder("People", dbCategory);
		builder.equal("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}

	/**
	 * SimpleShardByDBOnSqlServer，构建，查询，fieldList
	 **/
	public List<String> test_build_query_fieldList(List<Integer> CityID,
			DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				false);
		builder.select("Name");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints.sortBy(new StringComparator()), String.class);
	}

	/**
	 * SimpleShardByDBOnSqlServer，构建，查询，fieldListByPage
	 **/
	public List<String> test_build_query_fieldListByPage(List<Integer> CityID,
			int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				true);
		builder.select("Name");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		builder.orderBy("CityID", true);
		String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		int index = builder.getStatementParameterIndex();
		parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(index++, Types.INTEGER, pageSize * pageNo);
		return queryDao.query(sql, parameters, hints.sortBy(new StringComparator()), String.class);
	}

	/**
	 * SimpleShardByDBOnSqlServer，构建，查询，fieldSingle
	 **/
	public String test_build_query_fieldSingle(List<Integer> CityID,
			DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				false);
		builder.select("Name");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		return queryDao.queryForObjectNullable(sql, builder.buildParameters(),
				hints.sortBy(new StringComparator()), String.class);
	}

	/**
	 * SimpleShardByDBOnSqlServer，构建，查询，fieldFirst
	 **/
	public String test_build_query_fieldFirst(List<Integer> CityID,
			DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				false);
		builder.select("Name");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.buildFirst();
		return queryDao.queryFirstNullable(sql, builder.buildParameters(),
				hints.sortBy(new StringComparator()), String.class);
	}
	
	/**
	 * SimpleShardByDBOnSqlServer，构建，查询,queryFrom
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_build_queryFrom_pojoList(
			List<Integer> CityID, DalHints hints,int start,int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				false);
		builder.select("CityID", "Name", "ProvinceID", "PeopleID", "CountryID");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
//		return queryDao.query(sql, parameters, hints, parser);
		return queryDao.queryFrom(sql, parameters, hints.sortBy(new ShardColModShardByDBOnSqlServerComparator()), parser, start, count);
	}
	
	/**
	 * SimpleShardByDBOnSqlServer，构建，查询,queryFrom
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_build_queryFromPartialFieldsStrings_pojoList(
			List<Integer> CityID, DalHints hints,int start,int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				false);
		builder.select("CityID", "Name", "ProvinceID", "PeopleID", "CountryID");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
//		return queryDao.query(sql, parameters, hints, parser);
		return queryDao.queryFrom(sql, parameters, hints.partialQuery("CityID", "PeopleID").sortBy(new ShardColModShardByDBOnSqlServerComparator()), parser, start, count);
	}
	
	/**
	 * SimpleShardByDBOnSqlServer，构建，查询,queryFrom
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_build_queryFromPartialFieldsSet_pojoList(
			List<Integer> CityID, DalHints hints,int start,int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				false);
		builder.select("CityID", "Name", "ProvinceID", "PeopleID", "CountryID");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
//		return queryDao.query(sql, parameters, hints, parser);
		Set<String> columns = new HashSet<>();
		columns.add("CityID");
		columns.add("PeopleID");
		
		return queryDao.queryFrom(sql, parameters, hints.partialQuery(columns).sortBy(new ShardColModShardByDBOnSqlServerComparator()), parser, start, count);
	}

	/**
	 * 构建，查询
	**/
	public List<ShardColModShardByDBOnSqlServerGen> test_ClientQueryFrom_list(List<Integer> CityID, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

//		SelectSqlBuilder builder = new SelectSqlBuilder();
//		builder.select("CityID","Name","ProvinceID","PeopleID","CountryID");
//		builder.in("CityID", CityID, Types.INTEGER, false);
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		
		return client.queryFrom("CityID in (?) order by CityID", parameters, hints.sortBy(new ShardColModShardByDBOnSqlServerComparator()), start, count);
//		return client.query(builder, hints);
	}
	
	
	/**
	 * 构建，查询
	**/
	public List<ShardColModShardByDBOnSqlServerGen> test_ClientQueryFromPartialFieldsSet_list(List<Integer> CityID, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		
		Set<String> columns = new HashSet<>();
		columns.add("CityID");
		columns.add("Name");
		
		return client.queryFrom("CityID in (?) order by CityID", parameters, hints.partialQuery(columns).sortBy(new ShardColModShardByDBOnSqlServerComparator()), start, count);

	}
	
	/**
	 * 构建，查询
	**/
	public List<ShardColModShardByDBOnSqlServerGen> test_ClientQueryFromPartialFieldsStrings_list(List<Integer> CityID, DalHints hints, int start, int count) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "CityID", Types.INTEGER, CityID);
		
//		Set<String> columns = new HashSet<>();
//		columns.add("CityID");
//		columns.add("Name");
		
		return client.queryFrom("CityID in (?) order by CityID", parameters, hints.partialQuery("CityID","Name").sortBy(new ShardColModShardByDBOnSqlServerComparator()), start, count);

	}

	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，pojoList
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_def_queryFrom_pojoList(
			List<Integer> CityID, DalHints hints,int start,int count) throws SQLException {
		String sql = "select * from People with(nolock) where CityID in (?)";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
//		return (List<ShardColModShardByDBOnSqlServerGen>) queryDao
//				.query(sql, parameters, hints.partialQuery("CityID", "Name", "ProvinceID", "PeopleID", "CountryID"),
//						PeopleSimpleShardByDBOnSqlServerGenRowMapper);
		return queryDao.queryFrom(sql, parameters, hints.sortBy(new ShardColModShardByDBOnSqlServerComparator()), parser, start, count);
	}
	
	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，pojoList
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_def_queryFromPartialFieldsSet_pojoList(
			List<Integer> CityID, DalHints hints,int start,int count) throws SQLException {
		String sql = "select * from People with(nolock) where CityID in (?)";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
//		return (List<ShardColModShardByDBOnSqlServerGen>) queryDao
//				.query(sql, parameters, hints.partialQuery("CityID", "Name", "ProvinceID", "PeopleID", "CountryID"),
//						PeopleSimpleShardByDBOnSqlServerGenRowMapper);
		Set<String> columns = new HashSet<>();
		columns.add("CityID");
		columns.add("PeopleID");
		return queryDao.queryFrom(sql, parameters, hints.partialQuery(columns).sortBy(new ShardColModShardByDBOnSqlServerComparator()), parser, start, count);
	}
	
	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，pojoList
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_def_queryFromPartialFieldsStrings_pojoList(
			List<Integer> CityID, DalHints hints,int start,int count) throws SQLException {
		String sql = "select * from People with(nolock) where CityID in (?)";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
//		return (List<ShardColModShardByDBOnSqlServerGen>) queryDao
//				.query(sql, parameters, hints.partialQuery("CityID", "Name", "ProvinceID", "PeopleID", "CountryID"),
//						PeopleSimpleShardByDBOnSqlServerGenRowMapper);
//		Set<String> columns = new HashSet<>();
//		columns.add("CityID");
//		columns.add("PeopleID");
		return queryDao.queryFrom(sql, parameters, hints.partialQuery("CityID","PeopleID").sortBy(new ShardColModShardByDBOnSqlServerComparator()), parser, start, count);
	}
	
	/**
	 * SimpleShardByDBOnSqlServer，构建，查询
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_build_query_pojoList(
			List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				false);
		builder.select("CityID", "Name", "ProvinceID", "PeopleID", "CountryID");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		return queryDao.query(sql, parameters, hints.sortBy(new ShardColModShardByDBOnSqlServerComparator()), parser);
	}

	/**
	 * SimpleShardByDBOnSqlServer，构建，查询,pojoListByPage
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_build_query_pojoListByPage(
			List<Integer> CityID, int pageNo, int pageSize, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				true);
		builder.select("CityID", "Name", "ProvinceID", "PeopleID", "CountryID");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		builder.orderBy("CityID", true);
		String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
		int index = builder.getStatementParameterIndex();
		parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(index++, Types.INTEGER, pageSize * pageNo);
		return queryDao.query(sql, parameters, hints.sortBy(new ShardColModShardByDBOnSqlServerComparator()), parser);
	}

	/**
	 * SimpleShardByDBOnSqlServer，构建，查询,pojoSingle
	 **/
	public ShardColModShardByDBOnSqlServerGen test_build_query_pojoSingle(
			List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				false);
		builder.select("CityID", "Name", "ProvinceID", "PeopleID", "CountryID");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		return queryDao.queryForObjectNullable(sql, builder.buildParameters(),
				hints, parser);
	}

	/**
	 * SimpleShardByDBOnSqlServer，构建，查询,pojoFirst
	 **/
	public ShardColModShardByDBOnSqlServerGen test_build_query_pojoFirst(
			List<Integer> CityID, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("People", dbCategory,
				false);
		builder.select("CityID", "Name", "ProvinceID", "PeopleID", "CountryID");
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.buildFirst();
		return queryDao.queryFirstNullable(sql, builder.buildParameters(),
				hints.sortBy(new ShardColModShardByDBOnSqlServerComparator()), parser);
	}

	/**
	 * SimpleShardByDBOnSqlServer，构建，更新
	 **/
	public int test_build_update(String Name, List<Integer> CityID,
			DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		UpdateSqlBuilder builder = new UpdateSqlBuilder("People", dbCategory);
		builder.update("Name", Name, Types.VARCHAR);
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}

	/**
	 * SimpleShardByDBOnSqlServer，构建，新增
	 **/
	public int test_build_insert(Integer CityID, String Name,
			Integer ProvinceID, Integer CountryID, DalHints hints)
			throws SQLException {
		String sql = SQLParser
				.parse("INSERT INTO People ([CityID],[Name],[ProvinceID],[CountryID]) VALUES ( ? , ? , ? , ? )");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, "CityID", Types.INTEGER, CityID);
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		parameters.set(i++, "ProvinceID", Types.INTEGER, ProvinceID);
		parameters.set(i++, "CountryID", Types.INTEGER, CountryID);
		return client.update(sql, parameters, hints);
	}

	/**
	 * SimpleShardByDBOnSqlServer，构建，删除
	 **/
	public int test_build_delete(List<Integer> CityID, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		DeleteSqlBuilder builder = new DeleteSqlBuilder("People", dbCategory);
		builder.inNullable("CityID", CityID, Types.INTEGER, false);
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}

	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，fieldList
	 **/
	public List<String> test_def_query_fieldList(List<Integer> CityID,
			DalHints hints) throws SQLException {
		String sql = "SELECT Name FROM People with (nolock) WHERE CityID in (?)";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		return queryDao.query(sql, parameters, hints.sortBy(new StringComparator()), String.class);
	}

	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，fieldListByPage
	 **/
	public List<String> test_def_query_fieldListByPage(List<Integer> CityID,
			int pageNo, int pageSize, DalHints hints) throws SQLException {
		String sql = "SELECT Name FROM People WHERE CityID IN (?) ORDER BY CityID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		parameters.set(i++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(i++, Types.INTEGER, pageSize);
		return queryDao.query(sql, parameters, hints.sortBy(new StringComparator()), String.class);
	}

	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，fieldSingle
	 **/
	public String test_def_query_fieldSingle(List<Integer> CityID,
			DalHints hints) throws SQLException {
		String sql = "SELECT Name FROM People with (nolock) WHERE CityID in (?)";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		return queryDao.queryForObjectNullable(sql, parameters, hints,
				String.class);
	}

	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，fieldFirst
	 **/
	public String test_def_query_fieldFirst(List<Integer> CityID, DalHints hints)
			throws SQLException {
		String sql = "SELECT Name FROM People with (nolock) WHERE CityID in (?)";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		return queryDao
				.queryFirstNullable(sql, parameters, hints.sortBy(new StringComparator()), String.class);
	}

	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，pojoList
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_def_query_pojoList(
			List<Integer> CityID, DalHints hints) throws SQLException {
		String sql = "select * from People with(nolock) where CityID in (?)";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		return (List<ShardColModShardByDBOnSqlServerGen>) queryDao
				.query(sql, parameters, hints.sortBy(new ShardColModShardByDBOnSqlServerComparator()),
						PeopleSimpleShardByDBOnSqlServerGenRowMapper);
	}

	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，pojoListByPage
	 **/
	public List<ShardColModShardByDBOnSqlServerGen> test_def_query_pojoListByPage(
			List<Integer> CityID, int pageNo, int pageSize, DalHints hints)
			throws SQLException {
		String sql = "SELECT * FROM People WHERE CityID IN (?) ORDER BY PeopleID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		parameters.set(i++, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(i++, Types.INTEGER, pageSize);
		return (List<ShardColModShardByDBOnSqlServerGen>) queryDao
				.query(sql, parameters, hints.sortBy(new ShardColModShardByDBOnSqlServerComparator()),
						PeopleSimpleShardByDBOnSqlServerGenRowMapper);
	}

	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，pojoSingle
	 **/
	public ShardColModShardByDBOnSqlServerGen test_def_query_pojoSingle(
			List<Integer> CityID, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM People with (nolock) WHERE CityID in (?)";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		return (ShardColModShardByDBOnSqlServerGen) queryDao
				.queryForObjectNullable(sql, parameters, hints,
						PeopleSimpleShardByDBOnSqlServerGenRowMapper);
	}

	/**
	 * SimpleShardByDBOnSqlServer，自定义，查询，pojoFirst
	 **/
	public ShardColModShardByDBOnSqlServerGen test_def_query_pojoFirst(
			List<Integer> CityID, DalHints hints) throws SQLException {
		String sql = "SELECT * FROM People with (nolock) WHERE CityID in (?)";
		sql = SQLParser.parse(sql, CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		return (ShardColModShardByDBOnSqlServerGen) queryDao
				.queryFirstNullable(sql, parameters, hints.sortBy(new ShardColModShardByDBOnSqlServerComparator()),
						PeopleSimpleShardByDBOnSqlServerGenRowMapper);
	}

	/**
	 * SimpleShardByDBOnSqlServer，自定义，增删改
	 **/
	public int test_def_update(DalHints hints) throws SQLException {
		String sql = SQLParser.parse("truncate table People");
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		return baseClient.update(sql, parameters, hints);
	}

	/**
	 * 自定义 ，更新
	 **/
	public int test_def_update_in(String Name, List<Integer> CityID,
			DalHints hints) throws SQLException {
		String sql = SQLParser.parse(
				"update People set Name=? where CityID in (?)", CityID);
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		int i = 1;
		parameters.set(i++, "Name", Types.VARCHAR, Name);
		i = parameters.setInParameter(i, "CityID", Types.INTEGER, CityID);
		return baseClient.update(sql, parameters, hints);
	}

	private class ShardColModShardByDBOnSqlServerComparator implements Comparator<ShardColModShardByDBOnSqlServerGen> {
		@Override
		public int compare(ShardColModShardByDBOnSqlServerGen o1, ShardColModShardByDBOnSqlServerGen o2) {
			return new Integer(o1.getCityID()).compareTo(o2.getCityID());
		}
	}

	private class StringComparator implements Comparator<String>{
		@Override
		public int compare(String o1, String o2) {
			return new Integer( o1.compareTo(o2));
		}
	}
}