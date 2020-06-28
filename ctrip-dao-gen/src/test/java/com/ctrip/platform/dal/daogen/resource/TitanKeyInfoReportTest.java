package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.TitanKeyInfoReportDao;
import com.ctrip.platform.dal.daogen.config.MonitorConfig;
import com.ctrip.platform.dal.daogen.config.MonitorConfigManager;
import com.ctrip.platform.dal.daogen.entity.TitanKeyInfoReportDto;
import com.ctrip.platform.dal.daogen.util.DateUtils;
import com.ctrip.platform.dal.daogen.util.EmailUtils;
import com.ctrip.platform.dal.daogen.util.IPUtils;
import com.ctrip.platform.dal.daogen.util.WriteExcel;
import org.junit.Test;

import java.util.Date;

/**
 * Created by taochen on 2019/7/26.
 */
public class TitanKeyInfoReportTest {
//    @Test
    public void testTitanPluginsAPI() {
        TitanKeyInfoReportDao titanKeyInfoReportDao = TitanKeyInfoReportDao.getInstance();
        titanKeyInfoReportDao.getTiTanKeyInfoReport();
        TitanKeyInfoReportDto titanKeyInfoReportDto = titanKeyInfoReportDao.getTitanKeyInfoReportDto();
        System.out.println();
    }

    @Test
    public void testGetLocalIP() {
        System.out.println(IPUtils.getLocalHostIp());
    }

//    @Test
    public void testGetTiTanKeyInfoReport() {
        TitanKeyInfoReportDao titanKeyInfoReportDao = TitanKeyInfoReportDao.getInstance();
        titanKeyInfoReportDao.getTiTanKeyInfoReport();
    }

//    @Test
    public void testSendEmail() {
        TitanKeyInfoReportDao titanKeyInfoReportDao = TitanKeyInfoReportDao.getInstance();
        titanKeyInfoReportDao.getTiTanKeyInfoReport();
        String subject = "TitanKey IP直连统计(" + DateUtils.getBeforeOneDay(new Date()).substring(0,8) +  ")";
        EmailUtils.sendEmail(titanKeyInfoReportDao.generateBodyContent(), subject, MonitorConfigManager.getMonitorConfig().getDBEmailRecipient(),
                MonitorConfigManager.getMonitorConfig().getDBEmailCc(), WriteExcel.EXCEL_PATH);
    }

    @Test
    public void testMonitorConfig() {
        MonitorConfig monitorConfig = MonitorConfigManager.getMonitorConfig();
        System.out.println(monitorConfig);
    }

    @Test
    public void testGetDomainRead() {
        TitanKeyInfoReportDao titanKeyInfoReportDao = TitanKeyInfoReportDao.getInstance();
        titanKeyInfoReportDao.getTiTanKeyInfoReport();
        TitanKeyInfoReportDto titanKeyInfoReportDto = titanKeyInfoReportDao.getTitanKeyInfoReportDto();
        titanKeyInfoReportDto.getUnUseDynamicDSTitanKey();
    }
}
