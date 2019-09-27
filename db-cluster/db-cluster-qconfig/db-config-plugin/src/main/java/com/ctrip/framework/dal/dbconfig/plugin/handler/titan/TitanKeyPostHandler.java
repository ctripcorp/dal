package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.SiteInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.TitanUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * This handler is to add/update titan key.
 * Notice: 'dbName' set to lowercase.
 * Sample:
 * [POST]   http://qconfig.uat.qa.nt.ctripcorp.com/plugins/titan/config?appid=100010061&titankey=test1DB_W&env=uat&operator=lzyan
 * &subenv=fat1
 * <p>
 * [body] sample as bellow:
 * <p>
 * {
 * "keyName": "test1DB_W",
 * "providerName": "MySql.Data.MySqlClient",
 * "serverName": "127.0.0.1",
 * "serverIp": "127.0.0.1",
 * "port": "28747",
 * "uid": "tt_daltest_1",
 * "password": "111111",
 * "dbName": "test1DB",
 * "extParam": "useUnicode=true;",
 * "timeOut": 15,
 * "enabled": true,
 * "createUser": "testUser",
 * "updateUser": "testUser",
 * <p>
 * # optional
 * "whiteList": "111111,222222",
 * "blackList": "333333,444444",
 * "id": 1
 * }
 * <p>
 * <p>
 * Created by lzyan on 2017/08/18, 2018/06/01.
 */
public class TitanKeyPostHandler extends BaseAdminHandler implements TitanConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(TitanKeyPostHandler.class);

    private static final String URI = "/plugins/titan/config";
    private static final String METHOD = "POST";

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public TitanKeyPostHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
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
        String env = request.getParameter(REQ_PARAM_ENV);
        String subEnv = request.getParameter(REQ_PARAM_SUB_ENV);
        String titanKey = request.getParameter(REQ_PARAM_TITAN_KEY);

        titanKey = TitanUtils.formatTitanFileName(titanKey);
        request.setAttribute(REQ_ATTR_TITAN_KEY, titanKey);

        EnvProfile profile = new EnvProfile(env, subEnv);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        Stopwatch stopwatch = Stopwatch.createStarted();
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanKeyPostPlugin");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            String body = CommonHelper.getBody(request, false);

            /**
             * Sample body
             {
             "keyName": "testKey",
             "providerName": "MySql.Data.MySqlClient",
             "serverName": "127.0.0.1",
             "serverIp": "127.0.0.1",
             "port": "28747",
             "uid": "testUid",
             "password": "testPwd",
             "dbName": "testDB",
             "extParam": "useUnicode=true;",
             "timeOut": 10,
             "enabled": true,
             "createUser": "testUser",
             "updateUser": "testUser"
             }
             */
            if (!Strings.isNullOrEmpty(body)) {
                SiteInputEntity siteInputEntity = GsonUtils.json2T(body, SiteInputEntity.class);
                LOGGER.info("postHandleDetail(): siteInputEntity=" + siteInputEntity);
                if (siteInputEntity != null) {
                    //set 'dbName' to lowercase
                    String dbName = siteInputEntity.getDbName();
                    if (dbName != null) {
                        siteInputEntity.setDbName(dbName.toLowerCase());
                    }

                    EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
                    Preconditions.checkArgument(profile != null && !Strings.isNullOrEmpty(profile.formatProfile()),
                            "profile参数不能为空");

                    String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                    boolean permitted = checkPermission(clientIp, profile);

                    if (permitted) {
                        //更新TitanKey文件内容
                        update(request, siteInputEntity);
                    } else {
                        t.addData("postHandleDetail(): sitePermission=false, not allow to update!");
                        Cat.logEvent("TitanKeyPostPlugin", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to update! clientIp=" + clientIp);
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
                    }

                } else {
                    t.addData("postHandleDetail(): siteInputEntity=null, no need to add/update!");
                }
            } else {
                t.addData("postHandleDetail(): site body is null or empty, no need to add/update!");
            }

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DbConfigPluginException(e);
        } finally {
            t.complete();
            //metric cost
            stopwatch.stop();
            long cost = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        }
        return pluginResult;
    }

    //add/update titanKey file
    private void update(HttpServletRequest request, SiteInputEntity siteInputEntity) throws Exception {
        String group = request.getParameter(REQ_PARAM_TARGET_APPID);
        String dataId = (String) request.getAttribute(REQ_ATTR_TITAN_KEY);
        EnvProfile envProfile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(group), "group参数不能为空");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dataId), "dataId参数不能为空");
        Preconditions.checkArgument(envProfile != null && !Strings.isNullOrEmpty(envProfile.formatProfile()),
                "profile参数不能为空");
        Preconditions.checkArgument(TITAN_QCONFIG_KEYS_APPID.equals(group),
                "group=" + group + " not match, only can be " + TITAN_QCONFIG_KEYS_APPID);

        //get simple env
        String env = envProfile.formatEnv();

        //build updateConf from <siteInputEntity>
        Properties rawProp = format2Properties(siteInputEntity);

        PluginConfig config = getPluginConfigManager().getPluginConfig(envProfile);
        CryptoManager cryptoManager = new CryptoManager(config);

        //get current config from qconfig
        String profile = envProfile.formatProfile();
        ConfigField cm = new ConfigField(group, dataId, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cm);
        List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "TitanKeyPostHandler", configFieldList);
        if (configDetailList != null && !configDetailList.isEmpty()) {
            //已经存在，更新
            ConfigDetail configDetail = configDetailList.get(0);    //get first
            configDetail.setOldConfigDetail(configDetail);
            String encryptOldConf = configDetail.getContent();
            Properties oldProperties = CommonHelper.parseString2Properties(encryptOldConf);

            //decrypt
            Properties oldDecryptProp = cryptoManager.decrypt(dataSourceCrypto, keyService, oldProperties);
            //merge update
            Properties mergeProp = CommonHelper.merge(rawProp, oldDecryptProp);
            //field 'version' increase one    [2017-09-01]
            CommonHelper.increaseVersionInProperties(mergeProp, getQconfigService().getVersionIncrenment());

            //check db connection
            checkDbConnection(config, mergeProp, env);

            //encrypt content again
            Properties encProp = cryptoManager.encrypt(dataSourceCrypto, keyService, mergeProp);
            String encryptText = CommonHelper.parseProperties2String(encProp);
            //fill encryptText
            configDetail.setVersion(-2);    //修改
            configDetail.setContent(encryptText);

            //save to qconfig with public
            QconfigServiceUtils.batchSave(getQconfigService(), "TitanKeyPostHandler", Lists.newArrayList(configDetail), true,
                    QconfigServiceUtils.getTitanPluginPredicate());

            // 更新索引文件 [2018-09-26]
            boolean indexUpdateEnabled = false;
            String indexUpdateEnabledStr = config.getParamValue(INDEX_FILE_UPDATE_ENABLED);
            if (!Strings.isNullOrEmpty(indexUpdateEnabledStr)) {
                indexUpdateEnabled = Boolean.parseBoolean(indexUpdateEnabledStr);
            }
            if (indexUpdateEnabled) {
                String enabled_old = oldDecryptProp.getProperty(ENABLED);
                String enabled_input = rawProp.get(ENABLED) == null ? null : rawProp.get(ENABLED).toString();
                if (!Strings.isNullOrEmpty(enabled_input) && !enabled_input.equalsIgnoreCase(enabled_old)) {
                    IndexManager indexManager = new IndexManager(getQconfigService(), config);
                    String dbName = mergeProp.getProperty(CONNECTIONSTRING_DB_NAME);
                    String key = mergeProp.getProperty(CONNECTIONSTRING_KEY_NAME);
                    if (Boolean.parseBoolean(enabled_input)) {
                        //key激活
                        indexManager.add2Index(dbName, key, profile);
                    } else {
                        //key禁用
                        indexManager.removeFromIndex(dbName, key, profile);
                    }
                }
            }
        } else {
            //新增

            //field 'version' increase one    [2017-09-01]
            CommonHelper.increaseVersionInProperties(rawProp, getQconfigService().getVersionIncrenment());

            //check db connection
            checkDbConnection(config, rawProp, env);

            Properties encProp = cryptoManager.encrypt(dataSourceCrypto, keyService, rawProp);
            String encryptText = CommonHelper.parseProperties2String(encProp);
            //compose <configDetail>
            ConfigDetail configDetail = new ConfigDetail();
            configDetail.setConfigField(cm);
            configDetail.setVersion(-1);    //新增
            configDetail.setContent(encryptText);
            //save to qconfig with public
            QconfigServiceUtils.batchSave(getQconfigService(), "TitanKeyPostHandler", Lists.newArrayList(configDetail), true);

            // 更新索引文件 [2018-09-26]
            boolean indexUpdateEnabled = false;
            String indexUpdateEnabledStr = config.getParamValue(INDEX_FILE_UPDATE_ENABLED);
            if (!Strings.isNullOrEmpty(indexUpdateEnabledStr)) {
                indexUpdateEnabled = Boolean.parseBoolean(indexUpdateEnabledStr);
            }
            if (indexUpdateEnabled) {
                IndexManager indexManager = new IndexManager(getQconfigService(), config);
                String dbName = rawProp.getProperty(CONNECTIONSTRING_DB_NAME);
                String key = rawProp.getProperty(CONNECTIONSTRING_KEY_NAME);
                indexManager.add2Index(dbName, key, profile);
            }
        }
    }

    //format siteInputEntity to titanKey file content properties
    private Properties format2Properties(SiteInputEntity siteInputEntity) throws Exception {
        Properties properties = new Properties();
        HashMap<String, Object> map = CommonHelper.getFieldMap(siteInputEntity);
        if (map != null && !map.isEmpty()) {
            for (Map.Entry entry : map.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }
        return properties;
    }

    //check db connection
    private void checkDbConnection(PluginConfig config, Properties properties, String env) throws Exception {
        boolean needCheck = true;

        //no need check when enabled=false  [2018-01-19]
        Object enabledObj = properties.get(ENABLED);
        if (enabledObj != null) {
            needCheck = Boolean.valueOf(enabledObj.toString());
        }

        if (needCheck) {
            boolean forMHA = false;
            boolean validResult = new DbAccessManager(config).validConnection(properties, env, forMHA);
            if (!validResult) {
                throw new Exception("checkDbConnection(): validResult=false, db connection check failure!");
            }
        }
    }

}
