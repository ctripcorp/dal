package noShardTest;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilj on 2017/7/27.
 */
public class NoShardTransactionTestOnSqlServerNotSpringTest {
    private static NoShardTransactionTestOnSqlServerDao dao;

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
        dao= DalTransactionManager.create(NoShardTransactionTestOnSqlServerDao.class);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        dao.test_def_update(new DalHints());


        List<NoShardTransactionTestOnSqlServer> daoPojos1 = new ArrayList<NoShardTransactionTestOnSqlServer>(3);
        for(int i=0;i<6;i++)
        {
            NoShardTransactionTestOnSqlServer daoPojo = new NoShardTransactionTestOnSqlServer();
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
}
