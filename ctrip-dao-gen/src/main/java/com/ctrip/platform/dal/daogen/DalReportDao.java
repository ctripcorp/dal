package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.report.All;
import com.ctrip.platform.dal.daogen.report.App;
import com.ctrip.platform.dal.daogen.report.CMSApp;
import com.ctrip.platform.dal.daogen.report.CMSAppInfo;
import com.ctrip.platform.dal.daogen.report.DALLocalDatasource;
import com.ctrip.platform.dal.daogen.report.DalReport;
import com.ctrip.platform.dal.daogen.report.Machines;
import com.ctrip.platform.dal.daogen.report.RawInfo;
import com.ctrip.platform.dal.daogen.report.Report;
import com.ctrip.platform.dal.daogen.report.Root;
import com.ctrip.platform.dal.daogen.report.Types;
import com.ctrip.platform.dal.daogen.report.Version;
import com.ctrip.platform.dal.daogen.report.newReport.NewApp;
import com.ctrip.platform.dal.daogen.report.newReport.NewDALversion;
import com.ctrip.platform.dal.daogen.report.newReport.NewName;
import com.ctrip.platform.dal.daogen.report.newReport.NewReport;
import com.ctrip.platform.dal.daogen.report.newReport.NewRoot;
import com.ctrip.platform.dal.daogen.report.newReport.TypeDetails;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DalReportDao {
    private static DalReportDao reportDao = null;

    public static DalReportDao getInstance() {
        if (reportDao == null) {
            reportDao = new DalReportDao();
        }
        return reportDao;
    }

    private static final String DAL_LOCAL_DATASOURCE =
            "http://cat.ctripcorp.com/cat/r/e?domain=All&ip=All&type=DAL.local.datasource&min=-1&max=-1&forceDownload=json";

    private static final String NEW_DAL_VERSION_URL =
            "http://cat.ctripcorp.com/cat/r/globalEvent?type=DAL.version&op=appDetail&forceDownload=json";

    private static final String CMS_ALL_APPS_URL = "http://osg.ops.ctripcorp.com/api/11209";
    private static final String CMS_TOKEN = "70c152d9c4980f8843c497ed9b6b5386";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REQUEST_BODY = "request_body";

    private static final String LOCAL_DATASOURCE = "DAL.local.datasource";

    private static final String ALL = "All";

    private List<DalReport> reportList = null;
    private ConcurrentHashMap<String, CMSApp> appInfoMap = null;
    private Date lastUpdate = null;

    public boolean isTaskRunning = false;

    private static final int COLUMN_COUNT = 6;
    private static final int COLUMN_COUNT2 = 7;

    // minutes
    private static final long INIT_DELAY = 0;
    private static final long DELAY = 60;

    public void init() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new ReportTask(), INIT_DELAY, DELAY, TimeUnit.MINUTES);
    }

    public List<App> getLocalDatasourceAppList() throws Exception {
        List<App> list = new ArrayList<>();
        List<String> appIds = getLocalDatasourceAppIds();
        if (appIds.isEmpty())
            return list;
        Collections.sort(appIds);
        Map<String, DalReport> map = convertListToMap(reportList);
        Map<String, CMSApp> cmsMap = getAllCMSAppInfo();
        for (String appId : appIds) {
            App app = new App();
            app.setId(appId);
            DalReport report = map.get(appId);
            if (report != null) {
                app.setVersion(report.getVersion());
            }
            CMSApp cmsApp = cmsMap.get(appId);
            if (cmsApp != null) {
                app.setOrgName(cmsApp.getOrgName());
                app.setName(cmsApp.getAppName());
                app.setChineseName(cmsApp.getChineseName());
                app.setOwner(cmsApp.getOwner());
                app.setOwnerEmail(cmsApp.getOwnerEmail());
            }
            list.add(app);
        }

        return list;
    }

    private List<String> getLocalDatasourceAppIds() throws Exception {
        List<String> appIds = new ArrayList<>();
        Root root = HttpUtil.getJSONEntity(Root.class, DAL_LOCAL_DATASOURCE, null, HttpMethod.HttpGet);
        if (root == null)
            return appIds;
        Report report = root.getReport();
        if (report == null)
            return appIds;
        Machines machines = report.getMachines();
        if (machines == null)
            return appIds;
        All all = machines.getAll();
        if (all == null)
            return appIds;
        Types types = all.getTypes();
        if (types == null)
            return appIds;
        DALLocalDatasource localDatasource = types.getDalLocalDatasource();
        if (localDatasource == null)
            return appIds;
        Map<String, Version> map = localDatasource.getNames();
        if (map != null && map.size() > 0) {
            appIds.addAll(map.keySet());
        }
        return appIds;
    }

    public RawInfo getNewRawInfo() throws Exception {
        RawInfo raw = new RawInfo();
        NewRoot root = HttpUtil.getJSONEntity(NewRoot.class, NEW_DAL_VERSION_URL, null, HttpMethod.HttpGet);
        if (root == null)
            return raw;

        NewReport report = root.getReport();
        if (report == null)
            return raw;

        String[] bus = report.getBus();
        if (bus == null || bus.length == 0)
            return raw;

        TypeDetails typeDetails = report.getTypeDetails();
        if (typeDetails == null)
            return raw;

        NewDALversion dalVersion = typeDetails.getDaLVersion();
        if (dalVersion == null)
            return raw;

        Map<String, NewName> nameDetails = dalVersion.getNameDetails();
        if (nameDetails == null || nameDetails.isEmpty())
            return raw;

        List<String> depts = new ArrayList<>();
        depts.add(ALL);
        List<String> temp = Arrays.asList(bus);
        depts.addAll(temp);
        raw.setDepts(depts);

        List<String> versions = new ArrayList<>();
        versions.add(ALL);
        List<String> list = new ArrayList<>(nameDetails.keySet());
        Collections.sort(list);
        versions.addAll(list);
        raw.setVersions(versions);

        return raw;
    }

    public Map<String, List<String>> getMapByDept(String dept) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        List<DalReport> list = getDalReport(dept, null); // filter by dept
        if (list.isEmpty())
            return map;
        for (DalReport report : list) {
            List<String> appIds = report.getAppIds();
            if (appIds == null || appIds.isEmpty())
                continue;
            String version = report.getVersion();
            if (!map.containsKey(version))
                map.put(version, new ArrayList<String>());
            map.get(version).addAll(appIds);
        }
        return map;
    }

    public Map<String, List<String>> getMapByVersion(String version) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        List<DalReport> list = getDalReport(null, version); // filter by version
        if (list.isEmpty())
            return map;
        for (DalReport report : list) {
            List<String> appIds = report.getAppIds();
            if (appIds == null || appIds.isEmpty())
                continue;
            String dept = report.getDept();
            if (!map.containsKey(dept))
                map.put(dept, new ArrayList<String>());
            map.get(dept).addAll(appIds);
        }

        return map;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Map<String, CMSApp> getAllCMSAppInfo() {
        return appInfoMap;
    }

    public void processAppList(List<String> appIds, Map<String, CMSApp> appInfo, List<App> apps) {
        for (String appId : appIds) {
            App app = new App();
            app.setId(appId);
            CMSApp info = appInfo.get(appId);
            if (info != null) {
                app.setName(info.getAppName());
                app.setChineseName(info.getChineseName());
                app.setOwner(info.getOwner());
                app.setOwnerEmail(info.getOwnerEmail());
                app.setOwnerCode(info.getOwnerCode());
            } else {
                app.setName("");
                app.setChineseName("");
                app.setOwner("");
                app.setOwnerEmail("");
                app.setOwnerCode("");
            }
            apps.add(app);
        }
    }

    public Workbook getWorkbook() throws Exception {
        if (reportList == null || reportList.isEmpty())
            return null;

        Map<String, List<List<String>>> deptMap = new LinkedHashMap<>();
        Map<String, List<List<String>>> versionMap = new LinkedHashMap<>();
        try {
            Map<String, CMSApp> cmsAppMap = getAllCMSAppInfo();
            for (DalReport report : reportList) {
                processReport(report, cmsAppMap, deptMap, versionMap);
            }
        } catch (Exception e) {
            throw e;
        }

        try (Workbook workbook = generateWorkbook(deptMap, versionMap)) {
            return workbook;
        } catch (Exception e) {
            throw e;
        }
    }

    private void processReport(DalReport report, Map<String, CMSApp> cmsAppMap, Map<String, List<List<String>>> deptMap,
            Map<String, List<List<String>>> versionMap) {
        List<String> appIds = report.getAppIds();
        if (appIds == null || appIds.isEmpty())
            return;
        Map<String, CMSApp> appMap = new LinkedHashMap<>();
        for (String appId : appIds) {
            CMSApp app = cmsAppMap.get(appId);
            if (app != null)
                appMap.put(appId, app);
            else
                appMap.put(appId, new CMSApp());
        }

        String dept = report.getDept();
        String version = report.getVersion();
        if (!deptMap.containsKey(dept))
            deptMap.put(dept, new ArrayList<List<String>>());
        if (!versionMap.containsKey(version))
            versionMap.put(version, new ArrayList<List<String>>());

        for (Map.Entry<String, CMSApp> entry : appMap.entrySet()) {
            CMSApp app = entry.getValue();
            // dept
            List<String> row = new ArrayList<>();
            row.add(version);
            row.add(entry.getKey());
            row.add(app.getAppName());
            row.add(app.getChineseName());
            row.add(app.getOwner());
            row.add(app.getOwnerEmail());
            deptMap.get(dept).add(row);

            // version
            List<String> row2 = new ArrayList<>();
            row2.add(dept);
            row2.add(entry.getKey());
            row2.add(app.getAppName());
            row2.add(app.getChineseName());
            row2.add(app.getOwner());
            row2.add(app.getOwnerEmail());
            versionMap.get(version).add(row2);
        }
    }

    private Workbook generateWorkbook(Map<String, List<List<String>>> deptMap,
            Map<String, List<List<String>>> versionMap) {
        Workbook workbook = new HSSFWorkbook();
        if (deptMap != null && deptMap.size() > 0) {
            for (Map.Entry<String, List<List<String>>> entry : deptMap.entrySet()) {
                Sheet sheet = workbook.createSheet(entry.getKey());
                int index = 0;
                // title
                Row rowTitle = sheet.createRow(index);
                List<String> title = getDeptTitle();
                createCells(rowTitle, title);
                index++;
                // content
                for (List<String> list : entry.getValue()) {
                    Row row = sheet.createRow(index);
                    createCells(row, list);
                    index++;
                }
                setAutoSizeColumn(sheet, COLUMN_COUNT);
            }
        }

        if (versionMap != null && versionMap.size() > 0) {
            for (Map.Entry<String, List<List<String>>> entry : versionMap.entrySet()) {
                Sheet sheet = workbook.createSheet(entry.getKey());
                int index = 0;
                // title
                Row rowTitle = sheet.createRow(index);
                List<String> title = getVersionTitle();
                createCells(rowTitle, title);
                index++;
                // content
                for (List<String> list : entry.getValue()) {
                    Row row = sheet.createRow(index);
                    createCells(row, list);
                    index++;
                }
                setAutoSizeColumn(sheet, COLUMN_COUNT);
            }
        }

        return workbook;
    }

    public Workbook getWorkbook2() throws Exception {
        try (Workbook workbook = new HSSFWorkbook()) {
            List<App> list = getLocalDatasourceAppList();
            if (list == null || list.isEmpty())
                return workbook;

            int index = 0;
            Sheet sheet = workbook.createSheet(LOCAL_DATASOURCE);
            Row rowTitle = sheet.createRow(index);
            List<String> title = getLocalDatasourceTitle();
            createCells(rowTitle, title);
            index++;

            for (App app : list) {
                List<String> temp = new ArrayList<>();
                temp.add(app.getId());
                temp.add(app.getOrgName());
                temp.add(app.getName());
                temp.add(app.getChineseName());
                temp.add(app.getOwner());
                temp.add(app.getOwnerEmail());
                Row row = sheet.createRow(index);
                createCells(row, temp);
                index++;
            }
            setAutoSizeColumn(sheet, COLUMN_COUNT2);
            return workbook;
        } catch (Exception e) {
            throw e;
        }
    }

    private void setAutoSizeColumn(Sheet sheet, int length) {
        if (sheet != null) {
            for (int i = 0; i < length; i++) {
                sheet.autoSizeColumn(i);
            }
        }
    }

    private List<String> getDeptTitle() {
        List<String> title = new ArrayList<>();
        title.add("版本");
        List<String> temp = getCommonTitle();
        title.addAll(temp);
        return title;
    }

    private List<String> getVersionTitle() {
        List<String> title = new ArrayList<>();
        title.add("BU");
        List<String> temp = getCommonTitle();
        title.addAll(temp);
        return title;
    }

    private List<String> getLocalDatasourceTitle() {
        List<String> title = new ArrayList<>();
        title.add("App Id");
        title.add("BU");
        title.add("App Name");
        title.add("Chinese Name");
        title.add("Owner");
        title.add("Owner Email");
        return title;
    }

    private List<String> getCommonTitle() {
        List<String> title = new ArrayList<>();
        title.add("App Id");
        title.add("App Name");
        title.add("Chinese Name");
        title.add("Owner");
        title.add("Owner Email");
        return title;
    }

    private void createCells(Row row, List<String> list) {
        if (row == null || list == null)
            return;
        int length = list.size();
        for (int i = 0; i < length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(list.get(i));
        }
    }

    private List<DalReport> getDalReport(String dept, String version) {
        List<DalReport> list = new ArrayList<>();
        if (reportList == null || reportList.isEmpty())
            return list;

        for (DalReport report : reportList) {
            boolean flag = getFlag(report, dept, version);
            if (flag)
                list.add(report);
        }

        return list;
    }

    private boolean getFlag(DalReport report, String dept, String version) {
        boolean flag = false;
        if (dept != null && dept.length() > 0) {
            if (report.getDept().equals(dept))
                flag |= true;
            if (dept.equals(ALL))
                flag |= true;
        }
        if (version != null && version.length() > 0) {
            if (report.getVersion().equals(version))
                flag |= true;
            if (version.equals(ALL))
                flag |= true;
        }
        return flag;
    }

    private Map<String, DalReport> convertListToMap(List<DalReport> list) {
        Map<String, DalReport> map = new HashMap<>();
        if (list == null || list.isEmpty())
            return map;
        for (DalReport report : list) {
            List<String> appIds = report.getAppIds();
            if (appIds == null || appIds.isEmpty())
                continue;
            String dept = report.getDept();
            String version = report.getVersion();
            for (String appId : appIds) {
                DalReport temp = new DalReport();
                temp.setDept(dept);
                temp.setVersion(version);
                map.put(appId, temp);
            }
        }
        return map;
    }

    public void runTask() throws Exception {
        getAllAppInfoMap();
        getNewAllDalReportVector();
    }

    private void getNewAllDalReportVector() throws Exception {
        NewRoot root = HttpUtil.getJSONEntity(NewRoot.class, NEW_DAL_VERSION_URL, null, HttpMethod.HttpGet);
        if (root == null)
            return;

        NewReport report = root.getReport();
        if (report == null)
            return;

        TypeDetails typeDetails = report.getTypeDetails();
        if (typeDetails == null)
            return;

        NewDALversion dalVersion = typeDetails.getDaLVersion();
        if (dalVersion == null)
            return;

        Map<String, Map<String, List<String>>> map = getAppMap(dalVersion.getNameDetails());
        if (map == null || map.isEmpty())
            return;

        List<DalReport> list = convertMapToList(map);
        reportList = list;
        lastUpdate = new Date();
    }

    // outer key:BU,inner key:version,value:app ids
    private Map<String, Map<String, List<String>>> getAppMap(Map<String, NewName> nameDetails) {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        if (nameDetails == null || nameDetails.isEmpty())
            return result;

        for (Map.Entry<String, NewName> nameDetail : nameDetails.entrySet()) {
            NewName newName = nameDetail.getValue();
            if (newName == null)
                continue;

            String version = nameDetail.getKey();
            Map<String, NewApp> appDetails = newName.getAppDetails();
            if (appDetails == null || appDetails.isEmpty())
                continue;

            for (Map.Entry<String, NewApp> appDetail : appDetails.entrySet()) {
                String appId = appDetail.getKey();
                CMSApp appInfo = appInfoMap.get(appId);
                if (appInfo == null)
                    continue;

                String orgName = appInfo.getOrgName();
                if (!result.containsKey(orgName))
                    result.put(orgName, new HashMap<String, List<String>>());

                Map<String, List<String>> versionMap = result.get(orgName);
                if (!versionMap.containsKey(version))
                    versionMap.put(version, new ArrayList<String>());

                List<String> list = versionMap.get(version);
                list.add(appId);
            }
        }

        return result;
    }

    private List<DalReport> convertMapToList(Map<String, Map<String, List<String>>> map) {
        List<DalReport> list = new ArrayList<>();
        if (map == null || map.isEmpty())
            return list;

        for (Map.Entry<String, Map<String, List<String>>> entry1 : map.entrySet()) {
            Map<String, List<String>> temp1 = entry1.getValue();
            if (temp1 == null || temp1.isEmpty())
                continue;

            String orgName = entry1.getKey();
            for (Map.Entry<String, List<String>> entry2 : temp1.entrySet()) {
                List<String> appIds = entry2.getValue();
                if (appIds == null || appIds.isEmpty())
                    continue;

                String version = entry2.getKey();
                DalReport dalReport = new DalReport();
                dalReport.setDept(orgName);
                dalReport.setVersion(version);
                dalReport.setAppIds(appIds);

                list.add(dalReport);
            }
        }

        return list;
    }

    private void getAllAppInfoMap() throws Exception {
        ConcurrentHashMap<String, CMSApp> map = new ConcurrentHashMap<>();
        Map<String, String> parameters = new HashMap<>();
        parameters.put(ACCESS_TOKEN, CMS_TOKEN);
        parameters.put(REQUEST_BODY, "{}");
        CMSAppInfo info = HttpUtil.getJSONEntity(CMSAppInfo.class, CMS_ALL_APPS_URL, parameters, HttpMethod.HttpPost);
        if (info != null) {
            List<CMSApp> list = info.getData();
            if (list != null && !list.isEmpty()) {
                for (CMSApp app : list) {
                    map.put(app.getAppId(), app);
                }
            }
        }

        appInfoMap = map;
        lastUpdate = new Date();
    }

    private class ReportTask implements Runnable {
        @Override
        public void run() {
            try {
                isTaskRunning = true;
                runTask();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isTaskRunning = false;
            }
        }
    }

}
