package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.TitanKeyInfoReportDao;
import com.ctrip.platform.dal.daogen.entity.TitanKeyInfoReportDto;
import org.junit.Test;

/**
 * Created by taochen on 2019/7/26.
 */
public class TitanKeyInfoReportTest {
    @Test
    public void testTitanPluginsAPI() {
        TitanKeyInfoReportDao titanKeyInfoReportDao = TitanKeyInfoReportDao.getInstance();
        titanKeyInfoReportDao.getTiTanKeyInfoReport();
        TitanKeyInfoReportDto titanKeyInfoReportDto = titanKeyInfoReportDao.getTitanKeyInfoReportDto();
        System.out.println();
    }
}
