package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import qunar.tc.qconfig.common.exception.QServiceException;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * WhiteList key list by dbName handler
 * Sample:
 * [GET]   http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan/whitelist/listTitanKey?dbName=siccdb
 * &env=pro
 * <p>
 * Index file like:
 * [1] dbname_key_0, dbname_key_1, ...., dbname_key_29
 * <p>
 * Created by lzyan on 2018/9/25.
 */
public class KeyListByDbNameHandler extends BaseAdminHandler implements TitanConstants {

    public KeyListByDbNameHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
        super(qconfigService,pluginConfigManager);
    }

    @Override
    public String getUri() {
        return "/plugins/titan/whitelist/listTitanKey";
    }

    @Override
    public String getMethod() {
        return "GET";
    }

    @Override
    public PluginResult preHandle(HttpServletRequest request) {

        String env = request.getParameter(REQ_PARAM_ENV);
        if (Strings.isNullOrEmpty(env)) {   //默认: pro
            env = "pro";
        }
        //format <profile>
        EnvProfile profile = new EnvProfile(env);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        String dbName = request.getParameter("dbName");
        request.setAttribute(REQ_ATTR_DB_NAME, dbName);

        return PluginResult.oK();
    }


    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "KeyListByDbNameHandler");
        try {
            t.addData("running class=" + getClass().getSimpleName());
            EnvProfile envProfile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            Preconditions.checkArgument(envProfile != null && envProfile.formatProfile() != null,
                    "profile参数不能为空");
            t.addData("profile", envProfile.formatProfile());

            String dbName = (String) request.getAttribute(REQ_ATTR_DB_NAME);
            t.addData("dbName", dbName);
            Preconditions.checkArgument(!Strings.isNullOrEmpty(dbName), "dbName参数不能为空");


            //AdminSite白名单检查
            PluginConfig pluginConfig = getPluginConfigManager().getPluginConfig(envProfile);
            String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
            boolean sitePermission = checkPermission(clientIp, envProfile);
            if (sitePermission) {
                //get data from index file - (db-key)
                String indexPrefix = pluginConfig.getParamValue(INDEX_DBNAME_KEY_SHARD_PREFIX);
                int indexNumber = Integer.parseInt(pluginConfig.getParamValue(INDEX_DBNAME_KEY_SHARD_NUM));
                Set<String> keySet = getKeyFromIndex(envProfile.formatProfile(), indexPrefix, indexNumber, dbName);

                //set into return result
                pluginResult.setAttribute(keySet);

            } else {
                t.addData("postHandleDetail(): sitePermission=false, not allow to read index!");
                Cat.logEvent("KeyListByDbNameHandler", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to read index! clientIp=" + clientIp);
                pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_READ, "Access ip whitelist check fail! clientIp=" + clientIp);
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


    // get key from index file
    private Set<String> getKeyFromIndex(String profile, String indexPrefix, int indexNumber, String dbName) throws QServiceException, IOException {
        Set<String> keySet = Sets.newLinkedHashSet();
        if (!Strings.isNullOrEmpty(indexPrefix) && indexNumber >= 0) {
            String indexName = buildDbKeyIndexName(dbName, indexPrefix, indexNumber);
            String groupId = TITAN_QCONFIG_PLUGIN_APPID;
            keySet.addAll(loadDbKeyIndex(groupId, indexName, profile, dbName));
        }
        return keySet;
    }

    // load index file content to list
    private List<String> loadDbKeyIndex(String groupId, String dataId, String profile, String dbName) throws QServiceException, IOException {
        List<String> keyeList = null;
        ConfigField cf = new ConfigField(groupId, dataId, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cf);
        List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "KeyListByDbNameHandler", configFieldList);
        if (configDetailList != null && !configDetailList.isEmpty()) {
            ConfigDetail cd = configDetailList.get(0);
            String content = cd.getContent();
            if (!Strings.isNullOrEmpty(content)) {
                Properties properties = CommonHelper.parseString2Properties(content);
                if (properties != null) {
                    String keys = properties.getProperty(dbName);
                    if (!Strings.isNullOrEmpty(keys)) {
                        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
                        keyeList = splitter.splitToList(keys);
                    }
                }
            }
        }
        if (keyeList == null) {
            keyeList = Lists.newArrayList();
        }
        return keyeList;
    }

    // build target db-key index fileName
    private String buildDbKeyIndexName(String dbName, String indexPrefix, int indexNumber) {
        return indexPrefix + CommonHelper.locateIndex(dbName, indexNumber);
    }

}
