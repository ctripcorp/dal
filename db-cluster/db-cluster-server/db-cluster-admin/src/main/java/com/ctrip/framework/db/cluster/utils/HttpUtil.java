package com.ctrip.framework.db.cluster.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.db.cluster.entity.HttpMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.Map;

/**
 * Created by taochen on 2019/11/6.
 */
public class HttpUtil {
    public static <T> T getJSONEntity(Class<T> clazz, String url, Map<String, String> parameters, HttpMethod method)
            throws Exception {
        T result = null;
        if (url == null || url.trim().length() == 0)
            return result;

        URIBuilder builder = new URIBuilder(url);
        String content = null;
        if (method == HttpMethod.HttpGet) {
            if (parameters != null && parameters.size() > 0) {
                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    builder.addParameter(parameter.getKey(), parameter.getValue());
                }
            }

            URI uri = builder.build();
            HttpGet request = new HttpGet();
            request.setURI(uri);
            content = getHttpEntityContent(request);
        } else {
            URI uri = builder.build();
            HttpPost request = new HttpPost();
            request.setURI(uri);
            if (parameters != null && parameters.size() > 0) {
                JSONObject json = new JSONObject();
                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    json.put(parameter.getKey(), parameter.getValue());
                }
                StringEntity stringEntity = new StringEntity(json.toString());
                request.setEntity(stringEntity);
            }

            request.addHeader("content-type", "application/json");
            content = getHttpEntityContent(request);
        }

        try {
            result = JSON.parseObject(content, clazz);
        } catch (Throwable e) {
        }
        return result;
    }

    private static String getHttpEntityContent(HttpUriRequest request) throws Exception {
        String content = null;
        if (request == null)
            return content;
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                content = EntityUtils.toString(entity);
            } catch (Throwable e) {
                throw e;
            }
        } catch (Throwable e1) {
            throw e1;
        }

        return content;
    }
}
