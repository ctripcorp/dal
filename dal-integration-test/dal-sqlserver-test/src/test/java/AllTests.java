import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        shardTest.newVersionCodeTest.ignoreMissingFieldsAndAllowPartialTestOnSqlServerDaoUnitTest.class,
        shardTest.newVersionCodeTest.NewHintsOfCodeGenOnSqlServerDaoUnitTest.class,
        shardTest.newVersionCodeTest.PeopleShardColModByDBTableOnSqlServerDaoUnitTest.class,
        shardTest.newVersionCodeTest.PeopleShardColModShardByDBOnSqlServerDaoUnitTest.class,
        shardTest.newVersionCodeTest.TransactionWithShardOnSqlServerSpringTest.class,
        shardTest.newVersionCodeTest.TransactionWithShardOnSqlServerNotSpringTest.class,

        shardTest.oldVersionCodeTest.HintsOfCodeGenOnSqlServerDaoUnitTest.class,
        shardTest.oldVersionCodeTest.ignoreMissingFieldsAndAllowPartialTestOnSqlServerGenDaoUnitTest.class,
        shardTest.oldVersionCodeTest.ShardColModShardByDBOnSqlServerGenDaoUnitTest.class,
        shardTest.oldVersionCodeTest.SimpleShardByDBOnSqlServerGenDaoUnitTest.class,

        noShardTest.AllTypesCIDaoUnitTest.class,
        noShardTest.AllTypesOnSqlServerDaoUnitTest.class,
        noShardTest.DesignatedDatabaseOnSqlServerDaoUnitTest.class,
        noShardTest.NoShardOnSqlServerUnitTest.class,
        noShardTest.MasterOnlyOnSqlServerDaoUnitTest.class,
        noShardTest.QConfigTest1.class,
        noShardTest.NoShardTransactionTestOnSqlServerSpringTest.class,
        noShardTest.NoShardTransactionTestOnSqlServerNotSpringTest.class,
        noShardTest.SpTestDaoUnitTest.class,
        noShardTest.TestFor414.class,

        shardTest.newVersionCodeTest.RWShardTestOnSqlServer.class,
        noShardTest.RWTestOnSQLServer.class
})
public class AllTests {
  @BeforeClass
  public static void setUp() throws Exception {
    System.setProperty("env", "fat");
  }
}
