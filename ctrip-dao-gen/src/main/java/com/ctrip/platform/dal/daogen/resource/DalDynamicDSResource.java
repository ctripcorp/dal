package com.ctrip.platform.dal.daogen.resource;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.entity.*;
import org.apache.commons.lang.StringUtils;
import qunar.servlet.bean.AppInfo;

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
    @Path("getSwitchDSData")
    public void get() {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("executeCheckDynamicDS")
    public List<DynamicDSDataDto> executeCheckDynamicDS(@QueryParam("settingDate") String settingDate, @QueryParam("checkTitanKeys") String checkTitanKeys) throws Exception{
        List<DynamicDSDataDto> dynamicDSDataList = new ArrayList<>();
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
        Date checkTime = sdf.parse(settingDate.replace('T', ' '));
        Set<String> checkTitanKeySet = null;
        if (StringUtils.isNotBlank(checkTitanKeys)) {
            checkTitanKeySet = new HashSet<>();
            String[] checkTitanKeyArray = checkTitanKeys.split(",");
            for (String checkTitanKey : checkTitanKeyArray) {
                checkTitanKeySet.add(checkTitanKey);
            }
        }
        dalDynamicDSDao.checkSwitchDataSource(env, checkTime, checkTitanKeySet, TriggerMethod.MANUAL);
        for (Map.Entry<SwitchTitanKey, List<AppIDInfo>> titanKeyData: dalDynamicDSDao.getTitanKeyAppIDMap().entrySet()) {
            DynamicDSDataDto dynamicDSData = new DynamicDSDataDto();
            List<AppIDInfoDto> appIds  = new ArrayList<>();
            int switchCount = 0;
            int successCount = 0;
            String titanKeySwitchCount = titanKeyData.getKey().getSwitchCount().size() + "";

            for (AppIDInfo appIDInfo : titanKeyData.getValue()) {
                AppIDInfoDto appIDInfoDto = new AppIDInfoDto();
                String hostIPs = "";
                String hostSuccessCount = "";
                String hostSwitchCount = "";
                for (SwitchHostIPInfo switchHostIPInfo : appIDInfo.getHostIPInfolist()) {
                    if (switchHostIPInfo.getStartSwitchPoint() != null) {
                        switchCount = switchHostIPInfo.getStartSwitchPoint().size();
                    }
                    if (switchHostIPInfo.getEndSwitchPoint() != null) {
                        successCount = switchHostIPInfo.getEndSwitchPoint().size();
                    }
                    hostIPs += switchHostIPInfo.getHostIP() + "<br/>";
                    hostSuccessCount += successCount + "<br/>";
                    hostSwitchCount += switchCount + "<br/>";
                }
                appIDInfoDto.setAppID(appIDInfo.getAppID());
                appIDInfoDto.setHostIPs(hostIPs);
                appIDInfoDto.setHostSwitchCount(hostSwitchCount);
                appIDInfoDto.setHostSuccessCount(hostSuccessCount);
                appIds.add(appIDInfoDto);
            }
            dynamicDSData.setTitanKey(titanKeyData.getKey().getTitanKey());
            dynamicDSData.setAppIds(appIds);
            dynamicDSData.setTitanKeySwitchCount(titanKeySwitchCount);
            dynamicDSDataList.add(dynamicDSData);
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
        return dynamicDSDataList;
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
    public String getTitanKeySwitchTime(@QueryParam("titanKey") String titanKey) {
        Map<SwitchTitanKey, List<AppIDInfo>> titanKeyAppIDMap = dalDynamicDSDao.getTitanKeyAppIDMap();
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
                    switchCountTime.setTime(titanKeyTime.getKey());
                    switchCountTime.setCount(titanKeyTime.getValue());
                    switchCountTimeList.add(switchCountTime);
                }
                titanKeySwitchInfoDto.setSwitchs(switchCountTimeList);
                List<AppIDSwitchInfoDto> appIDSwitchInfoDtoList = new ArrayList<>();
                for (AppIDInfo appIDInfo : titanKeyData.getValue()) {
                    AppIDSwitchInfoDto appIDSwitchInfoDto = new AppIDSwitchInfoDto();
                    appIDSwitchInfoDto.setAppID(appIDInfo.getAppID());
                    List<DalClientSwitchInfoDto> dalClientList = new ArrayList<>();
                    for (SwitchHostIPInfo switchHostIPInfo : appIDInfo.getHostIPInfolist()) {
                        DalClientSwitchInfoDto dalClientSwitchInfoDto = new DalClientSwitchInfoDto();
                        dalClientSwitchInfoDto.setDalClientIP(switchHostIPInfo.getHostIP());
                        List<SwitchCountTime> switchs = new ArrayList<>();
                        for (Map.Entry<Integer, Integer> hostIPTime : switchHostIPInfo.getStartSwitchPoint().entrySet()) {
                            SwitchCountTime switchCountTime = new SwitchCountTime();
                            switchCountTime.setTime(hostIPTime.getKey());
                            switchCountTime.setCount(hostIPTime.getValue());
                            switchs.add(switchCountTime);
                        }
                        dalClientSwitchInfoDto.setSwitchs(switchs);
                        dalClientList.add(dalClientSwitchInfoDto);
                    }
                    appIDSwitchInfoDto.setDalClientList(dalClientList);
                    appIDSwitchInfoDtoList.add(appIDSwitchInfoDto);
                }
                titanKeySwitchInfoDto.setAppIDList(appIDSwitchInfoDtoList);
                break;
            }
        }
        return JSON.toJSONString(titanKeySwitchInfoDto);
    }
}
