package com.ctrip.platform.dal.daogen;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.config.MonitorConfigManager;
import com.ctrip.platform.dal.daogen.entity.*;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.util.DateUtils;
import com.ctrip.platform.dal.daogen.util.EmailUtils;
import com.ctrip.platform.dal.daogen.util.IPUtils;
import com.ctrip.platform.dal.daogen.util.WriteExcel;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.dianping.cat.Cat;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
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
    private static final long FIXED_RATE = 3600; //second

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
        long initDelay = DateUtils.getFixInitDelay(nowDate);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getTiTanKeyInfoReport();
                Date checkDate = new Date();
                if (DateUtils.checkIsSendEMailTime(checkDate)) {
                    String subject = "TitanKey IP直连统计(" + DateUtils.getBeforeOneDay(checkDate).substring(0,8) +  ")";
                    EmailUtils.sendEmail(generateBodyContent(), subject, MonitorConfigManager.getMonitorConfig().getDBEmailRecipient(),
                            MonitorConfigManager.getMonitorConfig().getDBEmailCc(), WriteExcel.EXCEL_PATH);
                }
            }
        }, initDelay, FIXED_RATE, TimeUnit.SECONDS);
    }

    public void getTiTanKeyInfoReport() {
        Env envEntity = Foundation.server().getEnv();
        String env = envEntity.name().toLowerCase();
        int total = getTitanKeyTotal(env);
        TitanKeyPluginsResponse response = callTitanPluginsAPI(env, total);
        if (response == null) {
            return;
        }
        int titanKeyCount = 0;
        int useMysqlCount = 0;
        int useSqlServerCount = 0;
        int directConnectMysqlCount = 0;
        int directConnectSqlServerCount = 0;
        List<AbnormalTitanKey> abnormalTitanKeys = new ArrayList<>();
        List<TitanKeyAPIInfo> unUseDynamicDSTitanKey = new ArrayList<>();
        for (TitanKeyAPIInfo titanKeyAPIInfo : response.getData().getData()) {
            //过滤子环境
            if (StringUtils.isEmpty(titanKeyAPIInfo.getSubEnv())) {
                String serverIp = titanKeyAPIInfo.getConnectionInfo().getServerIp();
                String serverName = titanKeyAPIInfo.getConnectionInfo().getServer();
                if (!IPUtils.isSlaveDomain(serverName) && !IPUtils.isSlaveDomain(serverIp)) {
                    titanKeyCount++;
                    if (MYSQL_PROVIDER_NAME.equalsIgnoreCase(titanKeyAPIInfo.getProviderName())) {
                        useMysqlCount++;
                        if (StringUtils.isNotBlank(serverIp) && IPUtils.isIPAddress(serverIp)) {
                            directConnectMysqlCount++;
                        } else if (StringUtils.isNotBlank(serverName) && IPUtils.isIPAddress(serverName)) {
                            directConnectMysqlCount++;
                        }
                        if ((StringUtils.isNotBlank(serverIp) && !IPUtils.isIPAddress(serverIp)) ||
                                (StringUtils.isEmpty(serverIp) && StringUtils.isNotBlank(serverName) && !IPUtils.isIPAddress(serverName))) {
                            unUseDynamicDSTitanKey.add(titanKeyAPIInfo);
                        }
                    } else if (SQLSERVER_PROVIDER_NAME.equalsIgnoreCase(titanKeyAPIInfo.getProviderName())) {
                        useSqlServerCount++;
                        if (StringUtils.isNotBlank(serverIp) && IPUtils.isIPAddress(serverIp)) {
                            directConnectSqlServerCount++;
                        } else if (StringUtils.isNotBlank(serverName) && IPUtils.isIPAddress(serverName)) {
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
        }
        titanKeyInfoReportDto.setTitanKeyCount(titanKeyCount);
        titanKeyInfoReportDto.setUseMysqlCount(useMysqlCount);
        titanKeyInfoReportDto.setUseSqlServerCount(useSqlServerCount);
        titanKeyInfoReportDto.setDirectConnectDBCount(directConnectSqlServerCount + directConnectMysqlCount);
        titanKeyInfoReportDto.setDirectConnectMysqlCount(directConnectMysqlCount);
        titanKeyInfoReportDto.setDirectConnectSqlServerCount(directConnectSqlServerCount);
        titanKeyInfoReportDto.setAbnormalTitanKeyList(abnormalTitanKeys);
        titanKeyInfoReportDto.setStatisticsDate(DateUtils.formatDate(new Date()));
        titanKeyInfoReportDto.setUnUseDynamicDSTitanKey(unUseDynamicDSTitanKey);
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

    public String generateBodyContent() {
        DecimalFormat df = new DecimalFormat("0.00%");
        String htmlTemplate = "<entry><content><![CDATA[%s]]></content></entry>";

//        String htmlTitanKeyTable = "<div><span style=\"font-size: 20px\">TitanKey IP直连统计</span></div><table style=\"border-collapse:collapse;width: auto\"><thead><tr><th style=\"border:1px solid #B0B0B0\" width= \"120\">TitanKey总数</th>" +
//                "<th style=\"border:1px solid #B0B0B0\" width= \"120\">使用MySql数量</th><th style=\"border:1px solid #B0B0B0\" width= \"150\">使用SqlServer数量</th><th style=\"border:1px solid #B0B0B0\" width= \"120\">IP直连数量</th>" +
//                "<th style=\"border:1px solid #B0B0B0\" width= \"150\">IP直连MySql数量</th><th style=\"border:1px solid #B0B0B0\" width= \"150\">IP直连SqlServer数量</th></tr></thead><tbody>%s</tbody></table>";

        String htmlTitanKeyTableDB = "<table style=\"border-collapse:collapse;width: auto\"><thead><tr><th style=\"border:1px solid #B0B0B0\" width= \"120\">TitanKey主库总数</th>" +
                "<th style=\"border:1px solid #B0B0B0\" width= \"150\">IP直连主库数量</th><th style=\"border:1px solid #B0B0B0\" width= \"150\">IP直连主库占比</th></tr></thead><tbody>%s</tbody></table>";

        String htmlTitanKeyTableMySql = "<table style=\"border-collapse:collapse;width: auto\"><thead><tr><th style=\"border:1px solid #B0B0B0\" width= \"120\">MySql主库总数</th>" +
                "<th style=\"border:1px solid #B0B0B0\" width= \"150\">IP直连MySql主库数量</th><th style=\"border:1px solid #B0B0B0\" width= \"150\">IP直连MySql主库占比</th></tr></thead><tbody>%s</tbody></table>";

        String htmlTitanKeyTableSqlServer = "<table style=\"border-collapse:collapse;width: auto\"><thead><tr><th style=\"border:1px solid #B0B0B0\" width= \"120\">SqlServer主库总数</th>" +
                "<th style=\"border:1px solid #B0B0B0\" width= \"150\">IP直连SqlServer主库数量</th><th style=\"border:1px solid #B0B0B0\" width= \"150\">IP直连SqlServer主库占比</th></tr></thead><tbody>%s</tbody></table>";

//        String bodyTemplate = "<tr><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td>" +
//                "<td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td></tr>";
//        String tableData = String.format(bodyTemplate, titanKeyInfoReportDto.getTitanKeyCount(), titanKeyInfoReportDto.getUseMysqlCount(), titanKeyInfoReportDto.getUseSqlServerCount(),
//                titanKeyInfoReportDto.getDirectConnectDBCount(), titanKeyInfoReportDto.getDirectConnectMysqlCount(), titanKeyInfoReportDto.getDirectConnectSqlServerCount());
//        String titanKeyTableContent = String.format(htmlTitanKeyTable, tableData);

        String bodyTemplateDB = "<tr><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td></tr>";
        double dbPercent = (double) titanKeyInfoReportDto.getDirectConnectDBCount() / titanKeyInfoReportDto.getTitanKeyCount();
        double mysqlPercent = (double) titanKeyInfoReportDto.getDirectConnectMysqlCount() / titanKeyInfoReportDto.getUseMysqlCount();
        double sqlServerPercent = (double) titanKeyInfoReportDto.getDirectConnectSqlServerCount() / titanKeyInfoReportDto.getUseSqlServerCount();
        String tableDataDB = String.format(bodyTemplateDB, titanKeyInfoReportDto.getTitanKeyCount(), titanKeyInfoReportDto.getDirectConnectDBCount(),
                df.format(dbPercent));
        String tableDataMySql = String.format(bodyTemplateDB, titanKeyInfoReportDto.getUseMysqlCount(), titanKeyInfoReportDto.getDirectConnectMysqlCount(),
                df.format(mysqlPercent));
        String tableDataSqlServer = String.format(bodyTemplateDB, titanKeyInfoReportDto.getUseSqlServerCount(), titanKeyInfoReportDto.getDirectConnectSqlServerCount(),
                df.format(sqlServerPercent));
        String titanKeyTableDBContent = String.format(htmlTitanKeyTableDB, tableDataDB);
        String titanKeyTableMySqlContent = String.format(htmlTitanKeyTableMySql, tableDataMySql);
        String titanKeyTableSqlServerContent = String.format(htmlTitanKeyTableSqlServer, tableDataSqlServer);

        String htmlAbnormalTitanKeyTable = "<div style=\"margin-top: 20px\"><span style=\"font-size: 20px\">TitanKey配置异常统计(TitanKey配置serverIp不是ip或者serverName不是域名)</span></div><table style=\"border-collapse:collapse\"><thead><tr><th style=\"border:1px solid #B0B0B0\" width= \"200\">TitanKey     </th>" +
                "<th style=\"border:1px solid #B0B0B0\" width= \"120\">ServerIp</th><th style=\"border:1px solid #B0B0B0\" width= \"120\">ServerName   </th></tr></thead><tbody>%s</tbody></table>";
        String htmlUnUseDynamicDSTitanKeyTable = "<div style=\"margin-top: 20px\"><span style=\"font-size: 20px\">未接入Dal动态数据源的TitanKey统计(非ip直连MySql主库，总数：%s)</span><p>%s</p></div>";
        String abnormalTitanKeyBodyTemplate = "<tr><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td><td style=\"border:1px solid #B0B0B0;text-align: center\">%s</td></tr>";
        StringBuilder sb = new StringBuilder();
        for (AbnormalTitanKey abnormalTitanKey : titanKeyInfoReportDto.getAbnormalTitanKeyList()) {
            sb.append(String.format(abnormalTitanKeyBodyTemplate, abnormalTitanKey.getTitanKey(), abnormalTitanKey.getServerIp(), abnormalTitanKey.getServerName()));
        }
        String abnormalTitanKeyTableContent = String.format(htmlAbnormalTitanKeyTable, sb.toString());

        boolean writeSuccess = WriteExcel.writeExcel(titanKeyInfoReportDto.getUnUseDynamicDSTitanKey());
        String unUseDynamicDSTitanKeyContent = String.format(htmlUnUseDynamicDSTitanKeyTable, titanKeyInfoReportDto.getUnUseDynamicDSTitanKey().size(), writeSuccess ? "见附件" : " 写入文件失败");

        return String.format(htmlTemplate, titanKeyTableDBContent + titanKeyTableMySqlContent + titanKeyTableSqlServerContent + abnormalTitanKeyTableContent + unUseDynamicDSTitanKeyContent);
    }
}
