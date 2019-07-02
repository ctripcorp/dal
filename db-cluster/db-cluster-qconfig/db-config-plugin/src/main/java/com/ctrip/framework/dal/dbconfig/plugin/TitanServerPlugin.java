package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.PermissionCheckEnum;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.service.validator.ValidateHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.NetworkUtil;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.common.exception.QServiceException;
import qunar.tc.qconfig.common.util.ChecksumAlgorithm;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author c7ch23en
 */
public class TitanServerPlugin extends ServerPluginAdapter implements TitanConstants {

    private static Logger logger = LoggerFactory.getLogger(TitanServerPlugin.class);
    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    @Override
    public void init() {
        //ignore
    }

    @Override
    public PluginResult preHandle(WrappedRequest wrappedRequest) {
        PluginResult pluginResult = null;
        try {
            HttpServletRequest request = wrappedRequest.getRequest();
            if (canProcess(request, TITAN_QCONFIG_KEYS_APPID)) {
                pluginResult = preHandleDetail(wrappedRequest);
                // If pluginResult.getCode != 0, log event
                if (pluginResult.getCode() != PluginStatusCode.OK) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("pluginResult.getCode()=").append(pluginResult.getCode()).append(", ");
                    sb.append("pluginResult.getMessage()=").append(pluginResult.getMessage());
                    String errMsg = sb.toString();
                    Cat.logEvent("TitanServerPlugin.PreHandle.Fail.StatusCode", String.valueOf(pluginResult.getCode()), Event.SUCCESS, errMsg);
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
            if (canProcess(request, TITAN_QCONFIG_KEYS_APPID)) {
                pluginResult = postHandleDetail(wrappedRequest);
                // If pluginResult.getCode != 0, log event
                if (pluginResult.getCode() != PluginStatusCode.OK) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("pluginResult.getCode()=").append(pluginResult.getCode()).append(", ");
                    sb.append("pluginResult.getMessage()=").append(pluginResult.getMessage());
                    String errMsg = sb.toString();
                    Cat.logEvent("TitanServerPlugin.PostHandle.Fail.StatusCode", String.valueOf(pluginResult.getCode()), Event.SUCCESS, errMsg);
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

        //check request schema is https [2017-12-14]
        PluginConfig config = new PluginConfig(getQconfigService(), envProfile);
        checkHttps(request, config);

        //format <titankey>
        String titankey = CommonHelper.formatTitanFileName(dataId); //getQconfigService(), dataId, profile
        request.setAttribute(REQ_ATTR_TITAN_KEY, titankey);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, envProfile);

        //set new 'titanKey' and 'profile' back [2017-11-01]
        ConfigField configField = new ConfigField(group, titankey, profile);
        ConfigDetail cd = new ConfigDetail(configField);
        PluginResult pluginResult = PluginResult.oK();
        pluginResult.setConfigs(cd); //set configDetail to pluginResult
        return pluginResult;
    }


    private PluginResult postHandleDetail(WrappedRequest wrappedRequest) throws Exception {
        PluginResult pluginResult = PluginResult.oK();
        String result = "";
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanServerPlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());
            HttpServletRequest request = wrappedRequest.getRequest();

            //get encryptText from <wrappedRequest> direly
            String encryptText = "";
            String group = null;
            String dataId = null;
            String profile = null;
            ConfigDetail configDetail = null;
            configDetail = wrappedRequest.getConfigs().get(0);
            encryptText = configDetail.getContent();
            group = configDetail.getConfigField().getGroup();
            dataId = configDetail.getConfigField().getDataId();
            profile = configDetail.getConfigField().getProfile();

            EnvProfile envProfile = new EnvProfile(profile);

            PluginConfig config = new PluginConfig(getQconfigService(), envProfile);
            CryptoManager cryptoManager = new CryptoManager(config);

            EnvProfile rawProfile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            // noParent check [2017-10-31]
            checkNoParent(dataId, rawProfile, envProfile, config);

            //decrypt in value
            Properties encryptProp = CommonHelper.parseString2Properties(encryptText);
            Properties originalProp = cryptoManager.decrypt(dataSourceCrypto, keyService, encryptProp);

            //黑白名单检查
            String clientAppId = getQconfigService().getClientAppid();

            String clientIp = NetworkUtil.getClientIp(request);
            // prepare eventName [2018-10-18]. Format:   dataId:subEnv:appId:ip
            String subEnv_input = rawProfile.formatSubEnv();
            String eventName = String.format("%s:%s:%s:%s", dataId, subEnv_input, clientAppId, clientIp);
            Cat.logEvent("TitanPlugin.Key.Request", eventName);
            Properties pluginProp = config.getCurrentContentProp();
            Properties keyProp = encryptProp;
            String env = CommonHelper.formatEnvFromProfile(profile);
            String parentPermission = fetchParentKeyPermission4Sub(group, dataId, profile, getQconfigService());
            if (!Strings.isNullOrEmpty(parentPermission)) {
                // 父环境权限不为空, 使用父环境key的权限覆盖 [2018-12-04]
                keyProp.setProperty(PERMISSIONS, parentPermission);
            }

            // 判断是否是公网请求
            String netType = NetworkUtil.getNetType(request);
            boolean fromPublicNet = NetworkUtil.isFromPublicNet(netType);
            Cat.logEvent("TitanServerPlugin.PostHandle.NetType", String.format("fromPublicNet:%s,netType:%s", fromPublicNet, netType));

            ValidateHandler validateHandler = new ValidateHandler(pluginProp, keyProp, clientAppId, clientIp, env, fromPublicNet);
            PermissionCheckEnum permissionCheck = validateHandler.doValid();
            if (permissionCheck == PermissionCheckEnum.PASS) {
                //拼接连接串: normal + failover
                String connString_normal = CommonHelper.buildConnectionString(originalProp, false);
                String connString_failover = CommonHelper.buildConnectionString(originalProp, true);
                //混淆连接串 [在QConfig外部做了, 这里直接返回明文]
                //String finalContent = RC4.encrypt(connString, dataId);
                //拼接最终连接串(2份)
                result = buildReturnResult(connString_normal, connString_failover);

                //log keyName in cat
                String keyName = originalProp.getProperty(CONNECTIONSTRING_KEY_NAME);
                Cat.logEvent("TitanKey." + keyName, clientAppId, Event.SUCCESS, clientIp);
            } else {
                result = "";
                String errMsg = "postHandleDetail(): permissionCheck=" + permissionCheck + ", not allow to read! dataId=" + dataId + ", clientAppId=" + clientAppId;
                t.addData(errMsg);
                Cat.logEvent("TitanServerPlugin", "NO_READ_PERMISSION", Event.SUCCESS, errMsg);

                switch (permissionCheck) {
                    case FAIL_KEY_DISABLED:
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_DISABLE, String.format("titankey=%s 被禁用, 数据库可能已经下线, 请联系dba! 参考: http://conf.ctripcorp.com/pages/viewpage.action?pageId=164602685", dataId));
                        break;
                    case FAIL_WHITE_LIST:
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, String.format("没有数据库读取权限(appId=%s 不在titankey=%s 的授权列表里面), 请在PaaS申请授权! http://help.paas.ctripcorp.com/ContinuousDelivery/jbgnsjsq.html 参考: http://conf.ctripcorp.com/pages/viewpage.action?pageId=164602685", clientAppId, dataId));
                        break;
                    case FAIL_BLACK_LIST:
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, String.format("没有数据库读取权限(appId=%s 在黑名单里面), 请联系dba! 参考: http://conf.ctripcorp.com/pages/viewpage.action?pageId=164602685", clientAppId));
                        break;
                    case FAIL_APPID_IP_CHECK:
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, String.format("没有数据库读取权限(appId=%s, clientIp=%s CMS信息不匹配)! 参考: http://conf.ctripcorp.com/pages/viewpage.action?pageId=164602685", clientAppId, clientIp));
                        break;
                    case FAIL_APPID_BLANK:
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, String.format("没有数据库读取权限(appId=%s, clientIp=%s, appId为空)! 参考: http://conf.ctripcorp.com/pages/viewpage.action?pageId=164602685", clientAppId, clientIp));
                        break;
                    default:
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, String.format("未知类型(permissionCheck=%s)! 参考: http://conf.ctripcorp.com/pages/viewpage.action?pageId=164602685", permissionCheck));
                        break;
                }
            }


            //set into return result
            configDetail.setChecksum(ChecksumAlgorithm.getChecksum(result));
            configDetail.setContent(result);
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

    //build return result
    private String buildReturnResult(String normalConnString, String failoverConnString) {
        StringBuilder sb = new StringBuilder();
        sb.append("normal=").append(normalConnString);
        sb.append(TITAN_QCONFIG_CONTENT_LINE_SPLITTER);
        sb.append("failover=").append(failoverConnString);
        return sb.toString();
    }

    // fetch parent key permission for subEnv not empty
    private String fetchParentKeyPermission4Sub(String group, String dataId, String profile, QconfigService qconfigService) throws IOException, QServiceException {
        String parentPermission = null;
        String subEnv = CommonHelper.getSubEnvFromProfile(profile);
        // 仅获取子环境key(subEnv不为空)对应父环境权限字段
        if (!Strings.isNullOrEmpty(subEnv)) {
            String topProfile = CommonHelper.formatProfileTopFromProfile(profile);
            ConfigField configField = new ConfigField(group, dataId, topProfile);
            Properties contentProp = QconfigServiceUtils.currentConfigWithoutPriority(qconfigService, "fetchParentKeyPermission4Sub", configField);
            if (contentProp != null) {
                parentPermission = (String) contentProp.get(PERMISSIONS);
            }
        }
        return parentPermission;
    }

    @Override
    public List<PluginRegisterPoint> registerPoints() {
        return Lists.newArrayList(
                PluginRegisterPoint.SERV_GET_CONFIG_FOR_TITAN,
                PluginRegisterPoint.SERV_FORCE_LOAD_FOR_TITAN);
    }

}
