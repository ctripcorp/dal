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

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by taochen on 2019/7/2.
 */
public class DalDynamicDSDao {
    private static final long FIXED_RATE = 60 * 60; //second

    private static final int RETRY_TIME = 3;

    private static final String TITANKEY_APPID = "100010061";

    private static final String TITAN_KEY_GET =
            "http://qconfig.ctripcorp.com/plugins/titan/config?appid=%s&titankey=%s&env=%s";

    private static DalDynamicDSDao dynamicDSDao = null;

    private Map<String, List<AppIDInfo>> TitanKeyAppIDMap = new HashMap<>();

    private DynamicDSAppIDProvider dynamicDSAppIDProvider = new TitanDynamicDSAppIDProvider();

    private SwitchDSDataProvider catSwitchDSDataProvider = new CatSwitchDSDataProvider();

    public static DalDynamicDSDao getInstance() {
        if (dynamicDSDao == null) {
            dynamicDSDao = new DalDynamicDSDao();
        }
        return dynamicDSDao;
    }

    public void init() {
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        //init_delay 设置为每个小时的59分执行
        Date checkTime = new Date();
        long initDelay = getFixInitDelay(checkTime);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                checkSwitchDataSource(env, checkTime, TriggerMethod.TIME);
            }
        }, initDelay, FIXED_RATE, TimeUnit.SECONDS);
    }

    public void checkSwitchDataSource(String env, Date checkTime, TriggerMethod method) {
        Set<String> TitanKeys = catSwitchDSDataProvider.getSwitchTitanKey(checkTime);
        if (TitanKeys == null || TitanKeys.size() == 0) {
            return;
        }
        try {
            initSwitchTitanKeyAndAppID(TitanKeys, env, checkTime, method);

        } catch (Exception e) {
            Cat.logError("check switch datasource fail in fix delay.", e);
        }
    }

    private synchronized void initSwitchTitanKeyAndAppID(Set<String> TitanKeys, String env, Date checkTime, TriggerMethod method) {
        List<TitanKeyInfo> TitanKeyInfoList = null;
        try {
            TitanKeyInfoList = getTitanKeyInfo(TitanKeys, env);
            for (TitanKeyInfo titanKeyInfo : TitanKeyInfoList) {
                List<String> appIDList = dynamicDSAppIDProvider.getDynamicDSAppID(titanKeyInfo);
                List<AppIDInfo> switchAppIDList = new ArrayList<>();
                for (String appID : appIDList) {
                    List<SwitchHostIPInfo> hostIPList = new ArrayList<>();
                    boolean isSwitch = catSwitchDSDataProvider.isSwitchInAppID(appID, checkTime, hostIPList);
                    if (isSwitch) {
                        AppIDInfo appIDInfo = new AppIDInfo();
                        appIDInfo.setAppID(appID);
                        appIDInfo.setHostIPInfolist(hostIPList);
                        switchAppIDList.add(appIDInfo);
                    }
                }
                if (TriggerMethod.MANUAL.equals(method)) {
                    TitanKeyAppIDMap.put(titanKeyInfo.getKeyName(), switchAppIDList);
                }
            }
        } catch (Exception e) {
            Cat.logError("get titanKey config failed from qconfig.", e);
        }
    }

    private List<TitanKeyInfo> getTitanKeyInfo(Set<String> TitanKeys, String env) throws Exception {
        List<TitanKeyInfo> TitanKeyInfoList = new ArrayList<>();
        for (String titanKey : TitanKeys) {
            String titanUrl = String.format(TITAN_KEY_GET, TITANKEY_APPID, titanKey, env);
            for (int i = 0; i < RETRY_TIME; ++i) {
                TitanResponse response = HttpUtil.getJSONEntity(TitanResponse.class, titanUrl, null, HttpMethod.HttpGet);
                if (response.getStatus() == 0) {
                    TitanKeyInfoList.add(response.getTitanKeyInfo());
                    break;
                }
            }
        }
        return TitanKeyInfoList;
    }

    private long getFixInitDelay(Date checkTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkTime);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        long initDelay = calendar.getTime().getTime()/1000 - checkTime.getTime()/1000;
        return initDelay;
    }

    public Map<String, List<AppIDInfo>> getTitanKeyAppIDMap() {
        return TitanKeyAppIDMap;
    }

    public void setTitanKeyAppIDMap(Map<String, List<AppIDInfo>> titanKeyAppIDMap) {
        TitanKeyAppIDMap = titanKeyAppIDMap;
    }
}
