package com.ctrip.framework.dal.dbconfig.plugin.handler.dal;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.DalConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DalClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
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

public class DalClusterReleaseHandler extends DalClusterBaseHandler implements DalConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(DalClusterAddHandler.class);

    private static final String URI = "/plugins/dal/config/release";
    private static final String METHOD = "POST";
    private static final String CAT_EVENT_TYPE = "DalPlugin.Release.Cluster";

    public DalClusterReleaseHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
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
        Transaction t = Cat.newTransaction("Dal.Admin.Plugin", "DalClusterReleaseHandler");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            String body = CommonHelper.getBody(request, false);
            if (!Strings.isNullOrEmpty(body)) {
                DalClusterEntity[] dalClusterEntities = GsonUtils.json2T(body, DalClusterEntity[].class);
                String operator = (String) request.getAttribute(REQ_ATTR_OPERATOR);
                Preconditions.checkNotNull(operator, "operator参数不能为空");

                LOGGER.info("postHandleDetail(): dalClusterEntities=" + dalClusterEntities);
                if (dalClusterEntities != null && dalClusterEntities.length > 0) {
                    EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
                    Preconditions.checkArgument(profile != null && !Strings.isNullOrEmpty(profile.formatProfile()),
                            "profile参数不能为空");

                    String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                    boolean permitted = checkPermission(clientIp, profile);

                    if (permitted) {
                        int saveCount = release(request, dalClusterEntities, operator);
                        t.addData("postHandleDetail(): saveCount=" + saveCount);
                    } else {
                        t.addData("postHandleDetail(): sitePermission=false, not allow to add!");
                        Cat.logEvent("DalClusterReleaseHandler", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to add! clientIp=" + clientIp);
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
                    }

                } else {
                    t.addData("postHandleDetail(): dalClusterEntities=null, no need to add!");
                    pluginResult = new PluginResult(PluginStatusCode.ILLEGAL_PARAMS, "no valid data found");
                }
            } else {
                t.addData("postHandleDetail(): site body is null or empty, no need to add!");
                pluginResult = new PluginResult(PluginStatusCode.ILLEGAL_PARAMS, "empty request body");
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

    private int release(HttpServletRequest request, DalClusterEntity[] addEntity, String operator) throws Exception {
        String group = CLUSTER_CONFIG_STORE_APP_ID;
        EnvProfile envProfile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(group), "group参数不能为空");
        Preconditions.checkArgument(envProfile != null && !Strings.isNullOrEmpty(envProfile.formatProfile()),
                "profile参数不能为空");

        List<ConfigDetail> configDetails = Lists.newArrayList();
        for (DalClusterEntity dalCluster : addEntity) {
            dalCluster.setOperator(operator);
            String dataId = DalClusterUtils.formatClusterName(dalCluster.getClusterName());    //Notice: extract match + lowercase
            Preconditions.checkArgument(!Strings.isNullOrEmpty(dataId), "dataId参数不能为空");
            Cat.logEvent("DalClusterReleasePlugin.ClusterName", dataId);

            String profile = envProfile.formatProfile();
            ConfigField configField = new ConfigField(group, dataId, profile);
            List<ConfigField> configFieldList = Lists.newArrayList(configField);
            String configContent = buildConfigContent(dalCluster, envProfile);

            //get current config from qconfig
            List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "DalClusterReleaseHandler", configFieldList);
            if (configDetailList != null && !configDetailList.isEmpty()) {
                //存在，更新
                ConfigDetail configDetail = configDetailList.get(0);  //get first
                configDetail.setContent(configContent);
                configDetails.add(configDetail);
            } else {
                // 不存在，新增
                ConfigDetail configDetail = new ConfigDetail();
                configDetail.setConfigField(configField);
                configDetail.setContent(configContent);
                configDetail.setVersion(-1);
                configDetails.add(configDetail);
            }
        }

        //save to qconfig
        int saveCount = 0;
        if (!configDetails.isEmpty()) {
            int cdListSize = configDetails.size();
            Cat.logEvent(CAT_EVENT_TYPE, "BatchSave.Before", Event.SUCCESS, "cdListSize=" + cdListSize);
            saveCount = QconfigServiceUtils.batchSave(getQconfigService(), "DalClusterReleaseHandler", configDetails, true);
            Cat.logEvent(CAT_EVENT_TYPE, "BatchSave.After", Event.SUCCESS, "cdListSize=" + cdListSize);
        }
        return saveCount;
    }

}
