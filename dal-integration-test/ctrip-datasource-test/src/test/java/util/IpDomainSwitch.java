package util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;


/**
 * Created by lilj on 2018/3/4.
 */
public class IpDomainSwitch {
    private static Logger log = LoggerFactory.getLogger(IpDomainSwitch.class);

    public void setFailover() throws Exception {
//        String setFailoverUrl = "http://10.5.110.38/switch/dal_ip_status/fat/dal_ip_status.key?appid=dal_test&status=failover";
        String setFailoverUrl = "http://10.5.110.38/doswitch/dal_ip_status/fat/dal_ip_status.key?appid=dal_test&status=failover";
        String result = getResponse(setFailoverUrl);
        if (result.equalsIgnoreCase("failover"))
            log.info("set failover done");
        else {
            log.info("set failover failed");
            fail();
        }
    }

    public void setNormal() throws Exception {
//        String setNormalUrl = "http://10.5.110.38/switch/dal_ip_status/fat/dal_ip_status.key?appid=dal_test&status=normal";
        String setNormalUrl = "http://10.5.110.38/doswitch/dal_ip_status/fat/dal_ip_status.key?appid=dal_test&status=normal";
        String result = getResponse(setNormalUrl);
        if (result.equalsIgnoreCase("normal"))
            log.info("set normal done");
        else {
            log.info("set normal failed");
            fail();
        }
    }

    public String getStatus() throws Exception {
//        String getStatusUrl = "http://10.5.110.38/switch/dal_ip_status/fat/dal_ip_status.key?appid=dal_test";
        String getStatusUrl = "http://10.5.110.38/doswitch/dal_ip_status/fat/dal_ip_status.key?appid=dal_test";
        String result = getResponse(getStatusUrl);
        if (result.equalsIgnoreCase("normal")) {
            log.info("current status is IP");
        } else if (result.equalsIgnoreCase("failover"))
            log.info("current status is domain");
        else {
            log.warn("current status is invalid");
            fail();
        }
        return result;
    }

    public String getResponse(String url) throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        get.addHeader("Authorization","Basic YWRtaW46YWRtaW4xMjM=");
        String result = "";
        try {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8").trim();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*@Test
    public void test() throws Exception{
       getStatus();
    }*/
}
