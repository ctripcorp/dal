import com.ctrip.platform.dal.dao.DalClientFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import switchtest.mybatis.*;
import switchtest.mybatiswithdal.TestBothDALAndMybatis;
import switchtest.mybatiswithdal.TestPoolProperties;

/**
 * Created by lilj on 2018/7/12.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConnectionStringSwitchTest.class,
        PoolPropertiesSwitchTest.class,
        QPSTest.class,
        ConnectionTest.class,
        IpDomainSwitchTest.class,

        TestBothDALAndMybatis.class,
        TestPoolProperties.class,
        //        TestLocalDalAndMybatisFat16.class,
        //        QPSTest.class,
})
public class AllTestsForSwitch {
    /*@BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"DalConfigForSwitch/Dal.config");
    }

    @AfterClass
    public static void tearDown() throws Exception{
        DalClientFactory.shutdownFactory();
    }*/
}
