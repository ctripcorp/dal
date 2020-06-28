package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.DalReportDao;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.report.App;
import com.ctrip.platform.dal.daogen.report.CMSApp;
import com.ctrip.platform.dal.daogen.report.Client;
import com.ctrip.platform.dal.daogen.report.Dept;
import com.ctrip.platform.dal.daogen.report.RawInfo;
import com.ctrip.platform.dal.daogen.report.newReport.NewDatabaseCategory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Workbook;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Resource
@Singleton
@Path("report")
public class DalReportResource {
    private static ObjectMapper mapper = new ObjectMapper();
    private static DalReportDao reportDao = null;
    private static final String MIME_EXCEL = "application/vnd.ms-excel";
    private static final String EXCEL_FILE_NAME = "dal_version";
    private static final String EXCEL_FILE_NAME2 = "dal_local_datasource";
    private static final String SQL_SERVER_CHECKED = "sqlServerChecked";
    private static final String MY_SQL_CHECKED = "mySqlChecked";

    // thread safe
    static {
        try {
            reportDao = DalReportDao.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initReportData() {
        reportDao.init();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getRaw")
    public RawInfo getRaw() throws Exception {
        return reportDao.getNewRawInfo();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("upgradeMysqlDBDomain")
    public String upgradeMysqlDBDomain() throws JsonProcessingException {
         return mapper.writeValueAsString(Boolean.valueOf(DalReportDao.getInstance().upgradeMysqlDBDomain()));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getReportByDept")
    public List<Dept> getReportByDept(@QueryParam("dept") String dept,
            @QueryParam("sqlServerChecked") boolean sqlServerChecked, @QueryParam("mySqlChecked") boolean mySqlChecked)
            throws Exception {
        try {
            List<Dept> list = null;
            if (dept == null || dept.length() == 0)
                return list;

            NewDatabaseCategory databaseCategory = getDatabaseCategory(sqlServerChecked, mySqlChecked);
            Map<String, List<String>> map = reportDao.getMapByDept(dept, databaseCategory);
            if (map == null || map.size() == 0)
                return list;

            list = new ArrayList<>();
            Map<String, CMSApp> appInfo = reportDao.getAllCMSAppInfo();
            Date lastUpdate = reportDao.getLastUpdate();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                List<String> appIds = entry.getValue();
                if (appIds == null || appIds.isEmpty())
                    continue;

                Dept d = new Dept();
                d.setLastUpdate(lastUpdate);
                d.setVersion(entry.getKey());
                List<App> apps = new ArrayList<>();
                reportDao.processAppList(appIds, appInfo, apps);
                d.setApps(apps);
                list.add(d);
            }
            return list;
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getReportByVersion")
    public List<Client> getReportByVersion(@QueryParam("version") String version,
            @QueryParam("sqlServerChecked") boolean sqlServerChecked, @QueryParam("mySqlChecked") boolean mySqlChecked)
            throws Exception {
        try {
            List<Client> list = null;
            if (version == null || version.length() == 0)
                return list;

            NewDatabaseCategory databaseCategory = getDatabaseCategory(sqlServerChecked, mySqlChecked);
            Map<String, List<String>> map = reportDao.getMapByVersion(version, databaseCategory);
            if (map == null || map.size() == 0)
                return list;

            list = new ArrayList<>();
            Map<String, CMSApp> appInfo = reportDao.getAllCMSAppInfo();
            Date lastUpdate = reportDao.getLastUpdate();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                List<String> appIds = entry.getValue();
                if (appIds == null || appIds.isEmpty())
                    continue;

                Client c = new Client();
                c.setLastUpdate(lastUpdate);
                c.setDept(entry.getKey());
                List<App> apps = new ArrayList<>();
                reportDao.processAppList(appIds, appInfo, apps);
                c.setApps(apps);
                list.add(c);
            }
            return list;
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @POST
    @Path("exportExcel")
    @Produces(MIME_EXCEL)
    public String exportExcel(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws Exception {
        Workbook workbook = null;
        OutputStream stream = null;
        try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream()) {
            response.setContentType(MIME_EXCEL);
            response.setHeader("Content-Disposition", String.format("attachment; filename=%s.xls", EXCEL_FILE_NAME));
            boolean sqlServerChecked = getSqlServerChecked(request);
            boolean mySqlChecked = getMySqlChecked(request);
            NewDatabaseCategory databaseCategory = getDatabaseCategory(sqlServerChecked, mySqlChecked);
            workbook = reportDao.getWorkbook(databaseCategory);
            workbook.write(byteArrayStream);
            byte[] outArray = byteArrayStream.toByteArray();
            stream = response.getOutputStream();
            stream.write(outArray);
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        } finally {
            if (workbook != null)
                workbook.close();
            if (stream != null) {
                stream.flush();
                stream.close();
            }
        }
        return "";
    }

    @GET
    @Path("forceFresh")
    public String forceFresh() throws Exception {
        String result = "Task is running,can't refresh right now,please try again later.";
        if (!reportDao.isTaskRunning) {
            reportDao.runTask();
            result = "Task completed.";
        }
        return result;
    }

    private NewDatabaseCategory getDatabaseCategory(boolean sqlServerChecked, boolean mySqlChecked) {
        if (sqlServerChecked && mySqlChecked)
            return NewDatabaseCategory.All;

        if (sqlServerChecked)
            return NewDatabaseCategory.SqlServer;

        if (mySqlChecked)
            return NewDatabaseCategory.MySql;

        return NewDatabaseCategory.All;
    }

    private boolean getSqlServerChecked(HttpServletRequest request) {
        return getCheckedParameter(request, SQL_SERVER_CHECKED);
    }

    private boolean getMySqlChecked(HttpServletRequest request) {
        return getCheckedParameter(request, MY_SQL_CHECKED);
    }

    private boolean getCheckedParameter(HttpServletRequest request, String parameterName) {
        if (request == null)
            return false;

        String parameter = request.getParameter(parameterName);
        if (parameter == null || parameter.isEmpty())
            return false;

        return Boolean.parseBoolean(parameter);
    }

}
