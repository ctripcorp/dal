import cluster.ClusterDaoTest;
import idegentest.IdGenPrefetchTest;
import idegentest.IdGenTestOnMysql;
import noshardtest.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import qunar.tc.qmq.dal.DatabaseSetAdaptTest;
import shardtest.newVersionCodeTest.*;
import shardtest.oldVersionCodeTest.*;
import shardtest.qmq.QmqDalIntegrationTest;
import testUtil.NetStatChecker;

/**
 * Created by lilj on 2018/4/22.
 */
    @RunWith(Suite.class)
    @Suite.SuiteClasses({
            IdGenTestOnMysql.class,
            IdGenPrefetchTest.class,
            EntityExtendsOnMysqlTest.class,
            ConnectionPhantomReferenceCleanerOnDalClientTest.class,
            MysqlAllTypesTableDaoUnitTest.class,
            DesignatedDatabaseOnMysqlGenDaoUnitTest.class,
            MasterOnlyOnMysqlDaoUnitTest.class,
            NoShardDalTransactionalTestOnMysqlNotSpringTest.class,
            NoShardDalTransactionalTestOnMysqlSpringTest.class,
            NoShardOnMysqlDaoUnitTest.class,
            NoShardTransactionalTestOnMysqlNotSpringTest.class,
            NoShardTransactionalTestOnMysqlSpringTest.class,
            QConfigTest2.class,
            RWTestOnMysql.class,
            KeyholderOnMysqlTest.class,
            DalTransactionalWithShardOnMysqlNotSpringTest.class,
            DalTransactionalWithShardOnMysqlSpringTest.class,
            FreeShardingStrategyByDBOnMysqlDaoUnitTest.class,
            FreeShardingStrategyByDBTableOnMysqlDaoUnitTest.class,
            IgnoreMissingFieldsAndAllowPartialTestOnMysqlDaoUnitTest.class,
            NewHintsOfCodeGenOnMysqlDaoUnitTest.class,
            PeopleShardColModShardByDBTableOnMysqlDaoUnitTest.class,
            PersonShardColModShardByDBOnMysqlDaoUnitTest.class,
            RWShardTestOnMysql.class,
            TransactionalWithShardOnMysqlSpringTest.class,
            TransactionalWithShardOnMysqlNotSpringTest.class,

            FreeShardingStrategyByTableOnMysqlGenDaoUnitTest.class,
            HintsOfCodeGenOnMySqlDaoUnitTest.class,
            IgnoreMissingFieldsAndAllowPartialTestOnMysqlGenDaoUnitTest.class,
            ShardColModShardByDBOnMysqlGenDaoUnitTest.class,
            ShardColModShardByDBTableOnMysqlGenDaoUnitTest.class,
            ShardColModShardByTableOnMysqlGenDaoUnitTest.class,
            SimpleShardByDBOnMysqlGenDaoUnitTest.class,
            SimpleShardByDBTableOnMysqlGenDaoUnitTest.class,
            SimpleShardByTableOnMysqlGenDaoUnitTest.class,

            QmqApiTest.class,
            TitanDataSourceTest.class,
            QmqDalIntegrationTest.class,
            DatabaseSetAdaptTest.class,

            DalTransactionalWithShardOnSqlServerNotSpringTest.class,
            DalTransactionalWithShardOnSqlServerSpringTest.class,
            FreeShardingStrategyByDBOnSqlServerDaoUnitTest.class,
            FreeShardingStrategyByDBTableOnSqlServerDaoUnitTest.class,
            IgnoreMissingFieldsAndAllowPartialTestOnSqlServerDaoUnitTest.class,
            NewHintsOfCodeGenOnSqlServerDaoUnitTest.class,
            PeopleShardColModByDBTableOnSqlServerDaoUnitTest.class,
            PeopleShardColModShardByDBOnSqlServerDaoUnitTest.class,
            RWShardTestOnSqlServer.class,
            TransactionalWithShardOnSqlServerSpringTest.class,
            TransactionalWithShardOnSqlServerNotSpringTest.class,

            HintsOfCodeGenOnSqlServerDaoUnitTest.class,
            ignoreMissingFieldsAndAllowPartialTestOnSqlServerGenDaoUnitTest.class,
            ShardColModShardByDBOnSqlServerGenDaoUnitTest.class,
            SimpleShardByDBOnSqlServerGenDaoUnitTest.class,


            SqlServerAllTypesCITableDaoUnitTest.class,
            SqlServerAllTypesTableDaoUnitTest.class,
            DesignatedDatabaseOnSqlServerDaoUnitTest.class,
            MasterOnlyOnSqlServerDaoUnitTest.class,
            NoShardDalTransactionalTestOnSqlServerNotSpringTest.class,
            NoShardDalTransactionalTestOnSqlServerSpringTest.class,
            NoShardOnSqlServerUnitTest.class,
            NoShardTransactionalTestOnSqlServerSpringTest.class,
            NoShardTransactionalTestOnSqlServerNotSpringTest.class,
            RWTestOnSQLServer.class,
            SpTestDaoUnitTest.class,
            TVPColumnsOrderDaoUnitTest.class,
            TestFor414.class,
            ClusterDaoTest.class
    })
    public class AllTests {
        @BeforeClass
        public static void setUpBeforeClass() throws Exception {
            if(NetStatChecker.netstatCMD()>12)
                Assert.fail("connection count greater than 12!!");
        }

        @AfterClass
        public static void tearDown() throws Exception{
            if(NetStatChecker.netstatCMD()>12)
                Assert.fail("connection count greater than 12!!");
        }
    }

