package dao.noshard;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import entity.MysqlPersonTable;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lilj on 2017/7/24.
 */
public class NoShardTransactionTestOnMysqlDao {
    private static final boolean ASC = true;
    private DalTableDao<MysqlPersonTable> client;
    private static final String DATA_BASE = "noShardTestOnMysql";
    private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
    private DalQueryDao queryDao = null;

    private DalRowMapper<MysqlPersonTable> personGenRowMapper = null;

    public NoShardTransactionTestOnMysqlDao() throws SQLException {
        this.client = new DalTableDao<>(new DalDefaultJpaParser<>(MysqlPersonTable.class,DATA_BASE));
        this.personGenRowMapper = new DalDefaultJpaMapper<>(MysqlPersonTable.class);
        this.queryDao = new DalQueryDao(DATA_BASE);
    }

//	/**
//	 * Query PersonGen by the specified ID
//	 * The ID must be a number
//	**/
//	public ShardTestTransactionTestOnMysql queryByPk(Number id, DalHints hints)
//			throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//		return client.queryByPk(id, hints);
//	}

    /**
     * Query Person by complex primary key
     **/
    public MysqlPersonTable queryByPk(Integer iD, DalHints hints)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        MysqlPersonTable pk = new MysqlPersonTable();
        pk.setID(iD);
        return client.queryByPk(pk, hints);
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
     * Query against sample pojo. All not null attributes of the passed in pojo
     * will be used as search criteria.
     **/
    public List<MysqlPersonTable> queryLike(MysqlPersonTable sample, DalHints hints)
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

    public int countWhereCondition(String RightsName,DalHints hints) throws SQLException {
        String whereCondition ="1=1 and Age in (select Age from person where Name like ?)";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, Types.VARCHAR, "%" + RightsName + "%");
        return client.count(whereCondition, parameters, hints).intValue();
    }

    /**
     * Query Person with paging function
     * The pageSize and pageNo must be greater than zero.
     */
    public List<MysqlPersonTable> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.selectAll().atPage(pageNo, pageSize).orderBy("ID,Name,Age", ASC);

        return client.query(builder, hints);
    }

    /**
     * Get all records from table
     */
    public List<MysqlPersonTable> queryAll(DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("ID,Name,Age", ASC);

        return client.query(builder, hints);
    }

//	/**
//	 * Query PersonGen with paging function
//	 * The pageSize and pageNo must be greater than zero.
//	 */
//	public List<ShardTestTransactionTestOnMysql> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//
//		SelectSqlBuilder builder = new SelectSqlBuilder();
//		builder.selectAll().atPage(pageNo, pageSize).orderBy("ID", ASC);
//
//		return client.query(builder, hints);
//	}
//	
//	/**
//	 * Get all records from table
//	 */
//	public List<ShardTestTransactionTestOnMysql> queryAll(DalHints hints) throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//		
//		SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("ID", ASC);
//		
//		return client.query(builder, hints);
//	}

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
     * 构建，查询
     **/
    public List<String> test_build_query_fieldList_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Name");
        builder.in("Age", Age, Types.INTEGER, false);
        builder.orderBy("Age", false);
        builder.orderBy("ID", true);
        return client.query(builder, hints, String.class);
    }

    /**
     * 构建，查询
     **/
    public List<String> test_build_query_fieldList_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Name");
        builder.in("Age", Age, Types.INTEGER, false);
        builder.orderBy("ID", true);
        builder.orderBy("Age", false);
        return client.query(builder, hints, String.class);
    }

    /**
     * 构建，查询
     **/
    public List<String> test_build_query_fieldListByPage_multipleOrderBy(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Name");
        builder.in("Age", Age, Types.INTEGER, false);
        builder.orderBy("Age", false);
        builder.orderBy("ID", true);
        builder.atPage(pageNo, pageSize);

        return client.query(builder, hints, String.class);
    }

    /**
     * 构建，查询
     **/
    public List<String> test_build_query_fieldListByPage_multipleOrderByReverse(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Name");
        builder.in("Age", Age, Types.INTEGER, false);
        builder.orderBy("ID", true);
        builder.orderBy("Age", false);
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
    public String test_build_query_field_first_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Name");
        builder.in("Age", Age, Types.INTEGER, false);
        builder.orderBy("Age", false);
        builder.orderBy("ID", true);
        builder.requireFirst();

        return client.queryObject(builder, hints, String.class);
    }

    /**
     * 构建，查询
     **/
    public String test_build_query_field_first_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Name");
        builder.in("Age", Age, Types.INTEGER, false);
        builder.orderBy("ID", true);
        builder.orderBy("Age", false);

        builder.requireFirst();

        return client.queryObject(builder, hints, String.class);
    }

    /**
     * 构建，查询
     **/
    public List<MysqlPersonTable> test_ClientQueryFrom_list(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

        return client.queryFrom("Age in ?", parameters, hints, start, count);
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

        return client.queryFrom("Age in ?", parameters, hints.partialQuery(columns), start, count);
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

        return client.queryFrom("Age in ?", parameters, hints.partialQuery("Age","Name"), start, count);
    }

    /**
     * 构建，查询
     **/
    public List<MysqlPersonTable> test_build_query_list_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Birth","Name","Age","ID");
//		builder.selectAll();
//		builder.equalNullable("1", 1, Types.INTEGER, false);
//		builder.and();
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("Age", false);
        builder.orderBy("ID", true);
//		builder.and();
        return client.query(builder, hints);


    }

    /**
     * 构建，查询
     **/
    public List<MysqlPersonTable> test_build_query_list_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Birth","Name","Age","ID");
//		builder.selectAll();
//		builder.equalNullable("1", 1, Types.INTEGER, false);
//		builder.and();
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("ID", true);
        builder.orderBy("Age", false);

//		builder.and();
        return client.query(builder, hints);
    }

    /**
     * 构建，查询部分字段
     **/
    public List<MysqlPersonTable> test_build_queryPartial_list_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Name","ID");
//		builder.selectAll();
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("Age", false);
        builder.orderBy("ID", true);
        return client.query(builder, hints);
    }

    /**
     * 构建，查询部分字段
     **/
    public List<MysqlPersonTable> test_build_queryPartial_list_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Name","ID");
//		builder.selectAll();
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("ID", true);
        builder.orderBy("Age", false);

        return client.query(builder, hints);
    }

    /**
     * 构建，查询
     **/
    public List<MysqlPersonTable> test_build_query_listByPage_multipleOrderBy(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Birth","Name","Age","ID");
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("Age", false);
        builder.orderBy("ID", true);
//		builder.range(start, count)
        builder.atPage(pageNo, pageSize);
        return client.query(builder, hints);
    }

    /**
     * 构建，查询
     **/
    public List<MysqlPersonTable> test_build_query_listByPage_multipleOrderByReverse(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Birth","Name","Age","ID");
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("ID", true);
        builder.orderBy("Age", false);

//		builder.range(start, count)
        builder.atPage(pageNo, pageSize);
        return client.query(builder, hints);
    }

    /**
     * 构建，查询部分字段
     **/
    public List<MysqlPersonTable> test_build_queryPartial_listByPage_multipleOrderBy(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Name","Age","ID");
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("Age", false);
        builder.orderBy("ID", true);
//		builder.range(start, count)
        builder.atPage(pageNo, pageSize);
        return client.query(builder, hints);
    }

    /**
     * 构建，查询部分字段
     **/
    public List<MysqlPersonTable> test_build_queryPartial_listByPage_multipleOrderByReverse(List<Integer> Age, int pageNo, int pageSize, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Name","Age","ID");
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("ID", true);
        builder.orderBy("Age", false);

//		builder.range(start, count)
        builder.atPage(pageNo, pageSize);
        return client.query(builder, hints);
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

    /**
     * 构建，查询
     **/
    public MysqlPersonTable test_build_query_first_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Birth","Name","Age","ID");
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("Age", false);
        builder.orderBy("ID",true);
        builder.requireFirst();
        return client.queryObject(builder, hints);
    }

    /**
     * 构建，查询
     **/
    public MysqlPersonTable test_build_query_first_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Birth","Name","Age","ID");
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("ID",true);
        builder.orderBy("Age", false);

        builder.requireFirst();
        return client.queryObject(builder, hints);
    }

    /**
     * 构建，查询
     **/
    public MysqlPersonTable test_build_queryPartial_first_multipleOrderBy(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Age","ID","Name");
//		builder.and();
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("Age", false);
        builder.orderBy("ID", true);
        builder.requireFirst();

        return client.queryObject(builder, hints);
    }

    /**
     * 构建，查询
     **/
    public MysqlPersonTable test_build_queryPartial_first_multipleOrderByReverse(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.select("Age","ID","Name");
//		builder.and();
        builder.inNullable("Age", Age, Types.INTEGER, false);
        builder.orderBy("ID", true);
        builder.orderBy("Age", false);

        builder.requireFirst();

        return client.queryObject(builder, hints);
    }


    /**
     * 自定义，查询
     **/
    public List<MysqlPersonTable> test_def_query(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

//		FreeSelectSqlBuilder<List<ShardTestTransactionTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
//		builder.setTemplate("select * from person where Age in (?)");
        String sql="select * from person where Age in (?)";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

//		builder.mapWith(personGenRowMapper);

        return queryDao.query(sql, parameters, hints, MysqlPersonTable.class);
    }

    /**
     * 自定义，查询
     **/
    public List<MysqlPersonTable> test_def_partialQuery(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

//		FreeSelectSqlBuilder<List<ShardTestTransactionTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
//		builder.setTemplate("select * from person where Age in (?)");
        String sql="select * from person where Age in (?)";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

//		builder.mapWith(personGenRowMapper);

        return queryDao.query(sql, parameters, hints.partialQuery("Name"), MysqlPersonTable.class);
    }

    /**
     * 自定义，查询
     **/
    public MysqlPersonTable test_def_queryForObject(List<Integer> ID, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where ID in (?)";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "ID", Types.INTEGER, ID);

        return queryDao.queryForObject(sql, parameters, hints, MysqlPersonTable.class);
    }

    /**
     * 自定义，查询
     **/
    public MysqlPersonTable test_def_partialQueryForObject(List<Integer> ID, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where ID in (?)";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "ID", Types.INTEGER, ID);

        return queryDao.queryForObject(sql, parameters, hints.partialQuery("Name"), MysqlPersonTable.class);
    }

    /**
     * 自定义，查询
     **/
    public MysqlPersonTable test_def_queryForObjectNullable(List<Integer> ID, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where ID in (?)";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "ID", Types.INTEGER, ID);

        return queryDao.queryForObjectNullable(sql, parameters, hints, MysqlPersonTable.class);
    }

    /**
     * 自定义，查询
     **/
    public MysqlPersonTable test_def_partialQueryForObjectNullable(List<Integer> ID, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where ID in (?)";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "ID", Types.INTEGER, ID);

        return queryDao.queryForObjectNullable(sql, parameters, hints.partialQuery("Name"), MysqlPersonTable.class);
    }

    /**
     * 自定义，查询
     **/
    public MysqlPersonTable test_def_queryFirst(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where Age in (?) order by ID desc";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

        return queryDao.queryFirst(sql, parameters, hints, MysqlPersonTable.class);
    }

    /**
     * 自定义，查询
     **/
    public MysqlPersonTable test_def_partialQueryFirst(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where Age in (?) order by ID desc";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

        return queryDao.queryFirst(sql, parameters, hints.partialQuery("Name"), MysqlPersonTable.class);
    }

    /**
     * 自定义，查询
     **/
    public MysqlPersonTable test_def_queryFirstNullable(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where Age in (?) order by ID desc";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

        return queryDao.queryFirstNullable(sql, parameters, hints, MysqlPersonTable.class);
    }

    /**
     * 自定义，查询
     **/
    public MysqlPersonTable test_def_partialQueryFirstNullable(List<Integer> Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where Age in (?) order by ID desc";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

        return queryDao.queryFirstNullable(sql, parameters, hints.partialQuery("Name"), MysqlPersonTable.class);
    }

    /**
     * 自定义，查询
     **/
    public List<MysqlPersonTable> test_def_queryTop(List<Integer> Age, DalHints hints, int count) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where Age in (?) order by ID desc";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

        return queryDao.queryTop(sql, parameters, hints, MysqlPersonTable.class,count);
    }

    /**
     * 自定义，查询
     **/
    public List<MysqlPersonTable> test_def_partialQueryTop(List<Integer> Age, DalHints hints, int count) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where Age in (?) order by ID desc";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

        return queryDao.queryTop(sql, parameters, hints.partialQuery("Name"), MysqlPersonTable.class,count);
    }

    /**
     * 自定义，查询
     **/
    public List<MysqlPersonTable> test_def_queryFrom(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where Age in (?) order by ID desc";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

        return queryDao.queryFrom(sql, parameters, hints, MysqlPersonTable.class,start,count);
    }

    /**
     * 自定义，查询
     **/
    public List<MysqlPersonTable> test_def_partialQueryFrom(List<Integer> Age, DalHints hints, int start, int count) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        String sql="select * from person where Age in (?) order by ID desc";
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        i = parameters.setInParameter(i, "Age", Types.INTEGER, Age);

        return queryDao.queryFrom(sql, parameters, hints.partialQuery("Name"), MysqlPersonTable.class,start,count);
    }

    /**
     * mysql, dao.noshardtest
     **/
    public int test_def_update (DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("truncate person");
        StatementParameters parameters = new StatementParameters();
        int i = 1;

        return queryDao.update(builder, parameters, hints);
    }

    /**
     * mysql, dao.noshardtest
     **/
//	public int test_def_update (DalHints hints,String tableShardID) throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//
//		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
//		builder.setTemplate("truncate person_"+tableShardID);
//		StatementParameters parameters = new StatementParameters();
//		int i = 1;
//
//		return queryDao.update(builder, parameters, hints);
//	}

    /**
     * ss
     **/
//	public List<ShardTestTransactionTestOnMysql> test_def_query(DalHints hints) throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//
//		FreeSelectSqlBuilder<List<ShardTestTransactionTestOnMysql>> builder = new FreeSelectSqlBuilder<>(dbCategory);
//		builder.setTemplate("select * from person");
//		StatementParameters parameters = new StatementParameters();
//		builder.mapWith(personGenRowMapper);
//
//		return queryDao.query(builder, parameters, hints);
//	}

    /**
     * mysql, dao.noshardtest
     **/
//	public int test_def_update (DalHints hints) throws SQLException {
//		hints = DalHints.createIfAbsent(hints);
//
//		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
//		builder.setTemplate("truncate person");
//		StatementParameters parameters = new StatementParameters();
//		int i = 1;
//
//		return queryDao.update(builder, parameters, hints);
//	}

    /**
     * mysql, dao.noshardtest
     **/
    public int test_def_update (DalHints hints,String tableShardID) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("truncate person_"+tableShardID);
        StatementParameters parameters = new StatementParameters();
        int i = 1;

        return queryDao.update(builder, parameters, hints);
    }

    /*@Transactional(logicDbName = DATA_BASE)
    public void transPass() throws Exception{
        MysqlPersonTable ret=queryByPk(1,null);
        ret.setAge(99);
        update(null,ret);
        ret.setID(3);
        delete(null,ret);
    }

    @Transactional(logicDbName = DATA_BASE)
    public void transFail() throws Exception{
            MysqlPersonTable ret=queryByPk(1,null);
            delete(null,ret);
            ret.setID(3);
            ret.setAge(99);
            update(null,ret);
            throw new SQLException();
    }*/

}
