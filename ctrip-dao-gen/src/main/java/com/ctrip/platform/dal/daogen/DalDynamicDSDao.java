package com.ctrip.platform.dal.daogen;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DynamicDS.CatSwitchDSDataProvider;
import com.ctrip.platform.dal.daogen.DynamicDS.DynamicDSAppIDProvider;
import com.ctrip.platform.dal.daogen.DynamicDS.SwitchDSDataProvider;
import com.ctrip.platform.dal.daogen.DynamicDS.TitanDynamicDSAppIDProvider;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by taochen on 2019/7/2.
 */
public class DalDynamicDSDao {
    private static final long FIXED_RATE = 60 * 60; //second

    private static final int RETRY_TIME = 3;

    private static final int STAY_TIME = 4;

    private static final String TITANKEY_APPID = "100010061";

    private static final String TITAN_KEY_GET =
            "http://qconfig.ctripcorp.com/plugins/titan/config?appid=%s&titankey=%s&env=%s";

    private static DalDynamicDSDao dynamicDSDao = null;

    private ScheduledExecutorService executor = null;

    private Map<SwitchTitanKey, List<AppIDInfo>> TitanKeyAppIDMap = new HashMap<>();

    private DynamicDSAppIDProvider dynamicDSAppIDProvider = new TitanDynamicDSAppIDProvider();

    private SwitchDSDataProvider catSwitchDSDataProvider = new CatSwitchDSDataProvider();

    //初始化guava cache
    private final LoadingCache<String, Map<SwitchTitanKey, List<AppIDInfo>>> titanKeySwitchCache = CacheBuilder.newBuilder()
            .expireAfterWrite(STAY_TIME, TimeUnit.DAYS).build(new CacheLoader<String, Map<SwitchTitanKey, List<AppIDInfo>>>() {
                @Override
                public Map<SwitchTitanKey, List<AppIDInfo>> load(String timeString) throws Exception {
                    checkSwitchDataSource(timeString, null, TriggerMethod.MANUAL);
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
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String checkTime = getBeforeOneHourDateString(new Date());
                checkSwitchDataSource(checkTime, null, TriggerMethod.AUTO);
            }
        }, initDelay, FIXED_RATE, TimeUnit.SECONDS);
    }

    public void checkSwitchDataSource(String checkTime, Set<String> checkTitanKeys, TriggerMethod method) {
        TitanKeyAppIDMap.clear();
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        Set<SwitchTitanKey> TitanKeys = catSwitchDSDataProvider.getSwitchTitanKey(checkTime, checkTitanKeys, env);
        if (TitanKeys == null || TitanKeys.size() == 0) {
            return;
        }
        StringBuilder switchTitanKeys = new StringBuilder();
        for (SwitchTitanKey switchTitanKey : TitanKeys) {
            switchTitanKeys.append(switchTitanKey).append(",");
        }
        Cat.logEvent("TitanKeySwitch", switchTitanKeys.toString());
        initSwitchTitanKeyAndAppID(TitanKeys, env, checkTime, method);
    }

    private synchronized void initSwitchTitanKeyAndAppID(Set<SwitchTitanKey> TitanKeys, String env, String checkTime, TriggerMethod method) {
        Map<String, List<AppIDInfo>> TitanKeyStringAppIDMap = new HashMap<>();
        List<SwitchTitanKey> switchTitanKeyList = getTitanKeyInfo(TitanKeys, env);
        Transaction t = Cat.newTransaction("DynamicDataSource", "catAPI_dal");
        try {
            for (SwitchTitanKey switchTitanKey : switchTitanKeyList) {
                List<String> appIDList = dynamicDSAppIDProvider.getDynamicDSAppID(switchTitanKey.getPermissions());
                List<AppIDInfo> switchAppIDList = new ArrayList<>();
                int i = 0;
                for (String appID : appIDList) {
                    List<SwitchHostIPInfo> hostIPList = new ArrayList<>();
                    boolean isSwitch = catSwitchDSDataProvider.isSwitchInAppID(switchTitanKey.getTitanKey(), appID, checkTime, hostIPList, env);
                    //cat 限流策略
                    Thread.sleep(600);
                    if (isSwitch) {
                        AppIDInfo appIDInfo = new AppIDInfo();
                        appIDInfo.setAppID(appID);
                        appIDInfo.setHostIPInfolist(hostIPList);
                        switchAppIDList.add(appIDInfo);
                    }
                }
                TitanKeyStringAppIDMap.put(switchTitanKey.getTitanKey(), switchAppIDList);
                Cat.logEvent("SwitchTitanKey", switchTitanKey.getTitanKey());
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
                titanKeySwitchCache.put(checkTime, tempTitanKeyAppIDMap);
            }
        } catch (Exception e) {
            Cat.logError("get titanKey config failed from qconfig.", e);
            t.setStatus(e);
        } finally {
            t.complete();
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

    // one hour delay
    private long getFixInitDelay(Date checkTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkTime);
        calendar.add(Calendar.HOUR, 1);
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
        calendar.add(Calendar.HOUR, -1);
        Date catTransactionDate = calendar.getTime();
        return formatCheckTime(catTransactionDate);
    }

    private String formatCheckTime(Date convertCheckTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(convertCheckTime).replaceAll("-| ", "");
        return dateString.substring(0, dateString.indexOf(":"));
    }

    public String getNowDateString(Date checkTime) {
        return formatCheckTime(checkTime);
    }
}
