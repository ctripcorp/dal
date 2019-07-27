package com.ctrip.platform.dal.daogen;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.entity.AbnormalTitanKey;
import com.ctrip.platform.dal.daogen.entity.TitanKeyAPIInfo;
import com.ctrip.platform.dal.daogen.entity.TitanKeyInfoReportDto;
import com.ctrip.platform.dal.daogen.entity.TitanKeyPluginsResponse;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.util.DateUtils;
import com.ctrip.platform.dal.daogen.util.IPUtils;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.dianping.cat.Cat;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by taochen on 2019/7/26.
 */
public class TitanKeyInfoReportDao {
    private static final long FIXED_RATE = 3600 * 24; //second

    private static final String TITANKEY_LIST_API = "http://qconfig.ctripcorp.com/plugins/titan/configs?appid=100010061&env=%s&pageNo=1&pageSize=%s";

    private static final String MYSQL_PROVIDER_NAME = "MySql.Data.MySqlClient";

    private static final String SQLSERVER_PROVIDER_NAME = "System.Data.SqlClient";

    private static final int RETRY_TIME = 3;

    private static TitanKeyInfoReportDao titanKeyInfoReportDao = null;

    private TitanKeyInfoReportDto titanKeyInfoReportDto = new TitanKeyInfoReportDto();

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static TitanKeyInfoReportDao getInstance() {
        if (titanKeyInfoReportDao == null) {
            titanKeyInfoReportDao = new TitanKeyInfoReportDao();
        }
        return titanKeyInfoReportDao;
    }
    private TitanKeyInfoReportDao() { }

    public void init() {
        //init_delay 设置为每个小时的整点执行
        Date nowDate = new Date();
        long initDelay = DateUtils.getZeroInitDelay(nowDate);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getTiTanKeyInfoReport();
                Date checkDate = new Date();
                if (DateUtils.checkIsSendEMailTime(checkDate)) {

                }
            }
        }, initDelay, FIXED_RATE, TimeUnit.SECONDS);
    }

    public void getTiTanKeyInfoReport() {
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        int total = getTitanKeyTotal(env);
        TitanKeyPluginsResponse response = callTitanPluginsAPI(env, total);
        int titanKeyCount = 0;
        int useMysqlCount = 0;
        int useSqlServerCount = 0;
        int directConnectMysqlCount = 0;
        int directConnectSqlServerCount = 0;
        List<AbnormalTitanKey> abnormalTitanKeys = new ArrayList<>();
        for (TitanKeyAPIInfo titanKeyAPIInfo : response.getData().getData()) {
            //过滤子环境
            if (StringUtils.isEmpty(titanKeyAPIInfo.getSubEnv())) {
                titanKeyCount++;
                String serverIp = titanKeyAPIInfo.getConnectionInfo().getServerIp();
                String serverName = titanKeyAPIInfo.getConnectionInfo().getServer();
                if (MYSQL_PROVIDER_NAME.equalsIgnoreCase(titanKeyAPIInfo.getProviderName())) {
                    useMysqlCount++;
                    if (StringUtils.isNotBlank(serverIp) && IPUtils.isIPAddress(serverIp)) {
                        directConnectMysqlCount++;
                    }
                    else if (StringUtils.isNotBlank(serverName) && IPUtils.isIPAddress(serverName)) {
                        directConnectMysqlCount++;
                    }
                }
                else if (SQLSERVER_PROVIDER_NAME.equalsIgnoreCase(titanKeyAPIInfo.getProviderName())) {
                    useSqlServerCount++;
                    if (StringUtils.isNotBlank(serverIp) && IPUtils.isIPAddress(serverIp)) {
                        directConnectSqlServerCount++;
                    }
                    else if (StringUtils.isNotBlank(serverName) && IPUtils.isIPAddress(serverName)) {
                        directConnectSqlServerCount++;
                    }
                }
                if ((StringUtils.isNotBlank(serverIp) && !IPUtils.isIPAddress(serverIp)) ||
                        (StringUtils.isNotBlank(serverName)) && IPUtils.isIPAddress(serverName)) {
                    AbnormalTitanKey abnormalTitanKey = new AbnormalTitanKey();
                    abnormalTitanKey.setTitanKey(titanKeyAPIInfo.getName());
                    abnormalTitanKey.setServerIp(serverIp);
                    abnormalTitanKey.setServerName(serverName);
                    abnormalTitanKeys.add(abnormalTitanKey);
                }
            }
        }
        titanKeyInfoReportDto.setTitanKeyCount(titanKeyCount);
        titanKeyInfoReportDto.setUseMysqlCount(useMysqlCount);
        titanKeyInfoReportDto.setUseSqlServerCount(useSqlServerCount);
        titanKeyInfoReportDto.setDirectConnectDBCount(directConnectSqlServerCount + directConnectMysqlCount);
        titanKeyInfoReportDto.setDirectConnectMysqlCount(directConnectMysqlCount);
        titanKeyInfoReportDto.setDirectConnectSqlServerCount(directConnectSqlServerCount);
        titanKeyInfoReportDto.setAbnormalTitanKeyList(abnormalTitanKeys);
    }

    private int getTitanKeyTotal(String env) {
        TitanKeyPluginsResponse response = callTitanPluginsAPI(env, 1);
        if (response != null) {
            return response.getData().getTotal();
        }
        return 0;
    }

    private TitanKeyPluginsResponse callTitanPluginsAPI(String env, int pageSize) {
        String formatUrl = String.format(TITANKEY_LIST_API, env, pageSize);
        TitanKeyPluginsResponse response = null;
        for (int i = 0; i < RETRY_TIME; ++i) {
            try {
                response = HttpUtil.getJSONEntity(TitanKeyPluginsResponse.class, formatUrl, null, HttpMethod.HttpGet);
                if (response.getStatus() == 0 && response.getData() != null) {
                    break;
                }
            } catch (Exception e) {
                Cat.logError("call titanUrl: " + formatUrl + " fail!", e);
            }
        }
        return response;
    }

    public TitanKeyInfoReportDto getTitanKeyInfoReportDto() {
        return titanKeyInfoReportDto;
    }

    private String generateBodyContent() {
        String htmlTemplate = "<entry><content><![CDATA[%s]]></content></entry>";
        String htmlTable = " <table style=\"border-collapse:collapse\"><thead><tr><th style=\"border:1px solid #B0B0B0\" width= \"200\">TitanKey总数</th>" +
                "<th style=\"border:1px solid #B0B0B0\" width= \"120\">使用MySql数量</th><th style=\"border:1px solid #B0B0B0\" width= \"120\">使用SqlServer数量</th><th style=\"border:1px solid #B0B0B0\" width= \"120\">IP直连数据库数量</th>" +
                "<th style=\"border:1px solid #B0B0B0\" width= \"120\">IP直连MySql数量</th><th style=\"border:1px solid #B0B0B0\" width= \"120\">IP直连SqlServer数量</th></tr></thead><tbody>%s</tbody></table>";
        String bodyTemplate = "<tr><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td>" +
                "<td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td></tr>";
        String table = String.format(bodyTemplate, titanKeyInfoReportDto.getTitanKeyCount(), titanKeyInfoReportDto.getUseMysqlCount(), titanKeyInfoReportDto.getUseSqlServerCount(),
                titanKeyInfoReportDto.getDirectConnectDBCount(), titanKeyInfoReportDto.getDirectConnectMysqlCount(), titanKeyInfoReportDto.getDirectConnectSqlServerCount());
        return null;
    }
}
