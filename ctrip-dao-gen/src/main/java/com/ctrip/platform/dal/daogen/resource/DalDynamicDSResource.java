package com.ctrip.platform.dal.daogen.resource;


import com.ctrip.platform.dal.daogen.DalDynamicDSDBDao;
import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.util.DateUtils;
import com.ctrip.platform.dal.daogen.utils.JsonUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by taochen on 2019/7/2.
 */

@Resource
@Singleton
@Path("dynamicDS")
public class DalDynamicDSResource {
    private static DalDynamicDSDao dalDynamicDSDao = null;

    static {
        try {
            dalDynamicDSDao = DalDynamicDSDao.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initDalDynamicDS() {
        dalDynamicDSDao.init();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getSwitchDSDataOneWeek")
    public List<TitanKeySwitchInfoDB> getSwitchDSDataOneWeek(@QueryParam("startCheckTime") String settingStartDate, @QueryParam("endCheckTime") String settingEndDate) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
        Date startCheckDate = sdf.parse(settingStartDate.replace('T', ' '));
        Date endCheckDate = sdf.parse(settingEndDate.replace('T', ' '));
        String startCheckTime = DateUtils.formatCheckTime(startCheckDate);
        String endCheckTime = DateUtils.formatCheckTime(endCheckDate);
        DalDynamicDSDBDao dalDynamicDSDBDao = DalDynamicDSDBDao.getInstance();
        return dalDynamicDSDao.mergeSwitchData(dalDynamicDSDBDao.queryInRange(startCheckTime, endCheckTime));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("executeCheckDynamicDS")
    public CheckDynamicDSResponse executeCheckDynamicDS(@QueryParam("settingDate") String settingDate, @QueryParam("checkTitanKeys") String checkTitanKeys) throws Exception{
        CheckDynamicDSResponse checkDynamicDSResponse = new CheckDynamicDSResponse();
        List<DynamicDSDataDto> dynamicDSDataList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
        Date checkDate = sdf.parse(settingDate.replace('T', ' '));
        String checkTime = dalDynamicDSDao.getNowDateString(checkDate);
        Set<String> checkTitanKeySet = null;
        if (StringUtils.isNotBlank(checkTitanKeys)) {
            checkTitanKeySet = new HashSet<>();
            String[] checkTitanKeyArray = checkTitanKeys.split(",");
            for (String checkTitanKey : checkTitanKeyArray) {
                checkTitanKeySet.add(checkTitanKey);
            }
        }
        //dalDynamicDSDao.checkSwitchDataSource(checkTime, checkTitanKeySet, TriggerMethod.MANUAL);
        Map<SwitchTitanKey, List<AppIDInfo>> fullTitanKeyAppIDMap = new HashMap<>();
        //todo 两个相同时间的请求间隔时间短还是会出现阻塞的情况
        if (!checkTime.equalsIgnoreCase(dalDynamicDSDao.getStatisticTime())) {
            fullTitanKeyAppIDMap = dalDynamicDSDao.getTitanKeyAppIDMap(checkTime);
        }
        //filter titankey
        Map<SwitchTitanKey, List<AppIDInfo>> TitanKeyAppIDMap = new HashMap<>();
        TitanKeyAppIDMap.putAll(fullTitanKeyAppIDMap);
        if (checkTitanKeySet != null && checkTitanKeySet.size() > 0) {
            for (SwitchTitanKey switchTitanKey : fullTitanKeyAppIDMap.keySet()) {
                if (!checkTitanKeySet.contains(switchTitanKey.getTitanKey())) {
                    TitanKeyAppIDMap.remove(switchTitanKey);
                }
            }
        }
        if (TitanKeyAppIDMap.size() > 0) {
            for (Map.Entry<SwitchTitanKey, List<AppIDInfo>> titanKeyData : TitanKeyAppIDMap.entrySet()) {
                DynamicDSDataDto dynamicDSData = new DynamicDSDataDto();
                List<AppIDInfoDto> appIds = new ArrayList<>();
                int switchCount = 0;
                int successCount = 0;
                String titanKeySwitchCount = titanKeyData.getKey().getSwitchCount().size() + "";

                for (AppIDInfo appIDInfo : titanKeyData.getValue()) {
                    AppIDInfoDto appIDInfoDto = new AppIDInfoDto();
//                    String hostIPs = "";
//                    String hostSuccessCount = "";
//                    String hostSwitchCount = "";
//                    for (SwitchHostIPInfo switchHostIPInfo : appIDInfo.getHostIPInfolist()) {
//                        if (switchHostIPInfo.getStartSwitchPoint() != null) {
//                            switchCount = switchHostIPInfo.getStartSwitchPoint().size();
//                        }
//                        if (switchHostIPInfo.getEndSwitchPoint() != null) {
//                            successCount = switchHostIPInfo.getEndSwitchPoint().size();
//                        }
//                        hostIPs += switchHostIPInfo.getHostIP() + "<br/>";
//                        hostSuccessCount += successCount + "<br/>";
//                        hostSwitchCount += switchCount + "<br/>";
//                    }
//                    appIDInfoDto.setAppID(appIDInfo.getAppID());
//                    appIDInfoDto.setHostIPs(hostIPs);
//                    appIDInfoDto.setHostSwitchCount(hostSwitchCount);
//                    appIDInfoDto.setHostSuccessCount(hostSuccessCount);
                    int appIDSwitchCount = 0;
                    int appIDSuccessCount = 0;
                    for (Integer value : appIDInfo.getAppIDSwitchTime().values()) {
                        appIDSwitchCount += value;
                    }
                    for (Integer value : appIDInfo.getAppIDSuccessTime().values()) {
                        appIDSuccessCount += value;
                    }
                    appIDInfoDto.setAppID(appIDInfo.getAppID());
                    appIDInfoDto.setHostIPCount(appIDInfo.getHostIPInfolist().size());
                    appIDInfoDto.setAppIDSwitchCount(appIDSwitchCount);
                    appIDInfoDto.setAppIDSuccessCount(appIDSuccessCount);
                    appIds.add(appIDInfoDto);
                }
                dynamicDSData.setTitanKey(titanKeyData.getKey().getTitanKey());
                dynamicDSData.setAppIds(appIds);
                dynamicDSData.setTitanKeySwitchCount(titanKeySwitchCount);
                dynamicDSDataList.add(dynamicDSData);
            }
        }
//        for (int i=0; i < 2; ++i) {
//            DynamicDSDataDto dynamicDSDataDto = new DynamicDSDataDto();
//            dynamicDSDataDto.setTitanKey("dalservice2db_w");
//            dynamicDSDataDto.setTitanKeySwitchCount("1");
//            List<AppIDInfoDto> appInfoList = new ArrayList<>();
//            AppIDInfoDto appIDInfoDto = new AppIDInfoDto();
//            appIDInfoDto.setAppID("930201");
//            appIDInfoDto.setHostIPs("10.32.20.128");
//            appIDInfoDto.setHostSuccessCount("1");
//            appIDInfoDto.setHostSwitchCount("2");
//            appInfoList.add(appIDInfoDto);
//            AppIDInfoDto appIDInfoDto2 = new AppIDInfoDto();
//            appIDInfoDto2.setAppID("930201");
//            appIDInfoDto2.setHostIPs("10.32.20.128");
//            appIDInfoDto2.setHostSuccessCount("1");
//            appIDInfoDto2.setHostSwitchCount("1");
//            appInfoList.add(appIDInfoDto2);
//            dynamicDSDataDto.setAppIds(appInfoList);
//            dynamicDSDataList.add(dynamicDSDataDto);
//
//            DynamicDSDataDto dynamicDSDataDto2 = new DynamicDSDataDto();
//            dynamicDSDataDto2.setTitanKey("titantest_lzyan_v_01");
//            dynamicDSDataDto2.setTitanKeySwitchCount("1");
//            List<AppIDInfoDto> appInfoList2 = new ArrayList<>();
//            AppIDInfoDto appIDInfoDto3 = new AppIDInfoDto();
//            appIDInfoDto3.setAppID("");
//            appIDInfoDto3.setHostIPs("");
//            appIDInfoDto3.setHostSwitchCount("");
//            appIDInfoDto3.setHostSuccessCount("");
//            appInfoList2.add(appIDInfoDto3);
//            dynamicDSDataDto2.setAppIds(appInfoList2);
//            dynamicDSDataList.add(dynamicDSDataDto2);
//        }
//        DynamicDSDataDto dynamicDSDataDto = new DynamicDSDataDto();
//        dynamicDSDataDto.setTitanKey("dalservice2db_w");
//        dynamicDSDataDto.setTitanKeySwitchCount("1");
//        List<AppIDInfoDto> appInfoList = new ArrayList<>();
//        AppIDInfoDto appIDInfoDto = new AppIDInfoDto();
//        appIDInfoDto.setAppID("930201");
//        appIDInfoDto.setHostIPs("10.32.20.128");
//        appIDInfoDto.setHostSuccessCount("1");
//        appIDInfoDto.setHostSwitchCount("2");
//        appInfoList.add(appIDInfoDto);
//        AppIDInfoDto appIDInfoDto2 = new AppIDInfoDto();
//        appIDInfoDto2.setAppID("930201");
//        appIDInfoDto2.setHostIPs("10.32.20.128");
//        appIDInfoDto2.setHostSuccessCount("1");
//        appIDInfoDto2.setHostSwitchCount("1");
//        appInfoList.add(appIDInfoDto2);
//        AppIDInfoDto appIDInfoDto3 = new AppIDInfoDto();
//        appIDInfoDto3.setAppID("930201");
//        appIDInfoDto3.setHostIPs("10.32.20.128");
//        appIDInfoDto3.setHostSuccessCount("1");
//        appIDInfoDto3.setHostSwitchCount("1");
//        appInfoList.add(appIDInfoDto3);
//        dynamicDSDataDto.setAppIds(appInfoList);
//        dynamicDSDataList.add(dynamicDSDataDto);
//
//        DynamicDSDataDto dynamicDSDataDto2 = new DynamicDSDataDto();
//        dynamicDSDataDto2.setTitanKey("titantest_lzyan_v_01");
//        dynamicDSDataDto2.setTitanKeySwitchCount("1");
//        List<AppIDInfoDto> appInfoList2 = new ArrayList<>();
//        AppIDInfoDto appIDInfoDto4 = new AppIDInfoDto();
//        appIDInfoDto4.setAppID("");
//        appIDInfoDto4.setHostIPs("");
//        appIDInfoDto4.setHostSwitchCount("");
//        appIDInfoDto4.setHostSuccessCount("");
//        appInfoList2.add(appIDInfoDto4);
//        dynamicDSDataDto2.setAppIds(appInfoList2);
//        dynamicDSDataList.add(dynamicDSDataDto2);
        Map<String, Integer> checkTimeSwitchCountMap = dalDynamicDSDao.getCheckTimeSwitchCountMap();
        if (checkTimeSwitchCountMap.containsKey(checkTime)) {
            checkDynamicDSResponse.setStatusCode(2);
            checkDynamicDSResponse.setStatisticTime(dalDynamicDSDao.getStatisticTime());
            checkDynamicDSResponse.setStatisticProgress(dalDynamicDSDao.getStatisticProgress());
            checkDynamicDSResponse.setSwitchTitanKeyCount(dalDynamicDSDao.getStatisticTitanKeyCount());
        }
        else if (checkTime.equalsIgnoreCase(dalDynamicDSDao.getStatisticTime())) {
            checkDynamicDSResponse.setStatusCode(1);
            checkDynamicDSResponse.setSwitchTitanKeyCount(dalDynamicDSDao.getStatisticTitanKeyCount());
            checkDynamicDSResponse.setStatisticProgress(dalDynamicDSDao.getStatisticProgress());
            checkDynamicDSResponse.setStatisticTime(dalDynamicDSDao.getStatisticTime());
        }
        else {
            checkDynamicDSResponse.setStatusCode(0);
            checkDynamicDSResponse.setDynamicDSDataList(dynamicDSDataList);
        }
        return checkDynamicDSResponse;
    }

//    @GET
//    @Produces(MediaType.TEXT_HTML)
//    @Path("getTitanKeySwitchTime")
//    public String getTitanKeySwitchTime(@QueryParam("titanKey") String titanKey) {
//        String titankeyStr = "titanKey: %s switch: %s<br/>";
//        String appIDString = "  AppID: %s<br/>";
//        String dalClientStr = "    dalClientIP: %s  switch: %s<br/>";
//        String switchTimeInfo = "time: %s   count: %s";
//        Map<SwitchTitanKey, List<AppIDInfo>> titanKeyAppIDMap = dalDynamicDSDao.getTitanKeyAppIDMap();
//        if (titanKeyAppIDMap == null || titanKeyAppIDMap.size() == 0) {
//            return "";
//        }
//        String result = "";
//        for (Map.Entry<SwitchTitanKey, List<AppIDInfo>> titanKeyData: titanKeyAppIDMap.entrySet()) {
//            SwitchTitanKey switchTitanKey = titanKeyData.getKey();
//            if (switchTitanKey.getTitanKey().equalsIgnoreCase(titanKey)) {
//                String titanKeySwitch = "";
//                    for (Map.Entry<Integer, Integer> titanKeyTime : switchTitanKey.getSwitchCount().entrySet()) {
//                        titanKeySwitch += String.format(switchTimeInfo, titanKeyTime.getKey(), titanKeyTime.getValue());
//                    }
//                    result += String.format(titankeyStr, switchTitanKey.getTitanKey(), titanKeySwitch);
//                    for (AppIDInfo appIDInfo : titanKeyData.getValue()) {
//                        result += String.format(appIDString, appIDInfo.getAppID());
//                        String hostIPSwitch = "";
//                        for (SwitchHostIPInfo switchHostIPInfo : appIDInfo.getHostIPInfolist()) {
//                            for (Map.Entry<Integer, Integer> hostIPTime : switchHostIPInfo.getStartSwitchPoint().entrySet()) {
//                                hostIPSwitch += String.format(switchTimeInfo, hostIPTime.getKey(), hostIPTime.getValue());
//                            }
//                            result += String.format(dalClientStr, switchHostIPInfo.getHostIP(), hostIPSwitch);
//                        }
//                    }
//                break;
//            }
//        }
//        return result;
//    }
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("getTitanKeySwitchTime")
    public String getTitanKeySwitchTime(@QueryParam("titanKey") String titanKey, @QueryParam("checkTime") String checkTime) {
        Map<SwitchTitanKey, List<AppIDInfo>> titanKeyAppIDMap = dalDynamicDSDao.getTitanKeyAppIDMap(checkTime);
        if (titanKeyAppIDMap == null || titanKeyAppIDMap.size() == 0) {
            return "";
        }
        TitanKeySwitchInfoDto titanKeySwitchInfoDto = new TitanKeySwitchInfoDto();
        for (Map.Entry<SwitchTitanKey, List<AppIDInfo>> titanKeyData: titanKeyAppIDMap.entrySet()) {
            SwitchTitanKey switchTitanKey = titanKeyData.getKey();
            if (switchTitanKey.getTitanKey().equalsIgnoreCase(titanKey)) {
                titanKeySwitchInfoDto.setTitanKey(switchTitanKey.getTitanKey());
                List<SwitchCountTime> switchCountTimeList = new ArrayList<>();
                for (Map.Entry<Integer, Integer> titanKeyTime : switchTitanKey.getSwitchCount().entrySet()) {
                    SwitchCountTime switchCountTime = new SwitchCountTime();
                    switchCountTime.setMinute(titanKeyTime.getKey());
                    switchCountTime.setCount(titanKeyTime.getValue());
                    switchCountTimeList.add(switchCountTime);
                }
                titanKeySwitchInfoDto.setSwitches(switchCountTimeList);
//                List<AppIDSwitchInfoDto> appIDSwitchInfoDtoList = new ArrayList<>();
                List<SwitchAppIDInfoDto> appIDSwitchInfoDtoList = new ArrayList<>();
                for (AppIDInfo appIDInfo : titanKeyData.getValue()) {
                    SwitchAppIDInfoDto switchAppIDInfoDto = new SwitchAppIDInfoDto();
                    switchAppIDInfoDto.setAppID(appIDInfo.getAppID());
                    List<SwitchCountTime> appIDStartSwitches = new ArrayList<>();
                    for (Map.Entry<Integer, Integer> appIDSwitchTime : appIDInfo.getAppIDSwitchTime().entrySet()) {
                        SwitchCountTime switchCountTime = new SwitchCountTime();
                        switchCountTime.setMinute(appIDSwitchTime.getKey());
                        switchCountTime.setCount(appIDSwitchTime.getValue());
                        appIDStartSwitches.add(switchCountTime);
                    }
                    switchAppIDInfoDto.setStartSwitches(appIDStartSwitches);
                    List<SwitchCountTime> appIDEndSwitches = new ArrayList<>();
                    for (Map.Entry<Integer, Integer> appIDSuccessSwitchTime : appIDInfo.getAppIDSuccessTime().entrySet()) {
                        SwitchCountTime switchCountTime = new SwitchCountTime();
                        switchCountTime.setMinute(appIDSuccessSwitchTime.getKey());
                        switchCountTime.setCount(appIDSuccessSwitchTime.getValue());
                        appIDEndSwitches.add(switchCountTime);
                    }
                    switchAppIDInfoDto.setEndSwitches(appIDEndSwitches);
//                    AppIDSwitchInfoDto appIDSwitchInfoDto = new AppIDSwitchInfoDto();
//                    appIDSwitchInfoDto.setAppID(appIDInfo.getAppID());
//                    List<DalClientSwitchInfoDto> dalClientList = new ArrayList<>();
//                    for (SwitchHostIPInfo switchHostIPInfo : appIDInfo.getHostIPInfolist()) {
//                        DalClientSwitchInfoDto dalClientSwitchInfoDto = new DalClientSwitchInfoDto();
//                        dalClientSwitchInfoDto.setClientIP(switchHostIPInfo.getHostIP());
//                        List<SwitchCountTime> startSwitches = new ArrayList<>();
//                        List<SwitchCountTime> endSwitches = new ArrayList<>();
//                        for (Map.Entry<Integer, Integer> hostIPTime : switchHostIPInfo.getStartSwitchPoint().entrySet()) {
//                            SwitchCountTime switchCountTime = new SwitchCountTime();
//                            switchCountTime.setMinute(hostIPTime.getKey());
//                            switchCountTime.setCount(hostIPTime.getValue());
//                            startSwitches.add(switchCountTime);
//                        }
//                        dalClientSwitchInfoDto.setStartSwitches(startSwitches);
//                        for (Map.Entry<Integer, Integer> hostIPTime : switchHostIPInfo.getEndSwitchPoint().entrySet()) {
//                            SwitchCountTime switchCountTime = new SwitchCountTime();
//                            switchCountTime.setMinute(hostIPTime.getKey());
//                            switchCountTime.setCount(hostIPTime.getValue());
//                            endSwitches.add(switchCountTime);
//                        }
//                        dalClientSwitchInfoDto.setEndSwitches(endSwitches);
//                        dalClientList.add(dalClientSwitchInfoDto);
//                    }
//                    appIDSwitchInfoDto.setClientList(dalClientList);
                    appIDSwitchInfoDtoList.add(switchAppIDInfoDto);
                }
                titanKeySwitchInfoDto.setAppIDList(appIDSwitchInfoDtoList);
                break;
            }
        }
        return JsonUtils.toJson(titanKeySwitchInfoDto);
    }
}
