package DRTestMybatisSQLServer;



/**
 * Created by lilj on 2017/11/2.
 */
public interface DRTestSQLServerMapper {
        String getHostNameSQLServer();
        void truncateTableSQLServer();
        DRTestMybatisSQLServerPojo getDRTestMybatisSQLServerPojo(int iD);
        void addDRTestMybatisSQLServerPojo(DRTestMybatisSQLServerPojo DRTestMybatisSQLServerPojo);
        void updateDRTestMybatisSQLServerPojo(DRTestMybatisSQLServerPojo DRTestMybatisSQLServerPojo);
}
