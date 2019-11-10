package com.ctrip.framework.dal.dbconfig.plugin.handler.dal;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.DalConstants;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DalClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DalClusterUpdateInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DatabaseInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DatabaseShardInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure.DalConfigure;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.*;
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
 * Created by shenjie on 2019/6/28.
 */
public class DalClusterUpdateHandler extends BaseAdminHandler implements DalConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(DalClusterUpdateHandler.class);

    private static final String URI = "/plugins/dal/config/update";
    private static final String METHOD = "POST";

    private static final String CAT_EVENT_TYPE = "DalPlugin.Update.Cluster";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public DalClusterUpdateHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
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
        String operator = request.getParameter(REQ_PARAM_OPERATOR);
        request.setAttribute(REQ_ATTR_OPERATOR, operator);

        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        Transaction t = Cat.newTransaction("Dal.Admin.Plugin", "DalClusterUpdateHandler");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            String body = CommonHelper.getBody(request, true);

            if (!Strings.isNullOrEmpty(body)) {
                // todo: 更新时，是传整个配置过来？账户密码呢？账户密码脱敏！！
                Cat.logEvent(CAT_EVENT_TYPE, "RequestBody", Event.SUCCESS, "body= " + RC4.encrypt(body, null));

                DalClusterUpdateInputEntity dalClusterUpdateInputEntity = GsonUtils.json2T(body, DalClusterUpdateInputEntity.class);
                String operator = (String) request.getAttribute(REQ_ATTR_OPERATOR);
                Preconditions.checkNotNull(operator, "operator参数不能为空");

                LOGGER.info("postHandleDetail(): dalClusterUpdateInputEntity=" + dalClusterUpdateInputEntity);
                if (dalClusterUpdateInputEntity != null) {
                    String env = dalClusterUpdateInputEntity.getEnv();
                    Preconditions.checkNotNull(env, "env不能为空");
                    EnvProfile profile = new EnvProfile(env);

                    String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                    boolean permitted = checkPermission(clientIp, profile);
                    Cat.logEvent(CAT_EVENT_TYPE, "Site.Permission", Event.SUCCESS, "sitePermission=" + permitted);

                    if (permitted) {
                        int saveCount = update(dalClusterUpdateInputEntity, operator);
                        t.addData("postHandleDetail(): saveCount=" + saveCount);
                    } else {
                        t.addData("postHandleDetail(): sitePermission=false, not allow to update!");
                        Cat.logEvent("DalClusterUpdatePlugin.NoSitePermission", clientIp);
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
                    }

                } else {
                    t.addData("postHandleDetail(): dalClusterUpdateInputEntity=null, no need to update!");
                }
            } else {
                t.addData("postHandleDetail(): site body is null or empty, no need to update!");
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

    private int update(DalClusterUpdateInputEntity updateInputEntity, String operator) throws Exception {
        String group = CLUSTER_CONFIG_STORE_APP_ID;
        String env = updateInputEntity.getEnv();
        EnvProfile envProfile = new EnvProfile(env);  //Notice: ":"

        PluginConfig pluginConfig = getPluginConfigManager().getPluginConfig(envProfile);
        CryptoManager cryptoManager = new CryptoManager(pluginConfig);

        List<ConfigDetail> configDetails = Lists.newArrayList();
        List<DalClusterEntity> dalclusters = updateInputEntity.getData();
        if (dalclusters != null && !dalclusters.isEmpty()) {
            for (DalClusterEntity dalCluster : dalclusters) {
                // todo: check cluster
                dalCluster.setOperator(operator);
                String dataId = DalClusterUtils.formatClusterName(dalCluster.getClusterName());    //Notice: extarct match + lowercase
                Cat.logEvent("DalClusterUpdatePlugin.ClusterName", dataId);

                //get current config from qconfig
                String profile = envProfile.formatProfile();
                ConfigField configField = new ConfigField(group, dataId, profile);
                List<ConfigField> configFieldList = Lists.newArrayList(configField);
                List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "DalClusterAddHandler", configFieldList);
                if (configDetailList == null || configDetailList.isEmpty()) {
                    //不存在，报错
                    throw new DbConfigPluginException(String.format("Dal cluster[%s] 不存在.", dalCluster.getClusterName()));
                } else {
                    // 覆盖
                    ConfigDetail configDetail = configDetailList.get(0);  //get first
                    String configContent = buildConfigContent(dalCluster, pluginConfig, cryptoManager);
                    configDetail.setContent(configContent);
                    configDetails.add(configDetail);
                }
            }
        }

        //save to qconfig
        int saveCount = 0;
        if (!configDetails.isEmpty()) {
            int cdListSize = configDetails.size();
            Cat.logEvent(CAT_EVENT_TYPE, "BatchSave.Before", Event.SUCCESS, "cdListSize=" + cdListSize);
            saveCount = QconfigServiceUtils.batchSave(getQconfigService(), "DalClusterUpdateHandler", configDetails, true);
            Cat.logEvent(CAT_EVENT_TYPE, "BatchSave.After", Event.SUCCESS, "cdListSize=" + cdListSize);
        }

        Cat.logEvent(CAT_EVENT_TYPE, "Save.Key.Count", Event.SUCCESS, "saveCount=" + saveCount);
        return saveCount;
    }

    private String buildConfigContent(DalClusterEntity dalCluster, PluginConfig pluginConfig, CryptoManager cryptoManager) throws Exception {
        // encrypt uid and password
        encryptUidAndPassword(dalCluster, pluginConfig, cryptoManager);

        DalConfigure configure = DalClusterUtils.formatCluster2Configure(dalCluster);
        String clientConfig = XmlUtils.toXml(configure);

        return clientConfig;
    }

    private void encryptUidAndPassword(DalClusterEntity dalCluster, PluginConfig pluginConfig, CryptoManager cryptoManager) throws Exception {
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
