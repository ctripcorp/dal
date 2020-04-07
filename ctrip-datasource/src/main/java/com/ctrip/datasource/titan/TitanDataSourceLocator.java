package com.ctrip.datasource.titan;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.sql.DataSource;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.JsonUtils;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.ctrip.datasource.common.enums.SourceType;
import com.ctrip.framework.foundation.Foundation;
import com.dianping.cat.Cat;

public class TitanDataSourceLocator {
    private static final ILogger logger = DalElementFactory.DEFAULT.getILogger();
    public static final String DB_NAME = "DBKeyName";
    private static final int DEFAULT_TIMEOUT = 15 * 1000;

    private static final Pattern dburlPattern = Pattern
            .compile("(data\\ssource|server|address|addr|network)=([^;]+)",Pattern.CASE_INSENSITIVE);
    private static final Pattern dbuserPattern = Pattern
            .compile("(uid|user\\sid)=([^;]+)",Pattern.CASE_INSENSITIVE);
    private static final Pattern dbpasswdPattern = Pattern
            .compile("(password|pwd)=([^;]+)",Pattern.CASE_INSENSITIVE);
    private static final Pattern dbnamePattern = Pattern
            .compile("(database|initial\\scatalog)=([^;]+)",Pattern.CASE_INSENSITIVE);
    private static final Pattern dbcharsetPattern = Pattern
            .compile("(charset)=([^;]+)",Pattern.CASE_INSENSITIVE);
    private static final Pattern dbportPattern = Pattern
            .compile("(port)=([^;]+)",Pattern.CASE_INSENSITIVE);
    private static final String PORT_SPLIT = ",";
    private static final String DBURL_SQLSERVER = "jdbc:sqlserver://%s:%s;DatabaseName=%s";
    private static final String DBURL_MYSQL = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=%s";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String DEFAULT_PORT = "3306";
    private static final String DRIVER_MYSQL ="com.mysql.jdbc.Driver";
    private static final String DRIVER_SQLSERVRE ="com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public DataSource getDataSource(String titanSvcUrl, String name) throws Exception {
        try {
            titanSvcUrl = validate("Titan service URL", titanSvcUrl);
            name = validate("All in one name", name);
            String appid = validate("App ID", Foundation.app().getAppId());

            DalPropertiesManager.getInstance().setup();

            Set<String> dbNames = new HashSet<>();
            dbNames.add(name);
            DataSourceConfigureManager.getInstance().setup(dbNames, SourceType.Remote);

            TitanData data = getConnectionStrings(titanSvcUrl, name, appid);
            String cs = decrypt(data.getConnectionString());
            DalConnectionString connectionString = new ConnectionString(name, cs, cs);
            DataSourceConfigure configure =
                    DataSourceConfigureManager.getInstance().mergeDataSourceConfig(connectionString);

            return new SingleDataSource(name, configure).getDataSource();
        } catch (Throwable e) {
            String msg = "Creating DataSource " + name + " error:" + e.getMessage();
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
    
    private String validate(String name, String originalValue) {
        if(originalValue == null || originalValue.trim().length() == 0)
            throw new IllegalArgumentException(name + " is empty!");
        
        originalValue = originalValue.trim();
        
        info(name + " is " + originalValue);

        return originalValue;
    }

    private TitanData getConnectionStrings(String svcUrl, String name, String appid) throws Exception {
        long start = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();

        URIBuilder builder = new URIBuilder(svcUrl);
        StringBuilder body = new StringBuilder();
        body.append("ids=").append(name);
        body.append("&appid=").append(appid);
        
        String subEnv = Foundation.server().getSubEnv();
        if (subEnv != null && subEnv.trim().length() > 0) {
            subEnv = subEnv.trim();
            body.append("&envt=").append(subEnv);
            info("Sub environment: " + subEnv);
        }

        String idc = Foundation.server().getDataCenter();
        if (idc != null && idc.trim().length() > 0) {
            idc = idc.trim();
            body.append("&idc=").append(idc);
            info("IDC:" + idc);
        }

        URI uri = builder.build();
        info(uri.toURL().toString());

        HttpClient sslClient = initWeakSSLClient();
        if (sslClient == null)
            throw new IllegalStateException("Can not create SSL");
        
        HttpPost httpPost = new HttpPost();
        httpPost.setURI(uri);
        httpPost.setEntity(new StringEntity(body.toString(), ContentType.APPLICATION_FORM_URLENCODED));

        HttpResponse response = sslClient.execute(httpPost);
        
        int code = response.getStatusLine().getStatusCode();

        HttpEntity entity = response.getEntity();

        String content = EntityUtils.toString(entity);
        
        if(code != 200) {
            throw new DalException(String.format("Fail to get ALL-IN-ONE from Titan service when send request. Code: %s. Message: %s",
                    code, content));
        }

        TitanResponse resp = null;
        try {
            resp = JsonUtils.fromJson(content, TitanResponse.class);
        } catch (Throwable e) {
            throw new DalException("Fail to get ALL-IN-ONE from Titan service when parse result. Message: " + content);
        }

        if (!"200".equals(resp.getStatus())) {
            throw new DalException(String.format("Fail to get ALL-IN-ONE from Titan service. Code: %s. Message: %s",
                    resp.getStatus(), resp.getMessage()));
        }

        TitanData data = resp.getData()[0];
        info("Parsing " + data.getName());
        // Fail fast
        if (data.getErrorCode() != null) {
            throw new DalException(String.format("Error get ALL-In-ONE info for %s. ErrorCode: %s Error message: %s",
                    data.getName(), data.getErrorCode(), data.getErrorMessage()));
        }

        // Decrypt raw connection string
        info(data.getName() + " loaded");
        if (data.getEnv() != null) {
            info(String.format("Sub environment %s detected.", data.getEnv()));
            reportTitanAccessSunEnv(subEnv, data.getName());
        }

        long cost = System.currentTimeMillis() - start;
        info("Time costed by getting all in one connection string from titan service(ms): " + cost);
        reportTitanAccessCost(cost);

        return data;
    }

    private HttpClient initWeakSSLClient() {
        HttpClientBuilder b = HttpClientBuilder.create();

        // setup a Trust Strategy that allows all certificates.
        //
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
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
        configBuilder.setConnectTimeout(DEFAULT_TIMEOUT);
        configBuilder.setSocketTimeout(DEFAULT_TIMEOUT);
        b.setDefaultRequestConfig(configBuilder.build());

        // finally, build the HttpClient;
        // -- done!
        HttpClient sslClient = b.build();

        return sslClient;
    }

    private String decrypt(String dataSource) {
        if (dataSource == null || dataSource.length() == 0) {
            return "";
        }
        byte[] sources = Base64.decodeBase64(dataSource);
        int dataLen = sources.length;
        int keyLen = (int) sources[0];
        int len = dataLen - keyLen - 1;
        byte[] datas = new byte[len];
        int offset = dataLen - 1;
        int i = 0;
        int j = 0;
        byte t;
        for (int o = 0; o < len; o++) {
            i = (i + 1) % keyLen;
            j = (j + sources[offset - i]) % keyLen;
            t = sources[offset - i];
            sources[offset - i] = sources[offset - j];
            sources[offset - j] = t;
            datas[o] =
                    (byte) (sources[o + 1] ^ sources[offset - ((sources[offset - i] + sources[offset - j]) % keyLen)]);
        }
        return new String(datas);
    }

    /**
     * parse "Data Source=devdb.dev.sh.ctriptravel.com,28747;UID=uws_AllInOneKey_dev;password=!QAZ@WSX1qaz2wsx; database=AbacusDB;"
     * 
     * @return DataSourceConfigure
     */
    private DataSourceConfigure parse(String name, String connStr) {
        DataSourceConfigure config = new DataSourceConfigure();

        String url = null;
        String userName = null;
        String password = null;
        String driverClass = null;
        
        String dbname = null, charset = null, dbhost = null;
        Matcher matcher = dbnamePattern.matcher(connStr);
        if (matcher.find()) {
            dbname = matcher.group(2);
        }

        matcher = dburlPattern.matcher(connStr);
        boolean isSqlServer;
        if (matcher.find()) {
            String[] dburls = matcher.group(2).split(PORT_SPLIT);
            dbhost = dburls[0];
            if (dburls.length == 2) {// is sqlserver
                isSqlServer = true;
                url = String.format(DBURL_SQLSERVER, dbhost, dburls[1], dbname);
            } else {// should be mysql
                isSqlServer = false;
                matcher = dbcharsetPattern.matcher(connStr);
                if (matcher.find()) {
                    charset = matcher.group(2);
                } else {
                    charset = DEFAULT_ENCODING;
                }
                matcher = dbportPattern.matcher(connStr);
                if (matcher.find()) {
                    url = String.format(DBURL_MYSQL, dbhost, matcher.group(2), dbname, charset);
                } else {
                    url = String.format(DBURL_MYSQL, dbhost, DEFAULT_PORT, dbname, charset);
                }
            }
            
            driverClass = isSqlServer?DRIVER_SQLSERVRE : DRIVER_MYSQL;
        }else
            throw new RuntimeException("The format of connection string is incorrect for " + name);

        matcher = dbuserPattern.matcher(connStr);
        if (matcher.find()) {
            userName = matcher.group(2);
        }
        
        matcher = dbpasswdPattern.matcher(connStr);
        if (matcher.find()) {
            password = matcher.group(2);
        }
            
        config.setConnectionUrl(url);
        config.setUserName(userName);
        config.setPassword(password);
        config.setDriverClass(driverClass);
        
        return config;
    }

    private void info(String msg) {
        logger.info(msg);
    }

    public static void reportTitanAccessSunEnv(String subEnv, String allInOneKey) {
        try {
            Cat.logEvent("Accessing Titan sub environment[Dal Java]", subEnv, "0", DB_NAME + "=" + allInOneKey);
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
    }

    public static void reportTitanAccessCost(long cost) {
        try {
            Cat.logSizeEvent("Accessing Titan cost[Dal Java]", cost);
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
    }
}
