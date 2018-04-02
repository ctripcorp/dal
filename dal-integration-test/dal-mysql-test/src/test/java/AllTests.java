import com.ctrip.framework.dal.qmq.QmqApiTest;
import noShardTest.TitanDataSourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import shardTest.newVersionCode.*;
import shardTest.oldVersionCode.FreeShardingStrategyByTableOnMysqlGenDaoUnitTest;
import util.NetStatChecker;

@RunWith(Suite.class)
@SuiteClasses({
        noShardTest.AllTypesOnMysqlDaoUnitTest.class,
        noShardTest.DesignatedDatabaseOnMysqlGenDaoUnitTest.class,
        noShardTest.MasterOnlyOnMysqlDaoUnitTest.class,
        noShardTest.NoShardDalTransactionalTestOnMysqlNotSpringTest.class,
        noShardTest.NoShardDalTransactionalTestOnMysqlSpringTest.class,
        noShardTest.NoShardOnMysqlDaoUnitTest.class,
        noShardTest.NoShardTransactionalTestOnMysqlNotSpringTest.class,
        noShardTest.NoShardTransactionalTestOnMysqlSpringTest.class,
        noShardTest.QConfigTest2.class,
        noShardTest.RWTestOnMysql.class,

        DalTransactionalWithShardOnMysqlNotSpringTest.class,
        DalTransactionalWithShardOnMysqlSpringTest.class,
        FreeShardingStrategyByDBOnMysqlDaoUnitTest.class,
        FreeShardingStrategyByDBTableOnMysqlDaoUnitTest.class,
        shardTest.newVersionCode.ignoreMissingFieldsAndAllowPartialTestOnMysqlDaoUnitTest.class,
        shardTest.newVersionCode.NewHintsOfCodeGenOnMysqlDaoUnitTest.class,
        shardTest.newVersionCode.PeopleShardColModShardByDBTableOnMysqlDaoUnitTest.class,
        shardTest.newVersionCode.PersonShardColModShardByDBOnMysqlDaoUnitTest.class,
        shardTest.newVersionCode.RWShardTestOnMysql.class,
        TransactionalWithShardOnMysqlSpringTest.class,
        TransactionalWithShardOnMysqlNotSpringTest.class,

        FreeShardingStrategyByTableOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.HintsOfCodeGenOnMySqlDaoUnitTest.class,
        shardTest.oldVersionCode.ignoreMissingFieldsAndAllowPartialTestOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.ShardColModShardByDBOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.ShardColModShardByDBTableOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.ShardColModShardByTableOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.SimpleShardByDBOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.SimpleShardByDBTableOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.SimpleShardByTableOnMysqlGenDaoUnitTest.class,


        QmqApiTest.class,
        TitanDataSourceTest.class,
})
public class AllTests {
    @BeforeClass
    public static void setUp() throws Exception {
        if(NetStatChecker.netstatCMD()>6)
            Assert.fail("connection count greater than 6!!");
    }

    @AfterClass
    public static void tearDown() throws Exception{
        if(NetStatChecker.netstatCMD()>6)
            Assert.fail("connection count greater than 6!!");
    }
}
