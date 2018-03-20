package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.DalReportDao;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.report.App;
import com.ctrip.platform.dal.daogen.report.CMSApp;
import com.ctrip.platform.dal.daogen.report.Client;
import com.ctrip.platform.dal.daogen.report.Dept;
import com.ctrip.platform.dal.daogen.report.RawInfo;
import org.apache.poi.ss.usermodel.Workbook;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
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
    private static DalReportDao reportDao = null;
    private static final String MIME_EXCEL = "application/vnd.ms-excel";
    private static final String EXCEL_FILE_NAME = "dal_version";
    private static final String EXCEL_FILE_NAME2 = "dal_local_datasource";

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
    @Path("getReportByDept")
    public List<Dept> getReportByDept(@QueryParam("dept") String dept) throws Exception {
        try {
            List<Dept> list = null;
            if (dept == null || dept.length() == 0)
                return list;

            Map<String, List<String>> map = reportDao.getMapByDept(dept);
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
    public List<Client> getReportByVersion(@QueryParam("version") String version) throws Exception {
        try {
            List<Client> list = null;
            if (version == null || version.length() == 0)
                return list;

            Map<String, List<String>> map = reportDao.getMapByVersion(version);
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

    @GET
    @Path("exportExcel")
    @Produces(MIME_EXCEL)
    public String exportExcel(@Context HttpServletResponse response) throws Exception {
        Workbook workbook = null;
        OutputStream stream = null;
        try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream()) {
            response.setContentType(MIME_EXCEL);
            response.setHeader("Content-Disposition", String.format("attachment; filename=%s.xls", EXCEL_FILE_NAME));
            workbook = reportDao.getWorkbook();
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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getLocalDatasourceAppList")
    public List<App> getLocalDatasourceAppList() throws Exception {
        return reportDao.getLocalDatasourceAppList();
    }

    @GET
    @Path("exportExcel2")
    @Produces(MIME_EXCEL)
    public String exportExcel2(@Context HttpServletResponse response) throws Exception {
        Workbook workbook = null;
        OutputStream stream = null;
        try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream()) {
            response.setContentType(MIME_EXCEL);
            response.setHeader("Content-Disposition", String.format("attachment; filename=%s.xls", EXCEL_FILE_NAME2));
            workbook = reportDao.getWorkbook2();
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

}
