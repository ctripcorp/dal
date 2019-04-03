package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.PermissionCheckEnum;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.service.validator.ValidateHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.*;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.common.exception.QServiceException;
import qunar.tc.qconfig.common.util.ChecksumAlgorithm;
import qunar.tc.qconfig.common.util.Constants;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Key;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author c7ch23en
 */
public class TitanServerPlugin extends ServerPluginAdapter implements TitanConstants {

    private static Logger logger = LoggerFactory.getLogger(TitanServerPlugin.class);
    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    //whether allow to process this request
    protected boolean canProcess(HttpServletRequest request) {
        boolean canDo = false;
        String group = request.getParameter(Constants.GROUP_NAME);
        if (TITAN_QCONFIG_KEYS_APPID.equals(group)) {
            canDo = true;
        }
        return canDo;
    }

    @Override
    public void init() {
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
                    Cat.logEvent("TitanPlugin.DecryptHook.PreHandle.Fail.StatusCode", String.valueOf(pluginResult.getCode()), Event.SUCCESS, errMsg);
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
                    Cat.logEvent("TitanPlugin.DecryptHook.PostHandle.Fail.StatusCode", String.valueOf(pluginResult.getCode()), Event.SUCCESS, errMsg);
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

    public PluginResult preHandleDetail(WrappedRequest wrappedRequest) throws Exception {
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
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        //set new 'titanKey' and 'profile' back [2017-11-01]
        ConfigField configField = new ConfigField(group, titankey, profile);
        ConfigDetail cd = new ConfigDetail(configField);
        PluginResult pluginResult = PluginResult.oK();
        pluginResult.setConfigs(cd); //set configDetail to pluginResult
        return pluginResult;
    }


    public PluginResult postHandleDetail(WrappedRequest wrappedRequest) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        PluginResult pluginResult = PluginResult.oK();
        String result = "";
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanKeyDecryptHookPlugin");
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

            //check request schema is https. (No need to check again, already checked in preHandleDetail) [2017-12-14]
            //checkHttps(request, config);

            //noParent check [2017-10-31]
            String profile_raw = (String) request.getAttribute(REQ_ATTR_TITAN_KEY);
            String subEnv_input = CommonHelper.getSubEnvFromProfile(profile_raw);
            String noParentSuffix = config.getParamValue(NO_PARENT_SUFFIX);
            boolean isPro = CommonHelper.checkPro(profile);
            boolean noParent = CommonHelper.checkSubEnvNoParent(subEnv_input, noParentSuffix, isPro);//use 'subEnv_input'
            if (noParent) {
                //compare used subEnv is just user input one
                String subEnv_actual = CommonHelper.getSubEnvFromProfile(profile);
                if (subEnv_input != null && !subEnv_input.equalsIgnoreCase(subEnv_actual)) {
                    //let it go when profile is like 'LPT:xxx'  [2018-02-23]
                    String topEnv = CommonHelper.formatEnvFromProfile(profile);
                    if (!CommonHelper.checkLptEnv(topEnv)) {
                        throw new IllegalArgumentException("dataId=" + dataId + ", noParent=true, subEnv not match! subEnv_input=" + subEnv_input + ", subEnv_actual=" + subEnv_actual);
                    }
                }
            }

            //decrypt in value
            Properties encryptProp = CommonHelper.parseString2Properties(encryptText);
            Properties originalProp = cryptoManager.decrypt(dataSourceCrypto, keyService, encryptProp);
            String originalText = CommonHelper.parseProperties2String(originalProp);
            //firstly set result with originalText
            result = originalText;
            //黑白名单检查
            String clientAppId = getQconfigService().getClientAppid();  //client appId
            // safety improvement [2018-09-27]
//            PermissionCheckEnum permissionCheck = PermissionCheckUtil.readPermissionCheck(originalText, clientAppId);

            String clientIp = NetworkUtil.getClientIp(request);
            // prepare eventName [2018-10-18]. Format:   dataId:subEnv:appId:ip
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
            ValidateHandler validateHandler = new ValidateHandler(pluginProp, keyProp, clientAppId, clientIp, env);
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
                Cat.logEvent("TitanQconfigPlugin.Decrypt.Hook", "NO_READ_PERMISSION", Event.SUCCESS, errMsg);

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
            //metric cost
            stopwatch.stop();
            long cost = stopwatch.elapsed(TimeUnit.MILLISECONDS);
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


    //https check
    private void checkHttps(HttpServletRequest request, PluginConfig config) throws Exception {
        String schema = request.getScheme();
        if (!REQUEST_SCHEMA_HTTPS.equalsIgnoreCase(schema)) {
            //check whether this http client ip in white list
            checkHttpClientValid(request, config);
        }
    }

    //check http client valid. (client ip in white list)
    private void checkHttpClientValid(HttpServletRequest request, PluginConfig config) throws Exception {
        //get http white list
        String httpWhiteList = config.getParamValue(HTTP_WHITE_LIST);
        String realClientIp = getRealClientIp(request, config);
        boolean inHttpWhiteList = PermissionCheckUtil.checkClientIpInHttpWhiteList(httpWhiteList, realClientIp);
        if (!inHttpWhiteList) {
            throw new IllegalAccessException("Invalid request schema, only support https! inHttpWhiteList=" + inHttpWhiteList + ", realClientIp=" + realClientIp);
        }
    }

    //get real client ip from token, it must equal to header value of 'X-Real-IP'
    private String getRealClientIp(HttpServletRequest request, PluginConfig config) throws Exception {
        String hiddenClientIp = null;
        String xRealIp = request.getHeader(X_REAL_IP);
        String ttToken = request.getHeader(TT_TOKEN);
        //if xRealIp is empty, use original clientIp
        if (Strings.isNullOrEmpty(xRealIp)) {
            xRealIp = NetworkUtil.getClientIp(request);
        }
        //FIXME: === Mock code ====
//        if(xRealIp == null) {
//            xRealIp = "1.1.1.1";
//        }
//        if(ttToken == null) {
//            ttToken = "fseYTdpoOWzdkkS5hcTfVWvuzHgETovQSQwOUMMq2ilm0wDOhRdL+OSbnynbrRgem+7UofvSpF9SgQ1eZrB6aXcgwsxAEFF3KZaXwObQ+ykCn+q4eKfYCMzkSCo1wNBRAgW09vV+194nVccMmkTg8iuo6kQK8XKr4EpMK3V6A8Y=";
//        }
        //FIXME: === Mock end ===

        if (!Strings.isNullOrEmpty(ttToken)) {
            String configKey = config.getParamValue(TOKEN_KEY);
            byte[] bb = Base64.decodeBase64(configKey);
            Key key = null;
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new ByteArrayInputStream(bb));
                key = (Key) ois.readObject();
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (Exception e) {
                        //
                    }
                }
            }
            List<String> list = SecurityUtil.decode(ttToken, key);
        /*
            100011420
            fat
            1.1.1.1         //target ip
            11111
            jinqiao
        */
            if (list != null && list.size() >= 3) {
                hiddenClientIp = list.get(2);
            }
        }

        if (Strings.isNullOrEmpty(xRealIp) || Strings.isNullOrEmpty(hiddenClientIp) || !xRealIp.equals(hiddenClientIp)) {
            StringBuilder sb = new StringBuilder(300);
            sb.append("Invalid request, [xRealIp, hiddenClientIp] they are empty or not equal! ");
            sb.append("xRealIp=").append(xRealIp).append(", ");
            sb.append("hiddenClientIp=").append(hiddenClientIp).append(", ");
            sb.append("ttToken=").append(ttToken);
            throw new IllegalAccessException(sb.toString());
        }
        return xRealIp;
    }

    // fetch parent key permission for subEnv not empty
    private String fetchParentKeyPermission4Sub(String group, String dataId, String profile, QconfigService qconfigService) throws IOException, QServiceException {
        String parentPermission = null;
        String subEnv = CommonHelper.getSubEnvFromProfile(profile);
        // 仅获取子环境key(subEnv不为空)对应父环境权限字段
        if (!Strings.isNullOrEmpty(subEnv)) {
            String topProfile = CommonHelper.formatProfileTopFromProfile(profile);
            ConfigField configField = new ConfigField(group, dataId, topProfile);
            Properties contentProp = QconfigServiceUtils.currentConfigWithPriority(qconfigService, "fetchParentKeyPermission4Sub", configField);
            if (contentProp != null) {
                parentPermission = (String) contentProp.get(PERMISSIONS);
            }
        }
        return parentPermission;
    }

    @Override
    public List<PluginRegisterPoint> registerPoints() {
        return Lists.newArrayList(PluginRegisterPoint.SERV_GET_CONFIG_FOR_TITAN, PluginRegisterPoint.SERV_FORCE_LOAD_FOR_TITAN);
    }

}
