package com.ctrip.platform.dal.daogen.DynamicDS;

import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.ctrip.platform.dal.daogen.utils.JsonUtils;
import com.dianping.cat.Cat;
import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * Created by taochen on 2019/7/3.
 */
public class CatSwitchDSDataProvider implements SwitchDSDataProvider {
    private static final String CAT_TRANSACTION_URL_PRO =
            "http://cat.ctripcorp.com/cat/r/t?domain=%s&date=%s&ip=%s&type=%s&min=-1&max=-1&name=%s&forceDownload=json";

    private static final String CAT_EVENT_TITAN_UPDATE_PRO =
            "http://cat.ctripcorp.com/cat/r/e?ip=All&domain=100005701&date=%s&type=%s&min=-1&max=-1&op=graphs&forceDownload=json";

    private static final String CAT_TRANSACTION_URL_UAT =
            "http://cat.uat.qa.nt.ctripcorp.com/cat/r/t?domain=%s&date=%s&ip=%s&type=%s&min=-1&max=-1&name=%s&forceDownload=json";

    private static final String CAT_EVENT_TITAN_UPDATE_UAT =
            "http://cat.uat.qa.nt.ctripcorp.com/cat/r/e?ip=All&domain=100005701&date=%s&type=%s&min=-1&max=-1&op=graphs&forceDownload=json";

    private static final String CAT_TRANSACTION_URL_FAT =
            "http://cat.fws.qa.nt.ctripcorp.com/cat/r/t?domain=%s&date=%s&ip=%s&type=%s&min=-1&max=-1&name=%s&forceDownload=json";
//    private static final String CAT_TRANSACTION_URL_FAT =
//        "http://cat.ctripcorp.com/cat/r/t?domain=%s&date=%s&ip=%s&type=%s&min=-1&max=-1&name=%s&forceDownload=json";

    private static final String CAT_EVENT_TITAN_UPDATE_FAT =
            "http://cat.fws.qa.nt.ctripcorp.com/cat/r/e?ip=All&domain=100005701&date=%s&type=%s&min=-1&max=-1&op=graphs&forceDownload=json";
//    private static final String CAT_EVENT_TITAN_UPDATE_FAT =
//        "http://cat.ctripcorp.com/cat/r/e?ip=All&domain=100005701&date=%s&type=%s&min=-1&max=-1&op=graphs&forceDownload=json";

    private static final String DAL_CONFIG_TRANSACTION_TYPE = "DAL.configure";

    private static final String DAL_CONFIG_TRANSACTION_DS_NAME = "DataSourceConfig::refreshDataSourceConfig:%s";

    private static final String DAL_DATASOURCE_TRANSACTION_TYPE = "DAL.dataSource";

    private static final String DAL_DATASOURCE_TRANSACTION_NAME = "DataSource::createDataSource:%s";

    private static final String DAL_TITAN_UPDATE_TYPE = "Titan.MHAUpdate.TitanKey.Success:%s";

    private static final String ALL_IP = "All";

    private static final int RETRY_TIME = 3;

    private RateLimiter rateLimiter = RateLimiter.create(20);

    @Override
    public AppIDInfo checkSwitchInAppID(String titanKey, String checkTime, String appID, String env) {
        List<String> ips = new ArrayList<>();
        Map<Integer, Integer> appIDSwitchTime = new HashMap<>();
        //long startTimeCatHostIPs = System.currentTimeMillis();
        boolean isAppSwitch = checkAppRefreshDataSourceTransaction(titanKey, appID, checkTime, ips, appIDSwitchTime, env);
        //long endTimeCatHostIPs = System.currentTimeMillis();
        //System.out.println("cat api time: " + (endTimeCatHostIPs - startTimeCatHostIPs));
//        if (isAppSwitch) {
//            for (String ip : ips) {
//                SwitchHostIPInfo switchHostIPInfo = checkIpRefreshDataSourceTransaction(titanKey, appID, ip, checkTime, env);
//                if (switchHostIPInfo != null) {
//                    hostIPList.add(switchHostIPInfo);
//                }
//            }
//        }
        AppIDInfo appIDInfo = null;
        if (isAppSwitch) {
            appIDInfo = new AppIDInfo();
            appIDInfo.setAppID(appID);
            appIDInfo.setHostIPInfolist(ips);
            appIDInfo.setAppIDSwitchTime(appIDSwitchTime);
            Map<Integer, Integer> appIDSuccessTime = getEndSwitchAppIDPoint(titanKey, appID, checkTime, env);
            appIDInfo.setAppIDSuccessTime(appIDSuccessTime);
        }
        return appIDInfo;
    }

    @Override
    public Set<SwitchTitanKey> getSwitchTitanKey(String checkTime, Set<String> checkTitanKeys, String env) {
        Set<SwitchTitanKey> titanKeys = new HashSet<>();
        String formatUrl = "FAT".equalsIgnoreCase(env) ? CAT_EVENT_TITAN_UPDATE_FAT : "UAT".equalsIgnoreCase(env) ?
                CAT_EVENT_TITAN_UPDATE_UAT : CAT_EVENT_TITAN_UPDATE_PRO;
        String url = String.format(formatUrl, checkTime, String.format(DAL_TITAN_UPDATE_TYPE, env));
        CatTransactionEntity catTransactionEntity = null;
        for (int i = 0; i < RETRY_TIME; i++) {
            try {
                catTransactionEntity = HttpUtil.getJSONEntity(CatTransactionEntity.class, url, null, HttpMethod.HttpGet);
                break;
            } catch (Exception e) {
                Cat.logError("get switch titan key fail.", e);
            }
        }
        CatTransactionReport configReport = null;
        if (catTransactionEntity != null) {
            configReport = catTransactionEntity.getReport();
        }
        Object types = null;
        if (configReport != null) {
            types = parseCatTransactionReportTypes(configReport, ALL_IP);
        }
        if (types == null) {
            return null;
        }
        Object namesObject = parseCatTransactionReportNames(configReport, ALL_IP, String.format(DAL_TITAN_UPDATE_TYPE, env));
        if (namesObject != null) {
            JsonObject namesJsonObject =JsonUtils.parseObject(namesObject.toString());
            for (Map.Entry<String, JsonElement> key : namesJsonObject.entrySet()) {
                if (ALL_IP.equalsIgnoreCase(key.getKey())) {
                    continue;
                }
                JsonObject switchTitanKeyObject = JsonUtils.parseObject(namesJsonObject.get(key.getKey()).toString());
                List<TransactionNameRange> ranges = Arrays.asList(JsonUtils.fromJson(switchTitanKeyObject.get("ranges").toString(), TransactionNameRange[].class));
                SwitchTitanKey switchTitanKey = new SwitchTitanKey();
                switchTitanKey.setTitanKey(key.getKey());
                Map<Integer, Integer> switchCount = new HashMap<>();
                for (TransactionNameRange range : ranges) {
                    if (range.getCount() != 0) {
                        switchCount.put(range.getValue(), range.getCount());
                    }
                }
                switchTitanKey.setSwitchCount(switchCount);
                if (checkTitanKeys != null) {
                    if (checkTitanKeys.contains(key.getKey())) {
                        titanKeys.add(switchTitanKey);
                    }
                }
                else {
                    titanKeys.add(switchTitanKey);
                }
            }
            return titanKeys;
        }
        return null;
    }

    @Override
    public TransactionSimple getTransactionSimpleByMessageId(String appID, String ip, long hour, int index) {
        return null;
    }

    public boolean checkAppRefreshDataSourceTransaction(String titanKey, String appID, String date, List<String> ips, Map<Integer, Integer> appIDSwitchTime, String env) {
        String formatUrl = "FAT".equalsIgnoreCase(env) ? CAT_TRANSACTION_URL_FAT : "UAT".equalsIgnoreCase(env) ?
                CAT_TRANSACTION_URL_UAT : CAT_TRANSACTION_URL_PRO;
        String url = String.format(formatUrl, appID, date, ALL_IP, DAL_CONFIG_TRANSACTION_TYPE, String.format(DAL_CONFIG_TRANSACTION_DS_NAME, titanKey));
        CatTransactionEntity catTransactionEntity = null;
        for (int i = 0; i < RETRY_TIME; i++) {
            try {
                rateLimiter.acquire(1);
                catTransactionEntity = HttpUtil.getJSONEntity(CatTransactionEntity.class, url, null, HttpMethod.HttpGet);
            } catch (Exception e) {
                Cat.logError("check appid:" + appID + "Refresh DataSource fail.", e);
            }
        }
        CatTransactionReport configReport = null;
        if (catTransactionEntity != null) {
            configReport = catTransactionEntity.getReport();
        }
        Object names = null;
        if (configReport != null) {
            names = parseCatTransactionReportNames(configReport, ALL_IP, DAL_CONFIG_TRANSACTION_TYPE);
        }
        if (names == null) {
            return false;
        }
        else {
            List<TransactionNameRange> ranges =parseCatTransactionReportRanges(names.toString(), String.format(DAL_CONFIG_TRANSACTION_DS_NAME, titanKey));
            for (TransactionNameRange range : ranges) {
                if (range.getCount() != 0) {
                    appIDSwitchTime.put(range.getValue(), range.getCount());
                }
            }
        }
        ips.addAll(configReport.getHostIPs());
        return true;
    }

    public Map<Integer, Integer> getEndSwitchAppIDPoint(String titanKey, String appID, String date, String env) {
        String formatUrl = "FAT".equalsIgnoreCase(env) ? CAT_TRANSACTION_URL_FAT : "UAT".equalsIgnoreCase(env) ?
                CAT_TRANSACTION_URL_UAT : CAT_TRANSACTION_URL_PRO;
        String url = String.format(formatUrl, appID, date, ALL_IP, DAL_DATASOURCE_TRANSACTION_TYPE, String.format(DAL_DATASOURCE_TRANSACTION_NAME, titanKey));
        CatTransactionEntity catTransactionEntity = null;
        Map<Integer, Integer> endSwitchPoint = new HashMap<>();
        for (int i = 0; i < RETRY_TIME; i++) {
            try {
                rateLimiter.acquire(1);
                catTransactionEntity = HttpUtil.getJSONEntity(CatTransactionEntity.class, url, null, HttpMethod.HttpGet);
            } catch (Exception e) {
                Cat.logError("get appid:" + appID + "Switch end Info fail.", e);
            }
        }
        Object names = null;
        if (catTransactionEntity != null) {
            names = parseCatTransactionReportNames(catTransactionEntity.getReport(), ALL_IP, DAL_DATASOURCE_TRANSACTION_TYPE);
        }
        if (names != null) {
            List<TransactionNameRange> ranges = parseCatTransactionReportRanges(names.toString(), String.format(DAL_DATASOURCE_TRANSACTION_NAME, titanKey));
            for (TransactionNameRange range : ranges) {
                if (range.getCount() != 0) {
                    endSwitchPoint.put(range.getValue(), range.getCount());
                }
            }
        }
        return endSwitchPoint;
    }

    public SwitchHostIPInfo checkIpRefreshDataSourceTransaction(String titanKey, String appID, String ip, String date, String env) {
        String formatUrl = "FAT".equalsIgnoreCase(env) ? CAT_TRANSACTION_URL_FAT : "UAT".equalsIgnoreCase(env) ?
                CAT_TRANSACTION_URL_UAT : CAT_TRANSACTION_URL_PRO;
        String url = String.format(formatUrl, appID, date, ip, DAL_CONFIG_TRANSACTION_TYPE, String.format(DAL_CONFIG_TRANSACTION_DS_NAME, titanKey));
        CatTransactionEntity catTransactionEntity = null;
        for (int i = 0; i < RETRY_TIME; i++) {
            try {
                catTransactionEntity = HttpUtil.getJSONEntity(CatTransactionEntity.class, url, null, HttpMethod.HttpGet);
            } catch (Exception e) {
                Cat.logError("check appid:" + appID + " ip:" + ip + "Refresh DataSource fail.", e);
            }
        }
        CatTransactionReport configReport = null;
        if (catTransactionEntity != null) {
            configReport = catTransactionEntity.getReport();
        }
        Object names = null;
        if (configReport != null) {
            names = parseCatTransactionReportNames(configReport, ip, DAL_CONFIG_TRANSACTION_TYPE);
        }
        if (names != null) {
            Map<Integer, Integer> startSwitchPoint = new HashMap<>();
            List<TransactionNameRange> ranges = parseCatTransactionReportRanges(names.toString(), String.format(DAL_CONFIG_TRANSACTION_DS_NAME, titanKey));
            for (TransactionNameRange range : ranges) {
                if (range.getCount() != 0) {
                    startSwitchPoint.put(range.getValue(), range.getCount());
                }
            }
            SwitchHostIPInfo switchHostIPInfo = new SwitchHostIPInfo();
            switchHostIPInfo.setHostIP(ip);
            switchHostIPInfo.setStartSwitchPoint(startSwitchPoint);
            Map<Integer, Integer> endSwitchPoint = getEndSwitchHostIPPoint(titanKey, appID, ip, date, env);
            switchHostIPInfo.setEndSwitchPoint(endSwitchPoint);
            return switchHostIPInfo;
        }
        return null;
    }

    private Map<Integer, Integer> getEndSwitchHostIPPoint(String titanKey, String appID, String ip, String date, String env) {
        String formatUrl = "FAT".equalsIgnoreCase(env) ? CAT_TRANSACTION_URL_FAT : "UAT".equalsIgnoreCase(env) ?
                CAT_TRANSACTION_URL_UAT : CAT_TRANSACTION_URL_PRO;
        String url = String.format(formatUrl, appID, date, ip, DAL_DATASOURCE_TRANSACTION_TYPE, String.format(DAL_DATASOURCE_TRANSACTION_NAME, titanKey));
        CatTransactionEntity catTransactionEntity = null;
        Map<Integer, Integer> endSwitchPoint = new HashMap<>();
        for (int i = 0; i < RETRY_TIME; i++) {
            try {
                catTransactionEntity = HttpUtil.getJSONEntity(CatTransactionEntity.class, url, null, HttpMethod.HttpGet);
            } catch (Exception e) {
                Cat.logError("get appid:" + appID + " ip:" + ip + "Switch HostIP Info fail.", e);
            }
        }
        Object names = null;
        if (catTransactionEntity != null) {
            names = parseCatTransactionReportNames(catTransactionEntity.getReport(), ip, DAL_DATASOURCE_TRANSACTION_TYPE);
        }
        if (names != null) {
            List<TransactionNameRange> ranges = parseCatTransactionReportRanges(names.toString(), String.format(DAL_DATASOURCE_TRANSACTION_NAME, titanKey));
            for (TransactionNameRange range : ranges) {
                if (range.getCount() != 0) {
                    endSwitchPoint.put(range.getValue(), range.getCount());
                }
            }
        }
        return endSwitchPoint;
    }

    private Object parseCatTransactionReportNames(CatTransactionReport configReport, String ip, String transactionType) {
        Object types = parseCatTransactionReportTypes(configReport, ip);
        if (types == null) {
            return null;
        }
        JsonObject typesJSONObject = JsonUtils.parseObject(types.toString());
        String  transactionTypeStr = typesJSONObject.get(transactionType).toString();
        JsonObject nameObject = JsonUtils.parseObject(transactionTypeStr);
        return nameObject.get("names");
    }

    private Object parseCatTransactionReportTypes(CatTransactionReport configReport, String ip) {
        JsonObject machinesObject = configReport.getMachines();
        return machinesObject.get(ip).getAsJsonObject().get("types");
    }

    private List<TransactionNameRange> parseCatTransactionReportRanges(String transactionNames, String transactionName) {
        JsonObject transactionNameObject = JsonUtils.parseObject(transactionNames);
        String tNames = transactionNameObject.get(transactionName).toString();
        JsonObject rangesObject = JsonUtils.parseObject(tNames);
        String ranges = rangesObject.get("ranges").toString();
        return Arrays.asList(JsonUtils.fromJson(ranges,TransactionNameRange[].class));
    }
}
