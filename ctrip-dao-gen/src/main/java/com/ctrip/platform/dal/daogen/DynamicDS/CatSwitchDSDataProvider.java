package com.ctrip.platform.dal.daogen.DynamicDS;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.platform.dal.daogen.entity.CatTransactionEntity;
import com.ctrip.platform.dal.daogen.entity.CatTransactionReport;
import com.ctrip.platform.dal.daogen.entity.SwitchHostIPInfo;
import com.ctrip.platform.dal.daogen.entity.TransactionNameRange;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.dianping.cat.Cat;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by taochen on 2019/7/3.
 */
public class CatSwitchDSDataProvider implements SwitchDSDataProvider {
    private static final String CAT_TRANSACTION_URL =
            "http://cat.ctripcorp.com/cat/r/t?domain=%s&date=%s&ip=%s&type=%s&min=-1&max=-1&name=%s&forceDownload=json";

    private static final String CAT_EVENT_TITAN_UPDATE =
            "http://cat.ctripcorp.com/cat/r/e?domain=100005701&ip=All&date=%s&reportType=day&op=view&group=SHAJQ&type=%s&forceDownload=json";

    private static final String DAL_CONFIG_TRANSACTION_TYPE = "DAL.configure";
//    private static final String DAL_CONFIG_TRANSACTION_TYPE = "SQL.task";

    private static final String DAL_CONFIG_TRANSACTION_DS_NAME = "DataSourceConfig::refreshDataSourceConfig%s";
//    private static final String DAL_CONFIG_TRANSACTION_DS_NAME = "GetShardingDataSetList,Shard:3";

    private static final String DAL_DATASOURCE_TRANSACTION_TYPE = "DAL.dataSource";

    private static final String DAL_DATASOURCE_TRANSACTION_NAME = "DataSource::createDataSource:%s";

    private static final String DAL_TITAN_UPDATE_TYPE = "Titan.MHAUpdate.TitanKey";

    private static final String ALL_IP = "All";

    @Override
    public boolean isSwitchInAppID(String appID, Date checkTime, List<SwitchHostIPInfo> hostIPList) {
        List<String> ips = new ArrayList<>();
        String beforeDate = getBeforeOneHourDateString(checkTime);
        String nowDate = getNowDateString(checkTime);
        boolean isAppSwitch = checkAppRefreshDataSourceTransaction(appID, beforeDate, ips);
        if (!isAppSwitch) {
            //可能跨小时
            isAppSwitch = checkAppRefreshDataSourceTransaction(appID, nowDate, ips);
        }
        if (isAppSwitch) {
            for (String ip : ips) {
                SwitchHostIPInfo switchHostIPInfo = checkIpRefreshDataSourceTransaction(appID, ip, beforeDate);
                if (switchHostIPInfo == null) {
                    //可能跨小时
                    switchHostIPInfo = checkIpRefreshDataSourceTransaction(appID, ip, nowDate);
                }
                if (switchHostIPInfo != null) {
                    hostIPList.add(switchHostIPInfo);
                }
            }
        }
        return isAppSwitch;
    }

    @Override
    public Set<String> getSwitchTitanKey(Date checkTime) {
        Set<String> titanKeys = new HashSet<>();
        String beforeDate = getBeforeOneHourDateString(checkTime);
        String url = String.format(CAT_EVENT_TITAN_UPDATE, beforeDate, DAL_TITAN_UPDATE_TYPE);
        CatTransactionEntity catTransactionEntity = null;
        try {
            catTransactionEntity =  HttpUtil.getJSONEntity(CatTransactionEntity.class, url, null, HttpMethod.HttpGet);
        } catch (Exception e) {
            Cat.logError("get switch titan key fail.", e);
        }
        CatTransactionReport configReport = catTransactionEntity.getReport();
        Object types = parseCatTransactionReportTypes(configReport, ALL_IP);
        if (types == null) {
            return null;
        }
        Object namesObject = parseCatTransactionReportNames(configReport, DAL_CONFIG_TRANSACTION_TYPE, ALL_IP);
        if (namesObject != null) {
            JSONObject namesJsonObject = JSON.parseObject(namesObject.toString());
            for (Object key : namesJsonObject.keySet()) {
                titanKeys.add(key.toString());
            }
            return titanKeys;
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

    private String getNowDateString(Date checkTime) {
        return formatCheckTime(checkTime);
    }

    private String formatCheckTime(Date convertCheckTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(convertCheckTime).replaceAll("-| ", "");
        return dateString.substring(0, dateString.indexOf(":"));
    }

    public boolean checkAppRefreshDataSourceTransaction(String appID, String date, List<String> ips) {
        String url = String.format(CAT_TRANSACTION_URL, appID, date, ALL_IP, DAL_CONFIG_TRANSACTION_TYPE, DAL_CONFIG_TRANSACTION_DS_NAME);
        CatTransactionEntity catTransactionEntity = null;
        try {
            catTransactionEntity =  HttpUtil.getJSONEntity(CatTransactionEntity.class, url, null, HttpMethod.HttpGet);
        } catch (Exception e) {
            Cat.logError("check appid:" + appID + "Refresh DataSource fail.", e);
        }
        CatTransactionReport configReport = catTransactionEntity.getReport();
        Object names = parseCatTransactionReportNames(configReport, ALL_IP, DAL_CONFIG_TRANSACTION_TYPE);
        if (names == null) {
            return false;
        }
        ips.addAll(configReport.getHostIPs());
        return true;
    }

    public SwitchHostIPInfo checkIpRefreshDataSourceTransaction(String appID, String ip, String date) {
        String url = String.format(CAT_TRANSACTION_URL, appID, date, ip, DAL_CONFIG_TRANSACTION_TYPE, DAL_CONFIG_TRANSACTION_DS_NAME);
        CatTransactionEntity catTransactionEntity = null;
        try {
            catTransactionEntity = HttpUtil.getJSONEntity(CatTransactionEntity.class, url, null, HttpMethod.HttpGet);
        } catch (Exception e) {
            Cat.logError("check appid:" + appID + " ip:" + ip + "Refresh DataSource fail.", e);
        }
        CatTransactionReport configReport = catTransactionEntity.getReport();
        Object names = parseCatTransactionReportNames(configReport, ip, DAL_CONFIG_TRANSACTION_TYPE);
        if (names != null) {
            Map<Integer, Integer> startSwitchPoint = new HashMap<>();
            List<TransactionNameRange> ranges = parseCatTransactionReportRanges(names.toString(), DAL_CONFIG_TRANSACTION_DS_NAME);
            for (TransactionNameRange range : ranges) {
                if (range.getCount() != 0) {
                    startSwitchPoint.put(range.getValue(), range.getCount());
                }
            }
            SwitchHostIPInfo switchHostIPInfo = new SwitchHostIPInfo();
            switchHostIPInfo.setHostIP(ip);
            switchHostIPInfo.setStartSwitchPoint(startSwitchPoint);
            Map<Integer, Integer> endSwitchPoint = getEndSwitchHostIPPoint(appID, ip, date);
            switchHostIPInfo.setEndSwitchPoint(endSwitchPoint);
            return switchHostIPInfo;
        }
        return null;
    }

    private Map<Integer, Integer> getEndSwitchHostIPPoint(String appID, String ip, String date) {
        String url = String.format(CAT_TRANSACTION_URL, appID, date, ip, DAL_DATASOURCE_TRANSACTION_TYPE, DAL_DATASOURCE_TRANSACTION_NAME);
        CatTransactionEntity catTransactionEntity = null;
        try {
            catTransactionEntity = HttpUtil.getJSONEntity(CatTransactionEntity.class, url, null, HttpMethod.HttpGet);
        } catch (Exception e) {
            Cat.logError("get appid:" + appID + " ip:" + ip + "Switch HostIP Info fail.", e);
        }
        Object names = parseCatTransactionReportNames(catTransactionEntity.getReport(), ip, DAL_DATASOURCE_TRANSACTION_TYPE);
        if (names != null) {
            Map<Integer, Integer> endSwitchPoint = new HashMap<>();
            List<TransactionNameRange> ranges = parseCatTransactionReportRanges(names.toString(), DAL_DATASOURCE_TRANSACTION_NAME);
            for (TransactionNameRange range : ranges) {
                if (range.getCount() != 0) {
                    endSwitchPoint.put(range.getValue(), range.getCount());
                }
            }
            return endSwitchPoint;
        }
        return null;
    }

    private Object parseCatTransactionReportNames(CatTransactionReport configReport, String ip, String transactionType) {
        Object types = parseCatTransactionReportTypes(configReport, ip);
        JSONObject typesJSONObject = JSON.parseObject(types.toString());
        String  transactionTypeStr = typesJSONObject.getString(transactionType);
        JSONObject nameObject = JSON.parseObject(transactionTypeStr);
        Object names = nameObject.get("names");
        return names;
    }

    private Object parseCatTransactionReportTypes(CatTransactionReport configReport, String ip) {
        JSONObject machinesObject = JSON.parseObject(configReport.getMachines());
        String ipString = machinesObject.get(ip).toString();
        JSONObject typeObject = JSON.parseObject(ipString);
        Object types = typeObject.get("types");
        return types;
    }

    private List<TransactionNameRange> parseCatTransactionReportRanges(String transactionNames, String transactionName) {
        JSONObject transactionNameObject = JSON.parseObject(transactionNames);
        String tNames = transactionNameObject.get(transactionName).toString();
        JSONObject rangesObject = JSON.parseObject(tNames);
        String ranges = rangesObject.get("ranges").toString();
        List<TransactionNameRange> transactionNameRanges = JSON.parseArray(ranges, TransactionNameRange.class);
        return transactionNameRanges;
    }
}
