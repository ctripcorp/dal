package com.ctrip.platform.dal.daogen;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DynamicDS.*;
import com.ctrip.platform.dal.daogen.config.MonitorConfigManager;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.util.DateUtils;
import com.ctrip.platform.dal.daogen.util.EmailUtils;
import com.ctrip.platform.dal.daogen.util.IPUtils;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.ctrip.platform.dal.daogen.utils.JsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private DalDynamicDSDao() { }

    public void init() {
        //init_delay 设置为每个小时的整点执行
        Date nowDate = new Date();
        long initDelay = DateUtils.getFixInitDelay(nowDate);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Date checkDate = new Date();
                String checkTime = DateUtils.getBeforeOneHourDateString(checkDate);
                notifyByEmail(checkDate);
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
                String ip = IPUtils.getExecuteIPFromQConfig();
                if (IPUtils.getLocalHostIp().equalsIgnoreCase(ip)) {
                    storeToDB(tempTitanKeyAppIDMap, checkTime);
                }
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
        String[] filterTitanKeyArray = MonitorConfigManager.getMonitorConfig().getFilterTitanKey().split(",");
        String filterTemplate = generateRegExp(filterTitanKeyArray);
        Pattern pattern = Pattern.compile(filterTemplate);
        for (SwitchTitanKey titanKey : TitanKeys) {
            Matcher matcher = pattern.matcher(titanKey.getTitanKey());
            if (matcher.matches()) {
                continue;
            }
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

    public List<TitanKeySwitchInfoDB> getSwitchDataInRange(String startCheckTime, String endCheckTime) {
        DalDynamicDSDBDao dalDynamicDSDBDao = DalDynamicDSDBDao.getInstance();
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
                existTitanKeySwitchInfoDB.setSwitchCount(switchCount + titanKeySwitchInfoDB.getSwitchCount());
                if (existTitanKeySwitchInfoDB.getCheckTime() < titanKeySwitchInfoDB.getCheckTime()) {
                    existTitanKeySwitchInfoDB.setAppIDCount(titanKeySwitchInfoDB.getAppIDCount());
                    existTitanKeySwitchInfoDB.setIpCount(titanKeySwitchInfoDB.getIpCount());
                }
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

    public Map<SwitchTitanKey, List<AppIDInfo>> getTitanKeyAppIDMap(String checkTime) {
        try {
            return titanKeySwitchCache.get(checkTime);
        } catch (ExecutionException e) {
            Cat.logError("get titanKey switch info error, key: " + checkTime, e);
        }
        return null;
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
        parameters.put("request_body", JsonUtils.toJson(queries));
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

    public void notifyByEmail(Date checkDate) {
        if (DateUtils.checkIsSendEMailTime(checkDate)) {
            String startCheckTime = DateUtils.getStartOneWeek(checkDate);
            String endCheckTime = DateUtils.getEndOneWeek(checkDate);
            List<TitanKeySwitchInfoDB> switchDataList = getSwitchDataInRange(startCheckTime, endCheckTime);
            String subject = String.format("动态数据源切换统计(%s-%s)",startCheckTime.substring(0,8), endCheckTime.substring(0,8));
            EmailUtils.sendEmail(generateBodyContent(switchDataList), subject, MonitorConfigManager.getMonitorConfig().getSwitchEmailRecipient(),
                    MonitorConfigManager.getMonitorConfig().getSwitchEmailCc(), null);
        }
    }

    public String generateBodyContent(List<TitanKeySwitchInfoDB> switchDataList) {
        String htmlTemplate = "<entry><content><![CDATA[%s]]></content></entry>";
        String htmlTable = " <table style=\"border-collapse:collapse\"><thead><tr><th style=\"border:1px solid #B0B0B0\" width= \"80\">序号</th><th style=\"border:1px solid #B0B0B0\" width= \"200\">TitanKey</th>" +
                "<th style=\"border:1px solid #B0B0B0\" width= \"120\">TitanKey切换次数</th><th style=\"border:1px solid #B0B0B0\" width= \"120\">客户端AppId数量</th><th style=\"border:1px solid #B0B0B0\" width= \"120\">客户端IP总数</th>" +
                "<th style=\"border:1px solid #B0B0B0\" width= \"120\">客户端切换总次数</th></tr></thead><tbody>%s</tbody></table>";
        String bodyTemplate = "<tr><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td>" +
                "<td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td></tr>";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int titanKeySwitchCountSum = 0;
        int clientSwitchCountSum = 0;
        for (TitanKeySwitchInfoDB switchData : switchDataList) {
            titanKeySwitchCountSum += switchData.getSwitchCount();
            clientSwitchCountSum += switchData.getSwitchCount() * switchData.getIpCount();
        }
        sb.append(String.format(bodyTemplate, "总数", switchDataList.size(), titanKeySwitchCountSum, "", "", clientSwitchCountSum));
        for (TitanKeySwitchInfoDB switchData : switchDataList) {
            ++i;
            sb.append(String.format(bodyTemplate, i, switchData.getTitanKey(), switchData.getSwitchCount(), switchData.getAppIDCount(), switchData.getIpCount(), switchData.getSwitchCount() * switchData.getIpCount()));
        }
        return String.format(htmlTemplate, String.format(htmlTable, sb.toString()));
    }

    public String generateRegExp(String[] filterTitanKeyArray) {
        StringBuilder sb = new StringBuilder();
        for (String filterTitanKey : filterTitanKeyArray) {
            sb.append("(").append(filterTitanKey).append(")|");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String getNowDateString(Date checkTime) {
        return DateUtils.formatCheckTime(checkTime);
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
