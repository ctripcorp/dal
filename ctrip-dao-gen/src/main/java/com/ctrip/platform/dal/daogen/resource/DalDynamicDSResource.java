package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.entity.*;

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
    public List<DynamicDSDataDto> executeCheckDynamicDS(@QueryParam("settingDate") String settingDate) throws Exception{
        List<DynamicDSDataDto> dynamicDSDataList = new ArrayList<>();
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
        Date checkTime = sdf.parse(settingDate.replace('T', ' '));
        dalDynamicDSDao.checkSwitchDataSource(env, checkTime, TriggerMethod.MANUAL);
        for (Map.Entry<SwitchTitanKey, List<AppIDInfo>> titanKeyData: dalDynamicDSDao.getTitanKeyAppIDMap().entrySet()) {
            DynamicDSDataDto dynamicDSData = new DynamicDSDataDto();
            String appIds  = "";
            String hostIps = "";
            String hostSuccessCount = "";
            String hostSwitchCount = "";
            String titanKeySwitchCount = titanKeyData.getKey().getSwitchCount().size() + "<br/>";
            for (AppIDInfo appIDInfo : titanKeyData.getValue()) {
                appIds += appIDInfo.getAppID() + "<br/>";
                for (SwitchHostIPInfo switchHostIPInfo : appIDInfo.getHostIPInfolist()) {
                    hostIps += switchHostIPInfo.getHostIP() + "<br/>";
                    hostSwitchCount += switchHostIPInfo.getStartSwitchPoint().size() + "<br/>";
                    hostSuccessCount += switchHostIPInfo.getEndSwitchPoint().size() + "<br/>";
                }
                hostIps += "<br/>";
                hostSwitchCount += "<br/>";
                hostSuccessCount += "<br/>";
            }
            dynamicDSData.setTitanKey(titanKeyData.getKey().getTitanKey());
            dynamicDSData.setAppIds(appIds);
            dynamicDSData.setHostIps(hostIps);
            dynamicDSData.setHostSuccessCount(hostSuccessCount);
            dynamicDSData.setHostSwitchCount(hostSwitchCount);
            dynamicDSData.setTitanKeySwitchCount(titanKeySwitchCount);
            dynamicDSDataList.add(dynamicDSData);
        }
        return dynamicDSDataList;
    }
}
