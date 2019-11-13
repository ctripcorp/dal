package com.ctrip.framework.dal.dbconfig.plugin.handler.dal;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.DalConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DalClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DatabaseInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DatabaseShardInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure.DalConfigure;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.DalClusterUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.RC4;
import com.ctrip.framework.dal.dbconfig.plugin.util.XmlUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

/**
 * Created by shenjie on 2019/8/9.
 */
public class DalClusterGetHandler extends BaseAdminHandler implements DalConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(DalClusterGetHandler.class);

    private static final String URI = "/plugins/dal/config/info";
    private static final String METHOD = "GET";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public DalClusterGetHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
        super(qconfigService, pluginConfigManager);
    }

    @Override
    public String getUri() {
        return URI;
    }

    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    public PluginResult preHandle(HttpServletRequest request) {
        String env = request.getParameter(REQ_PARAM_ENV);
        String subEnv = request.getParameter(REQ_PARAM_SUB_ENV);
        String clusterName = request.getParameter(REQ_PARAM_CLUSTER_NAME);

        EnvProfile profile = new EnvProfile(env, subEnv);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        request.setAttribute(REQ_ATTR_CLUSTER_NAME, clusterName);

        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        Transaction t = Cat.newTransaction("Dal.Admin.Plugin", "DalClusterGetHandler");
        try {
            t.addData("running class=" + getClass().getSimpleName());
            EnvProfile envProfile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            String clusterName = (String) request.getAttribute(REQ_ATTR_CLUSTER_NAME);
            Preconditions.checkArgument(envProfile != null && envProfile.formatProfile() != null,
                    "profile参数不能为空");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(clusterName), "clustername参数不能为空");

            //AdminSite白名单检查
            String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
            boolean permitted = checkPermission(clientIp, envProfile);
            if (permitted) {
                DalClusterEntity dalCluster = getCluster(clusterName, envProfile);
                pluginResult.setAttribute(dalCluster);
            } else {
                t.addData("postHandleDetail(): sitePermission=false, not allow to get!");
                Cat.logEvent("DalClusterGetPlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to get! clientIp=" + clientIp);
                pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, "Access ip whitelist check fail! clientIp=" + clientIp);
            }

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DbConfigPluginException(e);
        } finally {
            t.complete();
        }
        return pluginResult;
    }

    private DalClusterEntity getCluster(String clusterName, EnvProfile envProfile) throws Exception {
        // first set input profile, then set real profile
        String profile = envProfile.formatProfile();
        String group = CLUSTER_CONFIG_STORE_APP_ID;
        String dataId = clusterName.toLowerCase();
        ConfigField queryConfigField = new ConfigField(group, dataId, profile);

        // get cluster config from qconfig
        List<ConfigField> configFields = Lists.newArrayList(queryConfigField);
        List<ConfigDetail> configDetails = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "DalClusterGetHandler", configFields); //Exact match ???
        DalClusterEntity dalCluster;
        if (configDetails != null && !configDetails.isEmpty()) {
            dalCluster = buildCluster(configDetails.get(0).getContent(), envProfile);
        } else {
            // not exist, throw exception
            throw new DbConfigPluginException(String.format("Dal cluster[%s] 不存在.", clusterName));
        }
        return dalCluster;
    }

    private DalClusterEntity buildCluster(String content, EnvProfile envProfile) throws Exception {
        if (Strings.isNullOrEmpty(content)) {
            return null;
        }

        DalConfigure configure = (DalConfigure) XmlUtils.fromXml(content, DalConfigure.class);

        DalClusterEntity dalCluster = DalClusterUtils.formatConfigure2Cluster(configure);

        decryptUidAndPassword(dalCluster, envProfile);

        return dalCluster;
    }

    private void decryptUidAndPassword(DalClusterEntity dalCluster, EnvProfile envProfile) throws Exception {
        PluginConfig pluginConfig = getPluginConfigManager().getPluginConfig(envProfile);
        CryptoManager cryptoManager = new CryptoManager(pluginConfig);

        for (DatabaseShardInfo shard : dalCluster.getDatabaseShards()) {
            for (DatabaseInfo database : shard.getDatabases()) {
                String uid = database.getUid();
                String password = database.getPassword();
                Properties rawProperties = DalClusterUtils.buildDecryptProperties(uid, password, dalCluster.getSslCode());

                Properties decryptedProperties = cryptoManager.decrypt(dataSourceCrypto, keyService, rawProperties);
                String decryptedUid = decryptedProperties.getProperty(UID);
                String decryptedPassword = decryptedProperties.getProperty(PASSWORD);
                database.setUid(decryptedUid);
                database.setPassword(RC4.encrypt(decryptedPassword, null));
            }
        }
    }

}
