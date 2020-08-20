package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.config.*;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.util.PropertiesUtils;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.ClusterConfigProvider;
import com.ctrip.platform.dal.dao.configure.DatabasePropertiesParser;
import com.ctrip.platform.dal.dao.configure.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * @author c7ch23en
 */
public class CtripLocalClusterConfigProvider implements ClusterConfigProvider {

    private final CtripLocalResourceLoader resourceLoader;
    private final ClusterConfigParser fullParser;

    public CtripLocalClusterConfigProvider() {
        this(null);
    }

    public CtripLocalClusterConfigProvider(String userDefinedPath) {
        this(new CtripLocalResourceLoader(userDefinedPath), new ClusterConfigXMLParser());
    }

    public CtripLocalClusterConfigProvider(CtripLocalResourceLoader resourceLoader,
                                           ClusterConfigParser fullParser) {
        this.resourceLoader = resourceLoader;
        this.fullParser = fullParser;
    }

    @Override
    public ClusterConfig getClusterConfig(String clusterName) {
        Resource<String> resource = resourceLoader.getResource(buildFileName(clusterName));
        if (resource != null && !StringUtils.isTrimmedEmpty(resource.getContent()))
            return fullParser.parse(resource.getContent());
        resource = resourceLoader.getResource(CtripLocalDatabasePropertiesParser.FILE_LOCAL_DATABASES);
        if (resource != null)
            return parse(clusterName, resource.getContent());
        return parse(clusterName, (Properties) null);
    }

    protected String buildFileName(String clusterName) {
        return clusterName + ".xml";
    }

    protected ClusterConfig parse(String clusterName, String content) {
        Properties properties = null;
        try {
            properties = PropertiesUtils.toProperties(content);
        } catch (IOException e) {
            // ignore
        }
        return parse(clusterName, properties);
    }

    protected ClusterConfig parse(String clusterName, Properties properties) {
        return buildClusterConfig(clusterName, PropertiesUtils.filterProperties(properties, clusterName));
    }

    protected ClusterConfig buildClusterConfig(String clusterName, Properties clusterProperties) {
        ClusterConfigImpl clusterConfig = new ClusterConfigImpl(clusterName, DatabaseCategory.MYSQL, 1);
        DatabaseShardConfigImpl databaseShardConfig = new DatabaseShardConfigImpl(clusterConfig, 0);
        DatabaseConfigImpl databaseConfig = new DatabaseConfigImpl(databaseShardConfig);
        DatabasePropertiesParser propertiesParser =
                CtripLocalDatabasePropertiesParser.newInstance(clusterProperties, clusterName);
        databaseConfig.setIp(propertiesParser.getHost());
        databaseConfig.setPort(propertiesParser.getPort());
        databaseConfig.setDbName(propertiesParser.getDbName());
        databaseConfig.setUid(propertiesParser.getUid());
        databaseConfig.setPwd(propertiesParser.getPwd());
        databaseShardConfig.addDatabaseConfig(databaseConfig);
        clusterConfig.addDatabaseShardConfig(databaseShardConfig);
        return clusterConfig;
    }

}
