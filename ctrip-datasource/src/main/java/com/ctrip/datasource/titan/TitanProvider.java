package com.ctrip.datasource.titan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.SSLContext;

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
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ctrip.datasource.configure.AllInOneConfigureReader;
import com.ctrip.datasource.configure.ConnectionStringParser;
import com.ctrip.datasource.configure.DataSourceConfigureProcessor;
import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfig;
import com.ctrip.platform.dal.dao.configure.DatabasePoolConfigParser;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.status.ProductVersionManager;

public class TitanProvider implements DataSourceConfigureProvider {
    // This is to make sure we can get APPID if user really set so
    private static final Logger logger = LoggerFactory.getLogger(TitanProvider.class);
    private static final String EMPTY_ID = "999999";
    private static final int DEFAULT_TIMEOUT = 15 * 1000;

    public static final String SERVICE_ADDRESS = "serviceAddress";
    public static final String APPID = "appid";
    public static final String TIMEOUT = "timeout";
    public static final String USE_LOCAL_CONFIG = "useLocalConfig";
    public static final String DATABASE_CONFIG_LOCATION = "databaseConfigLocation";
    public static final String OVERWRITE_DATASOURCE_CONFIG = "overwriteDSConfig";
    private static final String PROD_SUFFIX = "_SH";

    private String svcUrl;
    private String appid;
    private String subEnv;
    private String idc;
    private int timeout;
    private boolean useLocal;
    private String databaseConfigLocation;
    private ConnectionStringParser parser = new ConnectionStringParser();
    // used for simulate prod environemnt
    private boolean isDebug;

    public static final String TITAN = "arch.dal.titan.subenv";
    public static final String ENV = "environment";
    public static final String SUB_ENV = "subEnvironment";
    public static final String DB_NAME = "DBKeyName";

    private static final String DAL_LOCAL_DATASOURCE = "DAL.local.datasource";
    private static final String CTRIP_DATASOURCE_VERSION = "Ctrip.datasource.version";

    public static class LogEntry {
        public static final int INFO = 0;
        public static final int WARN = 1;
        public static final int ERROR = 2;
        public static final int ERROR2 = 3;

        public int type;
        public String msg;
        public Throwable e;
    }

    // For dal ignite
    public static List<LogEntry> startUpLog = new ArrayList<>();
    public static Map<String, String> config = null;

    /**
     * Used to access local Database.config file fo dev environment
     */
    private AllInOneConfigureReader allinonProvider = new AllInOneConfigureReader();
    private Map<String, DataSourceConfigure> dataSourceConfigures;

    public void initialize(Map<String, String> settings) throws Exception {
        ProductVersionManager.getInstance().register(CTRIP_DATASOURCE_VERSION, initVersion());

        startUpLog.clear();
        config = new HashMap<>(settings);

        info("Initialize Titan provider");

        svcUrl = discoverTitanServiceUrl(settings);
        appid = discoverAppId(settings);
        subEnv = Foundation.server().getSubEnv();
        subEnv = subEnv == null ? null : subEnv.trim();
        idc = Foundation.server().getDataCenter();
        idc = (idc == null ? null : idc.trim());

        info("Titan Service Url: " + svcUrl);
        info("Appid: " + appid);
        info("Sub-environment: " + (subEnv == null ? "N/A" : subEnv));

        useLocal = Boolean.parseBoolean(settings.get(USE_LOCAL_CONFIG));
        info("Use local: " + useLocal);

        databaseConfigLocation = settings.get(DATABASE_CONFIG_LOCATION);
        info("DatabaseConfig location:" + (databaseConfigLocation == null ? "N/A" : databaseConfigLocation));

        String timeoutStr = settings.get(TIMEOUT);
        timeout = timeoutStr == null || timeoutStr.isEmpty() ? DEFAULT_TIMEOUT : Integer.parseInt(timeoutStr);
        info("Titan connection timeout: " + timeout);

        isDebug = Boolean.parseBoolean(settings.get("isDebug"));
    }

    private static final Map<String, String> titanMapping = new HashMap<>();

    static {
        // LPT,FAT/FWS,UAT,PRO
        titanMapping.put("FAT", "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("FWS", "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("LPT", "https://ws.titan.lpt.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("UAT", "https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("PRO", "https://ws.titan.ctripcorp.com/titanservice/query");
        // titanMapping.put("PRO", "http://10.32.20.154:8080/titanservice/query");
    }

    /**
     * Get titan service URL in order of 1. environment varaible 2. serviceAddress 3. local
     * 
     * @param settings
     * @return
     */
    private String discoverTitanServiceUrl(Map<String, String> settings) {
        // First we use environment to determine titan service address
        Env env = Foundation.server().getEnv();
        if (titanMapping.containsKey(env.toString()))
            return titanMapping.get(env.toString());

        // Then we check serviceAddress
        String svcUrl = settings.get(SERVICE_ADDRESS);

        // A little clean up
        if (svcUrl != null && svcUrl.trim().length() != 0) {
            svcUrl = svcUrl.trim();
            if (svcUrl.endsWith("/"))
                svcUrl = svcUrl.substring(0, svcUrl.length() - 1);
            return svcUrl;
        }

        // Indicate local database.config should be used
        return null;
    }

    private String discoverAppId(Map<String, String> settings) throws DalException {
        // First try framework foundation
        appid = Foundation.app().getAppId();
        if (!(appid == null || appid.trim().isEmpty()))
            return appid.trim();

        // Try pre-configred settings
        String appid = settings.get(APPID);
        if (!(appid == null || appid.trim().isEmpty()))
            return appid.trim();

        // Try original logic
        appid = LogConfig.getAppID();
        if (appid == null || appid.equals(EMPTY_ID))
            appid = Cat.getManager().getDomain();

        if (!(appid == null || appid.trim().isEmpty()))
            return appid.trim();

        DalException e = new DalException("Can not locate app.id for this application");
        error(e.getMessage(), e);
        throw e;
    }

    @Override
    public void setup(Set<String> dbNames) {
        // Try best to match pool configure for the given names.
        checkMissingPoolConfig(dbNames);

        // If it uses local Database.Config
        if (svcUrl == null || svcUrl.isEmpty() || useLocal) {
            dataSourceConfigures = allinonProvider.getDataSourceConfigures(dbNames, useLocal, databaseConfigLocation);
        } else {
            // If it uses Titan service
            // boolean isProdEnv = svcUrl.equals(titanMapping.get("PRO"));
            // Set<String> queryNames = isProdEnv ? normalizedForProd(dbNames) : dbNames;

            try {
                Map<String, TitanData> rawConnStrings = getConnectionStrings(dbNames);
                /*
                 * Map<String, TitanData> tmpRawConnStrings = getConnectionStrings(dbNames);
                 * 
                 * if (isProdEnv) { for (String name : dbNames) { if (name.endsWith(PROD_SUFFIX))
                 * rawConnStrings.put(name, tmpRawConnStrings.get(name)); else rawConnStrings.put(name,
                 * tmpRawConnStrings.get(name + PROD_SUFFIX)); } } else { rawConnStrings = tmpRawConnStrings; }
                 */
                dataSourceConfigures = getDataSourceConfigures(rawConnStrings);
            } catch (Exception e) {
                error("Fail to setup Titan Provider", e);
                throw new RuntimeException(e);
            }
        }

        for (String name : dbNames)
            processPoolSettings(name);

        info("--- End datasource config  ---");
    }

    private void checkMissingPoolConfig(Set<String> dbNames) {
        DatabasePoolConfigParser parser = DatabasePoolConfigParser.getInstance();
        if (parser.isDatasourceXmlExist())
            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCE, appid);

        for (String name : dbNames) {
            if (parser.contains(name))
                continue;

            String possibleName = name.endsWith(PROD_SUFFIX) ? name.substring(0, name.length() - PROD_SUFFIX.length())
                    : name + PROD_SUFFIX;

            if (parser.contains(possibleName)) {
                parser.copyDatabasePoolConifg(possibleName, name);
            } else {
                // It is strongly recommended to add datasource config in datasource.xml for each of the
                // connectionString in dal.config
                // Add missing one
                DatabasePoolConfig c = new DatabasePoolConfig();
                c.setName(name);
                parser.addDatabasePoolConifg(name, c);
            }
        }
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String dbName) {
        return dataSourceConfigures.get(dbName);
    }

    /*
     * Ctrip all in one key is not consistent between PROD and non PROD environment. In PROD, the all in one name will
     * be added with '_SH' suffix. To simplify suer end configuration, we auto add the '_SH' to name to get config.
     */
    private Set<String> normalizedForProd(Set<String> dbNames) {
        info("It is production environment and titan key will be appended with _SH suffix");
        Set<String> prodDbNames = new HashSet<>();
        for (String name : dbNames) {
            if (name.endsWith(PROD_SUFFIX))
                prodDbNames.add(name);
            else
                prodDbNames.add(name + PROD_SUFFIX);
        }
        return prodDbNames;
    }

    private Map<String, DataSourceConfigure> getDataSourceConfigures(Map<String, TitanData> rawConnData)
            throws Exception {
        Map<String, DataSourceConfigure> configures = new HashMap<>();
        for (Map.Entry<String, TitanData> entry : rawConnData.entrySet()) {
            if (isDebug) {
                configures.put(entry.getKey(), new DataSourceConfigure());
            } else {
                DataSourceConfigure configure =
                        parser.parse(entry.getKey(), decrypt(entry.getValue().getConnectionString()));
                configures.put(entry.getKey(), configure);
                info("Url:" + configure.getConnectionUrl());
            }
        }

        return configures;
    }

    private void processPoolSettings(String name) {
        info("--- Key datasource config for " + name + " ---");
        DatabasePoolConfig config = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg(name);
        // Process DatabasePoolConfig
        config = DataSourceConfigureProcessor.getDatabasePoolConfig(config);
        DatabasePoolConfigParser.getInstance().addDatabasePoolConifg(name, config);

        PoolProperties pc = config.getPoolProperties();
        info("connectionProperties: " + pc.getConnectionProperties());

        info("minIdle: " + pc.getMinIdle());
        info("maxAge: " + pc.getMaxAge());
        info("testWhileIdle: " + pc.isTestWhileIdle());

        info("testOnBorrow: " + pc.isTestOnBorrow());
        info("testOnReturn: " + pc.isTestOnReturn());

        info("maxActive: " + pc.getMaxActive());
        info("initialSize: " + pc.getInitialSize());
        info("removeAbandonedTimeout: " + pc.getRemoveAbandonedTimeout());
    }

    private Map<String, TitanData> getConnectionStrings(Set<String> dbNames) throws Exception {
        info("Start getting all in one connection string from titan service.");
        
        if(dbNames.isEmpty()) {
            warn("There is no titan key provided!!!");
            return new HashMap<>();
        }
            
        info("Database key names are " + dbNames);

        long start = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        for (String name : dbNames)
            sb.append(name.trim()).append(",");

        String ids = sb.substring(0, sb.length() - 1);
        Map<String, TitanData> result = new HashMap<>();

        if (isDebug) {
            for (String name : dbNames)
                result.put(name, new TitanData());
            return result;
        }

        info("Titan service URL: " + svcUrl);

        URIBuilder builder = new URIBuilder(svcUrl);
        StringBuilder body = new StringBuilder();
        body.append("ids=").append(ids);
        body.append("&appid=").append(appid);
        
        if (!(subEnv == null || subEnv.isEmpty())) {
            body.append("&envt=").append(subEnv);
            info("Sub environment: " + subEnv);
        }

        if (!(idc == null || idc.isEmpty())) {
            body.append("&idc=").append(idc);
            info("IDC:" + idc);
        }

        URI uri = builder.build();
        info(uri.toURL().toString());

        HttpClient sslClient = initWeakSSLClient();
        if (sslClient != null) {
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
                resp = JSON.parseObject(content, TitanResponse.class);
            } catch (Throwable e) {
                throw new DalException("Fail to get ALL-IN-ONE from Titan service when parse result. Message: " + content);
            }

            if (!"200".equals(resp.getStatus())) {
                throw new DalException(String.format("Fail to get ALL-IN-ONE from Titan service. Code: %s. Message: %s",
                        resp.getStatus(), resp.getMessage()));
            }

            for (TitanData data : resp.getData()) {
                info("Parsing " + data.getName());
                // Fail fast
                if (data.getErrorCode() != null) {
                    throw new DalException(String.format("Error get ALL-In-ONE info for %s. ErrorCode: %s Error message: %s",
                            data.getName(), data.getErrorCode(), data.getErrorMessage()));
                }

                // Decrypt raw connection string
                result.put(data.getName(), data);
                info(data.getName() + " loaded");
                if (data.getEnv() != null) {
                    info(String.format("Sub environment %s detected.", data.getEnv()));
                    reportTitanAccessSunEnv(subEnv, data.getName());
                    reportTitanAccessSunEnvMetrics(subEnv, data.getName());
                }
            }
        }

        long cost = System.currentTimeMillis() - start;
        info("Time costed by getting all in one connection string from titan service(ms): " + cost);
        reportTitanAccessCost(cost);

        return result;
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
        configBuilder.setConnectTimeout(timeout);
        configBuilder.setSocketTimeout(timeout);
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

    private void info(String msg) {
        logger.info(msg);

        LogEntry ent = new LogEntry();
        ent.type = LogEntry.INFO;
        ent.msg = msg;
        startUpLog.add(ent);
    }

    private void warn(String msg) {
        logger.warn(msg);

        LogEntry ent = new LogEntry();
        ent.type = LogEntry.WARN;
        ent.msg = msg;
        startUpLog.add(ent);
    }

    private void error(String msg, Throwable e) {
        logger.error(msg, e);

        LogEntry ent = new LogEntry();
        ent.type = LogEntry.ERROR2;
        ent.msg = msg;
        ent.e = e;
        startUpLog.add(ent);
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

    public void reportTitanAccessSunEnvMetrics(String subEnv, String allInOneKey) {
        if (subEnv == null)
            return;

        Map<String, String> tag = new HashMap<String, String>();
        tag.put(SUB_ENV, subEnv);
        tag.put(DB_NAME, allInOneKey);
        MetricManager.getMetricer().log(TITAN, 1, tag);
    }

    private String initVersion() {
        String path = "/CtripDatasourceVersion.prop";
        InputStream stream = Version.class.getResourceAsStream(path);
        if (stream == null) {
            return "UNKNOWN";
        }
        Properties props = new Properties();
        try {
            props.load(stream);
            stream.close();
            return (String) props.get("version");
        } catch (IOException e) {
            return "UNKNOWN";
        }
    }
}
