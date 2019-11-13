package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.ConnectionInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.SiteOutputEntity;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.plugin.ConfigDetail;
import qunar.tc.qconfig.plugin.ConfigField;
import qunar.tc.qconfig.plugin.QconfigService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonHelper {
    private static Logger logger = LoggerFactory.getLogger(CommonHelper.class);
    public static final String productType = "PRO";
    public static final String productTitanKeySuffix = "_sh";
    private static final Pattern lptSubEnvPattern = Pattern.compile("(LPT)(\\d*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern lptEnvPattern = Pattern.compile("(LPT)$", Pattern.CASE_INSENSITIVE);
    public static final ThreadLocal<DateFormat> dfFull = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    //get limit range random, [min, max)
    public static int limitRangeRandom(int min, int max){
        Random random = new Random();
        int randNum = random.nextInt(max - min + 1) + min;
        return randNum;
    }

    //get appId
    public static String getAppId() {
        String appId = Foundation.app().getAppId();
        if(appId == null){
            logger.error("getAppId(): appId is not set, please check your env configuration!");
            throw new IllegalArgumentException("getAppId(): appId is not set, please check your env configuration!");
        }
        return appId;
    }

    //get server ip
    public static String getServerIp(){
        String serverIp = Foundation.net().getHostAddress();
        return serverIp;
    }

    //get env
    public static Env getEnv(){
        Env env = Foundation.server().getEnv();
        return env;
    }

    //merge configuration
    public static String merge(String childConfiguration, String parentConfiguration) throws Exception {
        childConfiguration = childConfiguration == null ? "" : childConfiguration;
        parentConfiguration = parentConfiguration == null ? "" : parentConfiguration;
        try {
            Properties childProperties = new Properties();
            childProperties.load(new StringReader(childConfiguration));
            Properties parentProperties = new Properties();
            parentProperties.load(new StringReader(parentConfiguration));
            //merge
            for (Map.Entry<Object, Object> entry : childProperties.entrySet()) {
                parentProperties.put(entry.getKey(), entry.getValue());
            }
            return parseProperties2String(parentProperties);
        } catch (Exception e) {
            logger.error("merge(): parse configuration to properties error.", e);
            throw e;
        }
    }
    //merge configuration
    public static Properties merge(Properties childProperties, Properties parentProperties) {
        Properties resultProp = new Properties();
        //put parent
        for (Map.Entry<Object, Object> entry : parentProperties.entrySet()) {
            resultProp.put(entry.getKey(), entry.getValue());
        }
        //put son
        for (Map.Entry<Object, Object> entry : childProperties.entrySet()) {
            resultProp.put(entry.getKey(), entry.getValue());
        }
        return resultProp;

    }


    //parse properties to string
    public static String parseProperties2String(Properties properties) {
        if (properties == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            //result.append(JOINER.join(entry.getKey(), entry.getValue())).append("\n");
            result.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        return result.toString();
    }

    //parse properties to string
    public static Properties parseString2Properties(String configuration) throws IOException {
        Properties properties = null;
        if (configuration != null) {
            properties = new Properties();
            properties.load(new StringReader(configuration));
        }
        return properties;
    }


    /**
     * build connection string
     * @param properties
     * @param isFailover, false: return raw conn string(prefer use serverIp); true: return failover conn string(use serverName)
     * @return
     * @throws Exception
     */
    public static String buildConnectionString(Properties properties, boolean isFailover) throws Exception {
        String connString = null;
        if(properties != null){
            String providerName = (String) properties.get(TitanConstants.CONNECTIONSTRING_PROVIDER_NAME);
            Object serverName =  properties.get(TitanConstants.CONNECTIONSTRING_SERVER_NAME);
            Object serverIp =  properties.get(TitanConstants.CONNECTIONSTRING_SERVER_IP);
            Object port = properties.get(TitanConstants.CONNECTIONSTRING_PORT);
            Object uid = properties.get(TitanConstants.CONNECTIONSTRING_UID);
            Object pwd = properties.get(TitanConstants.CONNECTIONSTRING_PASSWORD);
            Object dbName = properties.get(TitanConstants.CONNECTIONSTRING_DB_NAME);
            String extParam = (String) properties.get(TitanConstants.CONNECTIONSTRING_EXT_PARAM);
            String version = (String) properties.get(TitanConstants.VERSION);

            //format serverName
            serverName = (serverName==null ? "" : serverName);

            //get host, use 'serverIp' first if it is not blank, otherwise use serverName
            Object host = Strings.isNullOrEmpty((String)serverIp) ? serverName : serverIp;
            if(isFailover){
                host = serverName;
            }

            switch (providerName) {
                case TitanConstants.NAME_MYSQL_PROVIDER:
                    connString = String.format(TitanConstants.FORMAT_MYSQL_CONNECTIONSTRING,
                            host,
                            port,
                            uid,
                            pwd,
                            dbName);
                    break;
                case TitanConstants.NAME_SQLSERVER_PROVIDER:
                    connString = String.format(TitanConstants.FORMAT_SQLSERVER_CONNECTIONSTRING,
                            host,
                            port,
                            uid,
                            pwd,
                            dbName);
                    break;
                default:
                    throw new IllegalArgumentException("providerName is invalid. Valid value are [System.Data.SqlClient, MySql.Data.MySqlClient].");
            }
            if(!Strings.isNullOrEmpty(extParam)) {
                connString = connString + extParam;
            }

            //combine 'version'
            if(!Strings.isNullOrEmpty(version)){
                if(!connString.endsWith(";")){
                    connString = connString + ";";
                }
                connString = connString + TitanConstants.VERSION + "=" + version;
            }else{
                logger.warn("buildConnectionString(): abnormal content, version is null or empty!");
            }
        }else{
            logger.warn("buildConnectionString(): properties is null, can't build connection string!");
        }
        return connString;
    }

    //build mha update start time
    public static String buildMhaUpdateStartTime(Properties properties) {
        String connString = null;
        if(properties != null && properties.containsKey(TitanConstants.MHA_UPDATE_START_TIME)){
            connString = (String)properties.get(TitanConstants.MHA_UPDATE_START_TIME);
        }
        return connString;
    }

    //format titanKey fileName, lowercase
    public static String formatTitanFileName(String titanKey){
        String result = titanKey;
        //to lowercase
        if(result != null) {
            result = result.toLowerCase();
        }
        return result;
    }

    //=== In further, exist both xxx and xxx_sh for pro in db/qconfig source, so no need to transform here [2017-11-08] ===
    //format titanKey fileName
//    public static String formatTitanFileName(QconfigService qconfigService, String titanKey, String profile) throws Exception {
//        titanKey = titanKey.toLowerCase();
//        String result = titanKey;
//        if(profile != null){
//            String env = profile;
//            if(env.contains(":")){
//                String[] envArray = env.split(":");
//                env = envArray[0];
//            }
//            //if pro env and not exist with current key, append with "_sh"
//            if(env.equalsIgnoreCase(productType) && !titanKey.endsWith(productTitanKeySuffix)){
//                //[1] check subEnv exact match
//                boolean withPriority = false;
//                boolean exist = checkTitanKeyExist(qconfigService, titanKey, profile, withPriority);
//                if(!exist){
//                    //[2] check subEnv exact match with "_sh"
//                    withPriority = false;
//                    String key_sh = titanKey + productTitanKeySuffix;
//                    exist = checkTitanKeyExist(qconfigService, key_sh, profile, withPriority);
//                    if(!exist){
//                        //[3] check from parent env match
//                        withPriority = true;
//                        exist = checkTitanKeyExist(qconfigService, titanKey, profile, withPriority);
//                        if(!exist){
//                            //[4] check from parent env match with "_sh"
//                            withPriority = true;
//                            exist = checkTitanKeyExist(qconfigService, key_sh, profile, withPriority);
//                            if(exist){
//                                result = key_sh;
//                            }
//                        }else{
//                            result = titanKey;
//                        }
//                    }else{
//                        result = key_sh;
//                    }
//                }
//            }
//        }
//
//        //to lowercase
//        if(result != null) {
//            result = result.toLowerCase();
//        }
//        return result;
//    }

    //format profile. eg: UAT -> uat:   |   FAT:FAT16 -> fat:FAT16
    public static String formatProfileFromEnv(String env){
        return formatProfileFromEnv(env, null);
    }

    //format profile: subEnv keep it when create since qconfig client case sensitive. eg: FAT, FAT16   ->  fat:FAT16
    public static String formatProfileFromEnv(String env, String subenv){
        String profile = env;
        if(env != null){
            if(!env.contains(":")){
                profile = env.toLowerCase() + ":";
                if(!Strings.isNullOrEmpty(subenv)){
                    profile += subenv;
                }
            }else{
                String[] entryArray = env.split(":");
                if(entryArray.length >= 2) {
                    profile = entryArray[0].toLowerCase() + ":" + entryArray[1];
                }
            }
        }
        //to lowercase  //subEnv is case sensitive for qconfig client.    [2017-11-03]
//        if(profile != null) {
//            profile = profile.toLowerCase();
//        }
        return profile;
    }

    //format profile. eg: fat:fat2   ->  fat:
    public static String formatProfileTopFromProfile(String rawProfile){
        String profile = rawProfile;
        if(rawProfile != null && rawProfile.contains(":")){
            int index = rawProfile.indexOf(":");
            profile = rawProfile.substring(0, index+1); //include ':'
        }
        return profile;
    }


    //raw profile. eg: uat:   ->  uat
    public static String formatEnvFromProfile(String profile){
        String env = profile;
        if(profile != null && profile.contains(":")){
            env = profile.split(":")[0];
        }
        return env;
    }
    //get sub env. eg: fat:fat2 ->  fat2
    public static String getSubEnvFromProfile(String profile){
        String subenv = "";
        if(profile != null && profile.contains(":")){
            String[] envEntry = profile.split(":");
            if(envEntry != null && envEntry.length >= 2){
                subenv = envEntry[1];
            }
        }
        return subenv;
    }

    /**
     * format profile for LPT. eg: fat:LPT20   ->  fat:lpt
     *  Only transform when env=fat
     * @param profile
     * @return
     * @throws Exception
     */
    public static String formatProfileForLpt(String profile) throws Exception {
        String newProfile = profile;
        if(profile != null && profile.contains(":")){
            String[] envEntry = profile.split(":");
            if(envEntry != null && envEntry.length >= 2){
                String env = envEntry[0];
                String subEnv = envEntry[1];
                if(!Strings.isNullOrEmpty(subEnv) && checkLptSubEnv(subEnv)
                        && TitanConstants.ENV_FAT.equalsIgnoreCase(env)){
                    newProfile = env + ":lpt";
                }
            }
        }
        return newProfile;
    }

    public static boolean checkLptSubEnv(String subEnv) throws Exception {
        Matcher matcher = lptSubEnvPattern.matcher(subEnv);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
    public static boolean checkLptEnv(String env) throws Exception {
        Matcher matcher = lptEnvPattern.matcher(env);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    //get body content in request
    public static String getBody(HttpServletRequest request, boolean printBody) throws Exception {
        String body = null;
        InputStream is = null;
        try {
            //get parameter 'ContentLength' from head
            int contentLength = request.getContentLength();
            logger.info("getBody(): contentLength=[" + contentLength + "]");
            if(contentLength > 0){
                is = request.getInputStream();
                //Read input data into <contents>
                byte[] contents = new byte[contentLength];
                int readCount = 0;
                while(readCount >= 0 && readCount < contentLength) {
                    readCount += is.read(contents, readCount, contentLength - readCount);
                }
                StringBuilder sb = new StringBuilder();
                sb.append("getBody(): content is: ");
                body = new String(contents, "UTF-8");
                if(printBody) {
                    sb.append(body);
                } else {
                    sb.append("<hidden>");
                }
                logger.info(sb.toString());
            }else{
                logger.warn("getBody(): contentLength<=0, can't get body content!");
            }
        } catch (Exception e) {
            logger.error("getBody(): get request body content error!", e);
            throw e;
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (Exception e) {
                    logger.error("is close error!", e);
                }
            }
        }
        return body;
    }


    /**
     * 获取对象的字段名和值
     * @param obj 反射的对象
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static HashMap<String, Object> getFieldMap(Object obj) throws IllegalAccessException, IllegalArgumentException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (obj == null)
            return null;
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int j=0; j < fields.length; j++) {
            fields[j].setAccessible(true);
            Object _Object = fields[j].get(obj);
            if(_Object != null) {
                map.put(fields[j].getName(), _Object);
            }
        }
        return map;
    }

    //
    //build SiteOutputEntity
    public static SiteOutputEntity buildSiteOutputEntity(Properties properties, boolean encodePwd, ConfigField cf) throws Exception {
        SiteOutputEntity siteOutputEntity = null;
        //Properties properties = parseString2Properties(configuration);
        if(properties != null){
            //prpare <connectionInfo>
            ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setServer(properties.getProperty(TitanConstants.CONNECTIONSTRING_SERVER_NAME));
            connectionInfo.setServerIp(properties.getProperty(TitanConstants.CONNECTIONSTRING_SERVER_IP));
            connectionInfo.setPort(properties.getProperty(TitanConstants.CONNECTIONSTRING_PORT));
            connectionInfo.setUid(properties.getProperty(TitanConstants.CONNECTIONSTRING_UID));
            connectionInfo.setPassword(properties.getProperty(TitanConstants.CONNECTIONSTRING_PASSWORD));
            connectionInfo.setDbName(properties.getProperty(TitanConstants.CONNECTIONSTRING_DB_NAME));
            connectionInfo.setExtParam(properties.getProperty(TitanConstants.CONNECTIONSTRING_EXT_PARAM));

            //prepare other
            String subEnv = getSubEnvFromProfile(cf.getProfile());
            String enabled = properties.getProperty(TitanConstants.ENABLED);
            String timeOut = properties.getProperty(TitanConstants.TIMEOUT);
            String sslCode = properties.getProperty(TitanConstants.SSLCODE);
            String createUser = properties.getProperty(TitanConstants.CREATE_USER);
            String updateUser = properties.getProperty(TitanConstants.UPDATE_USER);
            String whiteList = properties.getProperty(TitanConstants.WHITE_LIST);
            String blackList = properties.getProperty(TitanConstants.BLACK_LIST);
            String id = properties.getProperty(TitanConstants.ID);
            String permissions = properties.getProperty(TitanConstants.PERMISSIONS);
            String freeVerifyIpList = properties.getProperty(TitanConstants.FREE_VERIFY_IPLIST);
            String freeVerifyAppIdList = properties.getProperty(TitanConstants.FREE_VERIFY_APPID_LIST);
            String mhaLastUpdateTime = properties.getProperty(TitanConstants.MHA_LAST_UPDATE_TIME);

            //compose
            siteOutputEntity = new SiteOutputEntity();
            siteOutputEntity.setId((id==null ? null : Integer.valueOf(id)));
            siteOutputEntity.setTitanKey(cf.getDataId());
            siteOutputEntity.setName(properties.getProperty(TitanConstants.CONNECTIONSTRING_KEY_NAME));
            siteOutputEntity.setSubEnv(subEnv);
            siteOutputEntity.setEnabled((enabled==null ? true : Boolean.parseBoolean(enabled)));
            siteOutputEntity.setSslCode(sslCode);
            siteOutputEntity.setProviderName(properties.getProperty(TitanConstants.CONNECTIONSTRING_PROVIDER_NAME));
            siteOutputEntity.setTimeOut((timeOut==null ? Integer.valueOf(0) : Integer.valueOf(timeOut)));
            siteOutputEntity.setCreateUser(createUser);
            siteOutputEntity.setUpdateUser(updateUser);
            siteOutputEntity.setWhiteList(whiteList);
            siteOutputEntity.setBlackList(blackList);
            siteOutputEntity.setPermissions(permissions);
            siteOutputEntity.setFreeVerifyIpList(freeVerifyIpList);
            siteOutputEntity.setFreeVerifyAppIdList(freeVerifyAppIdList);
            siteOutputEntity.setMhaLastUpdateTime(mhaLastUpdateTime);
            siteOutputEntity.setConnectionInfo(connectionInfo);

            //connectionString ==> whole RC4 encode, use 'serverName' in connectionString
            String connString_enc = RC4.encrypt(siteOutputEntity.buildConnectionString(), siteOutputEntity.getName());
            siteOutputEntity.setConnectionString(connString_enc);

            if(encodePwd){
                String newPassword = RC4.encrypt(connectionInfo.getPassword(), siteOutputEntity.getName());
                connectionInfo.setPassword(newPassword);    //TitanConstants.PASSWORD_HIDDEN
            }
        }
        return siteOutputEntity;
    }


    //increase version in properties
    public static void increaseVersionInProperties(Properties properties, int versionIncrement){
        if(properties != null){
            String versionStr = properties.getProperty(TitanConstants.VERSION);
            if(Strings.isNullOrEmpty(versionStr)){
                versionStr = "0";
            }
            long version = Long.parseLong(versionStr);
            version = version + versionIncrement;
            properties.put(TitanConstants.VERSION, String.valueOf(version));
        }else{
            logger.warn("increaseVersionInProperties(): properties=null, can't increase version ...");
        }
    }

    //update mhaLastUpdate in properties
    public static void updateMhaLastUpdateInProperties(Properties properties){
        if(properties != null) {
            String mhaLastUpdate = dfFull.get().format(new Date());
            properties.put(TitanConstants.MHA_LAST_UPDATE_TIME, mhaLastUpdate);
        } else {
            logger.warn("updateMhaLastUpdateInProperties(): properties=null, can't update 'mhaLastUpdate' ...");
        }
    }

    //update mhaUpdateStartTime in properties
    public static void updateMhaUpdateStartTimeInProperties(Properties properties, Object mhaUpdateStartTime) {
        if (properties != null) {
            properties.put(TitanConstants.MHA_UPDATE_START_TIME, String.valueOf(mhaUpdateStartTime));
        } else {
            logger.warn("updateMhaUpdateStartTimeInProperties(): properties=null, can't update 'mhaUpdateStartTime' ...");
        }
    }

    /**
     * check current titanKey exist
     * @param qconfigService
     * @param titankey
     * @param profile
     * @param withPriority, true: from low to top fetch; false: exact match for current env
     * @return
     * @throws Exception
     */
    public static boolean checkTitanKeyExist(QconfigService qconfigService, String titankey, String profile, boolean withPriority) throws Exception {
        boolean exist = false;
        String group = TitanConstants.TITAN_QCONFIG_KEYS_APPID;     //appId
        String dataId = titankey;     //fileName = titanKey
        ConfigField configField = new ConfigField(group, dataId, profile);
        //get latest from qconfig
        List<ConfigField> configFieldList = Lists.newArrayList(configField);
        List<ConfigDetail> configDetailList = null;
        if(withPriority){
            configDetailList = qconfigService.currentConfigWithPriority(configFieldList);
        }else{
            configDetailList = qconfigService.currentConfigWithoutPriority(configFieldList);
        }
        if(configDetailList != null && !configDetailList.isEmpty()) {
            exist = true;
        }
        return exist;
    }

    /**
     * Check whether subEnv use no parent. Case insensitive
     * @param subEnv
     * @param noParentSuffix, eg: -AWS,-ALI
     * @param isPro
     * @return
     */
    public static boolean checkSubEnvNoParent(String subEnv, String noParentSuffix, boolean isPro){
        boolean noParent = false;
        if(!Strings.isNullOrEmpty(subEnv) && !Strings.isNullOrEmpty(noParentSuffix)){
            //处理大小写, 大小写不敏感
            subEnv = subEnv.toLowerCase();
            noParentSuffix = noParentSuffix.toLowerCase();

            String[] noParentSufArray = noParentSuffix.split(",");
            List<String> noParentSufList = Arrays.asList(noParentSufArray);
            if(isPro){  //use as suffix
                for(String noParentSuf : noParentSufList){
                    if(subEnv.endsWith(noParentSuf)){
                        noParent = true;
                        break;
                    }
                }
            }else{  //LPT, use as prefix
                for(String noParentSuf : noParentSufList){
                    if(subEnv.startsWith(noParentSuf)){
                        noParent = true;
                        break;
                    }
                }
            }
        }
        return noParent;
    }

    public static boolean checkPro(String profile){
        boolean isPro = false;
        String env = formatEnvFromProfile(profile);
        if(TitanConstants.ENV_PRO.equalsIgnoreCase(env)){
            isPro = true;
        }
        return isPro;
    }

    // Locate index num
    public static int locateIndex(String input, int indexNumber) {
        if (Strings.isNullOrEmpty(input)) {
            throw new IllegalArgumentException();
        }
        int res = Math.abs(input.hashCode() % indexNumber);
        return res;
    }

    // Merge 'permissions', permissions = union(permission, whiteList, input)
    public static void mergePermissions(Properties properties, String input) {
        if(properties != null) {
            String permissions = properties.getProperty(TitanConstants.PERMISSIONS);
            String whiteListStr = properties.getProperty(TitanConstants.WHITE_LIST);
            if(permissions == null) {
                permissions = "";
            }
            if(whiteListStr == null) {
                whiteListStr = "";
            }
            if(input == null) {
                input = "";
            }
            Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
            Joiner joiner = Joiner.on(",").skipNulls();
            List<String> oldPermissionList = splitter.splitToList(permissions);
            List<String> whiteList = splitter.splitToList(whiteListStr);
            List<String> inputList = splitter.splitToList(input);
            Set<String> appIdSet = Sets.newLinkedHashSet();
            appIdSet.addAll(oldPermissionList);
            appIdSet.addAll(whiteList);
            appIdSet.addAll(inputList);
            String targetAppIds = joiner.join(appIdSet);
            properties.put(TitanConstants.PERMISSIONS, targetAppIds);
        }
    }

    // Append item to input string
    public static String appendWithComma(String input, String item) {
        if(input == null) {
            input = "";
        }
        if(Strings.isNullOrEmpty(item)) {
            return input;
        }
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
        List<String> permList = splitter.splitToList(input);
        Set<String> permSet = Sets.newLinkedHashSet(permList);
        permSet.add(item);
        String result = Joiner.on(",").skipNulls().join(permSet);
        return result;
    }
    // Append itemList to input string
    public static String appendWithComma(String input, List<String> itemList) {
        if(input == null) {
            input = "";
        }
        if(itemList == null || itemList.isEmpty()) {
            return input;
        }
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
        List<String> permList = splitter.splitToList(input);
        Set<String> permSet = Sets.newLinkedHashSet(permList);
        permSet.addAll(itemList);
        String result = Joiner.on(",").skipNulls().join(permSet);
        return result;
    }

    // Remove item from input string
    public static String removeWithComma(String input, String item) {
        if(Strings.isNullOrEmpty(input)) {
            return input;
        }
        if(Strings.isNullOrEmpty(item)) {
            return input;
        }
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
        List<String> list = splitter.splitToList(input);
        Set<String> set = Sets.newLinkedHashSet(list);
        set.remove(item);
        String result = Joiner.on(",").skipNulls().join(set);
        return result;
    }
    // Remove itemList from input string
    public static String removeWithComma(String input, List<String> itemList) {
        if(Strings.isNullOrEmpty(input)) {
            return input;
        }
        if(itemList==null || itemList.isEmpty()) {
            return input;
        }
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
        List<String> list = splitter.splitToList(input);
        Set<String> set = Sets.newLinkedHashSet(list);
        set.removeAll(itemList);
        String result = Joiner.on(",").skipNulls().join(set);
        return result;
    }

    //trim end '_SH'
    public static String trimSH(String keyName) {
        String result = keyName;
        if(!Strings.isNullOrEmpty(keyName)) {
            //去除keyName末尾的'_SH'(大小写不敏感), 索引中的key都是不含'_SH'的。 abc_SH -> abc
            if(keyName.toUpperCase().endsWith(TitanConstants.KEY_TRAIL_SH)) {
                result = keyName.substring(0, keyName.length() - TitanConstants.KEY_TRAIL_SH.length());
            }
        }
        return result;
    }

    //get high env in list. eg: envList contains [pro, uat, fat], return pro.
    public static String getHighEnv(List<String> envList) {
        String result = null;
        if(envList != null && !envList.isEmpty()) {
            List<String> upperEnvList = Lists.newArrayList();
            for(String env : envList) {
                if(!Strings.isNullOrEmpty(env)) {
                    upperEnvList.add(env.toUpperCase());
                }
            }
            String envPro = TitanConstants.ENV_PRO.toUpperCase();
            String envUat = TitanConstants.ENV_UAT.toUpperCase();
            String envFat = TitanConstants.ENV_FAT.toUpperCase();
            if(upperEnvList.contains(envPro)) {
                result = envPro;
            } else if(upperEnvList.contains(envUat)) {
                result = envUat;
            } else if(upperEnvList.contains(envFat)) {
                result = envFat;
            }
        }
        return result;
    }

    //get string value from jsonElement
    public static String getStringValue(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? null : jsonElement.getAsString();
    }
    //get int value from jsonElement
    public static Integer getIntegerValue(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? null : jsonElement.getAsInt();
    }
    //get long value from jsonElement
    public static Long getLongValue(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? null : jsonElement.getAsLong();
    }

    // Idempotent check, input parm is list string splited by comma(,)
    public static boolean idempotentCheck(String inputA, String inputB) {
        boolean result = true;
        if(!Strings.isNullOrEmpty(inputA) && !Strings.isNullOrEmpty(inputB)) {
            if(!inputA.equals(inputB)) {
                Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
                List<String> aList = splitter.splitToList(inputA);
                Set<String> aSet = Sets.newHashSet(aList);
                List<String> bList = splitter.splitToList(inputB);
                Set<String> bSet = Sets.newHashSet(bList);
                if(!aSet.containsAll(bSet) || !bSet.containsAll(aSet)) {
                    result = false;
                }
            }
        } else if(Strings.isNullOrEmpty(inputA) && Strings.isNullOrEmpty(inputB)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }


}
