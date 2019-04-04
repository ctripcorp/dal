package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.KeyInfo;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.handler.PluginHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.TitanUtils;
import com.ctrip.framework.foundation.Env;
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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This handler is to force wash titan key file that encoded with old sslCode
 * Sample:
 *  [GET]   http://qconfig.uat.qa.nt.ctripcorp.com/plugins/titan/config/datawash?appid=100010061&env=uat&operator=lzyan
 *
 * Created by lzyan on 2017/09/07, 2018/06/01.
 */
public class TitanKeyForceDataWashHandler extends BaseAdminHandler implements TitanConstants {

    private static final String URI = "/plugins/titan/config/datawash";
    private static final String METHOD = "GET";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();
    private ExecutorService reEncryptService = Executors.newSingleThreadExecutor();
    private AtomicBoolean m_atomic = new AtomicBoolean(true);

    public TitanKeyForceDataWashHandler(QconfigService qconfigService) {
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
//        Stopwatch stopwatch = Stopwatch.createStarted();
        PluginResult pluginResult = PluginResult.oK();
        String result = "Request submit success";
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanKeyForceDataWashPlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());
            EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            String operator = (String) request.getAttribute(PluginConstant.REMOTE_USER);
            String remoteIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
            Preconditions.checkArgument(profile != null && profile.formatProfile() != null,
                    "profile参数不能为空");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(operator), "operator参数不能为空");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(remoteIp), "remoteIp参数不能为空");

            //AdminSite白名单检查
            String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
            boolean permitted = checkPermission(clientIp, profile);

            if(permitted){
                //清洗TitanKey文件
                dataWash(profile, operator, remoteIp);
                Cat.logEvent("TitanQconfigPlugin.SslCode.DataWash", "TitanFile.reEncrypt", Event.SUCCESS, "request submit over!");
            }else{
                t.addData("postHandleDetail(): sitePermission=false, not allow to do!");
                Cat.logEvent("TitanKeyForceDataWashPlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to do! clientIp=" + clientIp);
                pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
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

    //if sslCode different, re-encrypt all titanKey file with latest sslCode
    private void dataWash(final EnvProfile profile, final String operator, final String remoteIp){
        try {
            if (m_atomic.getAndSet(false)) {

                //生成一个sub Transaction, 这个forkedTransaction用来记录异步操作
                final com.dianping.cat.message.ForkedTransaction subTransaction = Cat.newForkedTransaction("TitanQconfigPlugin_Sub","Task_DataWash");
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        try {
                            //fork()
                            subTransaction.fork();
                            //add some data into Sub Transaction
                            StringBuilder sb = new StringBuilder();
                            sb.append("profile=").append(profile).append(", ");
                            sb.append("operator=").append(operator).append(", ");
                            sb.append("remoteIp=").append(remoteIp);
                            subTransaction.addData(sb.toString());

                            int pageNo = 1;
                            int pageSize = 10;
                            //get current latest sslCode
                            PluginConfig config = new PluginConfig(getQconfigService(), profile);
                            String newSslCode = config.getParamValue(SSLCODE);

                            //build query
                            String group = TITAN_QCONFIG_KEYS_APPID;     //keys appId
                            String dataId = "";
                            ConfigField configField = new ConfigField(group, dataId, profile.formatProfile());
                            PaginationResult<ConfigDetail> paginationResult = QconfigServiceUtils.query(getQconfigService(), "TitanKeyForceDataWashHandler", configField, pageNo, pageSize);
                            if(paginationResult != null){
                                long totalPage = paginationResult.getTotalPage();
                                for(int i=1; i <= totalPage; i++){
                                    pageNo = i;
                                    paginationResult = QconfigServiceUtils.query(getQconfigService(), "TitanKeyForceDataWashHandler", configField, pageNo, pageSize);
                                    updateByPaginationResult(paginationResult, newSslCode, profile, operator, remoteIp);
                                    TimeUnit.SECONDS.sleep(CommonHelper.limitRangeRandom(2, 5));
                                }
                            }
                            Cat.logEvent("TitanQconfigPlugin.SslCode.DataWash", "TitanFile.reEncrypt", Event.SUCCESS, "data wash all over!");
                            subTransaction.setStatus(Message.SUCCESS);
                        } catch (Exception e) {
                            Cat.logError("run(): data wash fail!", e);
                            subTransaction.setStatus(e);
                        } finally {
                            subTransaction.complete();
                        }
                    }
                };

                reEncryptService.execute(runnable);
            }else{
                Cat.logEvent("TitanQconfigPlugin.SslCode.DataWash", "TitanFile.m_atomic", Event.SUCCESS,"run(): m_atomic.getAndSet()=false, ignore this time update!");
            }
        } catch (Exception e) {
            Cat.logError("dataWash(): execute error!", e);
            throw e;
        } finally {
            m_atomic.set(true);
        }
    }

    //update for one page
    private void updateByPaginationResult(PaginationResult paginationResult, String newSslCode, EnvProfile profile, String operator, String remoteIp) throws Exception {
        List<ConfigDetail> cdList = null;
        if(paginationResult != null){
            cdList = paginationResult.getData();
            if(cdList != null && !cdList.isEmpty()){
                StringBuilder sb = new StringBuilder();
                boolean isFirst = true;

                PluginConfig config = new PluginConfig(getQconfigService(), profile);
                CryptoManager cryptoManager = new CryptoManager(config);
                String keyServiceUri = config.getParamValue(KEYSERVICE_SOA_URL);

                for(ConfigDetail cd : cdList){
                    String dataId = cd.getConfigField().getDataId();
                    Cat.logEvent("TitanQconfigPlugin.SslCode.DataWash", "TitanFile.reEncrypt", Event.SUCCESS, "dataId=" + dataId + " data wash begin ...");
                    if(isFirst){
                        isFirst = false;
                    }else{
                        sb.append(",");
                    }
                    sb.append(dataId);

                    //decrypt in value
                    String encryptText = cd.getContent();
                    Properties encryptProp = CommonHelper.parseString2Properties(encryptText);
                    Properties decryptProp = cryptoManager.decrypt(dataSourceCrypto, keyService, encryptProp);
                    KeyInfo keyInfo = keyService.getKeyInfo(newSslCode, keyServiceUri);
                    Properties newEncryptProp = cryptoManager.encrypt(dataSourceCrypto, keyInfo, decryptProp);
                    //reset 'sslCode' with new value. Notice: 'version' don't change it
                    newEncryptProp.put(SSLCODE, newSslCode);
                    String newEncText = CommonHelper.parseProperties2String(newEncryptProp);
                    //fill encryptText
                    cd.setVersion(-2);  //修改
                    cd.setContent(newEncText);
                }
                QconfigServiceUtils.batchSave(getQconfigService(), "TitanKeyForceDataWashHandler", cdList, true, operator, remoteIp);
                String savedDataIds = sb.toString();
                Cat.logEvent("TitanQconfigPlugin.SslCode.DataWash", "TitanFile.reEncrypt", Event.SUCCESS, "savedDataIds=[" + savedDataIds + "] data wash success!");
            }
        }
    }

//    @Override
//    public String key() {
//        String uri = "/plugins/titan/config/datawash";
//        String method = "GET";
//        return PluginKeyUtil.adminPluginKey(uri, method);
//    }


}
