package com.ctrip.framework.dal.dbconfig.plugin.handler.dal;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.DalConstants;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DalClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DatabaseInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DatabaseShardInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure.DalConfigure;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.DalClusterUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.XmlUtils;
import qunar.tc.qconfig.plugin.QconfigService;

import java.util.Properties;

public abstract class DalClusterBaseHandler extends BaseAdminHandler implements DalConstants {

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public DalClusterBaseHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
        super(qconfigService, pluginConfigManager);
    }

    protected String buildConfigContent(DalClusterEntity dalCluster, EnvProfile envProfile) throws Exception {
        // encrypt uid and password
        encryptUidAndPassword(dalCluster, envProfile);

        DalConfigure configure = DalClusterUtils.formatCluster2Configure(dalCluster);
        return XmlUtils.toXml(configure);
    }

    protected void encryptUidAndPassword(DalClusterEntity dalCluster, EnvProfile envProfile) throws Exception {
        PluginConfig pluginConfig = getPluginConfigManager().getPluginConfig(envProfile);
        CryptoManager cryptoManager = new CryptoManager(pluginConfig);

        String sslCode = pluginConfig.getParamValue(TitanConstants.SSLCODE);
        dalCluster.setSslCode(sslCode);

        for (DatabaseShardInfo shard : dalCluster.getDatabaseShards()) {
            for (DatabaseInfo database : shard.getDatabases()) {
                Properties rawProperties = DalClusterUtils.buildEncryptProperties(database.getUid(), database.getPassword());
                Properties encryptedProperties = cryptoManager.encrypt(dataSourceCrypto, keyService, rawProperties, sslCode);
                String uid = encryptedProperties.getProperty(UID);
                String password = encryptedProperties.getProperty(PASSWORD);
                database.setUid(uid);
                database.setPassword(password);
            }
        }
    }

}
