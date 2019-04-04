package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.KeyGetOutputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.RC4;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Get titan key handler
 * Sample:
 *  [GET]   http://qconfig.uat.qa.nt.ctripcorp.com/plugins/titan/config?appid=100010061&titankey=test1DB_W&env=uat
 *
 * Created by lzyan on 2017/8/18.
 */
public class TitanKeyGetHandler extends BaseAdminHandler implements TitanConstants {

    private static final String URI = "/plugins/titan/config";
    private static final String METHOD = "GET";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public TitanKeyGetHandler(QconfigService qconfigService) {
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
        String titankey = request.getParameter(REQ_PARAM_TITAN_KEY);

        EnvProfile profile = new EnvProfile(env);

        //format <titankey>
        titankey = CommonHelper.formatTitanFileName(titankey);  //getQconfigService(), titankey, profile
        request.setAttribute(REQ_ATTR_TITAN_KEY, titankey);

        //format <profile>
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        KeyGetOutputEntity keyGetOutputEntity = null;
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanKeyGetPlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());
            EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            String titankey = (String) request.getAttribute(REQ_ATTR_TITAN_KEY);
            Preconditions.checkArgument(profile != null && profile.formatProfile() != null,
                    "profile参数不能为空");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(titankey), "titankey参数不能为空");

            //AdminSite白名单检查
            String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
            boolean permitted = checkPermission(clientIp, profile);
            if(permitted){
                //first set input profile, then set real profile from getConfigPropContent()
                String[] inputProfileArray = new String[1];
                inputProfileArray[0] = profile.formatProfile();

                //获取TitanKey文件内容, 同时设置实际返回的profile到inputProfileArray
                Properties properties = getConfigPropContent(titankey, inputProfileArray);
                if(properties != null && !properties.isEmpty()) {
                    keyGetOutputEntity = buildKeyGetOutputEntityFromProperties(properties);
                    String subEnv = CommonHelper.getSubEnvFromProfile(inputProfileArray[0]);
                    keyGetOutputEntity.setSubEnv(subEnv); //set actual profile
                }
            }else{
                t.addData("postHandleDetail(): sitePermission=false, not allow to get!");
                Cat.logEvent("TitanKeyGetPlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to get! clientIp=" + clientIp);
                pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, "Access ip whitelist check fail! clientIp=" + clientIp);
            }



            //set into return result
            pluginResult.setAttribute(keyGetOutputEntity);

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DbConfigPluginException(e);
        } finally {
            t.complete();
            //metric cost
        }
        return pluginResult;
    }

    //get config content properties in titanKey file
    private Properties getConfigPropContent(String titankey, String[] inputProfileArray) throws Exception {
        Properties resultProp = new Properties();
        String group = TITAN_QCONFIG_KEYS_APPID;     //appId
        String dataId = titankey;     //fileName = titanKey
        String profile = inputProfileArray[0];
        ConfigField configField = new ConfigField(group, dataId, profile);
        //get latest from qconfig
        List<ConfigField> configFieldList = Lists.newArrayList(configField);
        List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "TitanKeyGetHandler", configFieldList); //Exact match ???
        if(configDetailList != null && !configDetailList.isEmpty()){
            ConfigDetail cd = configDetailList.get(0);
            ConfigField cm = cd.getConfigField();
            String content = cd.getContent();
            if(cm != null){
                String realProfile = cm.getProfile();
                inputProfileArray[0] = realProfile; //Notice: here set actual profile to return
                String topProfile = CommonHelper.formatProfileTopFromProfile(realProfile);
                PluginConfig config = new PluginConfig(getQconfigService(), new EnvProfile(topProfile));
                CryptoManager cryptoManager = new CryptoManager(config);

                //decrypt in value
                String encryptText = content;
                Properties encryptProp = CommonHelper.parseString2Properties(encryptText);
                Properties originalProp = cryptoManager.decrypt(dataSourceCrypto, keyService, encryptProp);

                //just return originalProp
                resultProp = originalProp;
            }
        }
        return resultProp;
    }

    //build KeyGetOutputEntity
    private KeyGetOutputEntity buildKeyGetOutputEntityFromProperties(Properties properties){
        KeyGetOutputEntity keyGetOutputEntity = new KeyGetOutputEntity();
        keyGetOutputEntity.setKeyName(properties.getProperty(CONNECTIONSTRING_KEY_NAME));
        keyGetOutputEntity.setProviderName(properties.getProperty(CONNECTIONSTRING_PROVIDER_NAME));
        keyGetOutputEntity.setServerName(properties.getProperty(CONNECTIONSTRING_SERVER_NAME));
        keyGetOutputEntity.setServerIp(properties.getProperty(CONNECTIONSTRING_SERVER_IP));
        keyGetOutputEntity.setPort(properties.getProperty(CONNECTIONSTRING_PORT));
        keyGetOutputEntity.setUid(properties.getProperty(CONNECTIONSTRING_UID));

        //password : RC4 encode
        String password = properties.getProperty(CONNECTIONSTRING_PASSWORD);
        if(!Strings.isNullOrEmpty(password)){
            password = RC4.encrypt(password, keyGetOutputEntity.getKeyName());
        }
        keyGetOutputEntity.setPassword(password);
        keyGetOutputEntity.setDbName(properties.getProperty(CONNECTIONSTRING_DB_NAME));
        keyGetOutputEntity.setExtParam(properties.getProperty(CONNECTIONSTRING_EXT_PARAM));

        //timeOut
        Integer timeOut = null;
        Object timeOutObj = properties.get(TIMEOUT);
        if(timeOutObj != null){
            timeOut = Integer.valueOf(timeOutObj.toString());
        }
        keyGetOutputEntity.setTimeOut(timeOut);
        keyGetOutputEntity.setSslCode(properties.getProperty(SSLCODE));

        //enabled
        Boolean enabled = null;
        Object enabledObj = properties.get(ENABLED);
        if(enabledObj != null){
            enabled = Boolean.valueOf(enabledObj.toString());
        }
        keyGetOutputEntity.setEnabled(enabled);

        keyGetOutputEntity.setCreateUser(properties.getProperty(CREATE_USER));
        keyGetOutputEntity.setUpdateUser(properties.getProperty(UPDATE_USER));
        keyGetOutputEntity.setWhiteList(properties.getProperty(WHITE_LIST));
        keyGetOutputEntity.setBlackList(properties.getProperty(BLACK_LIST));
        keyGetOutputEntity.setPermissions(properties.getProperty(PERMISSIONS));
        keyGetOutputEntity.setFreeVerifyIpList(properties.getProperty(FREE_VERIFY_IPLIST));
        keyGetOutputEntity.setFreeVerifyAppIdList(properties.getProperty(FREE_VERIFY_APPID_LIST));
        keyGetOutputEntity.setMhaLastUpdateTime(properties.getProperty(MHA_LAST_UPDATE_TIME));
        //id
        Integer id = null;
        Object idObj = properties.get(ID);
        if(idObj != null){
            id = Integer.valueOf(idObj.toString());
        }
        keyGetOutputEntity.setId(id);


        return keyGetOutputEntity;
    }


//    @Override
//    public String key() {
//        String uri = "/plugins/titan/config";
//        String method = "GET";
//        return PluginKeyUtil.adminPluginKey(uri, method);
//    }

}
