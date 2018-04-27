package com.ctrip.platform.dal.dao.tableDao.insertWithKeyholder.NonAutoIncrementIdentity;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

import java.sql.SQLException;
import java.util.List;

public class TestNonAutoIncrementIdentityDao {
    private static final boolean ASC = true;
    private DalTableDao<TestNonAutoIncrementIdentity> client;

    public TestNonAutoIncrementIdentityDao() throws SQLException {
        this.client = new DalTableDao<>(new DalDefaultJpaParser<>(TestNonAutoIncrementIdentity.class));
    }

    /**
     * Query against sample pojo. All not null attributes of the passed in pojo will be used as search criteria.
     **/
    public List<TestNonAutoIncrementIdentity> queryLike(TestNonAutoIncrementIdentity sample) throws SQLException {
        return queryLike(sample, null);
    }

    /**
     * Query against sample pojo. All not null attributes of the passed in pojo will be used as search criteria.
     **/
    public List<TestNonAutoIncrementIdentity> queryLike(TestNonAutoIncrementIdentity sample, DalHints hints)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        return client.queryLike(sample, hints);
    }

    /**
     * Insert single pojo
     *
     * @param daoPojo pojo to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int insert(TestNonAutoIncrementIdentity daoPojo) throws SQLException {
        return insert(null, daoPojo);
    }

    /**
     * Insert single pojo
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojo pojo to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int insert(DalHints hints, TestNonAutoIncrementIdentity daoPojo) throws SQLException {
        if (null == daoPojo) {
            return 0;
        }
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, daoPojo);
    }

    /**
     * Insert pojos one by one. If you want to inert them in the batch mode, user batchInsert instead. You can also use
     * the combinedInsert.
     *
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected
     */
    public int[] insert(List<TestNonAutoIncrementIdentity> daoPojos) throws SQLException {
        return insert(null, daoPojos);
    }

    /**
     * Insert pojos one by one. If you want to inert them in the batch mode, user batchInsert instead. You can also use
     * the combinedInsert.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     *        DalHintEnum.continueOnError can be used to indicate that the inserting can be go on if there is any
     *        failure.
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected
     */
    public int[] insert(DalHints hints, List<TestNonAutoIncrementIdentity> daoPojos) throws SQLException {
        if (null == daoPojos || daoPojos.size() <= 0) {
            return new int[0];
        }
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, daoPojos);
    }

    /**
     * Insert pojo and get the generated PK back in keyHolder. If the "set no count on" for MS SqlServer is set, the
     * operation may fail. Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
     *
     * @param keyHolder holder for generated primary keys
     * @param daoPojo pojo to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int insertWithKeyHolder(KeyHolder keyHolder, TestNonAutoIncrementIdentity daoPojo) throws SQLException {
        return insert(null, keyHolder, daoPojo);
    }

    /**
     * Insert pojo and get the generated PK back in keyHolder. If the "set no count on" for MS SqlServer is set, the
     * operation may fail. Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param keyHolder holder for generated primary keys
     * @param daoPojo pojo to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int insert(DalHints hints, KeyHolder keyHolder, TestNonAutoIncrementIdentity daoPojo) throws SQLException {
        if (null == daoPojo) {
            return 0;
        }
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, keyHolder, daoPojo);
    }

    /**
     * Insert pojos and get the generated PK back in keyHolder. If the "set no count on" for MS SqlServer is set, the
     * operation may fail. Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
     *
     * @param keyHolder holder for generated primary keys
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int[] insertWithKeyHolder(KeyHolder keyHolder, List<TestNonAutoIncrementIdentity> daoPojos)
            throws SQLException {
        return insert(null, keyHolder, daoPojos);
    }

    /**
     * Insert pojos and get the generated PK back in keyHolder. If the "set no count on" for MS SqlServer is set, the
     * operation may fail. Please don't pass keyholder for MS SqlServer to avoid the failure in such case.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     *        DalHintEnum.continueOnError can be used to indicate that the inserting can be go on if there is any
     *        failure.
     * @param keyHolder holder for generated primary keys
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int[] insert(DalHints hints, KeyHolder keyHolder, List<TestNonAutoIncrementIdentity> daoPojos)
            throws SQLException {
        if (null == daoPojos || daoPojos.size() <= 0) {
            return new int[0];
        }
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, keyHolder, daoPojos);
    }

    /**
     * Insert pojos in batch mode. The DalDetailResults will be set in hints to allow client know how the operation
     * performed in each of the shard.
     *
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected for inserting each of the pojo
     * @throws SQLException
     */
    public int[] batchInsert(List<TestNonAutoIncrementIdentity> daoPojos) throws SQLException {
        return batchInsert(null, daoPojos);
    }

    /**
     * Insert pojos in batch mode. The DalDetailResults will be set in hints to allow client know how the operation
     * performed in each of the shard.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected for inserting each of the pojo
     * @throws SQLException
     */
    public int[] batchInsert(DalHints hints, List<TestNonAutoIncrementIdentity> daoPojos) throws SQLException {
        if (null == daoPojos || daoPojos.size() <= 0) {
            return new int[0];
        }
        hints = DalHints.createIfAbsent(hints);
        return client.batchInsert(hints, daoPojos);
    }

    /**
     * Insert multiple pojos in one INSERT SQL The DalDetailResults will be set in hints to allow client know how the
     * operation performed in each of the shard.
     *
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int combinedInsert(List<TestNonAutoIncrementIdentity> daoPojos) throws SQLException {
        return combinedInsert(null, daoPojos);
    }

    /**
     * Insert multiple pojos in one INSERT SQL The DalDetailResults will be set in hints to allow client know how the
     * operation performed in each of the shard.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int combinedInsert(DalHints hints, List<TestNonAutoIncrementIdentity> daoPojos) throws SQLException {
        if (null == daoPojos || daoPojos.size() <= 0) {
            return 0;
        }
        hints = DalHints.createIfAbsent(hints);
        return client.combinedInsert(hints, daoPojos);
    }

    /**
     * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder. If the "set no count on" for
     * MS SqlServer is set, the operation may fail. Please don't pass keyholder for MS SqlServer to avoid the failure in
     * such case. The DalDetailResults will be set in hints to allow client know how the operation performed in each of
     * the shard.
     *
     * @param keyHolder holder for generated primary keys
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int combinedInsertWithKeyHolder(KeyHolder keyHolder, List<TestNonAutoIncrementIdentity> daoPojos)
            throws SQLException {
        return combinedInsert(null, keyHolder, daoPojos);
    }

    /**
     * Insert multiple pojos in one INSERT SQL and get the generated PK back in keyHolder. If the "set no count on" for
     * MS SqlServer is set, the operation may fail. Please don't pass keyholder for MS SqlServer to avoid the failure in
     * such case. The DalDetailResults will be set in hints to allow client know how the operation performed in each of
     * the shard.
     *
     * @param hints Additional parameters that instruct how DAL Client perform database operation.
     * @param keyHolder holder for generated primary keys
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<TestNonAutoIncrementIdentity> daoPojos)
            throws SQLException {
        if (null == daoPojos || daoPojos.size() <= 0) {
            return 0;
        }
        hints = DalHints.createIfAbsent(hints);
        return client.combinedInsert(hints, keyHolder, daoPojos);
    }
}
