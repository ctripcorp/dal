package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.MhaInputBasicData;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.MhaInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.TitanUpdateBasicData;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.TitanUpdateInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.service.DataSourceCrypto;
import com.ctrip.framework.dal.dbconfig.plugin.service.DefaultDataSourceCrypto;
import com.ctrip.framework.dal.dbconfig.plugin.service.KeyService;
import com.ctrip.framework.dal.dbconfig.plugin.service.Soa2KeyService;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by shenjie on 2019/6/26.
 */
public class TitanKeyUpdateHandler extends BaseAdminHandler implements TitanConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(TitanKeyUpdateHandler.class);

    private static final String URI = "/plugins/titan/config/update";
    private static final String METHOD = "POST";

    private static final String CAT_EVENT_TYPE = "TitanKeyUpdatePlugin.Update.TitanKey";

    private KeyListByDbNameHandler keyListByDbNameHandler;
    private TitanKeyMHAUpdateHandler titanKeyMHAUpdateHandler;

    private DataSourceCrypto dataSourceCrypto = DefaultDataSourceCrypto.getInstance();
    private KeyService keyService = Soa2KeyService.getInstance();

    public TitanKeyUpdateHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager, KeyListByDbNameHandler keyListByDbNameHandler, TitanKeyMHAUpdateHandler titanKeyMHAUpdateHandler) {
        super(qconfigService, pluginConfigManager);
        this.keyListByDbNameHandler = keyListByDbNameHandler;
        this.titanKeyMHAUpdateHandler = titanKeyMHAUpdateHandler;
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
        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        Transaction t = Cat.newTransaction("Titan.Admin.Plugin", "TitanKeyUpdateHandler");
        try {
            t.addData("running class=" + getClass().getSimpleName());
            String requestBody = CommonHelper.getBody(request, true);
            Cat.logEvent(CAT_EVENT_TYPE, "RequestBody", Event.SUCCESS, "requestBody= " + requestBody);
            if (!Strings.isNullOrEmpty(requestBody)) {
                TitanUpdateInputEntity titanUpdateInputEntity = GsonUtils.json2T(requestBody, TitanUpdateInputEntity.class);
                if (titanUpdateInputEntity != null) {
                    String env = titanUpdateInputEntity.getEnv();
                    Preconditions.checkArgument(!Strings.isNullOrEmpty(env), "env不能为空");
                    EnvProfile profile = new EnvProfile(env);

                    //AdminSite白名单检查
                    String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                    boolean permitted = checkPermission(clientIp, profile);
                    Cat.logEvent(CAT_EVENT_TYPE, "Site.Permission", Event.SUCCESS, "sitePermission=" + permitted);

                    if (permitted) {
                        // 更新titanKey文件
                        int saveCount = update(titanUpdateInputEntity);
                        t.addData("postHandleDetail(): saveCount=" + saveCount);
                    } else {
                        t.addData("postHandleDetail(): sitePermission=false, not allow to update!");
                        Cat.logEvent("TitanKeyUpdatePlugin.NoSitePermission", clientIp);
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
                    }

                } else {
                    t.addData("postHandleDetail(): titanUpdateInputEntity=null, no need to update!");
                }
            } else {
                t.addData("postHandleDetail(): titan update body is null or empty, no need to update!");
            }

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DbConfigPluginException(e);
        } finally {
            t.complete();
        }
        return pluginResult;
    }

    private int update(TitanUpdateInputEntity titanUpdateInputEntity) throws Exception {
        /**
         * 1.find titanKys by dbName
         * 2.get titanKeys
         * 3.update titanKeys
         */
        String env = titanUpdateInputEntity.getEnv();
        EnvProfile profile = new EnvProfile(env);
        PluginConfig pluginConfig = new PluginConfig(getQconfigService(), profile);
        String formattedProfile = profile.formatProfile();

        List<TitanUpdateBasicData> dbData = titanUpdateInputEntity.getDbData();
        List<MhaInputBasicData> mhaData = titanUpdateInputEntity.getMhaData();

        List<MhaInputBasicData> allMhaInputBasicData = Lists.newArrayList();
        if (dbData != null && !dbData.isEmpty()) {
            for (TitanUpdateBasicData basicData : dbData) {
                // get titanKeys by dbName
                String dbName = basicData.getDbName();
                if (dbName != null) {
                    dbName = dbName.toLowerCase();
                }
                Set<String> titanKeyNames = keyListByDbNameHandler.getTitanKeyNames(pluginConfig, formattedProfile, dbName);
                // get same domain titanKeys
                List<String> needUpdateTitanKeys = getNeedUpdateTitanKeys(titanKeyNames, formattedProfile, basicData.getDomain());
                // build mhaUpdate data
                List<MhaInputBasicData> mhaInputBasicData = buildMhaUpdateBasicData(needUpdateTitanKeys, basicData.getIp(), basicData.getPort());
                allMhaInputBasicData.addAll(mhaInputBasicData);
            }
        }

        if (mhaData != null && !mhaData.isEmpty()) {
            allMhaInputBasicData.addAll(mhaData);
        }

        MhaInputEntity mhaInputEntity = new MhaInputEntity(env, allMhaInputBasicData);
        int saveCount = titanKeyMHAUpdateHandler.updateMhaInput(null, mhaInputEntity);

        Cat.logEvent(CAT_EVENT_TYPE, "Save.Key.Count", Event.SUCCESS, "saveCount=" + saveCount);
        return saveCount;
    }

    private List<String> getNeedUpdateTitanKeys(Set<String> titanKeyNames, String env, String domain) throws Exception {
        List<String> needUpdateTitanKeys = Lists.newArrayList();
        if (titanKeyNames != null && !titanKeyNames.isEmpty()) {
            List<ConfigField> configFields = Lists.newArrayList();
            String group = TITAN_QCONFIG_KEYS_APPID;
            for (String titanKeyName : titanKeyNames) {
                ConfigField configField = new ConfigField(group, titanKeyName, env);
                configFields.add(configField);
            }
            // git titanKeys from qconfig
            List<ConfigDetail> configDetails = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "TitanKeyGetHandler", configFields);
            if (configDetails != null && !configDetails.isEmpty()) {
                for (ConfigDetail configDetail : configDetails) {
                    String content = configDetail.getContent();
                    Properties encryptedProp = CommonHelper.parseString2Properties(content);
                    String serverName = encryptedProp.getProperty(CONNECTIONSTRING_SERVER_NAME);
                    if (serverName.equalsIgnoreCase(domain)) {
                        String titanKeyName = encryptedProp.getProperty(CONNECTIONSTRING_KEY_NAME);
                        needUpdateTitanKeys.add(titanKeyName);
                    }
                }
            }
        }
        return needUpdateTitanKeys;
    }

    private List<MhaInputBasicData> buildMhaUpdateBasicData(List<String> titanKeyNames, String ip, Integer port) throws Exception {
        List<MhaInputBasicData> mhaInputBasicData = Lists.newArrayList();
        if (titanKeyNames != null && !titanKeyNames.isEmpty()) {
            for (String titanKeyName : titanKeyNames) {
                MhaInputBasicData data = new MhaInputBasicData(titanKeyName, ip, port);
                mhaInputBasicData.add(data);
            }
        }
        return mhaInputBasicData;
    }
}
