package com.ctrip.framework.dal.dbconfig.plugin.handler.mongo;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.MongoConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.mongo.MongoClusterEntity;
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
import java.util.*;


/**
 * Created by shenjie on 2019/4/3.
 */
public class MongoClusterPostHandler extends BaseAdminHandler implements MongoConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoClusterPostHandler.class);

    private static final String URI = "/plugins/mongo/config/add";
    private static final String METHOD = "POST";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public MongoClusterPostHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
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
        String operator = request.getParameter(REQ_PARAM_OPERATOR);

        request.setAttribute(REQ_ATTR_OPERATOR, operator);

        EnvProfile profile = new EnvProfile(env, subEnv);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        Transaction t = Cat.newTransaction("MongoQconfigPlugin", "MongoClusterPostPlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            String body = CommonHelper.getBody(request, false);

            if (!Strings.isNullOrEmpty(body)) {
                MongoClusterEntity mongoClusterEntity = GsonUtils.json2T(body, MongoClusterEntity.class);
                String operator = (String) request.getAttribute(REQ_ATTR_OPERATOR);
                Preconditions.checkNotNull(operator, "operator参数不能为空");

                LOGGER.info("postHandleDetail(): mongoClusterEntity=" + mongoClusterEntity);
                if (mongoClusterEntity != null) {
                    mongoClusterEntity.setOperator(operator);
                    mongoClusterEntity.setUpdateTime(new Date());

                    // todo: set 'dbName' to lowercase?
                    EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
                    Preconditions.checkArgument(profile != null && !Strings.isNullOrEmpty(profile.formatProfile()),
                            "profile参数不能为空");

                    String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                    boolean permitted = checkPermission(clientIp, profile);

                    if (permitted) {
                        add(request, mongoClusterEntity);
                    } else {
                        t.addData("postHandleDetail(): sitePermission=false, not allow to add!");
                        Cat.logEvent("MongoClusterPostPlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to add! clientIp=" + clientIp);
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
                    }

                } else {
                    t.addData("postHandleDetail(): mongoClusterEntity=null, no need to add!");
                }
            } else {
                t.addData("postHandleDetail(): mongo cluster body is null or empty, no need to add!");
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

    private void add(HttpServletRequest request, MongoClusterEntity mongoClusterEntity) throws Exception {

        String group = MONGO_CLIENT_APP_ID;
        String dataId = mongoClusterEntity.getClusterName();
        EnvProfile envProfile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dataId), "dataId参数不能为空");
        Preconditions.checkArgument(envProfile != null && !Strings.isNullOrEmpty(envProfile.formatProfile()),
                "profile参数不能为空");

        // format file name
        dataId = MongoUtils.formatClusterName(dataId);

        //build config from <mongoClusterEntity>
        Properties rawProp = format2Properties(mongoClusterEntity);

        PluginConfig config = getPluginConfigManager().getPluginConfig(envProfile);
        CryptoManager cryptoManager = new CryptoManager(config);

        //get current config from qconfig
        String profile = envProfile.formatProfile();
        ConfigField configField = new ConfigField(group, dataId, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(configField);
        List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "MongoClusterPostHandler", configFieldList);
        if (configDetailList != null && !configDetailList.isEmpty()) {
            //已经存在，报错
            throw new DbConfigPluginException(String.format("Mongo cluster[%s] 已经存在.", mongoClusterEntity.getClusterName()));
        } else {
            //新增
            add(rawProp, configField, cryptoManager);
        }
    }

    //format mongoClusterEntity to mongo cluster file content properties
    private Properties format2Properties(MongoClusterEntity mongoClusterEntity) throws Exception {
        Properties properties = new Properties();
        HashMap<String, Object> map = CommonHelper.getFieldMap(mongoClusterEntity);
        if (map != null && !map.isEmpty()) {
            for (Map.Entry entry : map.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }
        return properties;
    }

    private void add(Properties rawProp, ConfigField configField, CryptoManager cryptoManager) throws Exception {
        Properties encProp = cryptoManager.encrypt(dataSourceCrypto, keyService, rawProp);
        String encryptText = GsonUtils.t2Json(encProp);

        ConfigDetail configDetail = new ConfigDetail();
        configDetail.setConfigField(configField);
        configDetail.setVersion(-1);    //新增
        configDetail.setContent(encryptText);

        QconfigServiceUtils.batchSave(getQconfigService(), "MongoClusterPostHandler", Lists.newArrayList(configDetail), true);
    }

}
