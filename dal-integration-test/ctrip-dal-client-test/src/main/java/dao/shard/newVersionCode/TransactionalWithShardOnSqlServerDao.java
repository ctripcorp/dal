package dao.shard.newVersionCode;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.annotation.Shard;
import com.ctrip.platform.dal.dao.annotation.Transactional;
import entity.SqlServerPeopleTable;

import java.sql.SQLException;

/**
 * Created by lilj on 2017/7/24.
 */
public class TransactionalWithShardOnSqlServerDao extends TransactionWithShardOnSqlServerDao {
    private static final String DATA_BASE = "ShardColModByDBTableOnSqlServer";
    public TransactionalWithShardOnSqlServerDao() throws SQLException {
//		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(SqlServerPeopleTable.class));
//        this.client =new DalTableDao<>(SqlServerPeopleTable.class);
//        this.queryDao = new DalQueryDao(DATA_BASE);
    }
    /*private static final boolean ASC = true;
    private DalTableDao<SqlServerPeopleTable> client;
    private static final DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
    private DalQueryDao queryDao = null;
    private static final String DATA_BASE = "ShardColModByDBTableOnSqlServer";

    public TransactionalWithShardOnSqlServerDao() throws SQLException {
//		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(SqlServerPeopleTable.class));
        this.client =new DalTableDao<>(SqlServerPeopleTable.class);
        this.queryDao = new DalQueryDao(DATA_BASE);
    }

    *//**
     * Query PeopleGen by the specified ID
     * The ID must be a number
     **//*
    public SqlServerPeopleTable queryByPk(Number id, DalHints hints)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        return client.queryByPk(id, hints);
    }

    *//**
     * Query PeopleGen by PeopleGen instance which the primary key is set
     **//*
    public SqlServerPeopleTable queryByPk(SqlServerPeopleTable pk, DalHints hints)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        return client.queryByPk(pk, hints);
    }

    *//**
     * Query against sample pojo. All not null attributes of the passed in pojo
     * will be used as search criteria.
     **//*
    public List<SqlServerPeopleTable> queryLike(SqlServerPeopleTable sample, DalHints hints)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        return client.queryLike(sample, hints);
    }

    *//**
     * Get the all records count
     *//*
    public int count(DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        SelectSqlBuilder builder = new SelectSqlBuilder().selectCount();
        return client.count(builder, hints).intValue();
    }

    *//**
     * Query PeopleGen with paging function
     * The pageSize and pageNo must be greater than zero.
     *//*
    public List<SqlServerPeopleTable> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder();
        builder.selectAll().atPage(pageNo, pageSize).orderBy("PeopleID", ASC);

        return client.query(builder, hints);
    }

    *//**
     * Get all records from table
     *//*
    public List<SqlServerPeopleTable> queryAll(DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("PeopleID", ASC);

        return client.query(builder, hints);
    }

    *//**
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
     *//*
    public int insert(DalHints hints, SqlServerPeopleTable daoPojo) throws SQLException {
        if(null == daoPojo)
            return 0;
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, daoPojo);
    }

    *//**
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
     *//*
    public int[] insert(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
        if(null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, daoPojos);
    }

    *//**
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
     *//*
    public int insert(DalHints hints, KeyHolder keyHolder, SqlServerPeopleTable daoPojo) throws SQLException {
        if(null == daoPojo)
            return 0;
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, keyHolder, daoPojo);
    }

    *//**
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
     *//*
    public int[] insert(DalHints hints, KeyHolder keyHolder, List<SqlServerPeopleTable> daoPojos) throws SQLException {
        if(null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, keyHolder, daoPojos);
    }

    *//**
     * Insert pojos in batch mode. 
     * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected for inserting each of the pojo
     * @throws SQLException
     *//*
    public int[] batchInsert(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
        if(null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.batchInsert(hints, daoPojos);
    }

    *//**
     * Delete the given pojo.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojo pojo to be deleted
     * @return how many rows been affected
     * @throws SQLException
     *//*
    public int delete(DalHints hints, SqlServerPeopleTable daoPojo) throws SQLException {
        if(null == daoPojo)
            return 0;
        hints = DalHints.createIfAbsent(hints);
        return client.delete(hints, daoPojo);
    }

    *//**
     * Delete the given pojos list one by one.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojos list of pojos to be deleted
     * @return how many rows been affected
     * @throws SQLException
     *//*
    public int[] delete(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
        if(null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.delete(hints, daoPojos);
    }

    *//**
     * Delete the given pojo list in batch. 
     * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the dao.noshardtest.shardtest.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojos list of pojos to be deleted
     * @return how many rows been affected for deleting each of the pojo
     * @throws SQLException
     *//*
    public int[] batchDelete(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
        if(null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.batchDelete(hints, daoPojos);
    }

    *//**
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
     *//*
    public int update(DalHints hints, SqlServerPeopleTable daoPojo) throws SQLException {
        if(null == daoPojo)
            return 0;
        hints = DalHints.createIfAbsent(hints);
        return client.update(hints, daoPojo);
    }

    *//**
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
     *//*
    public int[] update(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
        if(null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.update(hints, daoPojos);
    }

    *//**
     * Update the given pojo list in batch. 
     *
     * @return how many rows been affected
     * @throws SQLException
     *//*
    public int[] batchUpdate(DalHints hints, List<SqlServerPeopleTable> daoPojos) throws SQLException {
        if(null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.batchUpdate(hints, daoPojos);
    }

    *//**
     * truncate
     **//*
    public int truncate (DalHints hints,String tableShardID) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("truncate table people"+tableShardID);
        StatementParameters parameters = new StatementParameters();
        int i = 1;

        return queryDao.update(builder, parameters, hints);
    }*/

    //不传shardID，不传dalhints，代码内设置hints Shardid
    @Transactional(logicDbName = DATA_BASE)
    public void transWithoutShardIDAndDalHintsWithNestHints() throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,new DalHints().inShard(0).inTableShard(0));
        ret.setName("transWithoutShardIDAndDalHintsWithNestHints");
        update(new DalHints().setShardValue(20).setTableShardValue(21),ret);
        ret.setPeopleID(3L);
        delete(new DalHints().setShardValue(20).inTableShard(1),ret);
    }

    //不传shardID，不传dalhints，代码内没有设置hints Shardid
    @Transactional(logicDbName = DATA_BASE)
    public void transWithoutShardIDAndDalHints() throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,new DalHints().inTableShard(0));
        ret.setName("transWithoutShardIDAndDalHints");
        update(new DalHints().setTableShardValue(21),ret);
        ret.setPeopleID(3L);
        delete(new DalHints().inTableShard(1),ret);
    }

    //不传shardID,传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithoutShardIDWithDalHints(DalHints hints) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,hints.inTableShard(0));
        ret.setName("transWithoutShardIDWithDalHints");
        update(hints.setTableShardValue(21),ret);
        ret.setPeopleID(3L);
        delete(hints.inTableShard(1),ret);
    }

    //传string @Shard，不传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithStringShardIDWithoutDalHints(@Shard String shardID) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,new DalHints().inTableShard(0));
        ret.setName("transWithStringShardIDWithoutDalHints");
        update(new DalHints().setTableShardValue(20),ret);
        ret.setPeopleID(3L);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传string @Shard，传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithStringShardIDWithDalHints(@Shard String shardID,DalHints hints) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,hints.inTableShard(0));
        ret.setName("transWithStringShardIDWithDalHints");
        update(hints.setTableShardValue(20),ret);
        ret.setPeopleID(3L);
        delete(hints.inTableShard(1),ret);
    }

    //传int @Shard，不传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntShardIDWithoutDalHints(@Shard int shardID) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,new DalHints().inTableShard(0));
        ret.setName("transWithIntShardIDWithoutDalHints");
        update(new DalHints().setTableShardValue(20),ret);
        ret.setPeopleID(3L);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传int @Shard，传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntShardIDWithDalHints(@Shard int shardID,DalHints hints) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,hints.inTableShard(0));
        ret.setName("transWithIntShardIDWithDalHints");
        update(hints.setTableShardValue(20),ret);
        ret.setPeopleID(3L);
        delete(hints.inTableShard(1),ret);
    }

    //传integer @Shard，不传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntegerShardIDWithoutDalHints(@Shard Integer shardID) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,new DalHints().inTableShard(0));
        ret.setName("transWithIntegerShardIDWithoutDalHints");
        update(new DalHints().setTableShardValue(20),ret);
        ret.setPeopleID(3L);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传integer @Shard，传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntegerShardIDWithDalHints(@Shard Integer shardID,DalHints hints) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,hints.inTableShard(0));
        ret.setName("transWithIntegerShardIDWithDalHints");
        update(hints.setTableShardValue(20),ret);
        ret.setPeopleID(3L);
        delete(hints.inTableShard(1),ret);
    }

    //传string shardID，没传dalhints,事务内dalhints指定string shardID
    @Transactional(logicDbName = DATA_BASE)
    public void transWithStringShardIDVSNestStringHints(@Shard String shardID) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,new DalHints().inShard("0").inTableShard(0));
        ret.setName("transWithStringShardIDVSNestStringHints");
        update(new DalHints().setShardValue("20").setTableShardValue(21),ret);
        ret.setPeopleID(3L);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传string shardID，没传dalhints，事务内dalhints指定int shardID
    @Transactional(logicDbName = DATA_BASE)
    public void transWithStringShardIDVSNestIntHints(@Shard String shardID) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,new DalHints().inShard(0).inTableShard(0));
        ret.setName("transWithStringShardIDVSNestIntHints");
        update(new DalHints().setShardValue(20).setTableShardValue(21),ret);
        ret.setPeopleID(3L);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传int shardID，没传dalhints，事务内dalhints指定string shardID
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntShardIDVSNestStringHints(@Shard int shardID) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,new DalHints().inShard("0").inTableShard(0));
        ret.setName("transWithIntShardIDVSNestStringHints");
        update(new DalHints().setShardValue("20").setTableShardValue(21),ret);
        ret.setPeopleID(3L);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传int shardID，没传dalhints，事务内dalhints指定int shardID
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntShardIDVSNestIntHints(@Shard int shardID) throws Exception{
        SqlServerPeopleTable ret=queryByPk(1,new DalHints().inShard(0).inTableShard(0));
        ret.setName("transWithIntShardIDVSNestIntHints");
        update(new DalHints().setShardValue(20).setTableShardValue(21),ret);
        ret.setPeopleID(3L);
        delete(new DalHints().inTableShard(1),ret);
    }

    @Transactional(logicDbName = DATA_BASE)
    public void transFail(@Shard int shardid,DalHints hints) throws Exception{
            SqlServerPeopleTable ret=queryByPk(1,hints.inTableShard(0));
            delete(hints.inTableShard(0),ret);
            ret.setPeopleID(3L);
            ret.setName("transFail");
            update(hints.inTableShard(1),ret);
            throw new SQLException();
    }

}
