package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.entity.AppIDInfo;
import com.ctrip.platform.dal.daogen.entity.TriggerMethod;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("executeCheckDynamicDS")
    public Map<String, List<AppIDInfo>> executeCheckDynamicDS(@QueryParam("checkTime") Date checkTime) {
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        dalDynamicDSDao.checkSwitchDataSource(env, checkTime, TriggerMethod.MANUAL);
        return null;
    }
}
