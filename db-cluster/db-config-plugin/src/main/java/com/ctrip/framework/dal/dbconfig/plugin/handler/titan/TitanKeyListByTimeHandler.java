package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.SiteOutputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * This handler is to list new modified titan key by input beginTime
 * Sample:
 *  [GET]   http://qconfig.uat.qa.nt.ctripcorp.com/plugins/titan/configs/bytime?appid=100010061&env=uat&beginTime=2017-10-10 09:00:00
 *
 *  @Parameter appid, keys appId. Fixed value(100010061)
 *  @Parameter env, can be [fat, uat, pro]
 *  @Parameter beginTime, begin time. Format: yyyy-MM-dd HH:mm:ss
 *
 * Created by lzyan on 2017/10/12, 2018/06/01.
 */
public class TitanKeyListByTimeHandler extends BaseAdminHandler implements TitanConstants {

    private static final String URI = "/plugins/titan/configs/bytime";
    private static final String METHOD = "GET";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public TitanKeyListByTimeHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
        super(qconfigService,pluginConfigManager);
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
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanKeyListByTimePlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            Preconditions.checkArgument(profile != null && !Strings.isNullOrEmpty(profile.formatProfile()),
                    "profile参数不能为空");

            String dateTimeStr = request.getParameter(BEGIN_TIME);  //format: yyyy-MM-dd HH:mm:ss
            Preconditions.checkArgument(!Strings.isNullOrEmpty(dateTimeStr), "beginTime参数不能为空,格式:yyyy-MM-dd HH:mm:ss");

            String group = TITAN_QCONFIG_KEYS_APPID;     //appId
            //get data list from qconfig
            List<ConfigDetail> cdList = QconfigServiceUtils.getLatestConfigs(getQconfigService(),
                    "TitanKeyListByTimeHandler", group, profile.formatProfile(), dateTimeStr);
            List<SiteOutputEntity> siteOutputEntityList = new ArrayList<>();
            if(cdList != null){
                PluginConfig config = getPluginConfigManager().getPluginConfig(profile);
                CryptoManager cryptoManager = new CryptoManager(config);
                //AdminSite白名单检查
                String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                boolean permitted = checkPermission(clientIp, profile);
                if(permitted){
                    ConfigField cf = null;
                    for(ConfigDetail cd : cdList){
                        cf = cd.getConfigField();
                        String tmpProfile = cf.getProfile();
                        String content = cd.getContent();
                        //decrypt in value
                        String encryptText = content;
                        Properties encryptProp = CommonHelper.parseString2Properties(encryptText);
                        Properties decryptProp = cryptoManager.decrypt(dataSourceCrypto, keyService, encryptProp);
                        //String originalText = HelpUtil.parseProperties2String(decryptProp);

                        //设置信息到SiteOutputEntity
                        boolean encodePwd = false;
                        SiteOutputEntity siteOutputEntity = CommonHelper.buildSiteOutputEntity(decryptProp, encodePwd, tmpProfile);
                        siteOutputEntity.setConnectionString(null); //No need this connString
                        siteOutputEntityList.add(siteOutputEntity);
                    }//End <for>
                }else{
                    t.addData("postHandleDetail(): sitePermission=false, not allow to list!");
                    Cat.logEvent("TitanKeyListByTimePlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to list! clientIp=" + clientIp);
                    pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, "Access ip whitelist check fail! clientIp=" + clientIp);
                }

            }//End <cdList>
            //set into return result
            pluginResult.setAttribute(siteOutputEntityList);


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
//        String uri = "/plugins/titan/configs/bytime";
//        String method = "GET";
//        return PluginKeyUtil.adminPluginKey(uri, method);
//    }

}
