package noshardtest;

import com.ctrip.platform.dal.dao.DalHints;
import dao.noshard.NoShardDalTransactionalTestOnSqlServerDao;
import dao.noshard.SqlServerDalTransactionalConfig;
import entity.SqlServerPeopleTable;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lilj on 2017/7/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath*:/transactionSqlServerTest.xml")
@ContextConfiguration(classes = SqlServerDalTransactionalConfig.class)
public class NoShardDalTransactionalTestOnSqlServerSpringTest {
    @Autowired
    private NoShardDalTransactionalTestOnSqlServerDao dao;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        dao.test_def_update(new DalHints());


        List<SqlServerPeopleTable> daoPojos1 = new ArrayList<SqlServerPeopleTable>(3);
        for(int i=0;i<6;i++)
        {
            SqlServerPeopleTable daoPojo = new SqlServerPeopleTable();
            daoPojo.setPeopleID(Long.valueOf(i)+1);
            daoPojo.setName("Initial_"+i);
            daoPojo.setCityID(i+20);
            daoPojo.setProvinceID(i+30);
            daoPojo.setCountryID(i+40);
            daoPojos1.add(daoPojo);
        }
        dao.insert(new DalHints(), daoPojos1);
    }

    @After
    public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints());
    }

    @Test
    public void transPassTest() throws Exception{
        dao.transPass();
        Assert.assertEquals(99,dao.queryByPk(1,null).getCityID().intValue());
        Assert.assertEquals(5,dao.count(null));
    }

    @Test
    public void transFailTest() throws Exception{
       try {
           dao.transFail();
       }
       catch (Exception e){

       }
        Assert.assertEquals(6,dao.count(null));
       Assert.assertEquals(22,dao.queryByPk(3,null).getCityID().intValue());
    }

    //    第四层异常，自己吞掉，其它层都成功，则除了自身没有操作成功，其他层都成功提交
    @Test
    public void nestTransactionTest1() throws Exception{
        try {
            dao.firstLevelTransaction(true, true,
                    true, true,
                    true, true,
                    false, false);
        } catch (Exception e){
            Assert.fail();
        }
        Assert.assertEquals(10,dao.count(null));
    }

    //    第四层异常，抛出，其它层都成功，则全部回退
    @Test
    public void nestTransactionTest2() throws Exception{
        try {
            dao.firstLevelTransaction(true, true,
                    true, true,
                    true, true,
                    false, true);
            Assert.fail();
        }catch (Exception e){
            e.printStackTrace();
//            Assert.assertEquals("Duplicate entry '1' for key 'PRIMARY'",e.getMessage());
        }
        Assert.assertEquals(6,dao.count(null));
    }

    //    第四层异常，抛出，被第三层捕获并吞掉，第三层成功，则因为冲突全部回退
    @Test
    public void nestTransactionTest3() throws Exception{
        try {
            dao.firstLevelTransaction(true, true,
                    true, true,
                    true, false,
                    false, true);
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
            dao.firstLevelTransaction(true, true,
                    true, false,
                    false, true,
                    false, true);
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
            dao.firstLevelTransaction(true, true,
                    true, false,
                    false, true,
                    true, true);
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
            dao.firstLevelTransaction(true, true,
                    true, true,
                    false, true,
                    true, true);
            Assert.fail();
        }catch (Exception e){
            e.printStackTrace();
        }
        Assert.assertEquals(6,dao.count(null));
    }
}
