package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.report.All;
import com.ctrip.platform.dal.daogen.report.App;
import com.ctrip.platform.dal.daogen.report.CMSApp;
import com.ctrip.platform.dal.daogen.report.CMSAppInfo;
import com.ctrip.platform.dal.daogen.report.DALLocalDatasource;
import com.ctrip.platform.dal.daogen.report.DALVersion;
import com.ctrip.platform.dal.daogen.report.DalReport;
import com.ctrip.platform.dal.daogen.report.Filter;
import com.ctrip.platform.dal.daogen.report.Machines;
import com.ctrip.platform.dal.daogen.report.NameDomain;
import com.ctrip.platform.dal.daogen.report.NameDomainCount;
import com.ctrip.platform.dal.daogen.report.RawInfo;
import com.ctrip.platform.dal.daogen.report.Report;
import com.ctrip.platform.dal.daogen.report.Root;
import com.ctrip.platform.dal.daogen.report.TypeDomains;
import com.ctrip.platform.dal.daogen.report.Types;
import com.ctrip.platform.dal.daogen.report.Url;
import com.ctrip.platform.dal.daogen.report.Version;
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
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DalReportDao {
    private static final String DAL_VERSION_URL =
            "http://cat.ctripcorp.com/cat/r/e?domain=All&type=DAL.version&forceDownload=json";
    private static final String DAL_VERSION_URL_FORMAT =
            "http://cat.ctripcorp.com/cat/r/e?op=graphs&domain=All&ip=%s&type=DAL.version&name=%s&forceDownload=json";
    private static final String DAL_LOCAL_DATASOURCE =
            "http://cat.ctripcorp.com/cat/r/e?domain=All&ip=All&type=DAL.local.datasource&min=-1&max=-1&forceDownload=json";
    private static final String DAL_JAVA = "java";

    private static final String CMS_ALL_APPS_URL = "http://osg.ops.ctripcorp.com/api/11209";
    private static final String CMS_TOKEN = "70c152d9c4980f8843c497ed9b6b5386";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REQUEST_BODY = "request_body";

    private static Vector<DalReport> reportVector = null;
    private static ConcurrentHashMap<String, CMSApp> reportMap = null;
    private static Date lastUpdate = null;

    // minutes
    private static final long initDelay = 0;
    private static final long delay = 5;

    public void init() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(new ReportTask(), initDelay, delay, TimeUnit.MINUTES);
    }

    public List<App> getLocalDatasourceAppList() throws Exception {
        List<App> list = new ArrayList<>();
        List<String> appIds = getLocalDatasourceAppIds();
        if (appIds == null || appIds.size() == 0)
            return list;
        Collections.sort(appIds);
        Map<String, DalReport> map = convertVectorToMap(reportVector);
        Map<String, CMSApp> cmsMap = getAllCMSAppInfo();
        for (String appId : appIds) {
            App app = new App();
            app.setId(appId);
            DalReport report = map.get(appId);
            if (report != null) {
                app.setDept(report.getDept());
                app.setVersion(report.getVersion());
            }
            CMSApp cmsApp = cmsMap.get(appId);
            if (cmsApp != null) {
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

    public RawInfo getRawInfo() throws Exception {
        RawInfo raw = new RawInfo();
        Root root = HttpUtil.getJSONEntity(Root.class, DAL_VERSION_URL, null, HttpMethod.HttpGet);
        if (root == null)
            return raw;
        Report report = root.getReport();
        if (report == null)
            return raw;
        String[] ips = report.getIps();
        if (ips == null)
            return raw;
        Machines machines = report.getMachines();
        if (machines == null)
            return raw;
        All all = machines.getAll();
        if (all == null)
            return raw;
        Types types = all.getTypes();
        if (types == null)
            return raw;
        DALVersion version = types.getDalVersion();
        if (version == null)
            return raw;
        Map<String, Version> map = version.getNames();
        if (map != null && map.size() > 0) {
            List<String> depts = Arrays.asList(ips);
            raw.setDepts(depts);

            List<String> versions = new ArrayList<>(map.keySet());
            versions = getFuzzyList(versions, DAL_JAVA);
            Collections.sort(versions);
            raw.setVersions(versions);
        }

        return raw;
    }

    public Map<String, List<String>> getMapByDept(String dept) throws Exception {
        Map<String, List<String>> map = new LinkedHashMap<>();
        List<DalReport> list = getDalReport(dept, null);
        if (list == null || list.size() == 0)
            return map;
        for (DalReport report : list) {
            List<String> appIds = report.getAppIds();
            if (appIds == null || appIds.size() == 0)
                continue;
            String version = report.getVersion();
            if (!map.containsKey(version))
                map.put(version, new ArrayList<String>());
            map.get(version).addAll(appIds);
        }
        return map;
    }

    public Map<String, List<String>> getMapByVersion(String version) throws Exception {
        Map<String, List<String>> map = new LinkedHashMap<>();
        List<DalReport> list = getDalReport(null, version);
        if (list == null || list.size() == 0)
            return map;
        for (DalReport report : list) {
            List<String> appIds = report.getAppIds();
            if (appIds == null || appIds.size() == 0)
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

    public Map<String, CMSApp> getAllCMSAppInfo() throws Exception {
        return reportMap;
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
        Workbook workbook = new HSSFWorkbook();
        if (reportVector == null || reportVector.size() == 0)
            return workbook;
        Map<String, CMSApp> cmsAppMap = getAllCMSAppInfo();
        Map<String, List<List<String>>> deptMap = new LinkedHashMap<>();
        Map<String, List<List<String>>> versionMap = new LinkedHashMap<>();
        for (DalReport report : reportVector) {
            List<String> appIds = report.getAppIds();
            if (appIds == null || appIds.size() == 0)
                continue;
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

        workbook = generateWorkbook(deptMap, versionMap);
        return workbook;
    }

    private Workbook generateWorkbook(Map<String, List<List<String>>> deptMap,
            Map<String, List<List<String>>> versionMap) throws Exception {
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
                setAutoSizeColumn(sheet);
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
                setAutoSizeColumn(sheet);
            }
        }

        return workbook;
    }

    private void setAutoSizeColumn(Sheet sheet) {
        if (sheet != null) {
            for (int i = 0; i < 6; i++) {
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

    private void getAllAppInfoMap() throws Exception {
        ConcurrentHashMap<String, CMSApp> map = new ConcurrentHashMap<>();
        Map<String, String> parameters = new HashMap<>();
        parameters.put(ACCESS_TOKEN, CMS_TOKEN);
        parameters.put(REQUEST_BODY, "{}");
        CMSAppInfo info = HttpUtil.getJSONEntity(CMSAppInfo.class, CMS_ALL_APPS_URL, parameters, HttpMethod.HttpPost);
        if (info != null) {
            List<CMSApp> list = info.getData();
            if (list != null && list.size() > 0) {
                for (CMSApp app : list) {
                    map.put(app.getAppId(), app);
                }
            }
        }

        reportMap = map;
        lastUpdate = new Date();
    }

    private void getAllDalReportVector() throws Exception {
        Vector<DalReport> vector = new Vector<>();
        RawInfo raw = getRawInfo();
        Filter filter = new Filter();
        filter.setDept("酒店");
        List<Url> urls = getUrls(raw, filter);
        if (urls != null && urls.size() > 0) {
            for (Url url : urls) {
                Root root = HttpUtil.getJSONEntity(Root.class, url.getUrl(), null, HttpMethod.HttpGet);
                List<String> appIds = getAppIds(root);
                if (appIds != null && appIds.size() > 0) {
                    DalReport report = new DalReport();
                    report.setAppIds(appIds);
                    report.setDept(url.getDept());
                    report.setVersion(url.getVersion());
                    vector.add(report);
                }
            }
        }
        reportVector = vector;
        lastUpdate = new Date();
    }

    private List<DalReport> getDalReport(String dept, String version) throws Exception {
        List<DalReport> list = new ArrayList<>();
        if (reportVector != null && reportVector.size() > 0) {
            for (DalReport report : reportVector) {
                boolean flag = false;
                if (dept != null && dept.length() > 0) {
                    if (report.getDept().equals(dept))
                        flag |= true;
                }
                if (version != null && version.length() > 0) {
                    if (report.getVersion().equals(version))
                        flag |= true;
                }
                if (flag)
                    list.add(report);
            }
        }
        return list;
    }

    private List<Url> getUrls(RawInfo rawInfo, Filter filter) {
        List<Url> urls = null;
        if (rawInfo == null)
            return urls;
        List<String> depts = rawInfo.getDepts();
        List<String> versions = rawInfo.getVersions();
        // filter
        if (filter != null) {
            depts = getFilteredList(depts, filter.getDept());
            versions = getFilteredList(versions, filter.getVersion());
        }
        if (depts == null || versions == null)
            return urls;
        urls = new ArrayList<>();
        for (String dept : depts) {
            for (String version : versions) {
                String format = String.format(DAL_VERSION_URL_FORMAT, dept, version);
                Url url = new Url();
                url.setDept(dept);
                url.setVersion(version);
                url.setUrl(format);
                urls.add(url);
            }
        }

        return urls;
    }

    private List<String> getFilteredList(List<String> raw, String filter) {
        if (raw == null || raw.size() == 0)
            return raw;
        if (filter == null || filter.length() == 0)
            return raw;
        if (!raw.contains(filter))
            return raw;
        List<String> list = new ArrayList<>();
        list.add(filter);
        return list;
    }

    private List<String> getAppIds(Root root) throws Exception {
        List<String> appIds = null;
        if (root == null)
            return appIds;
        Report report = root.getReport();
        if (report == null)
            return appIds;
        TypeDomains typeDomains = report.getTypeDomains();
        if (typeDomains == null)
            return appIds;
        DALVersion dalVersion = typeDomains.getDalVersion();
        if (dalVersion == null)
            return appIds;
        Map<String, NameDomain> nameDomainMap = dalVersion.getNameDomains();
        if (nameDomainMap == null || nameDomainMap.size() == 0)
            return appIds;
        Map.Entry<String, NameDomain> entry = nameDomainMap.entrySet().iterator().next();
        NameDomain nameDomain = entry.getValue();
        if (nameDomain == null)
            return appIds;
        Map<String, NameDomainCount> map = nameDomain.getNameDomainCounts();
        if (map == null || map.size() == 0)
            return appIds;
        appIds = new ArrayList<>(map.keySet());
        Collections.sort(appIds);
        return appIds;
    }

    private List<String> getFuzzyList(List<String> raw, String filter) {
        if (raw == null || raw.size() == 0)
            return raw;
        if (filter == null || filter.length() == 0)
            return raw;
        List<String> list = new ArrayList<>();
        for (String s : raw) {
            if (s.indexOf(filter) > -1) {
                list.add(s);
            }
        }
        return list;
    }

    private Map<String, DalReport> convertVectorToMap(Vector<DalReport> vector) {
        Map<String, DalReport> map = new HashMap<>();
        if (vector == null || vector.size() == 0)
            return map;
        for (DalReport report : vector) {
            List<String> appIds = report.getAppIds();
            if (appIds == null || appIds.size() == 0)
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

    private class ReportTask implements Runnable {
        @Override
        public void run() {
            try {
                getAllDalReportVector();
                getAllAppInfoMap();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}
