package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.PermissionCheckUtil;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import qunar.tc.qconfig.common.exception.QServiceException;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * WhiteList dbName list handler
 * Sample:
 * [GET]   http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan/whitelist/listAllDbName?
 * env=pro
 * <p>
 * Index file like:
 * [1] dbname_0, dbname_1, ...., dbname_4
 * <p>
 * Created by lzyan on 2018/9/25.
 */
public class DbNameListHandler extends BaseAdminHandler implements TitanConstants {

    public DbNameListHandler(QconfigService qconfigService) {
        super(qconfigService);
    }

    @Override
    public String getUri() {
        return "/plugins/titan/whitelist/listAllDbName";
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

        return PluginResult.oK();
    }


    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "DbNameListHandler");
        try {
            t.addData("running class=" + getClass().getSimpleName());
            EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            t.addData("profile", profile);
            Preconditions.checkArgument(profile != null && profile.formatProfile() != null,
                    "profile参数不能为空");


            //AdminSite白名单检查
            PluginConfig pluginConfig = new PluginConfig(getQconfigService(), profile);
            String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
            boolean permitted = checkPermission(clientIp, profile);
            if (permitted) {
                //get data from index file - (dbName)
                String indexPrefix = pluginConfig.getParamValue(INDEX_DBNAME_SHARD_PREFIX);
                int indexNumber = Integer.parseInt(pluginConfig.getParamValue(INDEX_DBNAME_SHARD_NUM));
                Set<String> allDbNameSet = getDbNameFromIndex(profile.formatProfile(), indexPrefix, indexNumber);

                //set into return result
                pluginResult.setAttribute(allDbNameSet);

            } else {
                t.addData("postHandleDetail(): sitePermission=false, not allow to read index!");
                Cat.logEvent("DbNameListHandler", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to read index! clientIp=" + clientIp);
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


    // get db name from index file
    private Set<String> getDbNameFromIndex(String profile, String indexPrefix, int indexNumber) throws QServiceException {
        Set<String> allDbNameSet = Sets.newLinkedHashSet();
        String groupId = TITAN_QCONFIG_PLUGIN_APPID;
        if (!Strings.isNullOrEmpty(indexPrefix) && indexNumber >= 0) {
            String indexName = null;
            for (int i = 0; i < indexNumber; i++) {
                indexName = indexPrefix + i;
                allDbNameSet.addAll(loadDbIndex(groupId, indexName, profile));
            }
        }
        return allDbNameSet;
    }

    // load index file content to list
    private List<String> loadDbIndex(String groupId, String dataId, String profile) throws QServiceException {
        List<String> dbNameList = null;
        ConfigField cf = new ConfigField(groupId, dataId, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cf);
        List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "DbNameListHandler", configFieldList);
        if (configDetailList != null && !configDetailList.isEmpty()) {
            ConfigDetail cd = configDetailList.get(0);
            String content = cd.getContent();
            if (!Strings.isNullOrEmpty(content)) {
                Splitter splitter = Splitter.on(DB_INDEX_DELIMITER).omitEmptyStrings().trimResults();   // "\n"
                dbNameList = splitter.splitToList(content);
            }
        }
        if (dbNameList == null) {
            dbNameList = Lists.newArrayList();
        }
        return dbNameList;
    }


}
