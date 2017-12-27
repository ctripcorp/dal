package com.dal.mysql.transaction.shard;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.annotation.Shard;
import com.ctrip.platform.dal.dao.annotation.Transactional;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.sqlbuilder.*;
import io.netty.channel.ChannelHandler;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lilj on 2017/7/24.
 */
public class TransactionWithShardOnMysqlDao {
    private static final boolean ASC = true;
    private DalTableDao<TransactionWithShardOnMysql> client;
    private static final String DATA_BASE = "ShardColModShardByDBTableOnMysql";
    private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
    private DalQueryDao queryDao = null;

    private DalRowMapper<TransactionWithShardOnMysql> TransactionWithShardOnMysqlRowMapper = null;

    public TransactionWithShardOnMysqlDao() throws SQLException {
//		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(TransactionWithShardOnMysql.class));
        this.client = new DalTableDao<>(TransactionWithShardOnMysql.class);
        this.TransactionWithShardOnMysqlRowMapper = new DalDefaultJpaMapper<>(TransactionWithShardOnMysql.class);
        this.queryDao = new DalQueryDao(DATA_BASE);
    }

    //不传shardID，不传dalhints，代码内设置hints Shardid
    @Transactional(logicDbName = DATA_BASE)
    public void transWithoutShardIDAndDalHintsWithNestHints() throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,new DalHints().inShard(0).inTableShard(0));
        ret.setName("transWithoutShardIDAndDalHintsWithNestHints");
        update(new DalHints().setShardValue(20).setTableShardValue(21),ret);
        ret.setID(3);
        delete(new DalHints().setShardValue(20).inTableShard(1),ret);
    }

    //不传shardID，不传dalhints，代码内没有设置hints Shardid
    @Transactional(logicDbName = DATA_BASE)
    public void transWithoutShardIDAndDalHints() throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,new DalHints().inTableShard(0));
        ret.setName("transWithoutShardIDAndDalHints");
        update(new DalHints().setTableShardValue(21),ret);
        ret.setID(3);
        delete(new DalHints().inTableShard(1),ret);
    }

    //不传shardID,传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithoutShardIDWithDalHints(DalHints hints) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,hints.inTableShard(0));
        ret.setName("transWithoutShardIDWithDalHints");
        update(hints.setTableShardValue(21),ret);
        ret.setID(3);
        delete(hints.inTableShard(1),ret);
    }

    //传string @Shard，不传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithStringShardIDWithoutDalHints(@Shard String shardID) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,new DalHints().inTableShard(0));
        ret.setName("transWithStringShardIDWithoutDalHints");
        update(new DalHints().setTableShardValue(20),ret);
        ret.setID(3);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传string @Shard，传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithStringShardIDWithDalHints(@Shard String shardID,DalHints hints) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,hints.inTableShard(0));
        ret.setName("transWithStringShardIDWithDalHints");
        update(hints.setTableShardValue(20),ret);
        ret.setID(3);
        delete(hints.inTableShard(1),ret);
    }

    //传int @Shard，不传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntShardIDWithoutDalHints(@Shard int shardID) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,new DalHints().inTableShard(0));
        ret.setName("transWithIntShardIDWithoutDalHints");
        update(new DalHints().setTableShardValue(20),ret);
        ret.setID(3);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传int @Shard，传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntShardIDWithDalHints(@Shard int shardID,DalHints hints) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,hints.inTableShard(0));
        ret.setName("transWithIntShardIDWithDalHints");
        update(hints.setTableShardValue(20),ret);
        ret.setID(3);
        delete(hints.inTableShard(1),ret);
    }

    //传integer @Shard，不传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntegerShardIDWithoutDalHints(@Shard Integer shardID) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,new DalHints().inTableShard(0));
        ret.setName("transWithIntegerShardIDWithoutDalHints");
        update(new DalHints().setTableShardValue(20),ret);
        ret.setID(3);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传integer @Shard，传dalhints
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntegerShardIDWithDalHints(@Shard Integer shardID,DalHints hints) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,hints.inTableShard(0));
        ret.setName("transWithIntegerShardIDWithDalHints");
        update(hints.setTableShardValue(20),ret);
        ret.setID(3);
        delete(hints.inTableShard(1),ret);
    }

    //传string shardID，没传dalhints,事务内dalhints指定string shardID
    @Transactional(logicDbName = DATA_BASE)
    public void transWithStringShardIDVSNestStringHints(@Shard String shardID) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,new DalHints().inShard("0").inTableShard(0));
        ret.setName("transWithStringShardIDVSNestStringHints");
        update(new DalHints().setShardValue("20").setTableShardValue(21),ret);
        ret.setID(3);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传string shardID，没传dalhints，事务内dalhints指定int shardID
    @Transactional(logicDbName = DATA_BASE)
    public void transWithStringShardIDVSNestIntHints(@Shard String shardID) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,new DalHints().inShard(0).inTableShard(0));
        ret.setName("transWithStringShardIDVSNestIntHints");
        update(new DalHints().setShardValue(20).setTableShardValue(21),ret);
        ret.setID(3);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传int shardID，没传dalhints，事务内dalhints指定string shardID
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntShardIDVSNestStringHints(@Shard int shardID) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,new DalHints().inShard("0").inTableShard(0));
        ret.setName("transWithIntShardIDVSNestStringHints");
        update(new DalHints().setShardValue("20").setTableShardValue(21),ret);
        ret.setID(3);
        delete(new DalHints().inTableShard(1),ret);
    }

    //传int shardID，没传dalhints，事务内dalhints指定int shardID
    @Transactional(logicDbName = DATA_BASE)
    public void transWithIntShardIDVSNestIntHints(@Shard int shardID) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,new DalHints().inShard(0).inTableShard(0));
        ret.setName("transWithIntShardIDVSNestIntHints");
        update(new DalHints().setShardValue(20).setTableShardValue(21),ret);
        ret.setID(3);
        delete(new DalHints().inTableShard(1),ret);
    }

    @Transactional(logicDbName = DATA_BASE)
    public void transFail(@Shard int shardid,DalHints hints) throws Exception{
        TransactionWithShardOnMysql ret=queryByPk(1,hints.inTableShard(0));
        delete(hints.inTableShard(0),ret);
        ret.setID(3);
        ret.setName("transFail");
        update(hints.inTableShard(1),ret);
        throw new SQLException();
    }

    /**
     * Query TransactionWithShardOnMysql by the specified ID
     * The ID must be a number
     **/
    public TransactionWithShardOnMysql queryByPk(Number id, DalHints hints)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        return client.queryByPk(id, hints);
    }

    /**
     * Query TransactionWithShardOnMysql by TransactionWithShardOnMysql instance which the primary key is set
     **/
    public TransactionWithShardOnMysql queryByPk(TransactionWithShardOnMysql pk, DalHints hints)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        return client.queryByPk(pk, hints);
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
    public int insert(DalHints hints, TransactionWithShardOnMysql daoPojo) throws SQLException {
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
    public int[] insert(DalHints hints, List<TransactionWithShardOnMysql> daoPojos) throws SQLException {
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
    public int insert(DalHints hints, KeyHolder keyHolder, TransactionWithShardOnMysql daoPojo) throws SQLException {
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
    public int[] insert(DalHints hints, KeyHolder keyHolder, List<TransactionWithShardOnMysql> daoPojos) throws SQLException {
        if(null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, keyHolder, daoPojos);
    }


    /**
     * Delete the given pojo.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojo pojo to be deleted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int delete(DalHints hints, TransactionWithShardOnMysql daoPojo) throws SQLException {
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
    public int[] delete(DalHints hints, List<TransactionWithShardOnMysql> daoPojos) throws SQLException {
        if(null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.delete(hints, daoPojos);
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
    public int update(DalHints hints, TransactionWithShardOnMysql daoPojo) throws SQLException {
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
    public int[] update(DalHints hints, List<TransactionWithShardOnMysql> daoPojos) throws SQLException {
        if(null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.update(hints, daoPojos);
    }


    /**
     * 自定义
     **/
    public int test_def_truncate (DalHints hints,String tableShardID) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("truncate people"+tableShardID);
        StatementParameters parameters = new StatementParameters();
        int i = 1;

        return queryDao.update(builder, parameters, hints);
    }



}
