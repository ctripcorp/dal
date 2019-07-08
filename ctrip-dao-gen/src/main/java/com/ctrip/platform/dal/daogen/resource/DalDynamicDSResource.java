package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.entity.AppIDInfo;
import com.ctrip.platform.dal.daogen.entity.DynamicDSDataDto;
import com.ctrip.platform.dal.daogen.entity.SwitchHostIPInfo;
import com.ctrip.platform.dal.daogen.entity.TriggerMethod;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public List<DynamicDSDataDto> executeCheckDynamicDS() throws Exception{
        List<DynamicDSDataDto> dynamicDSDataList = new ArrayList<>();
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        Date checkTime = new Date();
//        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
//        Date checkTime = sdf.parse("2019-07-08 12:23:00");
        dalDynamicDSDao.checkSwitchDataSource(env, checkTime, TriggerMethod.MANUAL);
        for (Map.Entry<String, List<AppIDInfo>> titanKeyData: dalDynamicDSDao.getTitanKeyAppIDMap().entrySet()) {
            DynamicDSDataDto dynamicDSData = new DynamicDSDataDto();
            String appIds  = "";
            String hostIps = "";
            String successCount = "";
            String switchCount = "";
            for (AppIDInfo appIDInfo : titanKeyData.getValue()) {
                appIds += appIDInfo.getAppID() + "<br/>";
                for (SwitchHostIPInfo switchHostIPInfo : appIDInfo.getHostIPInfolist()) {
                    hostIps += switchHostIPInfo.getHostIP() + "<br/>";
                    switchCount += switchHostIPInfo.getStartSwitchPoint().size() + "<br/>";
                    successCount += switchHostIPInfo.getEndSwitchPoint().size() + "<br/>";
                }
                hostIps += "<br/>";
                switchCount += "<br/>";
                successCount += "<br/>";
            }
            dynamicDSData.setTitanKey(titanKeyData.getKey());
            dynamicDSData.setAppIds(appIds);
            dynamicDSData.setHostIps(hostIps);
            dynamicDSData.setSuccessCount(successCount);
            dynamicDSData.setSwitchCount(switchCount);
            dynamicDSDataList.add(dynamicDSData);
        }
        return dynamicDSDataList;
    }
}
