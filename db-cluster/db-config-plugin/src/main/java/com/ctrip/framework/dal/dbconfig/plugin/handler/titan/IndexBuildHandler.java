package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.IndexBuildOutputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import qunar.tc.qconfig.common.bean.PaginationResult;
import qunar.tc.qconfig.common.exception.QServiceException;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Index build handler
 * Sample:
 * [GET]   http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan/index/build?env=fat
 * <p>
 * Index file:
 * [1] dbname_key_0, dbname_key_1, ...., dbname_key_29
 * [2] dbname_0, dbname_1, ...., dbname_4
 * <p>
 * Created by lzyan on 2018/9/21.
 */
public class IndexBuildHandler extends BaseAdminHandler implements TitanConstants {

    public IndexBuildHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
        super(qconfigService,pluginConfigManager);
    }

    @Override
    public String getUri() {
        return "/plugins/titan/index/build";
    }

    @Override
    public String getMethod() {
        return "GET";
    }

    @Override
    public PluginResult preHandle(HttpServletRequest request) {
        String env = request.getParameter(REQ_PARAM_ENV);

        //format <profile>
        EnvProfile profile = new EnvProfile(env);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        return PluginResult.oK();
    }


    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
        IndexBuildOutputEntity indexBuildOutputEntity = new IndexBuildOutputEntity();
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "IndexBuildHandler");
        try {
            t.addData("running class=" + getClass().getSimpleName());
            EnvProfile envProfile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
            Preconditions.checkArgument(envProfile != null && envProfile.formatProfile() != null,
                    "profile参数不能为空");

            //AdminSite白名单检查
            PluginConfig pluginConfig = getPluginConfigManager().getPluginConfig(envProfile);
            boolean indexEnabled = Boolean.parseBoolean(pluginConfig.getParamValue(INDEX_ENABLED));
            String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
            boolean sitePermission = checkPermission(clientIp, envProfile);
            if (indexEnabled && sitePermission) {
                Map<String, Set<String>> dbName2KeyMap = Maps.newLinkedHashMap();
                int pageNo = 1;
                int pageSize = 10;
                long total = 0;
                String group = TITAN_QCONFIG_KEYS_APPID;     //appId
                String dataId = "";     //fileName = titanKey

                String profile = envProfile.formatProfile();
                ConfigField configField = new ConfigField(group, dataId, profile);
                //get paged data from qconfig
                PaginationResult<ConfigDetail> paginationResult = QconfigServiceUtils.query(getQconfigService(), "IndexBuildHandler", configField, pageNo, pageSize);
                if (paginationResult != null) {
                    total = paginationResult.getTotal();
                }
                pageNo = 1;
                pageSize = 1000;
                while (pageNo * pageSize - total < pageSize) {
                    paginationResult = QconfigServiceUtils.query(getQconfigService(), "IndexBuildHandler", configField, pageNo, pageSize);
                    if (paginationResult != null) {
                        if (paginationResult.getData() != null) {
                            Set<String> keyNameSet = null;
                            for (ConfigDetail cd : paginationResult.getData()) {
                                String encryptText = cd.getContent();
                                Properties encryptProp = CommonHelper.parseString2Properties(encryptText);
                                String keyName = encryptProp.getProperty(CONNECTIONSTRING_KEY_NAME);
                                String dbName = encryptProp.getProperty(CONNECTIONSTRING_DB_NAME);
                                String enabled = encryptProp.getProperty(ENABLED);
                                // 仅使用 enabled=true 的key
                                if (!Strings.isNullOrEmpty(enabled) && Boolean.parseBoolean(enabled)) {
                                    keyNameSet = dbName2KeyMap.get(dbName);
                                    if (keyNameSet == null) {
                                        keyNameSet = Sets.newHashSet();
                                        dbName2KeyMap.put(dbName, keyNameSet);
                                    }
                                    //去除keyName末尾的'_SH'(大小写不敏感), 索引中的key都是不含'_SH'的。 abc_SH -> abc
                                    if (keyName.toUpperCase().endsWith(KEY_TRAIL_SH)) {
                                        keyName = keyName.substring(0, keyName.length() - KEY_TRAIL_SH.length());
                                    }
                                    keyNameSet.add(keyName);
                                }
                            }//End for
                        }
                    }
                    pageNo++;
                }


                //update index file - (db-key)
                String indexPrefix = pluginConfig.getParamValue(INDEX_DBNAME_KEY_SHARD_PREFIX);
                int indexNumber = Integer.parseInt(pluginConfig.getParamValue(INDEX_DBNAME_KEY_SHARD_NUM));
                Map<String, Properties> index2PropMap = buildDbKeyIndexData(dbName2KeyMap, indexPrefix, indexNumber);
                updateDbKeyIndex(index2PropMap, profile);


                //update index file - (dbName)
                Set<String> dbNameSet = dbName2KeyMap.keySet();
                indexPrefix = pluginConfig.getParamValue(INDEX_DBNAME_SHARD_PREFIX);
                indexNumber = Integer.parseInt(pluginConfig.getParamValue(INDEX_DBNAME_SHARD_NUM));
                Map<String, Set<String>> index2SetMap = buildDbIndexData(dbNameSet, indexPrefix, indexNumber);
                updateDbIndex(index2SetMap, profile);

                //build return data
                if (index2PropMap != null) {
                    Set<String> dbKeyIndexSet = index2PropMap.keySet();
                    indexBuildOutputEntity.setDbKeyIndexSet(dbKeyIndexSet);
                }
                if (index2SetMap != null) {
                    Set<String> dbIndexSet = index2SetMap.keySet();
                    indexBuildOutputEntity.setDbIndexSet(dbIndexSet);
                }

            } else {
                t.addData("postHandleDetail(): [indexEnabled, sitePermission] one of them is false, not allow to build index!");
                Cat.logEvent("IndexBuildHandler", "NO_PERMISSION", Event.SUCCESS, "indexEnabled=" + indexEnabled + ", sitePermission=" + sitePermission + ", not allow to build index! clientIp=" + clientIp);
                pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Switch or access ip whitelist check fail! clientIp=" + clientIp + ", indexEnabled=" + indexEnabled);
            }


            //set into return result
            pluginResult.setAttribute(indexBuildOutputEntity);

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


    // build db-key index file data
    private Map<String, Properties> buildDbKeyIndexData(Map<String, Set<String>> dbName2KeyMap, String indexPrefix, int indexNumber) {
        Map<String, Properties> index2PropMap = Maps.newHashMap();
        if (dbName2KeyMap != null && !dbName2KeyMap.isEmpty()) {
            Joiner commaJoiner = Joiner.on(",");
            String dbName = null;
            Set<String> keyNameSet = null;
            String keyNameString = null;
            Properties prop = null;
            String indexName = null;
            for (Map.Entry<String, Set<String>> entry : dbName2KeyMap.entrySet()) {
                dbName = entry.getKey();
                keyNameSet = entry.getValue();
                if (keyNameSet != null && !keyNameSet.isEmpty()) {
                    keyNameString = commaJoiner.join(keyNameSet);
                    indexName = indexPrefix + CommonHelper.locateIndex(dbName, indexNumber);
                    prop = index2PropMap.get(indexName);
                    if (prop == null) {
                        prop = new Properties();
                        index2PropMap.put(indexName, prop);
                    }
                    prop.put(dbName, keyNameString);
                }
            }
        }
        return index2PropMap;
    }

    // build db index file data
    private Map<String, Set<String>> buildDbIndexData(Set<String> dbNameSet, String indexPrefix, int indexNumber) {
        Map<String, Set<String>> index2SetMap = Maps.newHashMap();
        if (dbNameSet != null && !dbNameSet.isEmpty()) {
            Set<String> dbSubSet = null;
            String indexName = null;
            for (String dbName : dbNameSet) {
                if (dbName != null && !Strings.isNullOrEmpty(dbName)) {
                    indexName = indexPrefix + CommonHelper.locateIndex(dbName, indexNumber);
                    dbSubSet = index2SetMap.get(indexName);
                    if (dbSubSet == null) {
                        dbSubSet = Sets.newHashSet();
                        index2SetMap.put(indexName, dbSubSet);
                    }
                    dbSubSet.add(dbName);
                }
            }
        }
        return index2SetMap;
    }


    // update index
    private void updateDbKeyIndex(Map<String, Properties> index2PropMap, String profile) throws QServiceException {
        String groupId = TITAN_QCONFIG_PLUGIN_APPID;
        String dataId = null;   // file name
        String content = null;
        ConfigDetail configDetail = null;
        List<ConfigDetail> cdList = Lists.newArrayList();
        for (Map.Entry<String, Properties> entry : index2PropMap.entrySet()) {
            dataId = entry.getKey();
            content = CommonHelper.parseProperties2String(entry.getValue());
            long version = getVersion(groupId, dataId, profile);
            configDetail = new ConfigDetail(groupId, dataId, profile);
            configDetail.setVersion(version);
            configDetail.setContent(content);
            cdList.add(configDetail);
        }

        //save to qconfig with private
        if (!cdList.isEmpty()) {
            QconfigServiceUtils.batchSave(getQconfigService(), "IndexBuildHandler", cdList, false);
        }

    }

    // update dbName index
    private void updateDbIndex(Map<String, Set<String>> index2SetMap, String profile) throws QServiceException {
        String groupId = TITAN_QCONFIG_PLUGIN_APPID;
        String dataId = null;   // file name
        String content = null;
        ConfigDetail configDetail = null;
        List<ConfigDetail> cdList = Lists.newArrayList();
        Joiner joiner = Joiner.on(DB_INDEX_DELIMITER);   // "\n"
        for (Map.Entry<String, Set<String>> entry : index2SetMap.entrySet()) {
            dataId = entry.getKey();
            content = joiner.join(entry.getValue());
            long version = getVersion(groupId, dataId, profile);
            configDetail = new ConfigDetail(groupId, dataId, profile);
            configDetail.setVersion(version);
            configDetail.setContent(content);
            cdList.add(configDetail);
        }

        //save to qconfig with private
        if (!cdList.isEmpty()) {
            QconfigServiceUtils.batchSave(getQconfigService(), "IndexBuildHandler", cdList, false);
        }
    }


    // get verion (add/update)
    private long getVersion(String groupId, String dataId, String profile) throws QServiceException {
        long version = -2;
        //get current index from qconfig
        ConfigField cf = new ConfigField(groupId, dataId, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cf);
        List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(), "IndexBuildHandler", configFieldList);
        if (configDetailList != null && !configDetailList.isEmpty()) {
            //已经存在，更新
            version = -2;   //修改
        } else {
            version = -1;   //新增
        }
        return version;
    }


}
