package util;

import com.ctrip.platform.dal.dao.helper.JsonUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by lilj on 2018/3/4.
 */
public class ConnectionStringSwitch {

    private static Logger log = LoggerFactory.getLogger(ConnectionStringSwitch.class);

    public HttpResponse switchPostByDBAInterface(boolean isPro) throws Exception {
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
    }

    public HttpResponse switchPostByQconfigInterface(JsonArray jsonArray, boolean isPro) throws Exception {
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

    }

    public void postByMHA(boolean isPro) {
        try {
            HttpResponse response = switchPostByDBAInterface(isPro);
            /*if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                //解析json格式的返回结果
                JSONObject json = JSONObject.parseObject(EntityUtils.toString(resEntity).toString());
                log.info(String.format("MHA接口返回结果：%s", json.toString()));
                System.out.println(String.format("MHA接口返回结果：%s", json.toString()));
                try {
                    assertTrue(json.getBoolean("success"));
                    log.info("切换成功");
                } catch (Throwable error) {
                    log.error("调用MHA接口失败，原因：" + json.getString("message"), error);
                    fail();
                }
            }*/
            checkPostResponse(response);
        } catch (Exception e) {
            log.error("调用MHA接口失败", e);
            fail();
        }
    }

    public void postByQconfig(JsonArray jsonArray,boolean isPro) throws Exception {
        try {
            HttpResponse response = switchPostByQconfigInterface(jsonArray,isPro);
            checkPostResponse(response);
        } catch (Exception e) {
            log.error("切换成无效IP失败", e);
            fail();
        }
    }

    public void checkPostResponse(HttpResponse response) throws Exception{
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity resEntity = response.getEntity();
            //解析json格式的返回结果
            JsonObject json = JsonUtils.parseObject(EntityUtils.toString(resEntity));
            log.info(String.format("接口返回结果：%s", json.toString()));
            System.out.println(String.format("接口返回结果：%s", json.toString()));
            try {
                assertEquals(0, json.get("status").getAsInt());
                log.info("切换成功");
            } catch (Throwable error) {
                log.error("调用切换接口失败，原因：" + json.get("message").getAsString(), error);
                fail();
            }
        }
    }

    public void resetConnectionString(boolean isPro) throws Exception {
        //复位连接串
        log.info("reset connectionStrings");
        postByMHA(isPro);
        log.info("reset connectionStrings succeed");
        //等待5秒钟
        log.info("3 seconds after connectionStrings reset");
        Thread.sleep(3000);
    }
}
