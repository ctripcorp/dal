package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.MongoConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.MongoUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.NetworkUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.common.util.ChecksumAlgorithm;
import qunar.tc.qconfig.common.util.Constants;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

/**
 * Created by shenjie on 2019/4/4.
 */
public class MongoServerPlugin extends ServerPluginAdapter implements MongoConstants {

    private static Logger logger = LoggerFactory.getLogger(MongoServerPlugin.class);
    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    //whether allow to process this request
    private boolean canProcess(HttpServletRequest request) {
        boolean canDo = false;
        String group = request.getParameter(Constants.GROUP_NAME);
        if (MONGO_CLIENT_APP_ID.equals(group)) {
            canDo = true;
        }
        return canDo;
    }

    @Override
    public void init() {
        //ignore
    }

    @Override
    public List<PluginRegisterPoint> registerPoints() {
        return Lists.newArrayList(PluginRegisterPoint.SERV_GET_CONFIG_FOR_MONGO, PluginRegisterPoint.SERV_FORCE_LOAD_FOR_MONGO);
    }

    @Override
    public PluginResult preHandle(WrappedRequest wrappedRequest) {
        PluginResult pluginResult = null;
        try {
            HttpServletRequest request = wrappedRequest.getRequest();
            if (canProcess(request)) {
                pluginResult = preHandleDetail(wrappedRequest);
                // If pluginResult.getCode != 0, log event
                if (pluginResult.getCode() != PluginStatusCode.OK) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("pluginResult.getCode()=").append(pluginResult.getCode()).append(", ");
                    sb.append("pluginResult.getMessage()=").append(pluginResult.getMessage());
                    String errMsg = sb.toString();
                    Cat.logEvent("MongoServerPlugin.PreHandle.Fail.StatusCode", String.valueOf(pluginResult.getCode()), Event.SUCCESS, errMsg);
                    logger.warn("preHandle(): " + errMsg);
                }
            } else {
                //null : means go as qconfig old before
                pluginResult = null;
            }
        } catch (Exception e) {
            Cat.logError(e);
            pluginResult = new PluginResult(PluginStatusCode.TITAN_NOT_DEFINED, "preHandle(): handler error. " + e.getMessage());
        }
        return pluginResult;
    }

    @Override
    public PluginResult postHandle(WrappedRequest wrappedRequest) {
        PluginResult pluginResult = null;
        try {
            HttpServletRequest request = wrappedRequest.getRequest();
            if (canProcess(request)) {
                pluginResult = postHandleDetail(wrappedRequest);
                // If pluginResult.getCode != 0, log event
                if (pluginResult.getCode() != PluginStatusCode.OK) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("pluginResult.getCode()=").append(pluginResult.getCode()).append(", ");
                    sb.append("pluginResult.getMessage()=").append(pluginResult.getMessage());
                    String errMsg = sb.toString();
                    Cat.logEvent("MongoServerPlugin.PostHandle.Fail.StatusCode", String.valueOf(pluginResult.getCode()), Event.SUCCESS, errMsg);
                    logger.warn("postHandle(): " + errMsg);
                }
            } else {
                //null : means go as qconfig old before
                pluginResult = null;
            }
        } catch (Exception e) {
            Cat.logError(e);
            pluginResult = new PluginResult(PluginStatusCode.TITAN_NOT_DEFINED, "postHandle(): handler error. " + e.getMessage());
        }
        return pluginResult;
    }

    private PluginResult preHandleDetail(WrappedRequest wrappedRequest) throws Exception {
        HttpServletRequest request = wrappedRequest.getRequest();
        ConfigDetail configDetail = wrappedRequest.getConfigs().get(0);
        ConfigField cf = configDetail.getConfigField();
        String group = cf.getGroup();
        String dataId = cf.getDataId();
        String profile = cf.getProfile();

        //format profile when its subEnv like 'LPT10'
        profile = CommonHelper.formatProfileForLpt(profile);
        EnvProfile envProfile = new EnvProfile(profile);

        checkHttps(request);

        String clusterName = MongoUtils.formatClusterName(dataId); //getQconfigService(), dataId, profile
        request.setAttribute(REQ_ATTR_CLUSTER_NAME, clusterName);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, envProfile);

        //set new 'clusterName' and 'profile' back [2017-11-01]
        ConfigField configField = new ConfigField(group, clusterName, profile);
        ConfigDetail cd = new ConfigDetail(configField);
        PluginResult pluginResult = PluginResult.oK();
        pluginResult.setConfigs(cd); //set configDetail to pluginResult
        return pluginResult;
    }

    private PluginResult postHandleDetail(WrappedRequest wrappedRequest) throws Exception {
        PluginResult pluginResult = PluginResult.oK();
        Transaction t = Cat.newTransaction("MongoQconfigPlugin", "MongoServerPlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());
            HttpServletRequest request = wrappedRequest.getRequest();

            //get encryptText from <wrappedRequest> direly
            String encryptText = "";
            String dataId = null;
            String profile = null;
            ConfigDetail configDetail = null;
            configDetail = wrappedRequest.getConfigs().get(0);
            encryptText = configDetail.getContent();
            dataId = configDetail.getConfigField().getDataId();
            profile = configDetail.getConfigField().getProfile();

            EnvProfile envProfile = new EnvProfile(profile);

            PluginConfig config = new PluginConfig(getQconfigService(), envProfile);
            CryptoManager cryptoManager = new CryptoManager(config);

            //noParent check [2017-10-31]
            EnvProfile profile_raw = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            String subEnv_input = profile_raw.formatSubEnv();
            String noParentSuffix = config.getParamValue(NO_PARENT_SUFFIX);
            boolean isPro = CommonHelper.checkPro(profile);
            boolean noParent = CommonHelper.checkSubEnvNoParent(subEnv_input, noParentSuffix, isPro);//use 'subEnv_input'
            if (noParent) {
                //compare used subEnv is just user input one
                String subEnv_actual = envProfile.formatSubEnv();
                if (subEnv_input != null && !subEnv_input.equalsIgnoreCase(subEnv_actual)) {
                    //let it go when profile is like 'LPT:xxx'  [2018-02-23]
                    String topEnv = envProfile.formatEnv();
                    if (!CommonHelper.checkLptEnv(topEnv)) {
                        throw new IllegalArgumentException("dataId=" + dataId + ", noParent=true, subEnv not match! subEnv_input=" + subEnv_input + ", subEnv_actual=" + subEnv_actual);
                    }
                }
            }

            //decrypt in value
            Properties encryptProp = CommonHelper.parseString2Properties(encryptText);
            Properties originalProp = cryptoManager.decrypt(dataSourceCrypto, keyService, encryptProp);
            String originalText = CommonHelper.parseProperties2String(originalProp);

            //黑白名单检查
            String clientAppId = getQconfigService().getClientAppid();  //client appId

            String clientIp = NetworkUtil.getClientIp(request);
            // prepare eventName [2018-10-18]. Format:   dataId:subEnv:appId:ip
            String eventName = String.format("%s:%s:%s:%s", dataId, subEnv_input, clientAppId, clientIp);
            Cat.logEvent("MongoPlugin.Cluster.Request", eventName);


            //log clusterName in cat
            String clusterName = originalProp.getProperty(REQ_PARAM_CLUSTER_NAME);
            Cat.logEvent("MongoCluster." + clusterName, clientAppId, Event.SUCCESS, clientIp);

            //set into return result
            configDetail.setChecksum(ChecksumAlgorithm.getChecksum(originalText));
            configDetail.setContent(originalText);
            pluginResult.setConfigs(configDetail);

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
        return pluginResult;
    }

    //https check
    private void checkHttps(HttpServletRequest request) throws Exception {
        String schema = request.getScheme();
        if (!REQUEST_SCHEMA_HTTPS.equalsIgnoreCase(schema)) {
            throw new IllegalAccessException("Invalid request schema, only support https!");
        }
    }


}
