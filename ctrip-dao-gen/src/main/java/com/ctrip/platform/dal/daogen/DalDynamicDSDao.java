package com.ctrip.platform.dal.daogen;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DynamicDS.*;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.ctrip.soa.platform.basesystem.emailservice.v1.EmailServiceClient;
import com.ctrip.soa.platform.basesystem.emailservice.v1.SendEmailRequest;
import com.ctrip.soa.platform.basesystem.emailservice.v1.SendEmailResponse;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by taochen on 2019/7/2.
 */
public class DalDynamicDSDao {
    private static final long FIXED_RATE = 3600; //second

    private static final int RETRY_TIME = 3;

    private static final int STAY_TIME = 4;

    private static final int LRU_CACHE_SIZE = 20;

    private static final String TITANKEY_APPID = "100010061";

    private static final String TITAN_KEY_GET =
            "http://qconfig.ctripcorp.com/plugins/titan/config?appid=%s&titankey=%s&env=%s";

    private static final String RECEIVE_EMAIL = "rdkjdal@Ctrip.com";

    private static final String APP_PROPERTIES_CLASSPATH = "/META-INF/app.properties";

    private static final String CMS_APPID_IP = "http://osg.ops.ctripcorp.com/api/CMSGetApp/?_version=new";

    private static final String CMS_APPID_IP_FAT = "http://osg.ops.ctripcorp.com/api/CMSFATGetApp/?_version=new";

    private static final String CMS_APPID_IP_UAT = "http://osg.ops.ctripcorp.com/api/CMSUATGetApp/?_version=new";

    private static final String ACCESS_TOKEN = "96ddbe67728bc756466a226ec050456d";
    private static final String RETURN_FIELDS_ATTRIBUTE = "_returnFields";
    private static final String RESULT_BRANCH_ATTRIBUTE = "_resultBranch";
    private static final String RESULT_DEPTH_ATTRIBUTE = "_resultDepth";
    private static final String APP_IN_ATTRIBUTE = "appId@in";
    private static final String APP_ID = "appId";
    private static final String GROUP = "group";
    private static final String GROUPS = "groups";

    private static DalDynamicDSDao dynamicDSDao = null;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

//    private Map<SwitchTitanKey, List<AppIDInfo>> TitanKeyAppIDMap = new HashMap<>();

    private DynamicDSAppIDProvider dynamicDSAppIDProvider = new TitanDynamicDSAppIDProvider();

    private SwitchDSDataProvider catSwitchDSDataProvider = new CatSwitchDSDataProvider();

    private AtomicBoolean isRunning = new AtomicBoolean(false);

   // private boolean isRunning = false;

    private int statisticProgress = 0;

    private int statisticTitanKeyCount = 0;

    private String statisticTime = "";

    private Map<String, Integer> checkTimeSwitchCountMap = Collections.synchronizedMap(new LRUCache<>(LRU_CACHE_SIZE));

    //初始化guava cache
    private final LoadingCache<String, Map<SwitchTitanKey, List<AppIDInfo>>> titanKeySwitchCache = CacheBuilder.newBuilder()
            .expireAfterAccess(STAY_TIME, TimeUnit.DAYS).build(new CacheLoader<String, Map<SwitchTitanKey, List<AppIDInfo>>>() {
                @Override
                public Map<SwitchTitanKey, List<AppIDInfo>> load(String timeString) throws Exception {
                    Map<SwitchTitanKey, List<AppIDInfo>> TitanKeyAppIDMap = new HashMap<>();
                    checkSwitchDataSource(timeString, null, TitanKeyAppIDMap, TriggerMethod.MANUAL);
                    return TitanKeyAppIDMap;
                }
            });

    public static DalDynamicDSDao getInstance() {
        if (dynamicDSDao == null) {
            dynamicDSDao = new DalDynamicDSDao();
        }
        return dynamicDSDao;
    }

    public void init() {
        //init_delay 设置为每个小时的整点执行
        Date nowDate = new Date();
        long initDelay = getFixInitDelay(nowDate);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Date checkDate = new Date();
                String checkTime = getBeforeOneHourDateString(checkDate);
                if (checkIsSendEMailTime(checkDate)) {
                    List<TitanKeySwitchInfoDB> switchDataList = getSwitchDataInRange(checkDate, CheckTimeRange.ONE_WEEK);
                    sendEmail(switchDataList, checkDate, CheckTimeRange.ONE_WEEK);
                }
                Cat.logEvent("DynamicDSFixJob", checkTime);
                checkSwitchDataSource(checkTime, null, null, TriggerMethod.AUTO);
            }
        }, initDelay, FIXED_RATE, TimeUnit.SECONDS);
    }

    public void checkSwitchDataSource(String checkTime, Set<String> checkTitanKeys, Map<SwitchTitanKey, List<AppIDInfo>> TitanKeyAppIDMap, TriggerMethod method) {
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        Set<SwitchTitanKey> TitanKeys = catSwitchDSDataProvider.getSwitchTitanKey(checkTime, checkTitanKeys, env);
        if (TitanKeys == null || TitanKeys.size() == 0) {
            return;
        }
        StringBuilder switchTitanKeys = new StringBuilder();
        for (SwitchTitanKey switchTitanKey : TitanKeys) {
            switchTitanKeys.append(switchTitanKey.getTitanKey()).append(",");
        }
        Cat.logEvent("TitanKeySwitch", switchTitanKeys.toString());

        if (isRunning.compareAndSet(false, true)) {
            initSwitchTitanKeyAndAppID(TitanKeys, env, checkTime, TitanKeyAppIDMap, method);
        }
        else if (!statisticTime.equalsIgnoreCase(checkTime)){
            checkTimeSwitchCountMap.put(checkTime, TitanKeys.size());
        }
    }

    private void initSwitchTitanKeyAndAppID(Set<SwitchTitanKey> TitanKeys, String env, String checkTime, Map<SwitchTitanKey, List<AppIDInfo>> TitanKeyAppIDMap, TriggerMethod method) {
        statisticTime = checkTime;
        Map<String, List<AppIDInfo>> TitanKeyStringAppIDMap = new HashMap<>();
        List<SwitchTitanKey> switchTitanKeyList = getTitanKeyInfo(TitanKeys, env);
        statisticTitanKeyCount = switchTitanKeyList.size();
        Transaction t = Cat.newTransaction("DynamicDataSource", "catAPI_dal");
        try {
            Cat.logEvent("SwitchTitanKeyCount", String.valueOf(switchTitanKeyList.size()));
            //System.out.println("SwitchTitanKeyCount:" + switchTitanKeyList.size());
            int i = 0;
            for (SwitchTitanKey switchTitanKey : switchTitanKeyList) {
                List<String> appIDList = dynamicDSAppIDProvider.getDynamicDSAppID(switchTitanKey.getPermissions());
                List<AppIDInfo> switchAppIDList = getBatchAppIdIp(appIDList, env);
                ++i;
//                long startTimeCatAppID = System.currentTimeMillis();
//                for (String appID : appIDList) {
//                    //List<SwitchHostIPInfo> hostIPList = new ArrayList<>();
//                    long startTimeCatHostIPs = System.currentTimeMillis();
//                    AppIDInfo appIDInfo = catSwitchDSDataProvider.checkSwitchInAppID(switchTitanKey.getTitanKey(), checkTime, appID, env);
//                    if (appIDInfo != null) {
//                        switchAppIDList.add(appIDInfo);
//                    }
//                }
//                long endTimeCatAppID = System.currentTimeMillis();

                statisticProgress = i;
                //System.out.println("appIDs time: " + (endTimeCatAppID - startTimeCatAppID));
                //System.out.println(" cat api time: " + (endTimeCatHostIPs - startTimeCatHostIPs) + "ms. appid count: " +  appIDList.size());
                TitanKeyStringAppIDMap.put(switchTitanKey.getTitanKey(), switchAppIDList);
                Cat.logEvent("SwitchTitanKey.Statistic", "index: " + i + " name: " + switchTitanKey.getTitanKey());
                //System.out.println("index: " + i + " name: " + switchTitanKey.getTitanKey());
            }
            t.setStatus(Transaction.SUCCESS);
            if (TriggerMethod.MANUAL.equals(method)) {
                for (SwitchTitanKey switchTitanKey : switchTitanKeyList) {
                    TitanKeyAppIDMap.put(switchTitanKey, TitanKeyStringAppIDMap.get(switchTitanKey.getTitanKey()));
                }
            }
            else {
                Map<SwitchTitanKey, List<AppIDInfo>> tempTitanKeyAppIDMap = new HashMap<>();
                for (SwitchTitanKey switchTitanKey : switchTitanKeyList) {
                    tempTitanKeyAppIDMap.put(switchTitanKey, TitanKeyStringAppIDMap.get(switchTitanKey.getTitanKey()));
                }
                storeToDB(tempTitanKeyAppIDMap, checkTime);
                titanKeySwitchCache.put(checkTime, tempTitanKeyAppIDMap);
            }
        } catch (Exception e) {
            Cat.logError("get titanKey config failed from qconfig.", e);
            t.setStatus(e);
        } finally {
            t.complete();
            isRunning.compareAndSet(true, false);
            statisticTime = "";
            List<String> keys = new ArrayList<>();
            for (String key : checkTimeSwitchCountMap.keySet()) {
                keys.add(key);
            }
            titanKeySwitchCache.invalidateAll(keys);
            checkTimeSwitchCountMap.clear();
        }
    }

    private List<SwitchTitanKey> getTitanKeyInfo(Set<SwitchTitanKey> TitanKeys, String env) {
        List<SwitchTitanKey> TitanKeyInfoList = new ArrayList<>();
        for (SwitchTitanKey titanKey : TitanKeys) {
            String titanUrl = String.format(TITAN_KEY_GET, TITANKEY_APPID, titanKey.getTitanKey(), env);
            for (int i = 0; i < RETRY_TIME; ++i) {
                TitanResponse response = null;
                try {
                    response =  HttpUtil.getJSONEntity(TitanResponse.class, titanUrl, null, HttpMethod.HttpGet);
                    if (response.getStatus() == 0 && response.getData() != null) {
                        titanKey.setPermissions(response.getData().getPermissions());
                        TitanKeyInfoList.add(titanKey);
                        break;
                    }
                } catch (Exception e) {
                    Cat.logError("call titanUrl: " + titanUrl + " fail!", e);
                }
            }
        }
        return TitanKeyInfoList;
    }

    private void storeToDB(Map<SwitchTitanKey, List<AppIDInfo>> switchDatas, String checkTime) {
        List<TitanKeySwitchInfoDB> titanKeySwitchInfoList = new ArrayList<>();
        for (Map.Entry<SwitchTitanKey, List<AppIDInfo>> switchData : switchDatas.entrySet()) {
            TitanKeySwitchInfoDB titanKeySwitchInfoDB = new TitanKeySwitchInfoDB();
            titanKeySwitchInfoDB.setTitanKey(switchData.getKey().getTitanKey());
            //统计titankey切换次数
            int titanKeySwitchCount = 0;
            for (int value : switchData.getKey().getSwitchCount().values()) {
                titanKeySwitchCount += value;
            }
            titanKeySwitchInfoDB.setSwitchCount(titanKeySwitchCount);
            titanKeySwitchInfoDB.setAppIDCount(switchData.getValue().size());
            //统计ip数量
            int ipCount = 0;
            for (AppIDInfo appIDInfo : switchData.getValue()) {
                ipCount += appIDInfo.getHostIPInfolist().size();
            }
            titanKeySwitchInfoDB.setIpCount(ipCount);
            titanKeySwitchInfoDB.setCheckTime(Integer.valueOf(checkTime));
            titanKeySwitchInfoList.add(titanKeySwitchInfoDB);
        }
        DalDynamicDSDBDao dalDynamicDSDBDao = DalDynamicDSDBDao.getInstance();
        dalDynamicDSDBDao.batchInsertSwitchData(titanKeySwitchInfoList);
    }

    public List<TitanKeySwitchInfoDB> getSwitchDataInRange(Date nextWeekDate, CheckTimeRange checkTimeRange) {
        String startCheckTime = null;
        String endCheckTime = null;
        DalDynamicDSDBDao dalDynamicDSDBDao = DalDynamicDSDBDao.getInstance();
        if (CheckTimeRange.ONE_WEEK.equals(checkTimeRange)) {
            startCheckTime = getStartOneWeek(nextWeekDate);
            endCheckTime = getEndOneWeek(nextWeekDate);
        }
        return mergeSwitchData(dalDynamicDSDBDao.queryInRange(startCheckTime, endCheckTime));
    }

    public List<TitanKeySwitchInfoDB> mergeSwitchData(List<TitanKeySwitchInfoDB> switchDataList) {
        Map<String, TitanKeySwitchInfoDB> helpMap = new HashMap<>();
        List<TitanKeySwitchInfoDB> result = new ArrayList<>();
        for (TitanKeySwitchInfoDB titanKeySwitchInfoDB : switchDataList) {
            String titanKey = titanKeySwitchInfoDB.getTitanKey();
            if (helpMap.containsKey(titanKey)) {
                TitanKeySwitchInfoDB existTitanKeySwitchInfoDB = helpMap.get(titanKey);
                int switchCount = existTitanKeySwitchInfoDB.getSwitchCount();
                int appIDCount = existTitanKeySwitchInfoDB.getAppIDCount();
                int ipCount = existTitanKeySwitchInfoDB.getIpCount();
                existTitanKeySwitchInfoDB.setSwitchCount(switchCount + titanKeySwitchInfoDB.getSwitchCount());
                existTitanKeySwitchInfoDB.setAppIDCount(appIDCount + titanKeySwitchInfoDB.getAppIDCount());
                existTitanKeySwitchInfoDB.setIpCount(ipCount + titanKeySwitchInfoDB.getIpCount());
                helpMap.put(titanKey, existTitanKeySwitchInfoDB);
            }
            else {
                helpMap.put(titanKeySwitchInfoDB.getTitanKey(), titanKeySwitchInfoDB);
            }
        }
        for (TitanKeySwitchInfoDB titanKeySwitchInfoDB : helpMap.values()) {
            result.add(titanKeySwitchInfoDB);
        }
        return result;
    }

    // one hour delay
    private long getFixInitDelay(Date checkTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkTime);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime()/1000 - checkTime.getTime()/1000;
    }

    public Map<SwitchTitanKey, List<AppIDInfo>> getTitanKeyAppIDMap(String checkTime) {
        try {
            return titanKeySwitchCache.get(checkTime);
        } catch (ExecutionException e) {
            Cat.logError("get titanKey switch info error, key: " + checkTime, e);
        }
        return null;
    }

    private String getBeforeOneHourDateString(Date checkTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkTime);
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date catTransactionDate = calendar.getTime();
        return formatCheckTime(catTransactionDate);
    }

    public String formatCheckTime(Date convertCheckTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(convertCheckTime).replaceAll("-| ", "");
        return dateString.substring(0, dateString.indexOf(":"));
    }

    private String getStartOneWeek(Date nextWeekDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nextWeekDate);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return formatCheckTime(calendar.getTime());
    }

    private String getEndOneWeek(Date nextWeekDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nextWeekDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        return formatCheckTime(calendar.getTime());
    }

    private boolean checkIsSendEMailTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //每周一上午9点发邮件
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY &&
                calendar.get(Calendar.HOUR_OF_DAY) == 9) {
            return true;
        }
        return false;
    }

    public void sendEmail(List<TitanKeySwitchInfoDB> switchDataList, Date checkDate, CheckTimeRange checkTimeRange) {
        String startCheckTime = null;
        String endCheckTime = null;
        if (CheckTimeRange.ONE_WEEK.equals(checkTimeRange)) {
            startCheckTime = getStartOneWeek(checkDate);
            endCheckTime = getEndOneWeek(checkDate);
        }

        EmailServiceClient client = EmailServiceClient.getInstance();
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setAppID(getLocalAppID());
        sendEmailRequest.setBodyTemplateID(28030004);
        sendEmailRequest.setCharset("GB2312");
        sendEmailRequest.setIsBodyHtml(true);
        sendEmailRequest.setOrderID(0);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,2);
        sendEmailRequest.setExpiredTime(calendar);
        sendEmailRequest.setSendCode("28030004");
        sendEmailRequest.setSender("taochen@ctrip.com");
        sendEmailRequest.setSubject(String.format("%s-%s动态数据源切换统计",startCheckTime, endCheckTime));
        sendEmailRequest.setBodyContent(generateBodyContent(switchDataList));
        List<String> recipient = new ArrayList<>();
        recipient.add(RECEIVE_EMAIL);
        sendEmailRequest.setRecipient(recipient);
        try {
            SendEmailResponse response = client.sendEmail(sendEmailRequest);
            if (response != null && response.getResultCode() == 1) {
                return;
            }
            else {
                throw new Exception();
            }
        }catch (Exception e) {
            Cat.logError("send email fail, time:" + checkDate.toString(), e);
        }
    }

    public List<AppIDInfo> getBatchAppIdIp(List<String> appIds, String env) {
        String formatUrl = "FAT".equalsIgnoreCase(env) ? CMS_APPID_IP_FAT : "UAT".equalsIgnoreCase(env) ?
                CMS_APPID_IP_UAT : CMS_APPID_IP;
        List<AppIDInfo> switchAppIDList = new ArrayList<>();
        Transaction t = Cat.newTransaction("SwitchTitanKey", "getBatchAppIdIp");
        Map<String, Object> queries = Maps.newHashMap();
        queries.put(APP_IN_ATTRIBUTE, appIds);
        queries.put(RESULT_BRANCH_ATTRIBUTE, GROUP);
        queries.put(RESULT_DEPTH_ATTRIBUTE, 1);
        queries.put(RETURN_FIELDS_ATTRIBUTE, Lists.newArrayList(APP_ID, GROUPS));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("access_token", ACCESS_TOKEN);
        parameters.put("request_body", JSON.toJSONString(queries));
        try {
            AppIpGetResponse response = HttpUtil.getJSONEntity(AppIpGetResponse.class, formatUrl, parameters, HttpMethod.HttpPost);
            Boolean status = response.getStatus();
            List<App> apps = response.getData();
            if (status == null || apps == null || apps.isEmpty()) {
                return switchAppIDList;
            }
            if (status) {
                for (App app : apps) {
                    AppIDInfo appIDInfo = new AppIDInfo();
                    String appId = app.getAppId();
                    appIDInfo.setAppID(appId);
                    List<String> hostIP = new ArrayList<>();
                    List<Group> groups = app.getGroups();
                    if (groups != null && !groups.isEmpty()) {
                        for (Group group : groups) {
                            List<GroupMember> groupMembers = group.getAccessGroupMembers();
                            if (groupMembers != null && !groupMembers.isEmpty()) {
                                for (GroupMember groupMember : groupMembers) {
                                    String ip = groupMember.getIp();
                                    if (StringUtils.isNotBlank(ip)) {
                                        hostIP.add(ip);
                                    }
                                }
                            }
                        }
                    }
                    appIDInfo.setHostIPInfolist(hostIP);
                    switchAppIDList.add(appIDInfo);
                }
            }
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            Cat.logError("get app id ip fail!", e);
            t.setStatus(e);
        } finally {
            t.complete();
        }
        return switchAppIDList;
    }

    private String generateBodyContent(List<TitanKeySwitchInfoDB> switchDataList) {
        String htmlTemplate = "<entry><content><![CDATA[%s]]></content></entry>";
        String htmlTable = " <table style=\"border-collapse:collapse\"><thead><tr><th style=\"border:1px solid #B0B0B0\" width= \"80\">ID</th><th style=\"border:1px solid #B0B0B0\" width= \"200\">TitanKey</th>" +
                "<th style=\"border:1px solid #B0B0B0\" width= \"120\">SwitchCount</th><th style=\"border:1px solid #B0B0B0\" width= \"120\">AppId Count</th><th style=\"border:1px solid #B0B0B0\" width= \"120\">IP Count</th></tr></thead><tbody>%s</tbody></table>";
        String bodyTemplate = "<tr><td style=\"border:1px solid #B0B0B0\">%s</td><td style=\"border:1px solid #B0B0B0\">%s</td><td style=\"border:1px solid #B0B0B0\">%s</td>" +
                "<td style=\"border:1px solid #B0B0B0\">%s</td><td style=\"border:1px solid #B0B0B0\">%s</td></tr>";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (TitanKeySwitchInfoDB switchData : switchDataList) {
            ++i;
            sb.append(String.format(bodyTemplate, i, switchData.getTitanKey(), switchData.getSwitchCount(), switchData.getAppIDCount(), switchData.getIpCount()));
        }
        return String.format(htmlTemplate, String.format(htmlTable, sb.toString()));
    }

    private int getLocalAppID() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_CLASSPATH);
        Properties m_appProperties = new Properties();
        if (in == null) {
            in = DalDynamicDSDao.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
        }
        try {
            m_appProperties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            Cat.logError("get local appID fail!", e);
        }
        return Integer.valueOf(m_appProperties.getProperty("app.id"));
    }

    public String getNowDateString(Date checkTime) {
        return formatCheckTime(checkTime);
    }

    public int getStatisticProgress() {
        return statisticProgress;
    }

    public int getStatisticTitanKeyCount() {
        return statisticTitanKeyCount;
    }

    public String getStatisticTime() {
        return statisticTime;
    }

    public Map<String, Integer> getCheckTimeSwitchCountMap() {
        return checkTimeSwitchCountMap;
    }

    public void removeLoadingCache(String key) {
        titanKeySwitchCache.invalidate(key);
    }
}
