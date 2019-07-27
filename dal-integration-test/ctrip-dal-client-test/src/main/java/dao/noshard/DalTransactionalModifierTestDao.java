package dao.noshard;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.annotation.DalTransactional;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import entity.SqlServerPeopleTable;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Created by lilj on 2017/7/24.
 */
@Component
public class DalTransactionalModifierTestDao {
    private static final String DATA_BASE = "noShardTestOnSqlServer";
    private DalTableDao<SqlServerPeopleTable> client;
    private DalQueryDao queryDao = null;

    public DalTransactionalModifierTestDao() throws SQLException {
        this.client = new DalTableDao<>(new DalDefaultJpaParser<>(SqlServerPeopleTable.class));
        this.queryDao = new DalQueryDao(DATA_BASE);
    }

    public SqlServerPeopleTable queryByPk(Number id, DalHints hints)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        return client.queryByPk(id, hints);
    }

    public int delete(DalHints hints, SqlServerPeopleTable daoPojo) throws SQLException {
        if (null == daoPojo)
            return 0;
        hints = DalHints.createIfAbsent(hints);
        return client.delete(hints, daoPojo);
    }

    public int update(DalHints hints, SqlServerPeopleTable daoPojo) throws SQLException {
        if (null == daoPojo)
            return 0;
        hints = DalHints.createIfAbsent(hints);
        return client.update(hints, daoPojo);
    }
    /*private static final boolean ASC = true;
    private DalTableDao<SqlServerPeopleTable> client;
    private static final String DATA_BASE = "noShardTestOnSqlServer";
    private static final DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
    private DalQueryDao queryDao = null;

    public NoShardDalTransactionalTestOnSqlServerDao() throws SQLException {
        this.client = new DalTableDao<>(new DalDefaultJpaParser<>(SqlServerPeopleTable.class));
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

    */

    /**
     * ss
     **//*
    public int test_def_update (DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("truncate table People");
        StatementParameters parameters = new StatementParameters();
        int i = 1;

        return queryDao.update(builder, parameters, hints);
    }*/
    /*@DalTransactional(logicDbName = DATA_BASE)
    public void transPass() throws Exception {
        SqlServerPeopleTable ret = queryByPk(1, null);
        ret.setCityID(99);
        update(null, ret);
        ret.setPeopleID(3L);
        delete(null, ret);
    }

    @DalTransactional(logicDbName = DATA_BASE)
    public void transFail() throws Exception {
        SqlServerPeopleTable ret = queryByPk(1, null);
        delete(null, ret);
        ret.setCityID(99);
        ret.setPeopleID(3L);
        update(null, ret);
        throw new SQLException("Dal Test Exception");
    }*/

   /* @DalTransactional(logicDbName = DATA_BASE)
    public void firstLevelTransaction(Boolean firstLevelsuccessful,Boolean firstLevelThrowException,
                                      Boolean secondLevelsuccessful,Boolean secondLevelThrowException,
                                      Boolean thirdLevelsuccessful,Boolean thirdLevelthrowException,
                                      Boolean forthLevelsuccessful,Boolean forthLevelthrowException) throws Exception{
        try {
            SqlServerPeopleTable ret = new SqlServerPeopleTable();

            ret.setName("FirstLevel");
            insert(new DalHints().enableIdentityInsert(), ret);
            secondLevelTransaction(secondLevelsuccessful,secondLevelThrowException,
                    thirdLevelsuccessful,thirdLevelthrowException,
                    forthLevelsuccessful,forthLevelthrowException);
            System.out.println("first level done");
            if(!firstLevelsuccessful)
                throw new Exception("Dal Test Exception");
        }catch (Exception e){
            if (firstLevelThrowException)
                throw e;
        }
    }
    @DalTransactional(logicDbName = DATA_BASE)
    public void secondLevelTransaction(Boolean secondLevelsuccessful,Boolean secondLevelThrowException,
                                       Boolean thirdLevelsuccessful,Boolean thirdLevelthrowException,
                                       Boolean forthLevelsuccessful,Boolean forthLevelthrowException) throws Exception{
        try {
            SqlServerPeopleTable ret = new SqlServerPeopleTable();

            ret.setName("secondLevel");
            insert(new DalHints().enableIdentityInsert(), ret);
            thirdLevelTransaction(thirdLevelsuccessful,thirdLevelthrowException,
                    forthLevelsuccessful,forthLevelthrowException);
            System.out.println("second level done");
            if(!secondLevelsuccessful)
                throw new Exception("Dal Test Exception");
        } catch (Exception e) {
            if (secondLevelThrowException)
                throw e;
        }
    }

    @DalTransactional(logicDbName = DATA_BASE)
    public void thirdLevelTransaction(Boolean thirdLevelsuccessful,Boolean thirdLevelthrowException,
                                      Boolean forthLevelsuccessful,Boolean forthLevelthrowException) throws Exception{
        try {
            SqlServerPeopleTable ret = new SqlServerPeopleTable();
            ret.setName("thirdLevel");
            insert(new DalHints().enableIdentityInsert(), ret);
            forthLevelTransaction(forthLevelsuccessful,forthLevelthrowException);
            System.out.println("third level done");
            if(!thirdLevelsuccessful)
                throw new Exception("Dal Test Exception");
        } catch (Exception e) {
            if (thirdLevelthrowException)
                throw e;
        }
    }

    @DalTransactional(logicDbName = DATA_BASE)
    public void forthLevelTransaction(Boolean successful,Boolean throwException) throws Exception{
        try {
            SqlServerPeopleTable ret = new SqlServerPeopleTable();
            ret.setName("forthLevel");
            insert(new DalHints(), ret);
            System.out.println("forth level done");
            if(!successful)
                throw new Exception("Dal Test Exception");
        } catch (Exception e) {
            if (throwException)
                throw e;
        }
    }*/

    /*@DalTransactional(logicDbName = DATA_BASE)
    public void test() throws Exception{
        List<SqlServerPeopleTable> list=new ArrayList<>();
        SqlServerPeopleTable pojo1=new SqlServerPeopleTable();
        pojo1.setPeopleID(1L);
        pojo1.setName("he");
        list.add(pojo1);
        SqlServerPeopleTable pojo2=new SqlServerPeopleTable();
        pojo2.setPeopleID(2L);
        pojo1.setName("she");
        list.add(pojo2);

//        try {
        batchInsert(new DalHints().enableIdentityInsert(), list);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw e;
//        }

//        insert(new DalHints().enableIdentityInsert(),list);
    }*/

    /*public void callPrivateTransFail() throws Exception {
        privateTransFail();
    }*/

    public void callProtectedTransFail() throws Exception {
        protectedTransFail();
    }

    public void callDefaultTransFail() throws Exception {
        defalutTransFail();
    }

    @DalTransactional(logicDbName = DATA_BASE)
    protected void protectedTransFail() throws Exception {
        SqlServerPeopleTable ret = queryByPk(1, null);
        delete(null, ret);
        ret.setCityID(99);
        ret.setPeopleID(3L);
        update(null, ret);
        throw new SQLException("Dal Test Exception");
    }

    @DalTransactional(logicDbName = DATA_BASE)
    void defalutTransFail() throws Exception {
        SqlServerPeopleTable ret = queryByPk(1, null);
        delete(null, ret);
        ret.setCityID(99);
        ret.setPeopleID(3L);
        update(null, ret);
        throw new SQLException("Dal Test Exception");
    }

    /*@DalTransactional(logicDbName = DATA_BASE)
    private void privateTransFail() throws Exception {
        SqlServerPeopleTable ret = queryByPk(1, null);
        delete(null, ret);
        ret.setCityID(99);
        ret.setPeopleID(3L);
        update(null, ret);
        throw new SQLException("Dal Test Exception");
    }*/
}