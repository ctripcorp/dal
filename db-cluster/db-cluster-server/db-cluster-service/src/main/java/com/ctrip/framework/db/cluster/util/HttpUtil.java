package com.ctrip.framework.db.cluster.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.ctrip.framework.db.cluster.util.Constants.DEFAULT_TIMEOUT_MS;

/**
 * Created by shenjie on 2019/3/18.
 */
@Slf4j
public class HttpUtil {

    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final HttpUtil instance = new HttpUtil();

    public static HttpUtil getInstance() {
        return instance;
    }

    public String sendPost(String url, List<NameValuePair> urlParams, String message) throws Exception {
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(1);
        headers.put("Content-Type", "application/json");
        return sendPost(url, headers, urlParams, message, DEFAULT_TIMEOUT_MS);
    }

    public String sendPost(String url, Map<String, String> headers, List<NameValuePair> urlParams, String message, int timeout) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        httpPost.setConfig(requestConfig);

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        if (urlParams != null && !urlParams.isEmpty()) {
            httpPost.setURI(new URI(httpPost.getURI().toString() + "&" + EntityUtils.toString(new UrlEncodedFormEntity(urlParams))));
        }

        StringEntity entityRequest = new StringEntity(message);
        httpPost.setEntity(entityRequest);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        return getContent(response);
    }

    public String sendGet(String url, Map<String, String> headers, List<NameValuePair> urlParams, int timeout) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        httpGet.setConfig(requestConfig);

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }

        if (urlParams != null && !urlParams.isEmpty()) {
            httpGet.setURI(new URI(httpGet.getURI().toString() + "&" + EntityUtils.toString(new UrlEncodedFormEntity(urlParams))));
        }

        CloseableHttpResponse response = httpClient.execute(httpGet);
        return getContent(response);
    }

    private String getContent(CloseableHttpResponse response) throws IOException {
        String content = "";
        try {
            HttpEntity entity = response.getEntity();
            content = EntityUtils.toString(entity, Charsets.UTF_8);
            EntityUtils.consume(entity);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    log.warn("Close response encounter error!", e);
                }
            }
        }
        return content;
    }

}
