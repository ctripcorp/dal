package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.MhaInputBasicData;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.MhaInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import qunar.Config;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This handler is to update titan key for MHA
 * Sample:
 * [POST]   [生产] http://qconfig.ctripcorp.com/plugins/titan/config/mha?group=100010061
 * [测试] http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan/config/mha?group=100010061
 * [body] sample as bellow:
 * {
 * "env": "uat",
 * "data": [
 * {
 * "keyname": "test1db_w",
 * "server": "127.0.0.1",
 * "port": 55111
 * }
 * ]
 * }
 * <p>
 * [response]
 * [成功返回]
 * {
 * "status": 0,
 * "message": "正常",
 * "data": null
 * }
 * <p>
 * [失败返回]
 * {
 * "status": -1,
 * "message": "服务器内部错误:null",
 * "data": null
 * }
 * <p>
 * Created by lzyan on 2017/8/21, 2018/06/01, 2018/12/27.
 */
public class TitanKeyMHAUpdateHandler extends BaseAdminHandler implements TitanConstants {

    private static final String URI = "/plugins/titan/config/mha";
    private static final String METHOD = "POST";

    private static final String CAT_TRANSACTION_TYPE = "TitanKeyMHAUpdatePlugin.TitanKey.Update";
    private static final String CAT_EVENT_SUCCESS_TYPE = "Titan.MHAUpdate.TitanKey.Success:";
    private static final String CAT_EVENT_FAILED_TYPE = "Titan.MHAUpdate.TitanKey.Failed:";
    private static final String CAT_EVENT_NO_PERMISSION_TYPE = "Titan.MHAUpdate.NoSitePermission:";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public TitanKeyMHAUpdateHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
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
        request.setAttribute(MHA_START_TIME, System.currentTimeMillis());
        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
//        Stopwatch stopwatch = Stopwatch.createStarted();
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanKeyMHAUpdatePlugin");
        MhaInputEntity mhaInputEntity = null;
        try {
            t.addData("running class=" + getClass().getSimpleName());

            String body = CommonHelper.getBody(request, true);
            Cat.logEvent(CAT_TRANSACTION_TYPE, "TitanFile.MHA", Event.SUCCESS, "body= " + body);

            /**
             * Sample body
             {
             "env": "pro",
             "data": [
             {
             "keyname": "testKey_1",
             "server": "127.0.0.1",
             "port": 28747
             },
             {
             "keyname": "testKey_2",
             "server": "127.0.0.2",
             "port": 28747
             }
             ]
             }
             */
            if (!Strings.isNullOrEmpty(body)) {
                mhaInputEntity = GsonUtils.json2T(body, MhaInputEntity.class);
                if (mhaInputEntity != null) {
                    Cat.logEvent(CAT_TRANSACTION_TYPE, "TitanFile.MHA", Event.SUCCESS, "mhaInputEntity= " + mhaInputEntity.toString());

                    String env = mhaInputEntity.getEnv();
                    EnvProfile profile = new EnvProfile(env);

                    //AdminSite白名单检查
                    String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                    boolean permitted = checkPermission(clientIp, profile);
                    Cat.logEvent(CAT_TRANSACTION_TYPE, "Site.Permission", Event.SUCCESS, "sitePermission=" + permitted);
                    if (permitted) {
                        //MHA更新titanKey文件
                        int saveCount = updateMhaInput(request, mhaInputEntity);
                        t.addData("postHandleDetail(): saveCount=" + saveCount);
                        logEventSuccess(CAT_EVENT_SUCCESS_TYPE, mhaInputEntity, profile);
                    } else {
                        t.addData("postHandleDetail(): sitePermission=false, not allow to update!");
                        Cat.logEvent("TitanKeyMHAUpdatePlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to update! clientIp=" + clientIp);
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
                        logEventFail(CAT_EVENT_NO_PERMISSION_TYPE, mhaInputEntity);
                    }
                } else {
                    t.addData("postHandleDetail(): mhaInputEntity=null, no need to update!");
                }
            } else {
                t.addData("postHandleDetail(): mha body is null or empty, no need to update!");
            }

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            logEventFail(CAT_EVENT_FAILED_TYPE, mhaInputEntity);
            throw new DbConfigPluginException(e);
        } finally {
            t.complete();
            //metric cost
//            stopwatch.stop();
//            long cost = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        }
        return pluginResult;
    }

    private void logEventFail(String catEventType, MhaInputEntity mhaInputEntity) {
        try {
            if (mhaInputEntity != null) {
                String env = mhaInputEntity.getEnv();
                if (Strings.isNullOrEmpty(env)) {
                    Cat.logEvent("Titan.MHAUpdate.RequestBody.Error", "env");
                    return;
                }
                EnvProfile envProfile = new EnvProfile(env);
                String topEnv = envProfile.formatTopProfile();
                topEnv = topEnv.substring(0, topEnv.length() - 1);
                List<MhaInputBasicData> mhaInputBasicData = mhaInputEntity.getData();
                if (mhaInputBasicData != null && !mhaInputBasicData.isEmpty()) {
                    for (MhaInputBasicData data : mhaInputBasicData) {
                        String titanKeyName = data.getKeyname();
                        if (!Strings.isNullOrEmpty(titanKeyName)) {
                            Cat.logEvent(catEventType + topEnv, titanKeyName);
                        }
                    }
                    return;
                }
            }
            Cat.logEvent("Titan.MHAUpdate.RequestBody.Error", "mhaInputEntity");
        } catch (Exception e) {
            //ignore
        }

    }

    private void logEventSuccess(String catEventType, MhaInputEntity mhaInputEntity, EnvProfile envProfile) {
        try {
            String topEnv = envProfile.formatTopProfile();
            topEnv = topEnv.substring(0, topEnv.length() - 1);
            List<MhaInputBasicData> mhaInputBasicData = mhaInputEntity.getData();
            if (mhaInputBasicData != null && !mhaInputBasicData.isEmpty()) {
                for (MhaInputBasicData data : mhaInputBasicData) {
                    String titanKeyName = data.getKeyname();
                    if (!Strings.isNullOrEmpty(titanKeyName)) {
                        Cat.logEvent(catEventType + topEnv, titanKeyName);
                    }
                }
            }
        } catch (Exception e) {
            //ignore
        }

    }

    private int updateMhaInput(HttpServletRequest request, MhaInputEntity mhaInputEntity) throws Exception {
        String group = TITAN_QCONFIG_KEYS_APPID;   //appId
        String dataId = null;   //fileName
//        String profile = null;  //env

        String env = mhaInputEntity.getEnv();
        EnvProfile profile = new EnvProfile(env);  //Notice: ":"

        List<ConfigDetail> cdList = new ArrayList<ConfigDetail>();
        List<String> inputDataIdList = new ArrayList<String>();
        List<MhaInputBasicData> mhaBasicList = mhaInputEntity.getData();

        PluginConfig config = getPluginConfigManager().getPluginConfig(profile);
        CryptoManager cryptoManager = new CryptoManager(config);
        DbAccessManager dbAccessManager = new DbAccessManager(config);
        boolean forMHA = true;
        boolean needCheckDbConnection = dbAccessManager.needCheckDbConnection(forMHA);
        Cat.logEvent(CAT_TRANSACTION_TYPE, "NeedCheckDbConnection", Event.SUCCESS, "needCheckDbConnection=" + needCheckDbConnection);
        for (MhaInputBasicData mhaBD : mhaBasicList) {
            dataId = CommonHelper.formatTitanFileName(mhaBD.getKeyname());    //Notice: extarct match + lowercase
            inputDataIdList.add(dataId);
            Properties updateProp = buildUpdatePropFromBasicData(mhaBD);
            if (updateProp != null && !updateProp.isEmpty()) {
                //get current config from qconfig
                ConfigField cf = new ConfigField(group, dataId, profile.formatProfile());
                List<ConfigField> configFieldList = Lists.newArrayList(cf);
                List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "TitanKeyMHAUpdateHandler", configFieldList);
                if (configDetailList != null && !configDetailList.isEmpty()) {
                    ConfigDetail cd = configDetailList.get(0);
                    cd.setOldConfigDetail(cd);
                    String encryptOldConf = cd.getContent();

                    Properties encryptProp = CommonHelper.parseString2Properties(encryptOldConf);

                    // 这里直接merge, 不需要解密后再加密。仅在需要检测连通性时才做解密(目前开关是关闭的) [2018-08-03]
                    //merge update (serverIp, port)
                    Properties mergeProp = CommonHelper.merge(updateProp, encryptProp);

                    //field 'version' increase one
                    CommonHelper.increaseVersionInProperties(mergeProp, getQconfigService().getVersionIncrenment());

                    //field 'mhaLastUpdate' update [2018-12-27]
                    CommonHelper.updateMhaLastUpdateInProperties(mergeProp);

                    //field 'mhaUpdateStartTime' update
                    CommonHelper.updateMhaUpdateStartTimeInProperties(mergeProp, (Long)request.getAttribute(MHA_START_TIME));

                    if (needCheckDbConnection) {
                        //decrypt
                        Properties decryptProp = cryptoManager.decrypt(dataSourceCrypto, keyService, mergeProp);
                        boolean validResult = dbAccessManager.validConnection(decryptProp, env, forMHA);
                        if (!validResult) {
                            throw new Exception("update(): validResult=false, db connection check failure!");
                        }
                    }

                    String encryptText = CommonHelper.parseProperties2String(mergeProp);
                    //fill encryptText
                    cd.setContent(encryptText);

                    cdList.add(cd);
                } else {
                    throw new Exception("updateMhaInput(): configDetailList is null or empty for dataId=[" + dataId + "]!");
                }
            }
        }

        //save to qconfig
        int saveCount = 0;
        if (!cdList.isEmpty()) {
            int cdListSize = cdList.size();
            Cat.logEvent(CAT_TRANSACTION_TYPE, "BatchSave.Before", Event.SUCCESS, "cdListSize=" + cdListSize);
            saveCount = QconfigServiceUtils.batchSave(getQconfigService(), "TitanKeyMHAUpdateHandler", cdList, true,
                    QconfigServiceUtils.getTitanPluginPredicate());
            Cat.logEvent(CAT_TRANSACTION_TYPE, "BatchSave.After", Event.SUCCESS, "cdListSize=" + cdListSize);
        }

        Cat.logEvent(CAT_TRANSACTION_TYPE, "Save.Key.Count", Event.SUCCESS, "saveCount=" + saveCount);
        return saveCount;
    }

    private Properties buildUpdatePropFromBasicData(MhaInputBasicData mhaInputBasicData) {
        Properties properties = new Properties();
        if (mhaInputBasicData != null) {
            properties.put(CONNECTIONSTRING_SERVER_IP, mhaInputBasicData.getServer());
            properties.put(CONNECTIONSTRING_PORT, String.valueOf(mhaInputBasicData.getPort()));
        }
        return properties;
    }


}
