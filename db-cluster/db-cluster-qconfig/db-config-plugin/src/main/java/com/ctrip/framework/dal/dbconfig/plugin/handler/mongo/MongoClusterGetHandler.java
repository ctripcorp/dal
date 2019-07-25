package com.ctrip.framework.dal.dbconfig.plugin.handler.mongo;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.MongoConstants;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.mongo.MongoClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.mongo.MongoClusterGetOutputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
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
 * Created by shenjie on 2019/5/28.
 */
public class MongoClusterGetHandler extends BaseAdminHandler implements MongoConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoClusterGetHandler.class);

    private static final String URI = "/plugins/mongo/config/info";
    private static final String METHOD = "GET";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public MongoClusterGetHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
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
        Transaction t = Cat.newTransaction("MongoQconfigPlugin", "MongoClusterGetPlugin");
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
                // get cluster from qconfig
                MongoClusterGetOutputEntity mongoCluster = getCluster(clusterName, envProfile);
                // set result
                pluginResult.setAttribute(mongoCluster);
            } else {
                t.addData("postHandleDetail(): sitePermission=false, not allow to get!");
                Cat.logEvent("MongoClusterGetPlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to get! clientIp=" + clientIp);
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

    private MongoClusterGetOutputEntity getCluster(String clusterName, EnvProfile envProfile) throws Exception {
        // first set input profile, then set real profile
        String profile = envProfile.formatProfile();
        String group = MONGO_CLIENT_APP_ID;
        String dataId = clusterName.toLowerCase();
        ConfigField queryConfigField = new ConfigField(group, dataId, profile);

        // get cluster config from qconfig
        List<ConfigField> configFields = Lists.newArrayList(queryConfigField);
        List<ConfigDetail> configDetails = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "MongoClusterGetHandler", configFields); //Exact match ???

        // build cluster
        MongoClusterGetOutputEntity outputEntity = buildCluster(dataId, configDetails);

        return outputEntity;
    }

    private MongoClusterGetOutputEntity convert(MongoClusterEntity mongoCluster) {
        MongoClusterGetOutputEntity outputEntity = null;
        if (mongoCluster != null) {
            outputEntity = new MongoClusterGetOutputEntity();
            outputEntity.setClusterName(mongoCluster.getClusterName());
            outputEntity.setClusterType(mongoCluster.getClusterType());
            outputEntity.setDbName(mongoCluster.getDbName());
            outputEntity.setUserId(mongoCluster.getUserId());
            outputEntity.setNodes(mongoCluster.getNodes());
            outputEntity.setExtraProperties(mongoCluster.getExtraProperties());
            outputEntity.setEnabled(mongoCluster.getEnabled());
            outputEntity.setVersion(mongoCluster.getVersion());
            outputEntity.setOperator(mongoCluster.getOperator());
            outputEntity.setUpdateTime(CommonHelper.dfFull.get().format(mongoCluster.getUpdateTime()));
        }
        return outputEntity;
    }

    private MongoClusterGetOutputEntity buildCluster(String clusterName, List<ConfigDetail> configDetails) throws Exception {
        MongoClusterGetOutputEntity outputEntity = null;
        if (configDetails != null && !configDetails.isEmpty()) {
            ConfigDetail configDetail = configDetails.get(0);
            ConfigField configField = configDetail.getConfigField();

            if (configField != null) {
                // get real profile
                String realProfile = configField.getProfile();
                String topProfile = CommonHelper.formatProfileTopFromProfile(realProfile);

                // get encrypted cluster content
                String encryptedContent = configDetail.getContent();
                if (Strings.isNullOrEmpty(encryptedContent)) {
                    return outputEntity;
                }
                MongoClusterEntity mongoCluster = GsonUtils.json2T(encryptedContent, MongoClusterEntity.class);

                // decrypt userId
                decrypt(mongoCluster, topProfile);

                // clean sslCode and password
                outputEntity = convert(mongoCluster);
            }
        } else {
            // not exist, throw exception
            throw new DbConfigPluginException(String.format("Mongo cluster[%s] 不存在.", clusterName));
        }
        return outputEntity;
    }

    private void decrypt(MongoClusterEntity mongoCluster, String profile) throws Exception {
        String userId = mongoCluster.getUserId();
        String sslCode = mongoCluster.getSslCode();
        if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(sslCode)) {
            Properties needDecryptPro = new Properties();
            needDecryptPro.put(MongoConstants.CONNECTIONSTRING_USER_ID, mongoCluster.getUserId());
            needDecryptPro.put(TitanConstants.SSLCODE, mongoCluster.getSslCode());

            PluginConfig config = getPluginConfigManager().getPluginConfig(new EnvProfile(profile));
            CryptoManager cryptoManager = new CryptoManager(config);

            Properties decryptedProp = cryptoManager.decrypt(dataSourceCrypto, keyService, needDecryptPro);
            mongoCluster.setUserId(decryptedProp.getProperty(MongoConstants.CONNECTIONSTRING_USER_ID));
        }
    }

}
