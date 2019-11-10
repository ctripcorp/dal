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
import qunar.tc.qconfig.common.bean.PaginationResult;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This handler is to list titan key
 * Sample:
 *  [GET]   http://qconfig.uat.qa.nt.ctripcorp.com/plugins/titan/configs?appid=100010061&env=uat&pageNo=1&pageSize=15
 *
 *
 * Created by lzyan on 2017/8/18, 2018/06/01.
 */
public class TitanKeyListHandler extends BaseAdminHandler implements TitanConstants {

    private static final String URI = "/plugins/titan/configs";
    private static final String METHOD = "GET";

    private static final int PAGE_NO_DEFAULT = 1;
    private static final int PAGE_SIZE_DEFAULT = 15;

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();
    private static ExecutorService decryptService = Executors.newFixedThreadPool(5); //[2017-11-13]

    public TitanKeyListHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
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
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanKeyListPlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            Preconditions.checkArgument(profile != null && !Strings.isNullOrEmpty(profile.formatProfile()),
                    "profile参数不能为空");

            int pageNo = PAGE_NO_DEFAULT;
            String pageNo_str = request.getParameter("pageNo");
            if(!Strings.isNullOrEmpty(pageNo_str)){
                pageNo = Integer.parseInt(pageNo_str);
            }

            int pageSize = PAGE_SIZE_DEFAULT;
            String pageSize_str = request.getParameter("pageSize");
            if(!Strings.isNullOrEmpty(pageSize_str)){
                pageSize = Integer.parseInt(pageSize_str);
            }

            PluginConfig config = getPluginConfigManager().getPluginConfig(profile);
            //AdminSite白名单检查
            String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
            boolean permitted = checkPermission(clientIp, profile);
            if(permitted){
                String group = TITAN_QCONFIG_KEYS_APPID;     //appId
                String dataId = "";     //fileName = titanKey
                ConfigField configField = new ConfigField(group, dataId, profile.formatProfile());
                //get paged data from qconfig
                PaginationResult<ConfigDetail> paginationResult = QconfigServiceUtils.query(getQconfigService(),
                        "TitanKeyListHandler", configField, pageNo, pageSize);
                if(paginationResult != null){
                    List<SiteOutputEntity> siteOutputEntityList = Collections.synchronizedList(new ArrayList());

                    if(paginationResult.getData() != null){
                        CryptoManager cryptoManager = new CryptoManager(config);
                        final CountDownLatch latch = new CountDownLatch(paginationResult.getData().size());
                        for(ConfigDetail cd : paginationResult.getData()){
                            //decrypt and collect in separate thread
                            decryptAndCollectSiteOutputEntity(cd, cryptoManager, latch, siteOutputEntityList);
                        }
                        //wait all decrypt thread over
                        latch.await();
                    }//End <paginationResult>

                    //build return 'PaginationResult'
                    PaginationResult<SiteOutputEntity> pr = new PaginationResult<SiteOutputEntity>();
                    pr.setTotal(paginationResult.getTotal());
                    pr.setTotalPage(paginationResult.getTotalPage());
                    pr.setPageSize(paginationResult.getPageSize());
                    pr.setPage(paginationResult.getPage());
                    pr.setData(siteOutputEntityList);
                    //set into return result
                    pluginResult.setAttribute(pr);
                }
            }else{
                t.addData("postHandleDetail(): sitePermission=false, not allow to list!");
                Cat.logEvent("TitanKeyListPlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to list! clientIp=" + clientIp);
                pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, "Access ip whitelist check fail! clientIp=" + clientIp);
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
//        String uri = "/plugins/titan/configs";
//        String method = "GET";
//        return PluginKeyUtil.adminPluginKey(uri, method);
//    }

    //decrypt and collect siteOutputEntity in separate thread
    private void decryptAndCollectSiteOutputEntity(final ConfigDetail cd,
                                                   final CryptoManager cryptoManager,
                                                   final CountDownLatch latch,
                                                   final List<SiteOutputEntity> siteOutputEntityList){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    ConfigField cf = cd.getConfigField();
                    String content = cd.getContent();
                    //decrypt in value
                    String encryptText = content;
                    Properties encryptProp = CommonHelper.parseString2Properties(encryptText);
                    Properties decryptProp = cryptoManager.decrypt(dataSourceCrypto, keyService, encryptProp);
                    //String originalText = HelpUtil.parseProperties2String(decryptProp);

                    //设置信息到SiteOutputEntity
                    boolean encodePwd = true;
                    SiteOutputEntity siteOutputEntity = CommonHelper.buildSiteOutputEntity(decryptProp, encodePwd, cf);
                    siteOutputEntityList.add(siteOutputEntity);
                } catch (Exception e) {
                    Cat.logError("run(): decrypt and collect siteOutputEntity fail!", e);
                } finally {
                    latch.countDown();
                }
            }
        };
        decryptService.execute(runnable);
    }


}
