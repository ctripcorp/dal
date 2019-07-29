package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.TitanKeyInfoReportDao;
import com.ctrip.platform.dal.daogen.entity.TitanKeyInfoReportDto;
import com.dianping.cat.Cat;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by taochen on 2019/7/26.
 */
@Resource
@Singleton
@Path("titanKeyInfoReport")
public class TitanKeyInfoReportResource {
    private static TitanKeyInfoReportDao titanKeyInfoReportDao = null;

    static {
        try {
            titanKeyInfoReportDao = TitanKeyInfoReportDao.getInstance();
        } catch (Exception e) {
            Cat.logError("get TitanKeyInfoReportDao instance fail!", e);
        }
    }

    public static void initTitanKeyInfoReportDao() {
        titanKeyInfoReportDao.init();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getTitanKeyInfoReport")
    public TitanKeyInfoReportDto getTitanKeyInfoReport() {
        return titanKeyInfoReportDao.getTitanKeyInfoReportDto();
    }
}
