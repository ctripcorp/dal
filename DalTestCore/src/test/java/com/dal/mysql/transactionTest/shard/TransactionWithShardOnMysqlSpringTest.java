package com.dal.mysql.transactionTest.shard;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.dal.mysql.transaction.shard.TransactionWithShardOnMysql;
import com.dal.mysql.transaction.shard.TransactionWithShardOnMysqlDao;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;


/**
 * Created by lilj on 2017/7/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/transactionMysqlTest.xml")
public class TransactionWithShardOnMysqlSpringTest {
    @Autowired
    private TransactionWithShardOnMysqlDao dao;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        /**
         * Initialize DalClientFactory.
         * The Dal.config can be specified from class-path or local file path.
         * One of follow three need to be enabled.
         **/
        DalClientFactory.initClientFactory(); // load from class-path Dal.config
        DalClientFactory.warmUpConnections();

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        dao.test_def_truncate(new DalHints().inShard(0),"_0");
        dao.test_def_truncate(new DalHints().inShard(0),"_1");
        dao.test_def_truncate(new DalHints().inShard(1),"_0");
        dao.test_def_truncate(new DalHints().inShard(1),"_1");

        List<TransactionWithShardOnMysql> daoPojos1 = new ArrayList<>(2);
        for (int i = 0; i < 6; i++) {
            TransactionWithShardOnMysql daoPojo = new TransactionWithShardOnMysql();
            daoPojo.setCityID(200);
            daoPojo.setAge(i + 20);
            daoPojo.setName("InsertByfields_0fields_" + i);
            daoPojos1.add(daoPojo);
        }
        dao.insert(new DalHints(), daoPojos1);

        List<TransactionWithShardOnMysql> daoPojos2 = new ArrayList<>(2);
        for (int i = 0; i <6; i++) {
            TransactionWithShardOnMysql daoPojo = new TransactionWithShardOnMysql();
            daoPojo.setCityID(201);
            daoPojo.setAge(i + 20);
            daoPojo.setName("InsertByfields_1fields_" + i);
            daoPojos2.add(daoPojo);
        }
        dao.insert(new DalHints(), daoPojos2);
    }

    @After
    public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints());
    }

    //不传@Shard和Dalhints的话，即使transaction代码中设置shardID，也会忽略，并报错
    @Test
    public void transWithoutShardIDAndDalHintsWithNestHintsTest() throws Exception{
        try {
            dao.transWithoutShardIDAndDalHintsWithNestHints();
            fail();
        }catch (Exception e){}

        assertNotEquals("transWithoutShardIDAndDalHintsWithNestHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(1)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(0).inTableShard(1)));
    }

    //不传@Shard和Dalhints的话，transaction代码中没有设置shardID，并报错
    @Test
    public void transWithoutShardIDAndDalHintsTest() throws Exception{
        try {
        dao.transWithoutShardIDAndDalHints();
            fail();
        }catch (Exception e){}

        assertNotEquals("transWithoutShardIDAndDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(1)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(0).inTableShard(1)));
    }


    @Test
    public void transWithoutShardIDWithDalHintsTest() throws Exception{
        //int
        DalHints hints1=new DalHints();
        dao.transWithoutShardIDWithDalHints(hints1.setShardValue(20));

        assertNotEquals("transWithoutShardIDWithDalHints",dao.queryByPk(1,hints1.inTableShard(1)).getName());
        assertEquals(2,dao.count(hints1.inTableShard(1)));
        assertNull(dao.queryByPk(3, hints1.inTableShard(1)));

        //string
        DalHints hints2=new DalHints();
        dao.transWithoutShardIDWithDalHints(hints2.inShard("1"));
        assertNotEquals("transWithoutShardIDWithDalHints",dao.queryByPk(1,hints2.inTableShard(1)).getName());
        assertEquals(2,dao.count(hints2.inTableShard(1)));
        assertNull(dao.queryByPk(3, hints2.inTableShard(1)));

        //null
        try{
            dao.transWithoutShardIDWithDalHints(new DalHints());
            fail();
        }catch (Exception e){}
        assertNotEquals("transWithoutShardIDWithDalHints",dao.queryByPk(1,hints2.inTableShard(1)).getName());
        assertEquals(2,dao.count(hints2.inTableShard(1)));
        assertNull(dao.queryByPk(3, hints2.inTableShard(1)));
    }

    @Test
    public void transWithStringShardIDWithoutDalHintsTest() throws Exception{
        //@shard 传入null
        try{
            dao.transWithStringShardIDWithoutDalHints(null);
            fail();
        }catch (Exception e){}

        //@shard 传入string
        dao.transWithStringShardIDWithoutDalHints("0");
        assertEquals("transWithStringShardIDWithoutDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
    }

    @Test
    public void transWithIntShardIDWithoutDalHintsTest() throws Exception{
        dao.transWithIntShardIDWithoutDalHints(0);
        assertEquals("transWithIntShardIDWithoutDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
    }

    @Test
    public void transWithIntegerShardIDWithoutDalHintsTest() throws Exception{
        //integer @shard 传入null
        try{
            dao.transWithIntegerShardIDWithoutDalHints(null);
            fail();
        }catch (Exception e){}

        //integer @shard 传入interger
        dao.transWithIntegerShardIDWithoutDalHints(Integer.valueOf(0));
        assertEquals("transWithIntegerShardIDWithoutDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
    }

    @Test
    public void transWithStringShardIDWithDalHintsTest() throws Exception{
        //string shard, different int shardid in dalhints
        try{
            dao.transWithStringShardIDWithDalHints("0",new DalHints().inShard(1));
            fail();
        }catch (Exception e){}
        assertNotEquals("transWithStringShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));

        //string shard, different string shardid in dalhints
        try{
            dao.transWithStringShardIDWithDalHints("0",new DalHints().inShard("1"));
            fail();
        }catch (Exception e){}
        assertNotEquals("transWithStringShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));

        //string shard, null hints
        dao.transWithStringShardIDWithDalHints("0",new DalHints());
        assertEquals("transWithStringShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));

        //string shard, same int shardid in dalhints
        dao.transWithStringShardIDWithDalHints("0",new DalHints().inShard(0));
        assertEquals("transWithStringShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));

        //string shard, same string shardid in dalhints
        dao.transWithStringShardIDWithDalHints("1",new DalHints().inShard("1"));
        assertEquals("transWithStringShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(1).inTableShard(1)));
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
    }


    @Test
    public void transWithIntegerShardIDWithDalHintsTest() throws Exception{
        //Integer shard, different int shardid in dalhints
        try{
            dao.transWithIntegerShardIDWithDalHints(Integer.valueOf(0),new DalHints().inShard(1));
            fail();
        }catch (Exception e){}
        assertNotEquals("transWithIntegerShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));

        //Integer shard, different string shardid in dalhints
        try{
            dao.transWithIntegerShardIDWithDalHints(Integer.valueOf(0),new DalHints().inShard("1"));
            fail();
        }catch (Exception e){}
        assertNotEquals("transWithIntegerShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));

        //Integer shard, null hints
        dao.transWithIntegerShardIDWithDalHints(Integer.valueOf(0),new DalHints());
        assertEquals("transWithIntegerShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));

        //Integer shard, same int shardid in dalhints
        dao.transWithIntegerShardIDWithDalHints(Integer.valueOf(0),new DalHints().inShard(0));
        assertEquals("transWithIntegerShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));

        //Integer shard, same string shardid in dalhints
        dao.transWithIntegerShardIDWithDalHints(Integer.valueOf(1),new DalHints().inShard("1"));
        assertEquals("transWithIntegerShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(1).inTableShard(1)));
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
    }

    @Test
    public void transWithNullStringShardIDWithDalHintsTest() throws Exception{
        //null string shard, null hints
        try {
            dao.transWithStringShardIDWithDalHints(null, new DalHints());
            fail();
        }catch (Exception e){}

        //null string shard, int shardid in dalhints
        dao.transWithStringShardIDWithDalHints(null,new DalHints().inShard(0));
        assertEquals("transWithStringShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));

        //null shard, string shardid in dalhints
        dao.transWithStringShardIDWithDalHints(null,new DalHints().inShard("1"));
        assertEquals("transWithStringShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(1).inTableShard(1)));
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
    }

    @Test
    public void transWithNullIntegerShardIDWithDalHintsTest() throws Exception{
        //null Integer shard, null hints
        try {
            dao.transWithIntegerShardIDWithDalHints(null, new DalHints());
            fail();
        }catch (Exception e){}

        //null Integer shard, int shardid in dalhints
        dao.transWithIntegerShardIDWithDalHints(null,new DalHints().inShard(0));
        assertEquals("transWithIntegerShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));

        //null shard, string shardid in dalhints
        dao.transWithIntegerShardIDWithDalHints(null,new DalHints().inShard("1"));
        assertEquals("transWithIntegerShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(1).inTableShard(1)));
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(0)));
    }

    @Test
    public void transWithIntShardIDWithDalHintsTest() throws Exception{
        //int shard, null hints
        dao.transWithIntShardIDWithDalHints(0,new DalHints());
        assertEquals("transWithIntShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));

        //intshard, different int shardid in dalhints
        try{
            dao.transWithIntShardIDWithDalHints(0,new DalHints().inShard(1));
            fail();
        }catch (Exception e){}
        assertNotEquals("transWithIntShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));

        //int shard, different string shardid in dalhints
        try{
            dao.transWithIntShardIDWithDalHints(0,new DalHints().inShard("1"));
            fail();
        }catch (Exception e){}
        assertNotEquals("transWithIntShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));

        //int shard, same int shardid in dalhints
        dao.transWithIntShardIDWithDalHints(0,new DalHints().inShard(0));
        assertEquals("transWithIntShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));

        //int shard, same string shardid in dalhints
        dao.transWithIntShardIDWithDalHints(1,new DalHints().inShard("1"));
        assertEquals("transWithIntShardIDWithDalHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(0)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(1).inTableShard(1)));
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
    }

    @Test
    public void transWithStringShardIDVSNestStringHintsTest() throws Exception{
       //string shardid和内部hints不同
        try {
            dao.transWithStringShardIDVSNestStringHints("1");
            fail();
        }catch (Exception e){}

        //@shard 传null
        try {
            dao.transWithStringShardIDVSNestStringHints(null);
            fail();
        }catch (Exception e){}

        assertNotEquals("transWithStringShardIDVSNestStringHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(1)).getName());
        assertNotEquals("transWithStringShardIDVSNestStringHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(1)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(0).inTableShard(1)));
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));

        //string shardid和内部hints相同
        dao.transWithStringShardIDVSNestStringHints("0");
        assertEquals("transWithStringShardIDVSNestStringHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(1)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));
    }

    @Test
    public void transWithStringShardIDVSNestIntHintsTest() throws Exception{
        //string shardid和内部hints不同
        try {
            dao.transWithStringShardIDVSNestIntHints("1");
            fail();
        }catch (Exception e){}

        //@shard 传null
        try {
            dao.transWithStringShardIDVSNestIntHints(null);
            fail();
        }catch (Exception e){}

        assertNotEquals("transWithStringShardIDVSNestIntHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(1)).getName());
        assertNotEquals("transWithStringShardIDVSNestIntHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(1)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(0).inTableShard(1)));
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));

        //string shardid和内部hints相同
        dao.transWithStringShardIDVSNestIntHints("0");
        assertEquals("transWithStringShardIDVSNestIntHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(1)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));
    }

    @Test
    public void transWithIntShardIDVSNestIntHintsTest() throws Exception{
        //Int shardid和内部hints不同
        try {
            dao.transWithIntShardIDVSNestIntHints(1);
            fail();
        }catch (Exception e){}

        assertNotEquals("transWithIntShardIDVSNestIntHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(1)).getName());
        assertNotEquals("transWithIntShardIDVSNestIntHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(1)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(0).inTableShard(1)));
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));

        //Int shardid和内部hints相同
        dao.transWithIntShardIDVSNestIntHints(0);
        assertEquals("transWithIntShardIDVSNestIntHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(1)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
        assertEquals(3,dao.count(new DalHints().inShard(0).inTableShard(0)));
    }

    @Test
    public void transWithIntShardIDVSNestStringHintsTest() throws Exception{
        //Int shardid和内部hints不同
        try {
            dao.transWithIntShardIDVSNestStringHints(1);
            fail();
        }catch (Exception e){}

        assertNotEquals("transWithIntShardIDVSNestStringHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(1)).getName());
        assertNotEquals("transWithIntShardIDVSNestStringHints",dao.queryByPk(1,new DalHints().inShard(1).inTableShard(1)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(0).inTableShard(1)));
        assertEquals(3,dao.count(new DalHints().inShard(1).inTableShard(1)));

        //Int shardid和内部hints相同
        dao.transWithIntShardIDVSNestStringHints(0);
        assertEquals("transWithIntShardIDVSNestStringHints",dao.queryByPk(1,new DalHints().inShard(0).inTableShard(1)).getName());
        assertEquals(2,dao.count(new DalHints().inShard(0).inTableShard(1)));
        assertEquals(3,dao.count(new DalHints().inShard(0).inTableShard(0)));
    }

    @Test
    public void transFailTest() throws Exception{
        try {
            dao.transFail(0, new DalHints().inShard(0));
            fail();
        }catch (Exception e){}
        assertNotEquals("transFail",dao.queryByPk(3,new DalHints().inShard(0).inTableShard(1)).getName());
        assertEquals(3,dao.count(new DalHints().inShard(0).inTableShard(0)));

    }
}
