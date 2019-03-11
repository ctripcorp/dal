package noshardtest;

import com.ctrip.platform.dal.dao.DalHints;
import dao.noshard.NoShardDalTransactionalTestOnMysqlDao;
import entity.MysqlPersonTable;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lilj on 2017/7/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/DalTransactionalNoShardTest.xml")
public class NoShardDalTransactionalTestOnMysqlSpringTest {
    @Autowired
    private NoShardDalTransactionalTestOnMysqlDao dao;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        dao.test_def_update(new DalHints());
        List<MysqlPersonTable> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            MysqlPersonTable daoPojo = new MysqlPersonTable();
            if (i % 2 == 0) {
                daoPojo.setAge(20);
                daoPojo.setName("Initial_Shard_0" + i);
            } else {
                daoPojo.setAge(21);
                daoPojo.setName("Initial_Shard_1" + i);
            }
            daoPojos.add(daoPojo);
        }
        dao.insert(new DalHints(), daoPojos);
    }

    @After
    public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints());
    }

    @Test
    public void transPassTest() throws Exception{
        dao.transPass();
        Assert.assertEquals(99,dao.queryByPk(1,null).getAge().intValue());
        Assert.assertEquals(5,dao.count(null));
    }

    @Test
    public void transPassTestIsolution() throws Exception{
        dao.transPassisolution(new DalHints().setIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED));
        Assert.assertEquals(99,dao.queryByPk(1,null).getAge().intValue());
        Assert.assertEquals(5,dao.count(null));
    }

    @Test
    public void transFailTest() throws Exception {
        try {
            dao.transFail();
            Assert.fail();
        } catch (Exception e) {

        }
        Assert.assertEquals(6, dao.count(null));
        Assert.assertEquals(20, dao.queryByPk(3, null).getAge().intValue());
    }

    @Test
    public void transSetRollbackTest() throws Exception {
        try {
            dao.transSetRollback();
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(6, dao.count(null));
        Assert.assertEquals(20, dao.queryByPk(3, null).getAge().intValue());
    }

    @Test
    public void testTransSetRollbackAndThrowException() throws Exception {
        try {
            dao.transSetRollbackAndThrowException();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("transSetRollbackAndThrowException"));
        }
        Assert.assertEquals(6, dao.count(null));
        Assert.assertEquals(20, dao.queryByPk(3, null).getAge().intValue());
    }

    @Test
    public void testTransThrowExceptionAndAndSetRollback() throws Exception{
        try {
            dao.transThrowExceptionAndAndSetRollback();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Duplicate entry"));
        }
        Assert.assertEquals(6, dao.count(null));
        Assert.assertEquals(20, dao.queryByPk(3, null).getAge().intValue());
    }

    //    第四层异常，自己吞掉，其它层都成功，则除了自身没有操作成功，其他层都成功提交
    @Test
    public void nestTransactionTest1() throws Exception{
        try {
            dao.firstLevelTransaction(30, true,
                    20, true,
                    10, true,
                    1, false);
        } catch (Exception e){
            Assert.fail();
        }
        Assert.assertEquals(9,dao.count(null));
    }

    //    第四层异常，抛出，其它层都成功，则全部回退
    @Test
    public void nestTransactionTest2() throws Exception{
        try {
            dao.firstLevelTransaction(30, true,
                    20, true,
                    10, true,
                    1, true);
            Assert.fail();
        }catch (Exception e){
            Assert.assertEquals("Duplicate entry '1' for key 'PRIMARY'",e.getMessage());
        }
        Assert.assertEquals(6,dao.count(null));
    }

    //    第四层异常，抛出，被第三层捕获并吞掉，第三层成功，则因为冲突全部回退
    @Test
    public void nestTransactionTest3() throws Exception{
        try {
            dao.firstLevelTransaction(30, true,
                    20, true,
                    10, false,
                    1, true);
            Assert.fail();
        }catch (Exception e){
            e.printStackTrace();
//            Assert.assertEquals("The state of nesting transaction are conflicted,transaction has been rollbacked. Transaction level 3 conflicted with level 4, all levels of transaction status:[level 1, status:Commit] [level 2, status:Conflict] [level 3, status:Conflict] [level 4, status:Rollback] .",e.getMessage());
        }
        Assert.assertEquals(6,dao.count(null));
    }


    //    第四层异常，抛出，第三层异常，抛出，第二层吞掉，第二层成功，则因为冲突全部回退，
    //    且由于第三层在第四层开始之前抛异常，第四层不会执行
    @Test
    public void nestTransactionTest4() throws Exception{
        try {
            dao.firstLevelTransaction(30, true,
                    20, false,
                    1, true,
                    1, true);
        }catch (Exception e){
            e.printStackTrace();
//            Assert.assertEquals("The state of nesting transaction are conflicted,transaction has been rollbacked. Transaction level 2 conflicted with level 3, all levels of transaction status:[level 1, status:Commit] [level 2, status:Conflict] [level 3, status:Rollback] .",e.getMessage());
        }
        Assert.assertEquals(6,dao.count(null));
    }


//    第四层commit，第三层rollback，异常抛出被第二层吞掉，第二层提交时冲突，全部回滚
    @Test
    public void nestTransactionTest5() throws Exception{
        try {
            dao.firstLevelTransaction(30, true,
                    20, false,
                    1, true,
                    10, true);
            Assert.fail();
        }catch (Exception e){
            e.printStackTrace();
        }
         Assert.assertEquals(6,dao.count(null));
    }

    //    第四层commit，第三层rollback，异常抛出,则最终直接跑第三层异常，全部回滚
    @Test
    public void nestTransactionTest6() throws Exception{
        try {
            dao.firstLevelTransaction(30, true,
                    20, true,
                    1, true,
                    10, true);
            Assert.fail();
        }catch (Exception e){
            e.printStackTrace();
        }
        Assert.assertEquals(6,dao.count(null));
    }

    @Test
    public void nestTransactionSetRollbackAndThrowException() throws Exception{
        try {
            dao.thirdLevelTransactionSetRollback(30, true,
                    30, true);
            Assert.fail();
        }catch (Exception e){
            e.printStackTrace();
        }
        Assert.assertEquals(6,dao.count(null));
    }

    @Test
    public void nestTransactionThrowExceptionAndSetRollback() throws Exception{
        try {
            dao.thirdLevelTransactionSetRollback2(30, true,
                    100, true);
            Assert.fail();
        }catch (Exception e){
            e.printStackTrace();
        }
        Assert.assertEquals(6,dao.count(null));
    }


   /* @Test
    public void test() throws Exception {
        try {
            dao.test();
        }catch (SQLException e){
            e.getErrorCode();
        }
//        dao.thirdLevelTransaction(10,false,
//                1,true);
        Assert.assertEquals(6,dao.count(null));
    }*/

}
