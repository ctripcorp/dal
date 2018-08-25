package test.com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by lilj on 2018/5/3.
 */
public class LoggerHelperTest {
    @Test
    public void testToJSon() throws Exception {
        String finalString = "{\"HasSql\":\"1\",\"Hash\":\"\",\"SqlTpl\":\"/*100008424-DRTestDao.insert[MSG_ID:123456]*/INSERT INTO `testTable` (`Name`) VALUES(?)\",\"Param\":\"aXuR5QP4NhJhsYi3H+rKEdHG/06mtU6m\",\"IsSuccess\":\"1\",\"ErrorMsg\":\"\",\"CostDetail\":\"{'Decode'='16', 'Connect'='517', 'Prepare'='1', 'Execute'='11', 'ClearUp'='2'}\"}";

        Map<String, String> costDetailMap = new LinkedHashMap();
        costDetailMap.put("'Decode'", "'" + Long.toString(16l) + "'");
        costDetailMap.put("'Connect'", "'" + Long.toString(517l) + "'");
        costDetailMap.put("'Prepare'", "'" + Long.toString(1l) + "'");
        costDetailMap.put("'Execute'", "'" + Long.toString(11l) + "'");
        costDetailMap.put("'ClearUp'", "'" + Long.toString(2l) + "'");


        Map<String, String> logMap = new LinkedHashMap<>();
        logMap.put("HasSql", "1");
        logMap.put("Hash", "");
        logMap.put("SqlTpl", "/*100008424-DRTestDao.insert[MSG_ID:123456]*/INSERT INTO `testTable` (`Name`) VALUES(?)");
        logMap.put("Param", "aXuR5QP4NhJhsYi3H+rKEdHG/06mtU6m");
        logMap.put("IsSuccess", "1");
        logMap.put("ErrorMsg", "");
        logMap.put("CostDetail", costDetailMap.toString());

        for (int i = 0; i < 100; i++)
            assertEquals(finalString, LoggerHelper.toJson(logMap));

        Map<String, String> logMapWithNull = new LinkedHashMap<>();
        logMapWithNull.put("HasSql", "1");
        logMapWithNull.put("CostDetail", null);

        try {
            LoggerHelper.toJson(logMapWithNull);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testGetNormalSimplifiedDBUrl() throws Exception {
        String sqlserverUrl="jdbc:sqlserver://SqlServer:55111;jaasConfigurationName=SQLJDBCDriver;serverPreparedStatementDiscardThreshold=10;enablePrepareOnFirstPreparedStatementCall=false;fips=false;socketTimeout=100000;authentication=NotSpecified;authenticationScheme=nativeAuthentication;xopenStates=false;sendTimeAsDatetime=false;trustStoreType=JKS;trustServerCertificate=false;TransparentNetworkIPResolution=true;serverNameAsACE=false;sendStringParametersAsUnicode=false;selectMethod=direct;responseBuffering=adaptive;queryTimeout=-1;packetSize=8000;multiSubnetFailover=false;loginTimeout=15;lockTimeout=-1;lastUpdateCount=true;encrypt=false;disableStatementPooling=true;databaseName=sqlserverdb;columnEncryptionSetting=Disabled;applicationName=Microsoft JDBC Driver for SQL Server;applicationIntent=readwrite;";
        String mysqlUrl="jdbc:mysql://MySql:55111/mysqldb?useUnicode=true&characterEncoding=UTF-8";
        String emptyUrl = "";
        String nullUrl =null;
        String blankUrl = " ";
        String invalidMySqlUrl="jdbc:mysql://no question mark";
        String invalidSqlServerUrl="jdbc:sqlserver://;databaseName";
        String sqlServerUrlWithNoDatabaseName="jdbc:sqlserver://;abcdefg";
        String oracleUrl="jdbc:oracle:thin:@localhost:1521:orcl";


        String simplifiedSqlserverUrl =LoggerHelper.getSimplifiedDBUrl(sqlserverUrl);
        assertEquals("jdbc:sqlserver://SqlServer:55111/sqlserverdb",simplifiedSqlserverUrl);

        String simplifiedMysqlUrl = LoggerHelper.getSimplifiedDBUrl(mysqlUrl);
        assertEquals("jdbc:mysql://MySql:55111/mysqldb",simplifiedMysqlUrl);

        String simplifiedOracleUrl=LoggerHelper.getSimplifiedDBUrl(oracleUrl);
        assertEquals(oracleUrl,simplifiedOracleUrl);

        try {
            String simplifiedEmptyUrl = LoggerHelper.getSimplifiedDBUrl(emptyUrl);
            assertEquals("blank url", simplifiedEmptyUrl);

            String simplifiedNullUrl = LoggerHelper.getSimplifiedDBUrl(nullUrl);
            assertEquals("blank url", simplifiedNullUrl);

            String simplifiedBlankUrl = LoggerHelper.getSimplifiedDBUrl(blankUrl);
            assertEquals("blank url", simplifiedBlankUrl);

            String simplifiedInvalidMySqlUrl=LoggerHelper.getSimplifiedDBUrl(invalidMySqlUrl);
            assertEquals(invalidMySqlUrl,simplifiedInvalidMySqlUrl);

            String simplifiedInvalidSqlServerUrl=LoggerHelper.getSimplifiedDBUrl(invalidSqlServerUrl);
            assertEquals("jdbc:sqlserver:///blank database name",simplifiedInvalidSqlServerUrl);

            String simplifiedSqlServerUrlWithNoDatabaseName=LoggerHelper.getSimplifiedDBUrl(sqlServerUrlWithNoDatabaseName);
            assertEquals("jdbc:sqlserver:///blank database name",simplifiedSqlServerUrlWithNoDatabaseName);
        }catch (Throwable e){
            e.printStackTrace();
            fail();
        }
    }


}
