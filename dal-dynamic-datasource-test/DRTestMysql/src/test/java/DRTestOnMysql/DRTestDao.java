package DRTestOnMysql;

import com.alibaba.fastjson.JSONObject;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.InsertSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.UploadResult;
import qunar.tc.qconfig.client.Uploader;
import qunar.tc.qconfig.client.impl.ConfigUploader;
import qunar.tc.qconfig.client.impl.Snapshot;
import qunar.tc.qconfig.client.impl.VersionProfile;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import static org.junit.Assert.assertTrue;


public class DRTestDao {
    private static final boolean ASC = true;
    private DalTableDao<DRTestPojo> client;
    private static final String DATA_BASE = "noShardTestOnMysql";
    private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
    private DalQueryDao queryDaoMysql = null;
    /*private DalQueryDao queryDaoSqlServer = null;*/
    private static Logger log = LoggerFactory.getLogger(DRTestDao.class);
    private DalRowMapper<DRTestPojo> personGenRowMapper = null;

    public DRTestDao() throws SQLException {
        this.client = new DalTableDao<>(new DalDefaultJpaParser<>(DRTestPojo.class));
        this.personGenRowMapper = new DalDefaultJpaMapper<>(DRTestPojo.class);
        this.queryDaoMysql = new DalQueryDao(DATA_BASE);
//		this.queryDaoSqlServer = new DalQueryDao("noShardTestOnSqlServer");
    }

    public DRTestDao(String databaseSetName) throws SQLException {
       /* if(databaseSetName.equals("noShardTestOnSqlServer"))
            this.queryDaoSqlServer = new DalQueryDao("noShardTestOnSqlServer");*/
        this.client = new DalTableDao<>(DRTestPojo.class, databaseSetName);
        this.personGenRowMapper = new DalDefaultJpaMapper<>(DRTestPojo.class);
        this.queryDaoMysql = new DalQueryDao(databaseSetName);
    }


    /**
     * Query Person by complex primary key
     **/
    public DRTestPojo queryByPk(Integer iD, DalHints hints)
            throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        DRTestPojo pk = new DRTestPojo();
        pk.setID(iD);
        return client.queryByPk(pk, hints);
    }

    /**
     * Get the all records count
     */
    public int count(DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);
        SelectSqlBuilder builder = new SelectSqlBuilder().selectCount();
        return client.count(builder, hints).intValue();
    }


    /**
     * Insert pojo and get the generated PK back in keyHolder.
     * If the "set no count on" for MS SqlServer is set(currently set in Ctrip), the operation may fail.
     * Please don't pass keyholder for MS SqlServer to avoid the failure.
     *
     * @param hints   Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojo pojo to be inserted
     * @return how many rows been affected
     * @throws SQLException
     */
    public int insert(DalHints hints, DRTestPojo daoPojo) throws SQLException {
        if (null == daoPojo)
            return 0;
        hints = DalHints.createIfAbsent(hints);
        return client.insert(hints, daoPojo);
    }


    /**
     * Insert pojos in batch mode.
     * The DalDetailResults will be set in hints to allow client know how the operation performed in each of the shard.
     *
     * @param hints    Additional parameters that instruct how DAL Client perform database operation.
     * @param daoPojos list of pojos to be inserted
     * @return how many rows been affected for inserting each of the pojo
     * @throws SQLException
     */
    public int[] batchInsert(DalHints hints, List<DRTestPojo> daoPojos) throws SQLException {
        if (null == daoPojos || daoPojos.size() <= 0)
            return new int[0];
        hints = DalHints.createIfAbsent(hints);
        return client.batchInsert(hints, daoPojos);
    }


    /**
     * mysql, noshard
     **/
    public int test_def_update(DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("truncate testTable");
        StatementParameters parameters = new StatementParameters();
        int i = 1;

        return queryDaoMysql.update(builder, parameters, hints);
    }


    /**
     * createTable
     **/
    public int createTable() throws SQLException {
        return createTable(null);
    }

    /**
     * createTable
     **/
    public int createTable(DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("CREATE TABLE `testtable` ( `ID` int(11) NOT NULL AUTO_INCREMENT, `Name` varchar(45) DEFAULT NULL COMMENT '姓名', `Age` int(11) DEFAULT NULL COMMENT '年龄', `Birth` datetime DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`ID`) ) ");
        StatementParameters parameters = new StatementParameters();
        int i = 1;

        return queryDaoMysql.update(builder, parameters, hints);
    }

    /**
     * drop table
     **/
    public int dropTable() throws SQLException {
        return dropTable(null);
    }

    /**
     * drop table
     **/
    public int dropTable(DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);

        FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(dbCategory);
        builder.setTemplate("DROP TABLE IF EXISTS `testtable`");
        StatementParameters parameters = new StatementParameters();
        int i = 1;

        return queryDaoMysql.update(builder, parameters, hints);
    }

    /**
     * 自定义，查询
     **/
    public String selectHostname(DalHints hints) throws Exception {
        hints = DalHints.createIfAbsent(hints);

        FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate("select @@hostname");
        StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.INTEGER,delay);
        builder.simpleType().requireFirst().nullable();
        return queryDaoMysql.query(builder, parameters, hints);

    }

    /**
     * 自定义，查询
     **/
    public List<String> selectHostnameInAllShards(DalHints hints) throws Exception {
        hints = DalHints.createIfAbsent(hints);
        FreeSelectSqlBuilder<List<String>> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate("select @@hostname");
        StatementParameters parameters = new StatementParameters();
        builder.simpleType();
        return queryDaoMysql.query(builder, parameters, hints);
    }

    /**
     * 自定义，查询
     **//*
    public String selectHostnameSqlserver(DalHints hints) throws Exception {
		hints = DalHints.createIfAbsent(hints);

		FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(DatabaseCategory.SqlServer);
		builder.setTemplate("select @@SERVERNAME");
		StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.INTEGER,delay);
		builder.simpleType().requireFirst().nullable();
		return queryDaoSqlServer.query(builder, parameters, hints);

	}*/

    /**
     * 自定义，查询
     **/
    public String selectDatabase(DalHints hints) throws Exception {
        hints = DalHints.createIfAbsent(hints);

        FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate("select database()");
        StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.INTEGER,delay);
        builder.simpleType().requireFirst().nullable();
        return queryDaoMysql.query(builder, parameters, hints);

    }

    /**
     * 构建，新增
     **/
    public int test_build_insert(String Name, Integer Age, DalHints hints) throws SQLException {
        hints = DalHints.createIfAbsent(hints);
//        hints.inAllShards();
//        hints.asyncExecution();

        InsertSqlBuilder builder = new InsertSqlBuilder();
        builder.set("Name", Name, Types.VARCHAR);
        builder.set("Age", Age, Types.INTEGER);

        return client.insert(builder, hints);
    }

    /**
     * 自定义，查询
     **/
    public String testLongQuery(int delay, DalHints hints) throws Exception {
        hints = DalHints.createIfAbsent(hints);

        FreeSelectSqlBuilder<String> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate("select name from testTable where sleep(?) = 0 limit 1");
        StatementParameters parameters = new StatementParameters();
        parameters.set(1, Types.INTEGER, delay);
        builder.simpleType().requireFirst().nullable();
        return queryDaoMysql.query(builder, parameters, hints);
    }


    /*public HttpResponse switchPostByQconfigInterface(JsonArray jsonArray, boolean isPro) throws Exception {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("env", "fat");
        jsonObject.add("data", jsonArray);

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost;
        if (isPro)
            httppost = new HttpPost("http://qconfig.ctripcorp.com/plugins/titan/config/mha?group=100010061&operator=MHA");
        else
            httppost = new HttpPost("http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan/config/mha?group=100010061&operator=MHA");

        StringEntity se = new StringEntity(jsonObject.toString());
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        return response;

    }*/

   /* public HttpResponse switchPostByQconfigInterfacePro(JsonArray jsonArray) throws Exception {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("env", "fat");
        jsonObject.add("data", jsonArray);

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://qconfig.ctripcorp.com/plugins/titan/config/mha?group=100010061&operator=MHA");

        StringEntity se = new StringEntity(jsonObject.toString());
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        return response;

    }*/

    /*public HttpResponse switchPostByDBAInterface(boolean isPro) throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("clustername", "dalfat");
        if (isPro)
            jsonObject.addProperty("qconfigenv", "pro");
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://mysqlapi.db.fat.qa.nt.ctripcorp.com:8080/clusterapi/testhaswitch");

        StringEntity se = new StringEntity(jsonObject.toString());
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        return response;
    }*/

    /*public HttpResponse switchPostByDBAInterfacePro() throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("clustername", "dalfat");
        jsonObject.addProperty("qconfigenv", "pro");
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://mysqlapi.db.fat.qa.nt.ctripcorp.com:8080/clusterapi/testhaswitch");

        StringEntity se = new StringEntity(jsonObject.toString());
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        return response;
    }*/

    /*public void postByMHA() throws Exception {
        try {
            HttpResponse response = switchPostByDBAInterface();
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                //解析json格式的返回结果
                JSONObject json = JSONObject.parseObject(EntityUtils.toString(resEntity).toString());
                try {
                    assertTrue(json.getBoolean("success"));
                } catch (Error error) {
//                    error.printStackTrace();
                    log.error("切换返回值为false", error);
                }
            }
            log.info("切换完成");
        } catch (Exception e) {
            log.error("切换失败", e);
        }
    }*/

    /*public boolean uploadProperties(Map<String, String> map) throws Exception {
        Uploader uploader = ConfigUploader.getInstance();//获取uploader实例
        Optional<Snapshot<String>> result1 = uploader.getCurrent("datasource.properties");//拿到当前最新版本
        if (result1.isPresent()) {
            String oldContent = result1.get().getContent();//如果返回数据是properties文件
            Properties properties = new Properties();
            if (oldContent != null && oldContent.length() > 0) {
                properties.load(new StringReader(oldContent));
            }
            properties.clear();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());//更新配置
            }
            UploadResult result2 = uploader.uploadAtVersion(result1.get().getVersion(), "datasource.properties", parseProperties2String(properties), true);//如果已经存在，上传最新文件，并且设置为public文件
            System.out.println(result2);
            return true;
        } else {
//            UploadResult result2 = uploader.uploadAtVersion(VersionProfile.ABSENT, "datasource.properties", "connectionProperties=rewriteBatchedStatements=true;");//如果不存在，上传最新文件
//            System.out.println(result2);
            log.warn("datasource.properties不存在，请先添加");
            return false;
        }
    }


    public void testUpload(Map<String, String> map) throws Exception {
        Uploader uploader = ConfigUploader.getInstance();//获取uploader实例
        Optional<Snapshot<String>> result1 = uploader.getCurrent("test.properties");//拿到当前最新版本
        if (result1.isPresent()) {
            String oldContent = result1.get().getContent();//如果返回数据是properties文件
            Properties properties = new Properties();
            if (oldContent != null && oldContent.length() > 0) {
                properties.load(new StringReader(oldContent));
            }
            properties.clear();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
            UploadResult result2 = uploader.uploadAtVersion(result1.get().getVersion(), "test.properties", parseProperties2String(properties), true);//如果已经存在，上传最新文件，并且设置为public文件
            System.out.println(result2);
        } else {
//            UploadResult result2 = uploader.uploadAtVersion(VersionProfile.ABSENT, "datasource.properties", "connectionProperties=rewriteBatchedStatements=true;");//如果不存在，上传最新文件
//            System.out.println(result2);
            log.warn("test.properties不存在，请先添加");
        }
    }


    private String parseProperties2String(Properties properties) {
        if (properties == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            result.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        return result.toString();
    }*/

    public static void main(String args[]) {
       /* try {
            DRTestDao dao = new DRTestDao();
            Map<String, String> map = new HashMap<>();
            map.put("key3", "33");
//            map.put("key2","22");
            dao.testUpload(map);

            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(-1);*/
    }
}