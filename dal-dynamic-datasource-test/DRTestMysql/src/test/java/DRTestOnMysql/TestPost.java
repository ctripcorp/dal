package DRTestOnMysql;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by lilj on 2017/9/27.
 */
public class TestPost {
    public static void testPost() throws Exception {

        try{
            JsonObject jsonObject = new JsonObject();

            JsonArray jsonArray = new JsonArray();
            JsonObject subJson = new JsonObject();
            subJson.addProperty("keyname", "mysqldaltest01db_W");
            subJson.addProperty("server", "10.2.74.122");
            subJson.addProperty("port", "55111");
            jsonArray.add(subJson);

            jsonObject.addProperty("env", "fat");
            jsonObject.add("data", jsonArray);

//            System.out.println(jsonObject.toString());

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan/config/mha?group=100010061&operator=MHA");

            StringEntity se = new StringEntity(jsonObject.toString());
            httppost.setEntity(se);
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == HttpStatus.SC_OK){
                System.out.println("服务器正常响应.....");
                HttpEntity resEntity = response.getEntity();
                //解析json格式的返回结果
                JSONObject json = JSONObject.parseObject(EntityUtils.toString(resEntity).toString());
                System.out.println(json.toString());
                EntityUtils.consume(resEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws Exception {
        long start=System.currentTimeMillis();
           testPost();
           long end=System.currentTimeMillis();
           System.out.println("Time cost: "+ (end-start));
    }
}
