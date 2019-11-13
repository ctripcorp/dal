package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import qunar.tc.qconfig.plugin.ConfigDetail;
import qunar.tc.qconfig.plugin.ConfigField;
import qunar.tc.qconfig.plugin.QconfigService;

import java.util.List;
import java.util.Properties;

/**
 * Created by lzyan on 2018/9/26.
 */
public class IndexManager implements TitanConstants {
    private QconfigService qconfigService;
    private PluginConfig config = null;

    //=== Constructor ===
    public IndexManager(QconfigService qconfigService, PluginConfig config) {
        this.qconfigService = qconfigService;
        this.config = config;
    }

    // add dbName to index
    public void add2Index(String dbName, String key, String profile) throws Exception {
        if(!Strings.isNullOrEmpty(dbName) && !Strings.isNullOrEmpty(key) && !Strings.isNullOrEmpty(profile)) {
            // dbName to lowercase
            dbName = dbName.toLowerCase();
            profile = CommonHelper.formatProfileTopFromProfile(profile);

            //db-key index
            key = CommonHelper.trimSH(key);
            add2DbKeyIndex(dbName, key, profile);

            //db index
            add2DbIndex(dbName, profile);
        }
    }

    // remove dbName from index
    public void removeFromIndex(String dbName, String key, String profile) throws Exception {
        if(!Strings.isNullOrEmpty(dbName) && !Strings.isNullOrEmpty(key) && !Strings.isNullOrEmpty(profile)) {
            // dbName to lowercase
            dbName = dbName.toLowerCase();
            profile = CommonHelper.formatProfileTopFromProfile(profile);

            //db-key index
            key = CommonHelper.trimSH(key);
            removeFromDbKeyIndex(dbName, key, profile);

            //db index ==> no need to do again, already done in 'removeFromDbKeyIndex()'
            //removeFromDbIndex(dbName, profile);
        }
    }


    // add db-key to index file
    private void add2DbKeyIndex(String dbName, String key, String profile) throws Exception {
        // get index file name
        String groupId = TITAN_QCONFIG_PLUGIN_APPID;
        String indexPrefix = config.getParamValue(INDEX_DBNAME_KEY_SHARD_PREFIX);
        int indexNumber = Integer.parseInt(config.getParamValue(INDEX_DBNAME_KEY_SHARD_NUM));
        String indexName = indexPrefix + CommonHelper.locateIndex(dbName, indexNumber);

        // update index item
        List<ConfigDetail> cdList = Lists.newArrayList();
        ConfigField cf = new ConfigField(groupId, indexName, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cf);
        List<ConfigDetail> configDetailList = qconfigService.currentConfigWithoutPriority(configFieldList);
        if(configDetailList != null && !configDetailList.isEmpty()) {
            //更新索引文件
            ConfigDetail cd = configDetailList.get(0);
            String content = cd.getContent();
            if(content != null) {
                Properties properties = CommonHelper.parseString2Properties(content);
                if(properties == null) {
                    properties = new Properties();
                }
                String keys = properties.getProperty(dbName);
                String newKeys = CommonHelper.appendWithComma(keys, key);
                properties.put(dbName, newKeys);
                content = CommonHelper.parseProperties2String(properties);  //new content
                cd.setContent(content);
                cdList.add(cd);
            }
        } else {
            //新增索引文件
            ConfigDetail configDetail = new ConfigDetail();
            configDetail.setConfigField(cf);
            configDetail.setVersion(-1);    //新增
            configDetail.setContent(dbName + "=" + key);
            cdList.add(configDetail);
        }
        qconfigService.batchSave(cdList, false);
    }

    // add db to index file
    private void add2DbIndex(String dbName, String profile) throws Exception {
        // get index file name
        String groupId = TITAN_QCONFIG_PLUGIN_APPID;
        String indexPrefix = config.getParamValue(INDEX_DBNAME_SHARD_PREFIX);
        int indexNumber = Integer.parseInt(config.getParamValue(INDEX_DBNAME_SHARD_NUM));
        String indexName = indexPrefix + CommonHelper.locateIndex(dbName, indexNumber);

        // update index item
        List<ConfigDetail> cdList = Lists.newArrayList();
        ConfigField cf = new ConfigField(groupId, indexName, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cf);
        List<ConfigDetail> configDetailList = qconfigService.currentConfigWithoutPriority(configFieldList);
        if(configDetailList != null && !configDetailList.isEmpty()) {
            //更新索引文件
            ConfigDetail cd = configDetailList.get(0);
            String content = cd.getContent();
            if(content != null) {
                Splitter splitter = Splitter.on(DB_INDEX_DELIMITER).omitEmptyStrings().trimResults();   // "\n"
                List<String> dnList = splitter.splitToList(content);
                List<String> dbNameList = Lists.newArrayList(dnList);   //Notice: here 'dnList' can't be updated
                if(!dbNameList.contains(dbName)) {
                    dbNameList.add(dbName);
                    Joiner joiner = Joiner.on(DB_INDEX_DELIMITER);
                    content = joiner.join(dbNameList);  //new content
                    cd.setContent(content);
                    cdList.add(cd);
                }
            }
        } else {
            //新增索引文件
            ConfigDetail configDetail = new ConfigDetail();
            configDetail.setConfigField(cf);
            configDetail.setVersion(-1);    //新增
            configDetail.setContent(dbName);
            cdList.add(configDetail);
        }
        if(!cdList.isEmpty()) {
            qconfigService.batchSave(cdList, false);
        }
    }

    // remove db-key from index file
    private void removeFromDbKeyIndex(String dbName, String key, String profile) throws Exception {
        // get index file name
        String groupId = TITAN_QCONFIG_PLUGIN_APPID;
        String indexPrefix = config.getParamValue(INDEX_DBNAME_KEY_SHARD_PREFIX);
        int indexNumber = Integer.parseInt(config.getParamValue(INDEX_DBNAME_KEY_SHARD_NUM));
        String indexName = indexPrefix + CommonHelper.locateIndex(dbName, indexNumber);

        // update index item
        List<ConfigDetail> cdList = Lists.newArrayList();
        ConfigField cf = new ConfigField(groupId, indexName, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cf);
        List<ConfigDetail> configDetailList = qconfigService.currentConfigWithoutPriority(configFieldList);
        if(configDetailList != null && !configDetailList.isEmpty()) {
            //更新索引文件
            ConfigDetail cd = configDetailList.get(0);
            String content = cd.getContent();
            if(content != null) {
                Properties properties = CommonHelper.parseString2Properties(content);
                if(properties == null) {
                    properties = new Properties();
                }
                String keys = properties.getProperty(dbName);
                String newKeys = CommonHelper.removeWithComma(keys, key);
                if(!Strings.isNullOrEmpty(newKeys)) {
                    properties.put(dbName, newKeys);
                } else {
                    //如没有有效的key，则从索引中移除此dbName
                    properties.remove(dbName);
                    //同时从db索引中移除
                    removeFromDbIndex(dbName, profile);
                }
                content = CommonHelper.parseProperties2String(properties);  //new content
                cd.setContent(content);
                cdList.add(cd);
            }
        }
        qconfigService.batchSave(cdList, false);
    }

    // remove db from index file
    private void removeFromDbIndex(String dbName, String profile) throws Exception {
        // get index file name
        String groupId = TITAN_QCONFIG_PLUGIN_APPID;
        String indexPrefix = config.getParamValue(INDEX_DBNAME_SHARD_PREFIX);
        int indexNumber = Integer.parseInt(config.getParamValue(INDEX_DBNAME_SHARD_NUM));
        String indexName = indexPrefix + CommonHelper.locateIndex(dbName, indexNumber);

        // update index item
        List<ConfigDetail> cdList = Lists.newArrayList();
        ConfigField cf = new ConfigField(groupId, indexName, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cf);
        List<ConfigDetail> configDetailList = qconfigService.currentConfigWithoutPriority(configFieldList);
        if(configDetailList != null && !configDetailList.isEmpty()) {
            //更新索引文件
            ConfigDetail cd = configDetailList.get(0);
            String content = cd.getContent();
            if(!Strings.isNullOrEmpty(content)) {
                Splitter splitter = Splitter.on(DB_INDEX_DELIMITER).omitEmptyStrings().trimResults();
                List<String> dbNameList = splitter.splitToList(content);
                if(dbNameList.contains(dbName)) {
                    List<String> list = Lists.newLinkedList(dbNameList);
                    list.remove(dbName);
                    Joiner joiner = Joiner.on(DB_INDEX_DELIMITER);
                    content = joiner.join(list);  //new content
                    cd.setContent(content);
                    cdList.add(cd);
                }
            }
        }
        if(!cdList.isEmpty()) {
            qconfigService.batchSave(cdList, false);
        }
    }




}
