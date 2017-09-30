package com.ctrip.platform.dal.daogen.utils;

import com.alibaba.fastjson.JSON;
import com.ctrip.platform.dal.daogen.entity.Response;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class WebUtil {
    private static final String SERVICE_RUL = Configuration.get("allinone_service_url");
    private static final String APP_ID = Configuration.get("app_id");
    private static final int TIMEOUT = 15 * 1000;
    public static final String HTTP_CODE = "200";
    public static final String NO_VALIDATION = "-1";

    public static Response getAllInOneResponse(String keyname, String environment) throws Exception {
        Response res = null;
        if (keyname == null || keyname.isEmpty())
            return res;

        if (SERVICE_RUL == null || SERVICE_RUL.isEmpty()) {
            res = new Response();
            res.setStatus(NO_VALIDATION);
            return res;
        }

        if (APP_ID == null || APP_ID.isEmpty())
            return res;

        try {
            URIBuilder builder = new URIBuilder(SERVICE_RUL).addParameter("ids", keyname).addParameter("appid", APP_ID);
            if (environment != null && !environment.isEmpty())
                builder.addParameter("envt", environment);

            URI uri = builder.build();
            HttpClient sslClient = initWeakSSLClient();
            if (sslClient != null) {
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(uri);
                HttpResponse response = sslClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity);
                res = JSON.parseObject(content, Response.class);
            }

            return res;
        } catch (Throwable e) {
            throw e;
        }
    }

    private static HttpClient initWeakSSLClient() {
        HttpClientBuilder b = HttpClientBuilder.create();
        // setup a Trust Strategy that allows all certificates.
        //
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] arg0, String arg1) {
                    return true;
                }
            }).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            // do nothing, has been handled outside
        }
        b.setSslcontext(sslContext);

        // don't check Hostnames, either.
        // -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
        X509HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        // here's the special part:
        // -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
        // -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory)
                .build();

        // now, we create connection-manager using our Registry.
        // -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        b.setConnectionManager(connMgr);

        /**
         * Set timeout option
         */
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(TIMEOUT);
        configBuilder.setSocketTimeout(TIMEOUT);
        b.setDefaultRequestConfig(configBuilder.build());

        // finally, build the HttpClient;
        // -- done!
        HttpClient sslClient = b.build();
        return sslClient;
    }
}
