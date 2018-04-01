package DRTestMybatisMySQL;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import qunar.tc.qconfig.client.UploadResult;
import qunar.tc.qconfig.client.Uploader;
import qunar.tc.qconfig.client.impl.ConfigUploader;
import qunar.tc.qconfig.client.impl.Snapshot;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lilj on 2017/10/25.
 */
@Repository
public class DRTestMapperDao {
    @Autowired
    private DRTestMapper drTestMapper;

    private static Logger log= LoggerFactory.getLogger(DRTestMapperDao.class);

    public  String getHostNameMySQL(){
        return drTestMapper.getHostNameMySQL();
    }

    public  String getDatabaseMySQL(){
        return drTestMapper.getDatabaseMySQL();
    }


    public DRTestMybatisPojo getDRTestMybatisPojo(){
        return drTestMapper.getDRTestMybatisPojo(1);
    }

    public void addDRTestMybatisPojo(){
        DRTestMybatisPojo drTestMybatisPojo=new DRTestMybatisPojo();
        drTestMybatisPojo.setName("testMybatis");
        drTestMybatisPojo.setAge(29);
        drTestMapper.addDRTestMybatisPojo(drTestMybatisPojo);
    }

    public void updateDRTestMybatisPojo(){
        DRTestMybatisPojo drTestMybatisPojo=new DRTestMybatisPojo();
        drTestMybatisPojo.setID(1);
        drTestMybatisPojo.setName("testUpdateMybatis");
        drTestMybatisPojo.setAge(99);
        drTestMapper.updateDRTestMybatisPojo(drTestMybatisPojo);
    }

    public String testLongQuery(){
        return drTestMapper.testLongQuery();
    }

    public int getCount(){
        return drTestMapper.getCount();
    }

    public void truncateTable(){
        drTestMapper.truncateTableMySQL();
    }

    /*public HttpResponse switchPostByQconfigInterface(JsonArray jsonArray) throws Exception {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("env", "fat");
        jsonObject.add("data", jsonArray);

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan/config/mha?group=100010061&operator=MHA");

        StringEntity se = new StringEntity(jsonObject.toString());
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        return response;

    }

    public HttpResponse switchPostByQconfigInterfacePro(JsonArray jsonArray) throws Exception {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("env", "fat");
        jsonObject.add("data", jsonArray);

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://qconfig.ctripcorp.com/plugins/titan/config/mha?group=100010061&operator=MHA");

        StringEntity se = new StringEntity(jsonObject.toString());
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        return response;

    }

    public HttpResponse switchPostByDBAInterface() throws Exception {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("clustername", "dalfat");

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://mysqlapi.db.fat.qa.nt.ctripcorp.com:8080/clusterapi/testhaswitch");

        StringEntity se = new StringEntity(jsonObject.toString());
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        return response;
    }

    public HttpResponse switchPostByDBAInterfacePro() throws Exception {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("clustername", "dalfat");
        jsonObject.addProperty("qconfigenv","pro");
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://mysqlapi.db.fat.qa.nt.ctripcorp.com:8080/clusterapi/testhaswitch");

        StringEntity se = new StringEntity(jsonObject.toString());
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        return response;
    }

    public boolean uploadProperties(Map<String, String> map) throws Exception {
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

}
