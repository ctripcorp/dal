package com.ctrip.framework.dal.dbconfig.plugin.handler.mongo;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.MongoConstants;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.mongo.MongoClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.mongo.Node;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.MongoUtils;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by shenjie on 2019/5/28.
 */
public class MongoClusterUpdateHandler extends BaseAdminHandler implements MongoConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoClusterUpdateHandler.class);

    private static final String URI = "/plugins/mongo/config/update";
    private static final String METHOD = "POST";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public MongoClusterUpdateHandler(QconfigService qconfigService) {
        super(qconfigService);
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
        String operator = request.getParameter(REQ_PARAM_OPERATOR);

        request.setAttribute(REQ_ATTR_OPERATOR, operator);

        EnvProfile profile = new EnvProfile(env, subEnv);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        Transaction t = Cat.newTransaction("MongoQconfigPlugin", "MongoClusterUpdatePlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            Preconditions.checkArgument(profile != null && !Strings.isNullOrEmpty(profile.formatProfile()),
                    "profile参数不能为空");
            String operator = (String) request.getAttribute(REQ_ATTR_OPERATOR);
            Preconditions.checkNotNull(operator, "operator参数不能为空");

            String body = CommonHelper.getBody(request, false);

            if (!Strings.isNullOrEmpty(body)) {
                MongoClusterEntity newCluster = GsonUtils.json2T(body, MongoClusterEntity.class);
                LOGGER.info("postHandleDetail(): newMongoCluster=" + newCluster);

                if (newCluster != null) {
                    newCluster.setOperator(operator);

                    String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                    boolean permitted = checkPermission(clientIp, profile);

                    if (permitted) {
                        update(request, newCluster);
                    } else {
                        t.addData("postHandleDetail(): sitePermission=false, not allow to update!");
                        Cat.logEvent("MongoClusterUpdatePlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to update! clientIp=" + clientIp);
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
                    }

                } else {
                    t.addData("postHandleDetail(): mongoClusterEntity=null, no need to update!");
                }
            } else {
                t.addData("postHandleDetail(): mongo cluster body is null or empty, no need to update!");
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

    private void update(HttpServletRequest request, MongoClusterEntity newCluster) throws Exception {
        String group = MONGO_CLIENT_APP_ID;
        String dataId = newCluster.getClusterName();
        EnvProfile envProfile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dataId), "dataId参数不能为空");
        Preconditions.checkArgument(envProfile != null && !Strings.isNullOrEmpty(envProfile.formatProfile()),
                "profile参数不能为空");

        // format file name
        dataId = MongoUtils.formatClusterName(dataId);

        // get current config from qconfig
        ConfigField configField = new ConfigField(group, dataId, envProfile.formatProfile());
        List<ConfigField> configFields = Lists.newArrayList(configField);
        List<ConfigDetail> configDetails = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "MongoClusterUpdateHandler", configFields);
        if (configDetails == null || configDetails.isEmpty()) {
            // not exist, throw exception
            throw new DbConfigPluginException(String.format("Mongo cluster[%s] 不存在.", newCluster.getClusterName()));
        } else {
            // exist, update
            ConfigDetail configDetail = configDetails.get(0);

            // new cluster cover old
            MongoClusterEntity coveredCluster = buildCoveredCluster(newCluster, configDetail, envProfile);

            // update content to qconfig
            update(coveredCluster, configDetail, configField);
        }
    }

    private MongoClusterEntity buildCoveredCluster(MongoClusterEntity newCluster, ConfigDetail configDetail, EnvProfile envProfile) throws Exception {
        // get old cluster
        String encryptedOldConf = configDetail.getContent();
        MongoClusterEntity oldCluster = GsonUtils.json2T(encryptedOldConf, MongoClusterEntity.class);

        // encrypt new cluster userId and password
        PluginConfig config = new PluginConfig(getQconfigService(), envProfile);
        CryptoManager cryptoManager = new CryptoManager(config);
        encrypt(newCluster, cryptoManager);

        // new cluster cover old
        coverOldCluster(oldCluster, newCluster);

        return oldCluster;
    }

    private void encrypt(MongoClusterEntity newCluster, CryptoManager cryptoManager) throws Exception {
        String userId = newCluster.getUserId();
        String password = newCluster.getPassword();

        Properties needEncryptPro = new Properties();
        if (!Strings.isNullOrEmpty(userId)) {
            needEncryptPro.put(MongoConstants.CONNECTIONSTRING_USER_ID, userId);
        }
        if (!Strings.isNullOrEmpty(password)) {
            needEncryptPro.put(TitanConstants.CONNECTIONSTRING_PASSWORD, password);
        }

        Properties encryptedProp = cryptoManager.encrypt(dataSourceCrypto, keyService, needEncryptPro);
        newCluster.setUserId(encryptedProp.getProperty(MongoConstants.CONNECTIONSTRING_USER_ID));
        newCluster.setPassword(encryptedProp.getProperty(TitanConstants.CONNECTIONSTRING_PASSWORD));
        newCluster.setSslCode(encryptedProp.getProperty(TitanConstants.SSLCODE));
    }

    private void coverOldCluster(MongoClusterEntity oldCluster, MongoClusterEntity newCluster) {
        String clusterType = Strings.isNullOrEmpty(newCluster.getClusterType()) ? oldCluster.getClusterType() : newCluster.getClusterType();
        String dbName = Strings.isNullOrEmpty(newCluster.getDbName()) ? oldCluster.getDbName() : newCluster.getDbName();
        String userId = Strings.isNullOrEmpty(newCluster.getUserId()) ? oldCluster.getUserId() : newCluster.getUserId();
        String password = Strings.isNullOrEmpty(newCluster.getPassword()) ? oldCluster.getPassword() : newCluster.getPassword();
        List<Node> nodes = (newCluster.getNodes() == null || newCluster.getNodes().isEmpty()) ? oldCluster.getNodes() : newCluster.getNodes();
        Map<String, String> extraProperties = (newCluster.getExtraProperties() == null || newCluster.getExtraProperties().isEmpty()) ?
                oldCluster.getExtraProperties() : newCluster.getExtraProperties();
        Boolean enabled = newCluster.getEnabled() == null ? oldCluster.getEnabled() : newCluster.getEnabled();
        String sslCode = Strings.isNullOrEmpty(newCluster.getSslCode()) ? oldCluster.getSslCode() : newCluster.getSslCode();
        String operator = Strings.isNullOrEmpty(newCluster.getOperator()) ? oldCluster.getOperator() : newCluster.getOperator();

        oldCluster.setClusterType(clusterType);
        oldCluster.setDbName(dbName);
        oldCluster.setUserId(userId);
        oldCluster.setPassword(password);
        oldCluster.setNodes(nodes);
        oldCluster.setExtraProperties(extraProperties);
        oldCluster.setEnabled(enabled);
        oldCluster.setSslCode(sslCode);
        oldCluster.setOperator(operator);
        oldCluster.setUpdateTime(new Date());
    }

    private void update(MongoClusterEntity mongoCluster, ConfigDetail configDetail, ConfigField configField) throws Exception {
        // increase version
        int version = mongoCluster.getVersion() + 1;
        mongoCluster.setVersion(version);

        String content = GsonUtils.t2Json(mongoCluster);

        configDetail.setConfigField(configField);
        configDetail.setVersion(-2);    //修改
        configDetail.setContent(content);

        QconfigServiceUtils.batchSave(getQconfigService(), "MongoClusterUpdateHandler", Lists.newArrayList(configDetail), true);
    }
}
