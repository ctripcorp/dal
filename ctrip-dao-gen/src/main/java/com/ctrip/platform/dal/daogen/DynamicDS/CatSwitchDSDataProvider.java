package com.ctrip.platform.dal.daogen.DynamicDS;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.dianping.cat.Cat;
import com.google.common.util.concurrent.RateLimiter;

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

    private static final String DAL_TITAN_UPDATE_TYPE = "Titan.MHAUpdate.TitanKey";

    private static final String ALL_IP = "All";

    private static final int RETRY_TIME = 3;

    private RateLimiter rateLimiter = RateLimiter.create(20);

    @Override
    public boolean isSwitchInAppID(String titanKey, String appID, String checkTime, List<String> hostIPList, Map<Integer, Integer> appIDSwitchTime, String env) {
        List<String> ips = new ArrayList<>();
        long startTimeCatHostIPs = System.currentTimeMillis();
        boolean isAppSwitch = checkAppRefreshDataSourceTransaction(titanKey, appID, checkTime, hostIPList, appIDSwitchTime, env);
        long endTimeCatHostIPs = System.currentTimeMillis();
        //System.out.println("cat api time: " + (endTimeCatHostIPs - startTimeCatHostIPs));
//        if (isAppSwitch) {
//            for (String ip : ips) {
//                SwitchHostIPInfo switchHostIPInfo = checkIpRefreshDataSourceTransaction(titanKey, appID, ip, checkTime, env);
//                if (switchHostIPInfo != null) {
//                    hostIPList.add(switchHostIPInfo);
//                }
//            }
//        }
        return isAppSwitch;
    }

    @Override
    public Set<SwitchTitanKey> getSwitchTitanKey(String checkTime, Set<String> checkTitanKeys, String env) {
        Set<SwitchTitanKey> titanKeys = new HashSet<>();
        String formatUrl = "FAT".equalsIgnoreCase(env) ? CAT_EVENT_TITAN_UPDATE_FAT : "UAT".equalsIgnoreCase(env) ?
                CAT_EVENT_TITAN_UPDATE_UAT : CAT_EVENT_TITAN_UPDATE_PRO;
        String url = String.format(formatUrl, checkTime, DAL_TITAN_UPDATE_TYPE);
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
        Object namesObject = parseCatTransactionReportNames(configReport, ALL_IP, DAL_TITAN_UPDATE_TYPE);
        if (namesObject != null) {
            JSONObject namesJsonObject = JSON.parseObject(namesObject.toString());
            for (Object key : namesJsonObject.keySet()) {
                if (ALL_IP.equalsIgnoreCase(key.toString())) {
                    continue;
                }
                JSONObject switchTitanKeyObject = JSON.parseObject(namesJsonObject.get(key).toString());
                List<TransactionNameRange> ranges = JSON.parseArray(switchTitanKeyObject.getString("ranges"), TransactionNameRange.class);
                SwitchTitanKey switchTitanKey = new SwitchTitanKey();
                switchTitanKey.setTitanKey(key.toString());
                Map<Integer, Integer> switchCount = new HashMap<>();
                for (TransactionNameRange range : ranges) {
                    if (range.getCount() != 0) {
                        switchCount.put(range.getValue(), range.getCount());
                    }
                }
                switchTitanKey.setSwitchCount(switchCount);
                if (checkTitanKeys != null) {
                    if (checkTitanKeys.contains(key.toString())) {
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
            List<TransactionNameRange> ranges = parseCatTransactionReportRanges(names.toString(), String.format(DAL_CONFIG_TRANSACTION_DS_NAME, titanKey));
            for (TransactionNameRange range : ranges) {
                if (range.getCount() != 0) {
                    appIDSwitchTime.put(range.getValue(), range.getCount());
                }
            }
        }
        ips.addAll(configReport.getHostIPs());
        return true;
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
        JSONObject typesJSONObject = JSON.parseObject(types.toString());
        String  transactionTypeStr = typesJSONObject.getString(transactionType);
        JSONObject nameObject = JSON.parseObject(transactionTypeStr);
        return nameObject.get("names");
    }

    private Object parseCatTransactionReportTypes(CatTransactionReport configReport, String ip) {
        JSONObject machinesObject = JSON.parseObject(configReport.getMachines());
        String ipString = machinesObject.get(ip).toString();
        JSONObject typeObject = JSON.parseObject(ipString);
        return typeObject.get("types");
    }

    private List<TransactionNameRange> parseCatTransactionReportRanges(String transactionNames, String transactionName) {
        JSONObject transactionNameObject = JSON.parseObject(transactionNames);
        String tNames = transactionNameObject.get(transactionName).toString();
        JSONObject rangesObject = JSON.parseObject(tNames);
        String ranges = rangesObject.get("ranges").toString();
        return JSON.parseArray(ranges, TransactionNameRange.class);
    }
}
