package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.TitanKeyInfoReportDao;
import com.ctrip.platform.dal.daogen.config.MonitorConfigManager;
import com.ctrip.platform.dal.daogen.entity.TitanKeyInfoReportDto;
import com.ctrip.platform.dal.daogen.util.DateUtils;
import com.ctrip.platform.dal.daogen.util.EmailUtils;
import com.ctrip.platform.dal.daogen.util.WriteExcel;
import com.dianping.cat.Cat;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("test")
    public boolean testSendEmailAndWriteExcel() {
        TitanKeyInfoReportDao titanKeyInfoReportDao = TitanKeyInfoReportDao.getInstance();
        titanKeyInfoReportDao.getTiTanKeyInfoReport();
        String subject = "TitanKey IP直连统计(" + DateUtils.getBeforeOneDay(new Date()).substring(0,8) +  ")";
        EmailUtils.sendEmail(titanKeyInfoReportDao.generateBodyContent(), subject, "taochen@ctrip.com",
                "taochen@ctrip.com", WriteExcel.EXCEL_PATH);
        return true;
    }

}
