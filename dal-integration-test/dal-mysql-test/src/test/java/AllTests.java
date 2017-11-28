import com.ctrip.framework.dal.qmq.QmqApiTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        noShardTest.AllTypesOnMysqlDaoUnitTest.class,
        noShardTest.DesignatedDatabaseOnMysqlGenDaoUnitTest.class,
        noShardTest.MasterOnlyOnMysqlDaoUnitTest.class,
        noShardTest.NoShardOnMysqlDaoUnitTest.class,
        noShardTest.QConfigTest2.class,
        noShardTest.NoShardTransactionTestOnMysqlSpringTest.class,
        noShardTest.NoShardTransactionTestOnMysqlNotSpringTest.class,

        shardTest.newVersionCode.ignoreMissingFieldsAndAllowPartialTestOnMysqlDaoUnitTest.class,
        shardTest.newVersionCode.NewHintsOfCodeGenOnMysqlDaoUnitTest.class,
        shardTest.newVersionCode.PeopleShardColModShardByDBTableOnMysqlDaoUnitTest.class,
        shardTest.newVersionCode.PersonShardColModShardByDBOnMysqlDaoUnitTest.class,
        shardTest.newVersionCode.TransactionWithShardOnMysqlSpringTest.class,
        shardTest.newVersionCode.TransactionWithShardOnMysqlNotSpringTest.class,

        shardTest.oldVersionCode.HintsOfCodeGenOnMySqlDaoUnitTest.class,
        shardTest.oldVersionCode.ignoreMissingFieldsAndAllowPartialTestOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.ShardColModShardByDBOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.ShardColModShardByDBTableOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.ShardColModShardByTableOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.SimpleShardByDBOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.SimpleShardByDBTableOnMysqlGenDaoUnitTest.class,
        shardTest.oldVersionCode.SimpleShardByTableOnMysqlGenDaoUnitTest.class,

        QmqApiTest.class,

})
public class AllTests {
  @BeforeClass
  public static void setUp() throws Exception {
    System.setProperty("env", "fat");
  }
}
