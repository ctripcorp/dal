package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.KeyInfo;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.DataSourceCrypto;
import com.ctrip.framework.dal.dbconfig.plugin.service.DefaultDataSourceCrypto;
import com.ctrip.framework.dal.dbconfig.plugin.service.KeyService;
import com.ctrip.framework.dal.dbconfig.plugin.service.Soa2KeyService;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This handler is to add/update sslCode
 * Sample:
 *  [POST]   http://qconfig.uat.qa.nt.ctripcorp.com/plugins/titan/sslcode?appid=100010061&env=uat&operator=lzyan
 *
 * [body] only sslCode string
 ZI00000000000126

 *
 * Created by lzyan on 2017/8/29.
 */
public class TitanKeySSLCodeUpdateHandler extends BaseAdminHandler implements TitanConstants {

    private static final String URI = "/plugins/titan/sslcode";
    private static final String METHOD = "POST";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public TitanKeySSLCodeUpdateHandler(QconfigService qconfigService) {
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
        EnvProfile profile = new EnvProfile(env);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
//        Stopwatch stopwatch = Stopwatch.createStarted();
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanKeySSLCodeUpdatePlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            String body = CommonHelper.getBody(request, true);
            String newSslCode = body.trim();
            t.addData(" newSslCode=" + newSslCode);

            if(Strings.isNullOrEmpty(newSslCode)){
                t.addData("postHandleDetail(): newSslCode is null or empty, no need to add/update!");
                pluginResult = new PluginResult(PluginStatusCode.TITAN_NOT_DEFINED, "newSslCode is null or empty, can't update!");
            }else{
                //AdminSite白名单检查
                EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
                Preconditions.checkArgument(profile != null && !Strings.isNullOrEmpty(profile.formatProfile()),
                        "profile参数不能为空");
                String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                boolean permitted = checkPermission(clientIp, profile);
                if(permitted){
                    //更新sslCode
                    update(request, body);
                }else{
                    t.addData("postHandleDetail(): sitePermission=false, not allow to update sslCode!");
                    Cat.logEvent("TitanKeySSLCodeUpdatePlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to update sslCode! clientIp=" + clientIp);
                    pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
                }
            }

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DbConfigPluginException(e);
        } finally {
            t.complete();
            //metric cost
//            stopwatch.stop();
//            long cost = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        }
        return pluginResult;
    }

//    @Override
//    public String key() {
//        String uri = "/plugins/titan/sslcode";
//        String method = "POST";
//        return PluginKeyUtil.adminPluginKey(uri, method);
//    }


    //add/update sslCode
    private void update(HttpServletRequest request, String sslCode) throws Exception {
        String group = TITAN_QCONFIG_PLUGIN_APPID;
        String dataId = TITAN_QCONFIG_PLUGIN_CONFIG_FILE;
        EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
        String operator = (String) request.getAttribute(PluginConstant.REMOTE_USER);
        String remoteIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(operator), "operator参数不能为空");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(remoteIp), "remoteIp参数不能为空");

        //store new sslCode in map
        String keyServiceUri = new PluginConfig(getQconfigService(), profile).getParamValue(KEYSERVICE_SOA_URL);
        KeyInfo key = keyService.getKeyInfo(sslCode, keyServiceUri);
        if (key == null || key.getKey() == null || key.getKey().length() <= 0) {
            throw new IllegalArgumentException("update(): sslCode is invalid.");
        }


        String updateConf = SSLCODE + "=" + sslCode;
        List<ConfigDetail> cdList = new ArrayList<ConfigDetail>();
        //get current config from qconfig
        ConfigField cf = new ConfigField(group, dataId, profile.formatProfile());
        List<ConfigField> configFieldList = Lists.newArrayList(cf);
        List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "TitanKeySSLCodeUpdateHandler", configFieldList);
        if(configDetailList != null && !configDetailList.isEmpty()){
            ConfigDetail cd = configDetailList.get(0);
            String oldConf = cd.getContent();

            //merge update
            String newConf = CommonHelper.merge(updateConf, oldConf);

            //fill new versionData
            cd.setContent(newConf);

            cdList.add(cd);
        }else{
            //add new
            //compose <configDetail>
            ConfigDetail configDetail = new ConfigDetail();
            configDetail.setConfigField(cf);
            configDetail.setVersion(-1);    //新增
            configDetail.setContent(updateConf);
            cdList.add(configDetail);
        }
        QconfigServiceUtils.batchSave(getQconfigService(), "TitanKeySSLCodeUpdateHandler", cdList, false);

        //decrypt all titanKey file and re-encrypt with new sslCode
        // No need to do here, each will be updated when updating check by client. + force data wash plugin

    }

}
