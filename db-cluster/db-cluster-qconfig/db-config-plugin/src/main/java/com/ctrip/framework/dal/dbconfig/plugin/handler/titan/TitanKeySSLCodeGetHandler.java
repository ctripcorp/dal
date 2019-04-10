package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * This handler is to get latest sslCode
 * Sample:
 *  [GET]   http://qconfig.uat.qa.nt.ctripcorp.com/plugins/titan/sslcode?appid=100010061&env=uat
 *
 * Created by lzyan on 2017/09/12, 2018/06/01.
 */
public class TitanKeySSLCodeGetHandler extends BaseAdminHandler implements TitanConstants {

    private static final String URI = "/plugins/titan/sslcode";
    private static final String METHOD = "GET";

    public TitanKeySSLCodeGetHandler(QconfigService qconfigService) {
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
        String result = "";
//        Stopwatch stopwatch = Stopwatch.createStarted();
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanKeySSLCodeGetPlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            Preconditions.checkArgument(profile != null && !Strings.isNullOrEmpty(profile.formatProfile()),
                    "profile参数不能为空");
            PluginConfig config = new PluginConfig(getQconfigService(), profile);

            //AdminSite白名单检查
            String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
            boolean permitted = checkPermission(clientIp, profile);
            if(permitted){
                //获取最新sslCode
                result = config.getParamValue(SSLCODE);
            }else{
                t.addData("postHandleDetail(): sitePermission=false, not allow to get sslCode!");
                Cat.logEvent("TitanKeySSLCodeGetPlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to get sslCode! clientIp=" + clientIp);
                pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, "Access ip whitelist check fail! clientIp=" + clientIp);
            }


            //set into return result
            pluginResult.setAttribute(result);


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
//        String method = "GET";
//        return PluginKeyUtil.adminPluginKey(uri, method);
//    }

}
