import noShardTest.NoShardDalTransactionalTestOnSqlServerNotSpringTest;
import noShardTest.NoShardDalTransactionalTestOnSqlServerSpringTest;
import noShardTest.NoShardTransactionalTestOnSqlServerNotSpringTest;
import noShardTest.NoShardTransactionalTestOnSqlServerSpringTest;
import org.apache.zookeeper.server.quorum.FastLeaderElection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import shardTest.newVersionCodeTest.*;
import util.NetStatChecker;

@RunWith(Suite.class)
@SuiteClasses({
        DalTransactionalWithShardOnSqlServerNotSpringTest.class,
        DalTransactionalWithShardOnSqlServerSpringTest.class,
        FreeShardingStrategyByDBOnSqlServerDaoUnitTest.class,
        FreeShardingStrategyByDBTableOnSqlServerDaoUnitTest.class,
        shardTest.newVersionCodeTest.ignoreMissingFieldsAndAllowPartialTestOnSqlServerDaoUnitTest.class,
        shardTest.newVersionCodeTest.NewHintsOfCodeGenOnSqlServerDaoUnitTest.class,
        shardTest.newVersionCodeTest.PeopleShardColModByDBTableOnSqlServerDaoUnitTest.class,
        shardTest.newVersionCodeTest.PeopleShardColModShardByDBOnSqlServerDaoUnitTest.class,
        shardTest.newVersionCodeTest.RWShardTestOnSqlServer.class,
        TransactionalWithShardOnSqlServerSpringTest.class,
        TransactionalWithShardOnSqlServerNotSpringTest.class,

        shardTest.oldVersionCodeTest.HintsOfCodeGenOnSqlServerDaoUnitTest.class,
        shardTest.oldVersionCodeTest.ignoreMissingFieldsAndAllowPartialTestOnSqlServerGenDaoUnitTest.class,
        shardTest.oldVersionCodeTest.ShardColModShardByDBOnSqlServerGenDaoUnitTest.class,
        shardTest.oldVersionCodeTest.SimpleShardByDBOnSqlServerGenDaoUnitTest.class,


        noShardTest.AllTypesCIDaoUnitTest.class,
        noShardTest.AllTypesOnSqlServerDaoUnitTest.class,
        noShardTest.DesignatedDatabaseOnSqlServerDaoUnitTest.class,
        noShardTest.MasterOnlyOnSqlServerDaoUnitTest.class,
        NoShardDalTransactionalTestOnSqlServerNotSpringTest.class,
        NoShardDalTransactionalTestOnSqlServerSpringTest.class,
        noShardTest.NoShardOnSqlServerUnitTest.class,
        NoShardTransactionalTestOnSqlServerSpringTest.class,
        NoShardTransactionalTestOnSqlServerNotSpringTest.class,
        noShardTest.QConfigTest1.class,
        noShardTest.RWTestOnSQLServer.class,
        noShardTest.SpTestDaoUnitTest.class,
        noShardTest.TestFor414.class,


})
public class AllTests {
    @BeforeClass
    public static void setUp() throws Exception {
        System.setProperty("env", "fat");
        if(NetStatChecker.netstatCMD()>6)
            Assert.fail("connection count greater than 6!!");
    }

    @AfterClass
    public static void tearDown() throws Exception{
        if(NetStatChecker.netstatCMD()>6)
            Assert.fail("connection count greater than 6!!");
    }
}