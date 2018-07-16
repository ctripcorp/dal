package dao.noshard;


import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalCustomRowMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import entity.MysqlAllTypesTable;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Map;


public class AllTypesOnMysqlDao {
	private static final boolean ASC = true;
	private DalTableDao<MysqlAllTypesTable> client;
	private static final String DATA_BASE = "noShardTestOnMysql";
	private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
	private DalQueryDao queryDao = null;

	private DalRowMapper<MysqlAllTypesTable> allTypesRowMapper = null;

	public AllTypesOnMysqlDao() throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(MysqlAllTypesTable.class));
		this.allTypesRowMapper = new DalDefaultJpaMapper<>(MysqlAllTypesTable.class);
		this.queryDao = new DalQueryDao(DATA_BASE);
	}

    /**
	 * 自定义，查询，filedList
	**/
	public List<Integer> testDefQueryFieldList(Integer id) throws SQLException {
		return testDefQueryFieldList(id, null);
	}

	/**
	 * 自定义，查询，filedList
	**/
	public List<Integer> testDefQueryFieldList(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<Integer>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select intCol from all_types where idall_types>?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.simpleType();

		return queryDao.query(builder, parameters, hints);
	}

	public Integer testFreeSQLBuilderQueryMax(Integer id,DalHints hints) throws SQLException{
		hints= DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<Integer> builder=new FreeSelectSqlBuilder<>();
		builder.append("select max(IntCol)");
		builder.from("all_types").where();
		builder.greaterThan("idAll_Types",id, Types.INTEGER);
		builder.simpleType().requireFirst().nullable();
		return queryDao.query(builder,hints);
	}

	public List<Integer> testFreeSQLBuilderQueryFieldList(Integer id,DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<Integer>> builder=new FreeSelectSqlBuilder<>();
		builder.select("IntCol");
		builder.from("all_types").where();
		builder.greaterThan("idAll_Types",id, Types.INTEGER);
		builder.simpleType();
		return queryDao.query(builder,hints);
	}

	public List<Integer> testFreeSQLBuilderQueryFieldListByPage(Integer id,int pageNo,int pageSize,DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<Integer>> builder=new FreeSelectSqlBuilder<>();
		builder.select("IntCol");
		builder.from("all_types").where();
		builder.greaterThan("idAll_Types",id, Types.INTEGER);
		builder.orderBy("idAll_Types",true);
		builder.simpleType().atPage(pageNo,pageSize);
		return queryDao.query(builder,hints);
	}

	public Integer testFreeSQLBuilderQueryFieldSingle(Integer id,DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<Integer> builder=new FreeSelectSqlBuilder<>();
		builder.select("IntCol");
		builder.from("all_types").where();
		builder.equal("idAll_Types",id, Types.INTEGER);
		builder.simpleType().requireSingle().nullable();
		return queryDao.query(builder,hints);
	}

	public Integer testFreeSQLBuilderQueryFieldFirst(Integer id,DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<Integer> builder=new FreeSelectSqlBuilder<>();
		builder.select("IntCol");
		builder.from("all_types");
		builder.where();
		builder.greaterThan("idAll_Types",id, Types.INTEGER);
		builder.orderBy("idAll_Types",true);
		builder.simpleType().requireFirst().nullable();
		return queryDao.query(builder,hints);
	}
	/**
	 * 自定义，查询，filedListByPage
	**/
	public List<Integer> testDefQueryFieldListByPage(Integer id, int pageNo, int pageSize) throws SQLException {
		return testDefQueryFieldListByPage(id, pageNo, pageSize, null);
	}

	/**
	 * 自定义，查询，filedListByPage
	**/
	public List<Integer> testDefQueryFieldListByPage(Integer id, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<Integer>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select intCol from all_types where idall_types>? order by idall_types");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.simpleType().atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints);
	}

    /**
	 * 自定义，查询，fieldSingle
	**/
	public Integer testDefQueryFieldSingle(Integer id) throws SQLException {
		return testDefQueryFieldSingle(id, null);
	}

	/**
	 * 自定义，查询，fieldSingle
	**/
	public Integer testDefQueryFieldSingle(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<Integer> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select intcol from all_types where idall_types=?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.simpleType().requireSingle().nullable();

		return queryDao.query(builder, parameters, hints);
	}

    /**
	 * 自定义，查询，fieldFirst
	**/
	public Integer testDefQueryFieldFirst(Integer id) throws SQLException {
		return testDefQueryFieldFirst(id, null);
	}

	/**
	 * 自定义，查询，fieldFirst
	**/
	public Integer testDefQueryFieldFirst(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<Integer> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select intcol from all_types where idall_types>? order by idall_types");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.simpleType().requireFirst().nullable();

		return queryDao.query(builder, parameters, hints);
	}

    /**
	 * 自定义，查询，pojoList
	**/
	public List<MysqlAllTypesTable> testDefQueryPojoList(List<Integer> intcol) throws SQLException {
		return testDefQueryPojoList(intcol, null);
	}

	/**
	 * 自定义，查询，pojoList
	**/
	public List<MysqlAllTypesTable> testDefQueryPojoList(List<Integer> intcol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from all_types where intcol in (?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		i = parameters.setSensitiveInParameter(i, "intcol", Types.INTEGER, intcol);
		builder.mapWith(allTypesRowMapper);

		return queryDao.query(builder, parameters, hints);
	}

	public List<MysqlAllTypesTable> testFreeSqlQueryListByPage(Integer id, int pageNo, int pageSize, DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
		builder.selectAll();
		builder.from("all_types").where(Expressions.greaterThan("idAll_types",id, Types.INTEGER)).orderBy("idAll_types",true);
		builder.mapWith(allTypesRowMapper).atPage(pageNo,pageSize);
		return queryDao.query(builder,hints);
	}

	public MysqlAllTypesTable testFreeSqlQuerySingle(Integer id, DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<MysqlAllTypesTable> builder=new FreeSelectSqlBuilder<>();
		builder.selectAll();
		builder.from("all_types").where(Expressions.equal("idAll_types",id, Types.INTEGER));
		builder.mapWith(allTypesRowMapper).requireSingle().nullable();
		return queryDao.query(builder,hints);
	}

	public MysqlAllTypesTable testFreeSqlQueryFirst(Integer id, DalHints hints) throws SQLException{
		hints=DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<MysqlAllTypesTable> builder=new FreeSelectSqlBuilder<>();
		builder.selectAll();
		builder.from("all_types").where(Expressions.greaterThan("idAll_types",id, Types.INTEGER)).orderBy("idAll_types",true);
		builder.mapWith(allTypesRowMapper).requireFirst().nullable();
		return queryDao.query(builder,hints);
	}

    /**
	 * 自定义，查询，pojoListByPage
	**/
	public List<MysqlAllTypesTable> testDefQueryPojoListByPage(Integer id, int pageNo, int pageSize) throws SQLException {
		return testDefQueryPojoListByPage(id, pageNo, pageSize, null);
	}

	/**
	 * 自定义，查询，pojoListByPage
	**/
	public List<MysqlAllTypesTable> testDefQueryPojoListByPage(Integer id, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from all_types where idall_Types>? order by idall_types");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.mapWith(allTypesRowMapper).atPage(pageNo, pageSize);

		return queryDao.query(builder, parameters, hints);
	}


    /**
	 * 自定义，查询，pojoSingle
	**/
	public MysqlAllTypesTable testDefQueryPojoSingle(Integer id) throws SQLException {
		return testDefQueryPojoSingle(id, null);
	}

	/**
	 * 自定义，查询，pojoSingle
	**/
	public MysqlAllTypesTable testDefQueryPojoSingle(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<MysqlAllTypesTable> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from all_types where idall_types=?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.mapWith(allTypesRowMapper).requireSingle().nullable();

		return (MysqlAllTypesTable)queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义，查询，pojoFirst
	**/
	public MysqlAllTypesTable testDefQueryPojoFirst(Integer id) throws SQLException {
		return testDefQueryPojoFirst(id, null);
	}

	/**
	 * 自定义，查询，pojoFirst
	**/
	public MysqlAllTypesTable testDefQueryPojoFirst(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<MysqlAllTypesTable> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("select * from all_types where idall_types>? order by idall_types");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);
		builder.mapWith(allTypesRowMapper).requireFirst().nullable();

		return (MysqlAllTypesTable)queryDao.query(builder, parameters, hints);
	}

	/**
	 * 自定义，update
	**/
	public int testDefUpdate (String varcharcol, String charcol) throws SQLException {
		return testDefUpdate(varcharcol, charcol, null);
	}

	/**
	 * 自定义，update
	**/
	public int testDefUpdate (String varcharcol, String charcol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("update all_types set varcharcol=? where charcol like ?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "varcharcol", Types.VARCHAR, varcharcol);
		parameters.setSensitive(i++, "charcol", Types.CHAR, charcol);

		return queryDao.update(builder, parameters, hints);
	}

	/**
	 * 自定义，update
	 **/
	public int testFreeSqlUpdate (String varcharcol, String charcol,Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.update("all_types").set("VarCharCol","CharCol");
		builder.set("varCharCol",varcharcol, Types.VARCHAR).set("charCol",charcol, Types.CHAR);
		builder.where(Expressions.equal("idAll_Types",id, Types.INTEGER));

		return queryDao.update(builder, hints);
	}

	/**
	 * 自定义，删除
	**/
	public int testDefDelete (Integer id) throws SQLException {
		return testDefDelete(id, null);
	}

	/**
	 * 自定义，删除
	 **/
	public int testFreeSqlDelete (Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
//		builder.setLogicDbName(DATA_BASE);
		builder.deleteFrom("all_types").where().equal("idAll_types",id, Types.INTEGER);

		return queryDao.update(builder, hints);
	}

	/**
	 * 自定义，删除
	**/
	public int testDefDelete (Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.setTemplate("delete from all_types where idAll_Types=?");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "id", Types.INTEGER, id);

		return queryDao.update(builder, parameters, hints);
	}

	/**
	 * 自定义，insert
	**/
	public int testDefInsert (String varcharcol, Integer intcol) throws SQLException {
		return testDefInsert(varcharcol, intcol, null);
	}


	/**
	 * 自定义，insert
	 **/
	public int testFreeSqlInsert (String varcharcol, Integer intcol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder();
		builder.insertInto("all_types").values("VarCharCol","IntCol");
		builder.set("VarCharCol",varcharcol, Types.VARCHAR);
		builder.set("IntCol",intcol, Types.INTEGER);
		/*builder.append("insert into").appendTable("all_types (varcharcol,intcol)").append("values (?,?)");
		builder.set("vacharcol",varcharcol, Types.VARCHAR);
		builder.set("intcol",intcol, Types.INTEGER);*/
//		builder.setTemplate("insert into all_types (varcharcol,intcol) values (?,?)");
//		StatementParameters parameters = new StatementParameters();
//		int i = 1;
//		parameters.setSensitive(i++, "varcharcol", Types.VARCHAR, varcharcol);
//		parameters.setSensitive(i++, "intcol", Types.INTEGER, intcol);

		return queryDao.update(builder, hints);
	}

	/**
	 * 自定义，insert
	**/
	public int testDefInsert (String varcharcol, Integer intcol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("insert into all_types (varcharcol,intcol) values (?,?)");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.setSensitive(i++, "varcharcol", Types.VARCHAR, varcharcol);
		parameters.setSensitive(i++, "intcol", Types.INTEGER, intcol);

		return queryDao.update(builder, parameters, hints);
	}

	/**
	 * truncate
	**/
	public int testDefTruncate () throws SQLException {
		return testDefTruncate(null);
	}

	/**
	 * truncate
	**/
	public int testDefTruncate (DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
		builder.setTemplate("truncate all_types");
		StatementParameters parameters = new StatementParameters();
		int i = 1;

		return queryDao.update(builder, parameters, hints);
	}


	/**
	 * Query AllTypes by the specified ID
	 * The ID must be a number
	**/
	public MysqlAllTypesTable queryByPk(Number id)
			throws SQLException {
		return queryByPk(id, null);
	}

	/**
	 * Query AllTypes by the specified ID
	 * The ID must be a number
	**/
	public MysqlAllTypesTable queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}

	/**
	 * Query AllTypes by AllTypes instance which the primary key is set
	**/
	public MysqlAllTypesTable queryByPk(MysqlAllTypesTable pk)
			throws SQLException {
		return queryByPk(pk, null);
	}

	/**
	 * Query AllTypes by AllTypes instance which the primary key is set
	**/
	public MysqlAllTypesTable queryByPk(MysqlAllTypesTable pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<MysqlAllTypesTable> queryLike(MysqlAllTypesTable sample)
			throws SQLException {
		return queryLike(sample, null);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<MysqlAllTypesTable> queryLike(MysqlAllTypesTable sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryLike(sample, hints);
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
	 * Query AllTypes with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<MysqlAllTypesTable> queryAllByPage(int pageNo, int pageSize)  throws SQLException {
		return queryAllByPage(pageNo, pageSize, null);
	}

	/**
	 * Query AllTypes with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<MysqlAllTypesTable> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll().atPage(pageNo, pageSize).orderBy("idAll_Types", ASC);

		return client.query(builder, hints);
	}

	/**
	 * Get all records from table
	 */
	public List<MysqlAllTypesTable> queryAll() throws SQLException {
		return queryAll(null);
	}

	/**
	 * Get all records from table
	 */
	public List<MysqlAllTypesTable> queryAll(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("idAll_Types", ASC);

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
	public int insert(MysqlAllTypesTable daoPojo) throws SQLException {
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
	public int insert(DalHints hints, MysqlAllTypesTable daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
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
	public int[] insert(List<MysqlAllTypesTable> daoPojos) throws SQLException {
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
	public int[] insert(DalHints hints, List<MysqlAllTypesTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
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
	public int insertWithKeyHolder(KeyHolder keyHolder, MysqlAllTypesTable daoPojo) throws SQLException {
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
	public int insert(DalHints hints, KeyHolder keyHolder, MysqlAllTypesTable daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
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
	public int[] insertWithKeyHolder(KeyHolder keyHolder, List<MysqlAllTypesTable> daoPojos) throws SQLException {
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
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<MysqlAllTypesTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojos);
	}

	/**
	 * Insert pojos in batch mode.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 *
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchInsert(List<MysqlAllTypesTable> daoPojos) throws SQLException {
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
	public int[] batchInsert(DalHints hints, List<MysqlAllTypesTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 *
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(List<MysqlAllTypesTable> daoPojos) throws SQLException {
		return combinedInsert(null, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 *
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, List<MysqlAllTypesTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set, the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 *
	 * @param keyHolder holder for generated primary keys
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsertWithKeyHolder(KeyHolder keyHolder, List<MysqlAllTypesTable> daoPojos) throws SQLException {
		return combinedInsert(null, keyHolder, daoPojos);
	}

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder.
	 * If the "set no count on" for MS SqlServer is set, the operation may fail.
	 * Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 *
	 * @param hints Additional parameters that instruct how DAL Client perform database operation.
	 * @param keyHolder holder for generated primary keys
	 * @param daoPojos list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<MysqlAllTypesTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, keyHolder, daoPojos);
	}

	/**
	 * Delete the given pojo.
	 *
	 * @param daoPojo pojo to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int delete(MysqlAllTypesTable daoPojo) throws SQLException {
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
	public int delete(DalHints hints, MysqlAllTypesTable daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
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
	public int[] delete(List<MysqlAllTypesTable> daoPojos) throws SQLException {
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
	public int[] delete(DalHints hints, List<MysqlAllTypesTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojos);
	}

	/**
	 * Delete the given pojo list in batch.
	 * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
	 *
	 * @param daoPojos list of pojos to be deleted
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	public int[] batchDelete(List<MysqlAllTypesTable> daoPojos) throws SQLException {
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
	public int[] batchDelete(DalHints hints, List<MysqlAllTypesTable> daoPojos) throws SQLException {
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
	 * @param daoPojo pojo to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int update(MysqlAllTypesTable daoPojo) throws SQLException {
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
	public int update(DalHints hints, MysqlAllTypesTable daoPojo) throws SQLException {
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
	 * @param daoPojos list of pojos to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] update(List<MysqlAllTypesTable> daoPojos) throws SQLException {
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
	public int[] update(DalHints hints, List<MysqlAllTypesTable> daoPojos) throws SQLException {
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
	public int[] batchUpdate(List<MysqlAllTypesTable> daoPojos) throws SQLException {
		return batchUpdate(null, daoPojos);
	}

	/**
	 * Update the given pojo list in batch.
	 *
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int[] batchUpdate(DalHints hints, List<MysqlAllTypesTable> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchUpdate(hints, daoPojos);
	}


	public List<Map<String,Object>> testFreeSqlGroupByHaving(List<String> VarCharCol) throws Exception{
		DalCustomRowMapper mapper = new DalCustomRowMapper("VarCharCol", "Count");
		FreeSelectSqlBuilder<List<Map<String,Object>>> builder=new FreeSelectSqlBuilder<>();
		builder.select().append("VarCharCol,count(*) as Count").from("all_types").groupBy("VarCharCol").having("VarCharCol in (?)").setIn("VarCharCol",VarCharCol, Types.VARCHAR);
		builder.mapWith(mapper);
		return queryDao.query(builder,new DalHints());
	}

	public List<MysqlAllTypesTable> testFreeSqlLikePattern(String varcharcol, MatchPattern matchPattern) throws Exception{
		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
		builder.select("VarCharCol","BigIntCol").from("all_types").where().like("VarCharCol",varcharcol,matchPattern,Types.VARCHAR);
		builder.mapWith(MysqlAllTypesTable.class);
		return queryDao.query(builder,new DalHints().allowPartial());
	}

	public List<MysqlAllTypesTable> testFreeSqlNotLikePattern(String varcharcol, MatchPattern matchPattern) throws Exception{
		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
		builder.select("VarCharCol","BigIntCol").from("all_types").where().notLike("VarCharCol",varcharcol,matchPattern,Types.VARCHAR);
		builder.mapWith(allTypesRowMapper);
		return queryDao.query(builder,new DalHints().allowPartial());
	}

	public List<Map<String,Object>> testFreeSqlGroupByHaving2(List<String> VarCharCol) throws Exception{
		FreeSelectSqlBuilder<List<Map<String,Object>>> builder=new FreeSelectSqlBuilder<>();
		builder.append("select VarCharCol, count(*) as Count").from("all_types").groupBy("VarCharCol").having("VarCharCol in (?)").setIn("VarCharCol",VarCharCol, Types.VARCHAR);
		builder.mapWith(new DalColumnMapRowMapper());
		return queryDao.query(builder, new DalHints());
	}

	public List<MysqlAllTypesTable> testNotNotNull(int low, int upper, List<Long> BigIntCol, String VarCharCol) throws  Exception{
		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
		builder.selectAll().from("all_types").where();
		builder.notBetween("IntCol",low,upper, Types.INTEGER).and();
		builder.notIn("BigIntCol",BigIntCol, Types.BIGINT).and();
		builder.notLike("VarCharCol",VarCharCol, Types.VARCHAR);
		builder.mapWith(allTypesRowMapper);
		return queryDao.query(builder,new DalHints());
	}

	public List<MysqlAllTypesTable> testNotNullable(Integer low, Integer upper, List<Long> BigIntCol, String VarCharCol) throws  Exception{
		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
		builder.selectAll().from("all_types").where();
		builder.notBetween("IntCol",low,upper, Types.INTEGER).ignoreNull().and();
		builder.notIn("BigIntCol",BigIntCol, Types.BIGINT).ignoreNull().and();
		builder.notLike("VarCharCol",VarCharCol, Types.VARCHAR).ignoreNull();
		builder.mapWith(allTypesRowMapper);
		return queryDao.query(builder,new DalHints());
	}

	public List<MysqlAllTypesTable> testFreeSqlQueryNotNullWithSet(String CharCol, String SetCol, Boolean BitCol, Integer IntCol, Integer SmallIntCol, Integer MediumIntCol, BigDecimal DecimalCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarCharCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
//		SelectSqlBuilder builder=new SelectSqlBuilder();
//		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.selectAll();
		builder.from("all_types");
		builder.where();
		builder.like("CharCol").set("CharCol",CharCol,Types.CHAR);
		builder.and();
		builder.leftBracket();
		builder.equal("SetCol").set("SetCol", SetCol, Types.CHAR);
		builder.or();
		builder.not();
		builder.notEqual("BitCol").set("BitCol", BitCol, Types.BIT);
		builder.rightBracket();
		builder.and();
		builder.greaterThan("IntCol").set("IntCol", IntCol, Types.INTEGER);
		builder.and();
		builder.lessThan("SmallIntCol").set("SmallIntCol", SmallIntCol, Types.SMALLINT);
		builder.and();
		builder.greaterThanEquals("MediumIntCol").set("MediumIntCol", MediumIntCol, Types.INTEGER);
		builder.and();
		builder.lessThanEquals("DecimalCol").set("DecimalCol", DecimalCol, Types.DECIMAL);
		builder.and();
		builder.between("BigIntCol", BigIntCol_start, BigIntCol_end, Types.BIGINT);
		builder.and();
		builder.in("VarCharCol").setIn("VarCharCol", VarCharCol, Types.VARCHAR);
		builder.and();
		builder.isNull("FloatCol");
		builder.and();
		builder.isNotNull("DoubleCol");

		builder.orderBy("idAll_Types", false);

		builder.mapWith(allTypesRowMapper);
		return queryDao.query(builder,hints);
	}

	public List<MysqlAllTypesTable> testFreeSqlQueryNotNull(String CharCol, String SetCol, Boolean BitCol, Integer IntCol, Integer SmallIntCol, Integer MediumIntCol, BigDecimal DecimalCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarCharCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
        FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
//		SelectSqlBuilder builder=new SelectSqlBuilder();
//		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.selectAll();
		builder.from("all_types");
		builder.where();
		builder.like("CharCol", CharCol, Types.CHAR);
		builder.and();
		builder.leftBracket();
		builder.equal("SetCol", SetCol, Types.CHAR);
		builder.or();
		builder.not();
		builder.notEqual("BitCol", BitCol, Types.BIT);
		builder.rightBracket();
		builder.and();
		builder.greaterThan("IntCol", IntCol, Types.INTEGER);
		builder.and();
		builder.lessThan("SmallIntCol", SmallIntCol, Types.SMALLINT);
		builder.and();
		builder.greaterThanEquals("MediumIntCol", MediumIntCol, Types.INTEGER);
		builder.and();
		builder.lessThanEquals("DecimalCol", DecimalCol, Types.DECIMAL);
		builder.and();
		builder.between("BigIntCol", BigIntCol_start, BigIntCol_end, Types.BIGINT);
		builder.and();
		builder.in("VarCharCol", VarCharCol, Types.VARCHAR);
		builder.and();
		builder.isNull("FloatCol");
		builder.and();
		builder.isNotNull("DoubleCol");

		builder.orderBy("idAll_Types", false);

		builder.mapWith(allTypesRowMapper);
		return queryDao.query(builder, hints);
	}

	public List<MysqlAllTypesTable> testFreeSqlQueryWithAppendNullable(String CharCol, String SetCol, Boolean BitCol, Integer IntCol, Integer SmallIntCol, Integer MediumIntCol, BigDecimal DecimalCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarCharCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
//		SelectSqlBuilder builder=new SelectSqlBuilder();
//		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.selectAll();
		builder.append("from all_types where");
		builder.appendWhen(CharCol!=null,"CharCol like ?");
		builder.and();
//		builder.bracket("SetCol=? or not BitCol!=?").setNullable("SetCol", SetCol, Types.CHAR).setNullable("BitCol",BitCol, Types.BIT);
		builder.leftBracket();
		builder.appendWhen(SetCol!=null,new Expressions.Expression("SetCol=?"));
		builder.or();
		builder.not();
		builder.appendWhen(BitCol!=null,"BitCol!=?");
		builder.rightBracket();
		builder.and();
		builder.appendWhen(IntCol!=null,"IntCol>?");
		builder.and();
		builder.appendWhen(SmallIntCol!=null,"SmallIntCol<?");
		builder.and();
		builder.appendWhen(MediumIntCol!=null,"MediumIntCol>=?", "");
		builder.and();
		builder.appendWhen(DecimalCol!=null,new Expressions.Expression("DecimalCol<=?"));
		builder.and();
		builder.between("BigIntCol",BigIntCol_start,BigIntCol_end, Types.BIGINT).ignoreNull();
		builder.and();
		builder.in("VarCharCol").setInNullable("VarCharCol", VarCharCol, Types.VARCHAR);
		builder.and();
		builder.isNull("FloatCol");
		builder.and();
		builder.isNotNull("DoubleCol");

		builder.orderBy("idAll_Types", false);

		builder.mapWith(allTypesRowMapper);
		return queryDao.query(builder, hints);
	}

	public List<MysqlAllTypesTable> testFreeSqlSetNullableAndIncludeAll(String varcharcol, DalHints hints) throws Exception{
		hints=DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
		builder.append("select * from all_types");
		builder.where(builder.includeAll());
		builder.between("VarCharCol").setNullable("VarCharCol",varcharcol, Types.VARCHAR).ignoreNull();
		builder.mapWith(allTypesRowMapper);
		return queryDao.query(builder,hints);
	}

	public List<MysqlAllTypesTable> testFreeSqlSetNullableAndExcludeAll(String varcharcol, DalHints hints) throws Exception{
		hints=DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
		builder.append("select * from all_types");
		builder.where(builder.excludeAll());
		builder.append(Expressions.between("VarCharCol")).ignoreNull().setNullable("VarCharCol",varcharcol, Types.VARCHAR);
		builder.mapWith(allTypesRowMapper);
		return queryDao.query(builder,hints);
	}

	public List<MysqlAllTypesTable> testFreeSqlQueryNullable(String CharCol, String SetCol, Boolean BitCol, Integer IntCol, Integer SmallIntCol, Integer MediumIntCol, BigDecimal DecimalCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarCharCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder=new FreeSelectSqlBuilder<>();
//		SelectSqlBuilder builder=new SelectSqlBuilder();
//		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.selectAll();
		builder.from("all_types");
		builder.where();
		builder.like("CharCol", CharCol, Types.CHAR).ignoreNull();
		builder.and();
		builder.leftBracket();
		builder.equal("SetCol", SetCol, Types.CHAR).ignoreNull();
		builder.or();
		builder.not();
		builder.notEqual("BitCol", BitCol, Types.BIT).ignoreNull();
		builder.rightBracket();
		builder.and();
		builder.greaterThan("IntCol", IntCol, Types.INTEGER).ignoreNull();
		builder.and();
		builder.lessThan("SmallIntCol", SmallIntCol, Types.SMALLINT).ignoreNull();
		builder.and();
		builder.greaterThanEquals("MediumIntCol", MediumIntCol, Types.INTEGER).ignoreNull();
		builder.and();
		builder.lessThanEquals("DecimalCol", DecimalCol, Types.DECIMAL).ignoreNull();
		builder.and();
		builder.between("BigIntCol", BigIntCol_start, BigIntCol_end, Types.BIGINT).ignoreNull();
		builder.and();
		builder.in("VarCharCol", VarCharCol, Types.VARCHAR).ignoreNull();
		builder.and();
		builder.isNull("FloatCol");
		builder.and();
		builder.isNotNull("DoubleCol");

		builder.orderBy("idAll_Types", false);

		builder.mapWith(allTypesRowMapper);
		return queryDao.query(builder, hints);
	}

	/**
	 * 构建,query
	**/
	public List<MysqlAllTypesTable> test_build_query_notnull(String CharCol, String SetCol, Boolean BitCol, Integer IntCol, Integer SmallIntCol, Integer MediumIntCol, BigDecimal DecimalCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarCharCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
//		SelectSqlBuilder builder = new SelectSqlBuilder("all_types", dbCategory, false);
		SelectSqlBuilder builder=new SelectSqlBuilder();
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
		return client.query(builder, hints);
	}
	/**
	 * 构建,query,nullable
	**/
	public List<MysqlAllTypesTable> test_build_query_nullable(String CharCol, String SetCol, Boolean BitCol, Integer IntCol, Integer SmallIntCol, Integer MediumIntCol, BigDecimal DecimalCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarCharCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
//		SelectSqlBuilder builder = new SelectSqlBuilder("all_types", dbCategory, false);
		SelectSqlBuilder builder=new SelectSqlBuilder();
		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.likeNullable("CharCol", CharCol, Types.CHAR, false);
		builder.and();
		builder.leftBracket();
		builder.equalNullable("SetCol", SetCol, Types.CHAR, false);
		builder.or();
		builder.not();
		builder.notEqualNullable("BitCol", BitCol, Types.BIT, false);
		builder.rightBracket();
		builder.and();
		builder.greaterThanNullable("IntCol", IntCol, Types.INTEGER, false);
		builder.and();
		builder.lessThanNullable("SmallIntCol", SmallIntCol, Types.SMALLINT, false);
		builder.and();
		builder.greaterThanEqualsNullable("MediumIntCol", MediumIntCol, Types.INTEGER, false);
		builder.and();
		builder.lessThanEqualsNullable("DecimalCol", DecimalCol, Types.DECIMAL, false);
		builder.and();
		builder.betweenNullable("BigIntCol", BigIntCol_start, BigIntCol_end, Types.BIGINT, false);
		builder.and();
		builder.inNullable("VarCharCol", VarCharCol, Types.VARCHAR, false);
		builder.and();
		builder.isNull("FloatCol");
		builder.and();
		builder.isNotNull("DoubleCol");
		builder.orderBy("idAll_Types", false);
//	    String sql = builder.build();
//		StatementParameters parameters = builder.buildParameters();
//		return queryDao.query(sql, parameters, hints, parser);
//		return queryDao.queryFirstNullable(sql, parameters, hints, parser);
		return client.query(builder, hints);
	}

	/**
	 * 构建,query,nullable
	**/
	public MysqlAllTypesTable test_build_query_firstnullable(String CharCol, String SetCol, Boolean BitCol, Integer IntCol, Integer SmallIntCol, Integer MediumIntCol, BigDecimal DecimalCol, Long BigIntCol_start, Long BigIntCol_end, List<String> VarCharCol, Date DateCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder=new SelectSqlBuilder();
		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.likeNullable("CharCol", CharCol, Types.CHAR, false);
		builder.and();
		builder.leftBracket();
		builder.equalNullable("SetCol", SetCol, Types.CHAR, false);
		builder.or();
		builder.not();
		builder.notEqualNullable("BitCol", BitCol, Types.BIT, false);
		builder.rightBracket();
		builder.and();
		builder.greaterThanNullable("IntCol", IntCol, Types.INTEGER, false);

		builder.and();
		builder.lessThanNullable("SmallIntCol", SmallIntCol, Types.SMALLINT, false);
		builder.and();
		builder.greaterThanEqualsNullable("MediumIntCol", MediumIntCol, Types.INTEGER, false);
		builder.and();
		builder.lessThanEqualsNullable("DecimalCol", DecimalCol, Types.DECIMAL, false);
		builder.and();
		builder.betweenNullable("BigIntCol", BigIntCol_start, BigIntCol_end, Types.BIGINT, false);
		builder.and();
		builder.inNullable("VarCharCol", VarCharCol, Types.VARCHAR, false);
		builder.and();
		builder.equal("DateCol", DateCol, Types.DATE, false);;
		builder.and();
		builder.isNull("FloatCol");
		builder.and();
		builder.isNotNull("DoubleCol");
		builder.orderBy("idAll_Types", false);

		builder.requireFirst();
		return client.queryObject(builder, hints);

	}

    /**
	 * 构建，查询，filedList
	**/
	public List<Integer> testBuildQueryFieldList(Integer id) throws SQLException {
		return testBuildQueryFieldList(id, null);
	}

	/**
	 * 构建，查询，filedList
	**/
	public List<Integer> testBuildQueryFieldList(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("IntCol");
		builder.greaterThan("idAll_Types", id, Types.INTEGER, false);

		return client.query(builder, hints, Integer.class);
	}

    /**
	 * 构建，查询，fieldListByPage
	**/
	public List<Integer> testBuildQueryFieldListByPage(Integer id, int pageNo, int pageSize) throws SQLException {
		return testBuildQueryFieldListByPage(id, pageNo, pageSize, null);
	}

	/**
	 * 构建，查询，fieldListByPage
	**/
	public List<Integer> testBuildQueryFieldListByPage(Integer id, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("IntCol");
		builder.greaterThan("idAll_Types", id, Types.INTEGER, false);
		builder.orderBy("idAll_Types", false);
		builder.atPage(pageNo, pageSize);

		return client.query(builder, hints, Integer.class);
	}

    /**
	 * 构建，查询，fieldsingle
	**/
	public Integer testBuildQueryFieldSingle(Integer id) throws SQLException {
		return testBuildQueryFieldSingle(id, null);
	}

	/**
	 * 构建，查询，fieldsingle
	**/
	public Integer testBuildQueryFieldSingle(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("IntCol");
		builder.equal("idAll_Types", id, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints, Integer.class);
	}

    /**
	 * 构建，查询，fieldFirst
	**/
	public Integer testBuildQueryFieldFirst(Integer id) throws SQLException {
		return testBuildQueryFieldFirst(id, null);
	}

	/**
	 * 构建，查询，fieldFirst
	**/
	public Integer testBuildQueryFieldFirst(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("IntCol");
		builder.greaterThan("idAll_Types", id, Types.INTEGER, false);
		builder.orderBy("idAll_Types", false);
		builder.requireFirst();

		return client.queryObject(builder, hints, Integer.class);
	}

    /**
	 * 构建，查询，pojoList
	**/
	public List<MysqlAllTypesTable> testBuildQueryPojoList(Integer id) throws SQLException {
		return testBuildQueryPojoList(id, null);
	}

	/**
	 * 构建，查询，pojoList
	**/
	public List<MysqlAllTypesTable> testBuildQueryPojoList(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TimeStampCol2","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.greaterThanNullable("idAll_Types", id, Types.INTEGER, true);

		return client.query(builder, hints);
	}

    /**
	 * 构建，查询，pojoListByPage
	**/
	public List<MysqlAllTypesTable> testBuildQueryPojoListByPage(Integer id, int pageNo, int pageSize) throws SQLException {
		return testBuildQueryPojoListByPage(id, pageNo, pageSize, null);
	}

	/**
	 * 构建，查询，pojoListByPage
	**/
	public List<MysqlAllTypesTable> testBuildQueryPojoListByPage(Integer id, int pageNo, int pageSize, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TimeStampCol2","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.greaterThan("idAll_Types", id, Types.INTEGER, false);
		builder.orderBy("idAll_Types", false);
		builder.atPage(pageNo, pageSize);

		return client.query(builder, hints);
	}

    /**
	 * 构建，查询，pojoSingle
	**/
	public MysqlAllTypesTable testBuildQueryPojoSingle(Integer id) throws SQLException {
		return testBuildQueryPojoSingle(id, null);
	}

	/**
	 * 构建，查询，pojoSingle
	**/
	public MysqlAllTypesTable testBuildQueryPojoSingle(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TimeStampCol2","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.equal("idAll_Types", id, Types.INTEGER, false);
		builder.requireSingle();

		return client.queryObject(builder, hints);
	}

    /**
	 * 构建，查询，pojoFirst
	**/
	public MysqlAllTypesTable testBuildQueryPojoFirst(Integer id) throws SQLException {
		return testBuildQueryPojoFirst(id, null);
	}

	/**
	 * 构建，查询，pojoFirst
	**/
	public MysqlAllTypesTable testBuildQueryPojoFirst(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select("SetCol","TextCol","DecimalCol","TimeCol","FloatCol","BinaryCol","LongTextCol","BitCol","BlobCol","VarCharCol","DoubleCol","TinyBlobCol","MediumIntCol","CharCol","TimeStampCol","EnumCol","TinyIntCol","YearCol","SmallIntCol","MediumBlobCol","TimeStampCol2","TinyTextCol","MediumTextCol","BigIntCol","DateTimeCol","LongBlobCol","GeometryCol","IntCol","idAll_Types","DateCol","VarBinaryCol");
		builder.greaterThan("idAll_Types", id, Types.INTEGER, false);
		builder.orderBy("idAll_Types", false);
	    builder.requireFirst();

		return client.queryObject(builder, hints);
	}

    /**
	 * 构建，update
	**/
	public int testBuildUpdate(String VarCharCol, Integer intcol) throws SQLException {
		return testBuildUpdate(VarCharCol, intcol, null);
	}

	/**
	 * 构建，update
	**/
	public int testBuildUpdate(String VarCharCol, Integer intcol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
		builder.update("VarCharCol", VarCharCol, Types.VARCHAR);
		builder.equal("idAll_Types", intcol, Types.INTEGER, false);

		return client.update(builder, hints);
	}

    /**
	 * 构建，insert
	**/
	public int testBuildInsert(String VarCharCol, Integer TinyIntCol) throws SQLException {
		return testBuildInsert(VarCharCol, TinyIntCol, null);
	}

	/**
	 * 构建，insert
	**/
	public int testBuildInsert(String VarCharCol, Integer TinyIntCol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		InsertSqlBuilder builder = new InsertSqlBuilder();
		builder.set("VarCharCol", VarCharCol, Types.VARCHAR);
		builder.setSensitive("TinyIntCol", TinyIntCol, Types.TINYINT);

		return client.insert(builder, hints);
	}

    /**
	 * 构建，delete
	**/
	public int testBuildDelete(Integer id) throws SQLException {
		return testBuildDelete(id, null);
	}

	/**
	 * 构建，delete
	**/
	public int testBuildDelete(Integer id, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		DeleteSqlBuilder builder = new DeleteSqlBuilder();
		builder.equalNullable("idAll_Types", id, Types.INTEGER, true);

		return client.delete(builder, hints);
	}

	/**
	 * all columns
	 **/
	public List<MysqlAllTypesTable> testDefaultTypesWithAllColumns(Integer intcol, Long bigintcol, Integer mediumintcol, Integer smallintcol, Integer tinyintcol, Float floatcol, Double doublecol, BigDecimal decimalcol, List<String> charcol, List<String> varcharcol, Date datecol, Timestamp datetimecol, Time timecol, List<String> longtextcol, List<String> mediumtextcol, String textcol, String tinytextcol, Boolean bitcol, String enumcol, String setcol, DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<List<MysqlAllTypesTable>> builder = new FreeSelectSqlBuilder();
		builder.setTemplate("select * from all_types where intcol=? and bigintcol=? and mediumintcol=? and smallintcol=? and tinyintcol=? and floatcol>? and doublecol>? and decimalcol>? and charcol in (?) and varcharcol in (?) and datecol=? and datetimecol=? and timecol=? and longtextcol in (?) and mediumtextcol in (?) and textcol=? and tinytextcol=? and bitcol=? and enumcol=? and setcol=? order by idall_types");
		StatementParameters parameters = new StatementParameters();
		int i = 1;
		parameters.set(i++, "intcol", intcol);
		parameters.set(i++,  bigintcol);
		parameters.setSensitive(i++, "mediumintcol", mediumintcol);
		parameters.setSensitive(i++,  smallintcol);
		parameters.setSensitive(i++, "tinyintcol", tinyintcol);
		parameters.setSensitive(i++, floatcol);
		parameters.setSensitive(i++, "doublecol", doublecol);
		parameters.setSensitive(i++,  decimalcol);
		i=parameters.setInParameter(i++, "charcol", charcol);
		i=parameters.setSensitiveInParameter(i++, "varcharcol", varcharcol);
		parameters.setSensitive(i++, "datecol", datecol);
		parameters.setSensitive(i++,  datetimecol);
		parameters.setSensitive(i++, "timecol", timecol);
		i=parameters.setInParameter(i++, longtextcol);
		i=parameters.setSensitiveInParameter(i++, mediumtextcol);
		parameters.setSensitive(i++, "textcol", textcol);
		parameters.setSensitive(i++,  tinytextcol);
		parameters.setSensitive(i++, "bitcol", bitcol);
		parameters.setSensitive(i++, "enumcol", enumcol);
		parameters.setSensitive(i++,  setcol);
		builder.mapWith(allTypesRowMapper);

		return queryDao.query(builder, parameters, hints);
	}
}
