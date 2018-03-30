package noShardTest;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
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
@ContextConfiguration("classpath*:/TransactionalTestOnMysql.xml")
public class NoShardTransactionalTestOnMysqlSpringTest {
    @Autowired
    private NoShardTransactionalTestOnMysqlDao dao;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        /**
         * Initialize DalClientFactory.
         * The Dal.config can be specified from class-path or local file path.
         * One of follow three need to be enabled.
         **/
        DalClientFactory.initClientFactory(); // load from class-path Dal.config
        DalClientFactory.warmUpConnections();
//        client = DalClientFactory.getClient(DATA_BASE);
//        dao = new NoShardTransactionTestOnMysqlDao();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        dao.test_def_update(new DalHints());
        List<NoShardTransactionTestOnMysql> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            NoShardTransactionTestOnMysql daoPojo = new NoShardTransactionTestOnMysql();
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
    public void transFailTest() throws Exception{
       try {
           dao.transFail();
       }
       catch (Exception e){

       }
        Assert.assertEquals(6,dao.count(null));
       Assert.assertEquals(20,dao.queryByPk(3,null).getAge().intValue());
    }

}
