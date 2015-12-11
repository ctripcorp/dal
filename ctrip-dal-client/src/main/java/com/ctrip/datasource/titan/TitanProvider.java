package com.ctrip.datasource.titan;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ctrip.datasource.configure.AllInOneConfigureReader;
import com.ctrip.datasource.configure.ConnectionStringParser;
import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.dianping.cat.Cat;

public class TitanProvider implements DataSourceConfigureProvider {
	// This is to make sure we can get APPID if user really set so
	private static final Logger logger = LoggerFactory.getLogger(TitanProvider.class);
	private static final String EMPTY_ID = "999999";
	
	public static final String SERVICE_ADDRESS = "serviceAddress";
	public static final String APPID = "appid";
	public static final String USE_LOCAL_CONFIG = "useLocalConfig";
	private static final String PROD_SUFFIX = "_SH";
	
	private String svcUrl;
	private String appid;
	private boolean useLocal;
	private ConnectionStringParser parser = new ConnectionStringParser();
	
	/**
	 * Used to access local Database.config file fo dev environment
	 */
	private AllInOneConfigureReader allinonProvider = new AllInOneConfigureReader();
	private Map<String, DataSourceConfigure> dataSourceConfigures;
	
	public void initialize(Map<String, String> settings) throws Exception {
		logger.info("Initialize Titan provider");
		svcUrl = settings.get(SERVICE_ADDRESS);
		appid = settings.get(APPID);
		
		// First try 
		if(appid == null || appid.isEmpty()) {
			appid = getPreConfiguredAppId();
		}
		logger.info("Appid: " +appid);
		
		useLocal = Boolean.parseBoolean(settings.get(USE_LOCAL_CONFIG));
		logger.info("Use local: " +useLocal);
	}
	
	public static String getPreConfiguredAppId() {
		String appid = LogConfig.getAppID();
		if(appid == null || appid.equals(EMPTY_ID))
			appid = Cat.getManager().getDomain();
		return appid;
	}
	
	public void setSvcUrl(String svcUrl) {
		this.svcUrl = svcUrl;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	@Override
	public void setup(Set<String> dbNames) {
		// Assume it is local
		if(svcUrl == null || svcUrl.isEmpty() || useLocal) {
			dataSourceConfigures = allinonProvider.getDataSourceConfigures(dbNames, useLocal);
		} else {
			// If it is not local dev environment or the AllInOne file does not exist
			try {
				Map<String, String> rawConnStrings = getConnectionStrings(dbNames);
				dataSourceConfigures = getDataSourceConfigures(rawConnStrings);
				return;
			} catch (Throwable e) {
				logger.warn("Cannot found config from Titan service for " + dbNames);
			}
			
			logger.info("Try to reloacte with \"_SH\"");
			Map<String, String> rawConnStrings = new HashMap<>();
			try {
				Map<String, String> tmpRawConnStrings = getConnectionStrings(getProdDbNames(dbNames));
				for(String name: dbNames)
					rawConnStrings.put(name, tmpRawConnStrings.get(name + PROD_SUFFIX));
				dataSourceConfigures = getDataSourceConfigures(rawConnStrings);
			} catch (Exception e) {
				throw new RuntimeException("Failed to retrieve config with \"_SH\"", e);
			}
		}
	}
	
	@Override
	public DataSourceConfigure getDataSourceConfigure(String dbName) {
		return dataSourceConfigures.get(dbName);
	}
	
	private Set<String> getProdDbNames(Set<String> dbNames) {
		/*
		 * Ctrip all in one key is not consist in between PROD and non PROD environment.
		 * In PROD, the all in one name will be added with '_SH' suffix. To simplify suer
		 * end configuration, we auto add the '_SH' to name to get config.
		 */
		Set<String> prodDbNames = new HashSet<>();
		for(String name: dbNames)
			prodDbNames.add(name + PROD_SUFFIX);
		return prodDbNames;
	}
	
	private Map<String, DataSourceConfigure> getDataSourceConfigures(Map<String, String> rawConnStrings) throws Exception{
		Map<String, DataSourceConfigure> configures = new HashMap<>();
		for(Map.Entry<String, String> entry: rawConnStrings.entrySet()) {
			configures.put(entry.getKey(), parseConfig(entry.getKey(), decrypt(entry.getValue())));
		}
		
		return configures;
	}
	
	private Map<String, String> getConnectionStrings(Set<String> dbNames) throws Exception{
		logger.info("Start getting all in one connection string from titan service.");
		long start = System.currentTimeMillis();
		
		StringBuilder sb = new StringBuilder();
		for(String name: dbNames)
			sb.append(name).append(",");

		String ids = sb.substring(0, sb.length()-1);
        Map<String, String> result = new HashMap<>();

        URI uri = new URIBuilder(svcUrl).addParameter("ids", ids).addParameter("appid", appid).build();
        HttpClient sslClient = initWeakSSLClient();
        if (sslClient != null) {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(uri);

            HttpResponse response = sslClient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            String content = EntityUtils.toString(entity);

            TitanResponse resp = JSON.parseObject(content, TitanResponse.class);
            
            if(!"200".equals(resp.getStatus())) {
            	throw new RuntimeException(String.format("Fail to get ALL-IN-ONE from Titan service. Code: %s. Message: %s", resp.getStatus(), resp.getMessage()));
            }
            
            for(TitanData data: resp.getData()) {
            	//Fail fast
	            if(data.getErrorCode() != null)
	            	throw new RuntimeException(String.format("Error get ALL-In-ONE info for %s. ErrorCode: %s Error message: %s", data.getName(), data.getErrorCode(), data.getErrorMessage()));

            	//Decrypt raw connection string
            	result.put(data.getName(), data.getConnectionString());
            }
        }
	    
	    long cost = System.currentTimeMillis() - start;
		logger.info("Time costed by getting all in one connection string from titan service(ms): " + cost);
		Cat.logSizeEvent("Accessing Titan cost[Dal Java]", cost);

	    return result;
	}
	
	private DataSourceConfigure parseConfig(String name, String connectionString) {
		DataSourceConfigure config = parser.parse(name, connectionString);
		return config;
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
	    //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
	    X509HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

	    // here's the special part:
	    //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
	    //      -- and create a Registry, to register it.
	    //
	    SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
	    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	            .register("http", PlainConnectionSocketFactory.getSocketFactory())
	            .register("https", sslSocketFactory)
	            .build();

	    // now, we create connection-manager using our Registry.
	    //      -- allows multi-threaded use
	    PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
	    b.setConnectionManager(connMgr);

	    // finally, build the HttpClient;
	    //      -- done!
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
	        datas[o] = (byte) (sources[o + 1] ^ sources[offset - ((sources[offset - i] + sources[offset - j]) % keyLen)]);
	    }
	    return new String(datas);
	}
}
