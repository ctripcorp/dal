package com.ctrip.datasource.net;

import com.dianping.cat.Cat;
import com.google.common.base.Preconditions;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpExecutor {

    private static final Logger logger = LoggerFactory.getLogger(HttpExecutor.class);
    public static final int DEFAULT_TIMEOUT_MS = 10000;

    private CloseableHttpClient httpClient = null;

    private static class HttpExecutorSingletonHolder {
        private static final HttpExecutor instance = new HttpExecutor();
    }

    private HttpExecutor(){
        init();
    }

    public static HttpExecutor getInstance(){
        return HttpExecutorSingletonHolder.instance;
    }

    private void init() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContextBuilder builder = SSLContexts.custom();
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });
            SSLContext sslContext = builder.build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }

                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (Exception ex) {
            logger.error("init(): init sslsf fail!", ex);
            Cat.logError("init(): init sslsf fail!", ex);
        }

        // init connectionManager
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(30);
        connectionManager.setDefaultMaxPerRoute(20);

        // init httpClient
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    public String executePost(String url, Map<String, String> headers, String message, int timeout) throws IOException {
        Preconditions.checkNotNull(httpClient, "Create HttpClient Exception");
        HttpPost post = new HttpPost(url);
        //set timeout
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
        post.setConfig(requestConfig);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                post.addHeader(entry.getKey(), entry.getValue());
            }
        }
        StringEntity entityRequest = new StringEntity(message);
        post.setEntity(entityRequest);
        CloseableHttpResponse response = httpClient.execute(post);
        return getContent(response);
    }

    public String executeGet(String url, Map<String, String> headers, int timeout) throws IOException {
        Preconditions.checkNotNull(httpClient, "Create HttpClient Exception");
        HttpGet get = new HttpGet(url);
        //set timeout
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
        get.setConfig(requestConfig);
        //set header
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                get.addHeader(entry.getKey(), entry.getValue());
            }
        }
        CloseableHttpResponse response = httpClient.execute(get);
        return getContent(response);
    }

    private String getContent(CloseableHttpResponse response) throws IOException {
        String content = "";
        try {
            HttpEntity entity = response.getEntity();
            Header ceheader = entity.getContentEncoding();
            if (ceheader != null) {
                HeaderElement[] codecs = ceheader.getElements();
                for (int i = 0; i < codecs.length; i++) {
                    if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                        response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                    }
                }
            }
            content = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    logger.warn("getContent(): response close error!", e);
                }
            }
        }
        return content;
    }

}
