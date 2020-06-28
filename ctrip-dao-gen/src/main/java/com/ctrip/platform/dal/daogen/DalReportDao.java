package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.hickwall.HickwallMetrics;
import com.ctrip.platform.dal.daogen.report.App;
import com.ctrip.platform.dal.daogen.report.CMSApp;
import com.ctrip.platform.dal.daogen.report.CMSAppInfo;
import com.ctrip.platform.dal.daogen.report.DalReport;
import com.ctrip.platform.dal.daogen.report.LangType;
import com.ctrip.platform.dal.daogen.report.RawInfo;
import com.ctrip.platform.dal.daogen.report.VersionStats;
import com.ctrip.platform.dal.daogen.report.newReport.CatClientVersion;
import com.ctrip.platform.dal.daogen.report.newReport.CtripDalClientVersionRawInfo;
import com.ctrip.platform.dal.daogen.report.newReport.CtripDatasourceVersion;
import com.ctrip.platform.dal.daogen.report.newReport.CtripDatasourceVersionRawInfo;
import com.ctrip.platform.dal.daogen.report.newReport.NewApp;
import com.ctrip.platform.dal.daogen.report.newReport.NewDALversion;
import com.ctrip.platform.dal.daogen.report.newReport.NewDatabaseCategory;
import com.ctrip.platform.dal.daogen.report.newReport.NewName;
import com.ctrip.platform.dal.daogen.report.newReport.NewReport;
import com.ctrip.platform.dal.daogen.report.newReport.NewRoot;
import com.ctrip.platform.dal.daogen.report.newReport.SQLdatabase;
import com.ctrip.platform.dal.daogen.report.newReport.TypeDetails;
import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;
import com.dianping.cat.Cat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DalReportDao {
    private static DalReportDao reportDao = null;
    private static final String DAL_COUNT = "fx.dal.used.count";
    private static final String DAL_DATASOURCE_COUNT = "fx.dal_datasource.used.count";
    private static final String BU = "BU";
    private static final String VERSION = "version";
    private static final String DB_NAME = "dao";

    private DalQueryDao client;
    private DalTableDao<DalGroupDB> dao;

    private DalReportDao() {
        client = new DalQueryDao(DB_NAME);
        try {
            dao = new DalTableDao<>(new DalDefaultJpaParser<>(DalGroupDB.class));
        } catch (SQLException e) {
            Cat.logError(e);
        }
    }
    public static DalReportDao getInstance() {
        if (reportDao == null) {
            reportDao = new DalReportDao();
        }
        return reportDao;
    }

    private static final String DAL_VERSION_URL =
            "http://cat.ctripcorp.com/cat/r/globalEvent?type=DAL.version&op=appDetail&forceDownload=json";

    private static final String CTRIP_DATASOURCE_VERSION_URL =
            "http://cat.ctripcorp.com/cat/r/globalEvent?type=Ctrip.datasource.version&op=appDetail&forceDownload=json";

    private static final String SQL_DATABASE_URL =
            "http://cat.ctripcorp.com/cat/r/globalEvent?type=SQL.database&op=appDetail&forceDownload=json";

    private static final String CAT_APPID_URL =
            "http://cat.ctripcorp.com/cat/r/globalEvent?type=Cat.Client.Version&op=appDetail&forceDownload=json";

    private static final String CMS_ALL_APPS_URL = "http://osg.ops.ctripcorp.com/api/17676";
    private static final String CMS_TOKEN = "70c152d9c4980f8843c497ed9b6b5386";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REQUEST_BODY = "request_body";

    private static final String ALL = "All";
    private static final String MYSQL = "mysql";

    private static final String JAVA = "java";
    private static final String NET = "net";

    private List<DalReport> reportList = null;
    private ConcurrentHashMap<String, CMSApp> appInfoMap = new ConcurrentHashMap<>();
    private Date lastUpdate = null;
    private Map<String, List<NewDatabaseCategory>> databaseCategoryMap = new HashMap<>();
    private VersionStats versionStats = new VersionStats();

    public boolean isTaskRunning = false;

    private static final int COLUMN_COUNT = 6;

    // minutes
    private static final long INIT_DELAY = 0;
    private static final long DELAY = 60; // 2 60

    public void init() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new ReportTask(), INIT_DELAY, DELAY, TimeUnit.MINUTES);
    }

    public RawInfo getNewRawInfo() throws Exception {
        RawInfo raw = new RawInfo();
        CtripDalClientVersionRawInfo dalVersion = getCtripDalVersion();
        CtripDatasourceVersionRawInfo datasourceVersion = getCtripDatasourceVersion();

        // depts
        Set<String> set = new HashSet<>();
        set.addAll(dalVersion.getDepts());
        set.addAll(datasourceVersion.getDepts());

        // versions
        List<String> list = new ArrayList<>();
        list.addAll(dalVersion.getVersions());
        list.addAll(datasourceVersion.getFormattedVersions());
        Collections.sort(list);

        // add depts
        List<String> depts = new ArrayList<>();
        depts.add(ALL);
        depts.addAll(set);
        raw.setDepts(depts);

        // add versions
        List<String> versions = new ArrayList<>();
        versions.add(ALL);
        versions.addAll(list);
        raw.setVersions(versions);

        return raw;
    }

    private CtripDalClientVersionRawInfo getCtripDalVersion() throws Exception {
        CtripDalClientVersionRawInfo result = new CtripDalClientVersionRawInfo();
        NewRoot root = HttpUtil.getJSONEntity(NewRoot.class, DAL_VERSION_URL, null, HttpMethod.HttpGet);
        if (root == null)
            return result;

        NewReport report = root.getReport();
        if (report == null)
            return result;

        String[] bus = report.getBus();
        if (bus == null || bus.length == 0)
            return result;

        TypeDetails typeDetails = report.getTypeDetails();
        if (typeDetails == null)
            return result;

        NewDALversion dalVersion = typeDetails.getDaLVersion();
        if (dalVersion == null)
            return result;

        Map<String, NewName> nameDetails = dalVersion.getNameDetails();
        if (nameDetails == null || nameDetails.isEmpty())
            return result;

        List<String> temp = Arrays.asList(bus);
        result.setDepts(temp);

        List<String> versions = new ArrayList<>(nameDetails.keySet());
        result.setVersions(versions);
        return result;
    }

    private CtripDatasourceVersionRawInfo getCtripDatasourceVersion() throws Exception {
        CtripDatasourceVersionRawInfo result = new CtripDatasourceVersionRawInfo();
        NewRoot root = HttpUtil.getJSONEntity(NewRoot.class, CTRIP_DATASOURCE_VERSION_URL, null, HttpMethod.HttpGet);
        if (root == null)
            return result;

        NewReport report = root.getReport();
        if (report == null)
            return result;

        String[] bus = report.getBus();
        if (bus == null || bus.length == 0)
            return result;

        TypeDetails typeDetails = report.getTypeDetails();
        if (typeDetails == null)
            return result;

        CtripDatasourceVersion datasourceVersion = typeDetails.getCtripDatasourceVersion();
        if (datasourceVersion == null)
            return result;

        Map<String, NewName> nameDetails = datasourceVersion.getNameDetails();
        if (nameDetails == null || nameDetails.isEmpty())
            return result;

        List<String> temp = Arrays.asList(bus);
        result.setDepts(temp);

        List<String> versions = new ArrayList<>(nameDetails.keySet());
        result.setVersions(versions);
        return result;
    }

    public Map<String, List<String>> getMapByDept(String dept, NewDatabaseCategory databaseCategory) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        List<DalReport> list = getDalReport(dept, null); // filter by dept
        if (list.isEmpty())
            return map;
        for (DalReport report : list) {
            List<String> appIds = report.getAppIds();
            if (appIds == null || appIds.isEmpty())
                continue;

            appIds = filterAppIds(appIds, databaseCategory); // filter by databasecategory
            String version = report.getVersion();
            if (!map.containsKey(version))
                map.put(version, new ArrayList<String>());
            map.get(version).addAll(appIds);
        }
        return map;
    }

    public Map<String, List<String>> getMapByVersion(String version, NewDatabaseCategory databaseCategory) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        List<DalReport> list = getDalReport(null, version); // filter by version
        if (list.isEmpty())
            return map;
        for (DalReport report : list) {
            List<String> appIds = report.getAppIds();
            if (appIds == null || appIds.isEmpty())
                continue;

            appIds = filterAppIds(appIds, databaseCategory); // filter by databasecategory
            String dept = report.getDept();
            if (!map.containsKey(dept))
                map.put(dept, new ArrayList<String>());
            map.get(dept).addAll(appIds);
        }

        return map;
    }

    public boolean upgradeMysqlDBDomain() {
        try {
            String formatDomain = "%s.mysql.db.dev.qa.nt.ctripcorp.com";
            String querySql = "select * from alldbs where db_address='pub.mysql.db.dev.sh.ctripcorp.com'";
            List<DalGroupDB> dalGroupDBList = client.query(querySql, new StatementParameters(), new DalHints(), DalGroupDB.class);

            List<DalGroupDB> updateGroupDBList = new ArrayList<>();
            for (DalGroupDB dalGroupDB : dalGroupDBList) {
                DalGroupDB updateGroupDB = new DalGroupDB();
                updateGroupDB.setId(dalGroupDB.getId());
                String dbName = dalGroupDB.getDb_catalog();
                if (dbName.substring(dbName.length()-2).toLowerCase().equals("db")) {
                    dbName = dbName.substring(0, dbName.length() - 2);
                }
                String newMySqlDomain = String.format(formatDomain, dbName);
                updateGroupDB.setDb_address(newMySqlDomain);
                dalGroupDB.setDb_address(newMySqlDomain);
                if (testConnection(dalGroupDB)) {
                    updateGroupDBList.add(updateGroupDB);
                }
            }
            if(updateGroupDBList.size() > 0) {
                dao.batchUpdate(new DalHints(), updateGroupDBList);
            }
            return true;
        } catch (SQLException e) {
            Cat.logError(e);
        }
        return false;
    }

    private boolean testConnection(DalGroupDB dalGroupDB) {
        boolean result = false;
        Connection conn = null;
        try {
            conn = DataSourceUtil.getConnection(dalGroupDB.getDb_address(), dalGroupDB.getDb_port(), dalGroupDB.getDb_user(), dalGroupDB.getDb_password(),
                    dalGroupDB.getDb_providerName(), dalGroupDB.getDb_catalog());
            result = true;
        } catch (Exception e) {
            Cat.logError(e);

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {

                }
            }
        }
        return result;
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

    private List<String> filterAppIds(List<String> appIds, NewDatabaseCategory databaseCategory) {
        if (appIds == null || appIds.isEmpty())
            return appIds;

        if (databaseCategory.equals(NewDatabaseCategory.All))
            return appIds;

        List<String> result = filterAppIdsByDatabaseCategory(appIds, databaseCategory);
        return result;
    }

    private List<String> filterAppIdsByDatabaseCategory(List<String> appIds, NewDatabaseCategory databaseCategory) {
        List<String> result = new ArrayList<>();
        for (String appId : appIds) {
            List<NewDatabaseCategory> temp = databaseCategoryMap.get(appId);
            if (temp == null || temp.isEmpty())
                continue;

            if (temp.contains(databaseCategory))
                result.add(appId);
        }

        return result;
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

    public Workbook getWorkbook(NewDatabaseCategory databaseCategory) throws Exception {
        if (reportList == null || reportList.isEmpty())
            return null;

        Map<String, List<List<String>>> deptMap = new LinkedHashMap<>();
        Map<String, List<List<String>>> versionMap = new LinkedHashMap<>();
        try {
            Map<String, CMSApp> cmsAppMap = getAllCMSAppInfo();
            Map<String, Map<String, List<DalReport>>> map = convertListToNestingMap(reportList);
            for (Map.Entry<String, Map<String, List<DalReport>>> entry : map.entrySet()) {
                for (Map.Entry<String, List<DalReport>> entry2 : entry.getValue().entrySet()) {
                    for (DalReport report : entry2.getValue()) {
                        processReport(report, cmsAppMap, deptMap, versionMap, databaseCategory);
                    }
                }
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

    private Map<String, Map<String, List<DalReport>>> convertListToNestingMap(List<DalReport> list) {
        Map<String, Map<String, List<DalReport>>> map = new TreeMap<>();
        if (list == null || list.isEmpty())
            return map;

        for (DalReport report : list) {
            String dept = report.getDept();
            String version = report.getVersion();
            if (!map.containsKey(dept))
                map.put(dept, new TreeMap<>());

            Map<String, List<DalReport>> temp = map.get(dept);
            if (!temp.containsKey(version))
                temp.put(version, new ArrayList<>());

            temp.get(version).add(report);
        }

        return map;
    }

    private void processReport(DalReport report, Map<String, CMSApp> cmsAppMap, Map<String, List<List<String>>> deptMap,
            Map<String, List<List<String>>> versionMap, NewDatabaseCategory databaseCategory) {
        List<String> appIds = report.getAppIds();
        if (appIds == null || appIds.isEmpty())
            return;

        Map<String, CMSApp> appMap = new LinkedHashMap<>();
        appIds = filterAppIds(appIds, databaseCategory);
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
            deptMap.put(dept, new ArrayList<>());
        if (!versionMap.containsKey(version))
            versionMap.put(version, new ArrayList<>());

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

    public void runTask() throws Exception {
        getAllAppInfoMap();
        getNewAllDalReportVector();
        getNewDatabaseCategoryMap();
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

    private void getNewDatabaseCategoryMap() throws Exception {
        NewRoot root = HttpUtil.getJSONEntity(NewRoot.class, SQL_DATABASE_URL, null, HttpMethod.HttpGet);
        if (root == null)
            return;

        NewReport report = root.getReport();
        if (report == null)
            return;

        TypeDetails typeDetails = report.getTypeDetails();
        if (typeDetails == null)
            return;

        SQLdatabase sqlDatabase = typeDetails.getSqlDatabase();
        if (sqlDatabase == null)
            return;

        Map<String, List<NewDatabaseCategory>> map = getDatabaseCategoryMap(sqlDatabase.getNameDetails());
        if (map == null || map.isEmpty())
            return;

        databaseCategoryMap = map;
        lastUpdate = new Date();
    }

    private void getNewAllDalReportVector() throws Exception {
        List<DalReport> result = new ArrayList<>();
        List<DalReport> dalVersionList = getDalVersionVector();
        List<DalReport> datasourceVersionList = getDatasourceVersionVector();
        List<String> catAppIds = getCatAppIds();
        datasourceVersionList = formattedDataSourceVersionList(datasourceVersionList);
        result.addAll(dalVersionList);
        result.addAll(datasourceVersionList);
        setDalVersionStats();
        logDalVersionStats();
        logDalAndDataSourceUsedAppCount(dalVersionList, datasourceVersionList);

        reportList = result;
        lastUpdate = new Date();
    }

    private void logDalAndDataSourceUsedAppCount(List<DalReport> dalVersionList, List<DalReport> datasourceVersionList) {
        for (DalReport dalReport : dalVersionList) {
            Map<String, String> tags = new HashMap<>();
            tags.put(BU, dalReport.getDept());
            tags.put(VERSION, dalReport.getVersion());
            HickwallMetrics.logMetricValue(DAL_COUNT, tags, dalReport.getAppIds().size());
        }

        for (DalReport dalReport : datasourceVersionList) {
            Map<String, String> tags = new HashMap<>();
            tags.put(BU, dalReport.getDept());
            tags.put(VERSION, dalReport.getVersion());
            HickwallMetrics.logMetricValue(DAL_DATASOURCE_COUNT, tags, dalReport.getAppIds().size());
        }
    }

    private List<DalReport> getDalVersionVector() throws Exception {
        List<DalReport> list = new ArrayList<>();
        NewRoot root = HttpUtil.getJSONEntity(NewRoot.class, DAL_VERSION_URL, null, HttpMethod.HttpGet);
        if (root == null)
            return list;

        NewReport report = root.getReport();
        if (report == null)
            return list;

        TypeDetails typeDetails = report.getTypeDetails();
        if (typeDetails == null)
            return list;

        NewDALversion dalVersion = typeDetails.getDaLVersion();
        if (dalVersion == null)
            return list;

        Map<String, NewName> nameDetails = dalVersion.getNameDetails();
        if (nameDetails == null || nameDetails.isEmpty())
            return list;

        processAppIds(nameDetails);

        Map<String, Map<String, List<String>>> map = getAppMap(dalVersion.getNameDetails());
        if (map == null || map.isEmpty())
            return list;

        list = convertMapToList(map);
        return list;
    }

    private void processAppIds(Map<String, NewName> nameDetails) {
        Set<String> javaAppIds = new HashSet<>();
        Set<String> netAppIds = new HashSet<>();

        for (Map.Entry<String, NewName> entry : nameDetails.entrySet()) {
            NewName temp = entry.getValue();
            if (temp == null)
                continue;

            Map<String, NewApp> appDetails = temp.getAppDetails();
            if (appDetails == null || appDetails.isEmpty())
                continue;

            String key = entry.getKey().toLowerCase();
            Set<String> appIds = appDetails.keySet();

            if (key.indexOf(JAVA) > -1) {
                javaAppIds.addAll(appIds);
            } else if (key.indexOf(NET) > -1) {
                netAppIds.addAll(appIds);
            }
        }

        versionStats.setCtripDalClientAppIds(javaAppIds);
        versionStats.setNetDalAppIds(netAppIds);
    }

    private List<DalReport> getDatasourceVersionVector() throws Exception {
        List<DalReport> list = new ArrayList<>();
        NewRoot root = HttpUtil.getJSONEntity(NewRoot.class, CTRIP_DATASOURCE_VERSION_URL, null, HttpMethod.HttpGet);
        if (root == null)
            return list;

        NewReport report = root.getReport();
        if (report == null)
            return list;

        TypeDetails typeDetails = report.getTypeDetails();
        if (typeDetails == null)
            return list;

        CtripDatasourceVersion datasourceVersion = typeDetails.getCtripDatasourceVersion();
        if (datasourceVersion == null)
            return list;

        Map<String, NewName> nameDetails = datasourceVersion.getNameDetails();
        if (nameDetails == null || nameDetails.isEmpty())
            return list;

        processDataSourceAppIds(nameDetails);

        Map<String, Map<String, List<String>>> map = getAppMap(datasourceVersion.getNameDetails());
        if (map == null || map.isEmpty())
            return list;

        list = convertMapToList(map);
        return list;
    }

    private void processDataSourceAppIds(Map<String, NewName> nameDetails) {
        Set<String> datasourceAppIds = new HashSet<>();

        for (Map.Entry<String, NewName> entry : nameDetails.entrySet()) {
            NewName temp = entry.getValue();
            if (temp == null)
                continue;

            Map<String, NewApp> appDetails = temp.getAppDetails();
            if (appDetails == null || appDetails.isEmpty())
                continue;

            Set<String> appIds = appDetails.keySet();
            datasourceAppIds.addAll(appIds);
        }

        versionStats.setTempDataSourceAppIds(datasourceAppIds);
    }

    private List<DalReport> formattedDataSourceVersionList(List<DalReport> list) {
        List<DalReport> result = new ArrayList<>();
        if (list == null || list.isEmpty())
            return result;

        for (DalReport report : list) {
            String version = String.format("datasource-%s", report.getVersion());
            DalReport newReport = new DalReport();
            newReport.setDept(report.getDept());
            newReport.setVersion(version);
            newReport.setAppIds(report.getAppIds());
            result.add(newReport);
        }

        return result;
    }

    private List<String> getCatAppIds() throws Exception {
        List<String> result = new ArrayList<>();
        NewRoot root = HttpUtil.getJSONEntity(NewRoot.class, CAT_APPID_URL, null, HttpMethod.HttpGet);
        if (root == null)
            return result;

        NewReport report = root.getReport();
        if (report == null)
            return result;

        TypeDetails typeDetails = report.getTypeDetails();
        if (typeDetails == null)
            return result;

        CatClientVersion catClientVersion = typeDetails.getCatClientVersion();
        if (catClientVersion == null)
            return result;

        Map<String, NewApp> appDetails = catClientVersion.getAppDetails();
        if (appDetails == null || appDetails.isEmpty())
            return result;

        Set<String> set = appDetails.keySet();
        versionStats.setAllAppIdsInCat(set);
        result.addAll(set);
        return result;
    }

    private void setDalVersionStats() {
        setAppIds();
        setDalAppIds();
    }

    private void setAppIds() {
        if (appInfoMap == null)
            return;

        Set<String> allAppIds = new HashSet<>();
        Set<String> allJavaAppIds = new HashSet<>();
        Set<String> allNetAppIds = new HashSet<>();

        for (Map.Entry<String, CMSApp> entry : appInfoMap.entrySet()) {
            allAppIds.add(entry.getKey());
            LangType langType = entry.getValue().getLangType();
            if (langType.equals(LangType.Java)) {
                allJavaAppIds.add(entry.getKey());
            } else if (langType.equals(LangType.Net)) {
                allNetAppIds.add(entry.getKey());
            }
        }

        versionStats.setAllAppIds(allAppIds);
        versionStats.setAllJavaAppIds(allJavaAppIds);
        versionStats.setAllNetAppIds(allNetAppIds);

        setCatAppIds(allJavaAppIds, allNetAppIds);
    }

    private void setCatAppIds(Set<String> allJavaAppIds, Set<String> allNetAppIds) {
        Set<String> allAppIdsInCat = versionStats.getAllAppIdsInCat();
        if (allAppIdsInCat == null || allAppIdsInCat.isEmpty())
            return;

        Set<String> tempJavaAppIds = new HashSet<>(allJavaAppIds);
        Set<String> tempNetAppIds = new HashSet<>(allNetAppIds);

        Set<String> allJavaAppIdsInCat = new HashSet<>();
        Set<String> allNetAppIdsInCat = new HashSet<>();

        for (String appId : allAppIdsInCat) {
            if (tempJavaAppIds.contains(appId)) {
                allJavaAppIdsInCat.add(appId);
            } else if (tempNetAppIds.contains(appId)) {
                allNetAppIdsInCat.add(appId);
            }
        }

        versionStats.setAllJavaAppIdsInCat(allJavaAppIdsInCat);
        versionStats.setAllNetAppIdsInCat(allNetAppIdsInCat);
    }

    private void setDalAppIds() {
        Set<String> ctripDalClientAppIds = versionStats.getCtripDalClientAppIds();
        Set<String> tempDataSourceAppIds = versionStats.getTempDataSourceAppIds();
        Set<String> javaAppIds = new HashSet<>();
        javaAppIds.addAll(ctripDalClientAppIds);
        javaAppIds.addAll(tempDataSourceAppIds);
        versionStats.setJavaDalAppIds(javaAppIds);

        Set<String> datasourceAppIds = new HashSet<>();
        for (String appId : tempDataSourceAppIds) {
            if (!ctripDalClientAppIds.contains(appId))
                datasourceAppIds.add(appId);
        }

        versionStats.setCtripDataSourceAppIds(datasourceAppIds);
    }

    private void logDalVersionStats() {
        HickwallMetrics.setAllMetricValue(versionStats.getAllAppIds().size());
        HickwallMetrics.setAllJavaMetricValue(versionStats.getAllJavaAppIds().size());
        HickwallMetrics.setAllNetMetricValue(versionStats.getAllNetAppIds().size());

        HickwallMetrics.setAllJavaInCatMetricValue(versionStats.getAllJavaAppIdsInCat().size());
        HickwallMetrics.setAllNetInCatMetricValue(versionStats.getAllNetAppIdsInCat().size());

        HickwallMetrics.setJavaAllMetricValue(versionStats.getJavaDalAppIds().size());
        HickwallMetrics.setJavaCtripDalClientMetricValue(versionStats.getCtripDalClientAppIds().size());
        HickwallMetrics.setJavaCtripDataSourceMetricValue(versionStats.getCtripDataSourceAppIds().size());
        HickwallMetrics.setNetDalMetricValue(versionStats.getNetDalAppIds().size());
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

            Map<String, NewApp> appDetails = newName.getAppDetails();
            if (appDetails == null || appDetails.isEmpty())
                continue;

            String version = nameDetail.getKey();
            for (Map.Entry<String, NewApp> appDetail : appDetails.entrySet()) {
                String appId = appDetail.getKey();
                CMSApp appInfo = appInfoMap.get(appId);
                if (appInfo == null)
                    continue;

                String orgName = appInfo.getOrganizationName();
                if (!result.containsKey(orgName))
                    result.put(orgName, new HashMap<>());

                Map<String, List<String>> versionMap = result.get(orgName);
                if (!versionMap.containsKey(version))
                    versionMap.put(version, new ArrayList<>());

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

    // key:AppId
    private Map<String, List<NewDatabaseCategory>> getDatabaseCategoryMap(Map<String, NewName> nameDetails) {
        Map<String, List<NewDatabaseCategory>> map = new HashMap<>();
        if (nameDetails == null || nameDetails.isEmpty())
            return map;

        for (Map.Entry<String, NewName> nameDetail : nameDetails.entrySet()) {
            NewName newName = nameDetail.getValue();
            if (newName == null)
                continue;

            Map<String, NewApp> appDetails = newName.getAppDetails();
            if (appDetails == null || appDetails.isEmpty())
                continue;

            String sqlDatabase = nameDetail.getKey();
            NewDatabaseCategory category = getDatabaseCategoryBySqlDatabase(sqlDatabase);
            for (Map.Entry<String, NewApp> appDetail : appDetails.entrySet()) {
                String appId = appDetail.getKey();
                if (!map.containsKey(appId))
                    map.put(appId, new ArrayList<>());

                List<NewDatabaseCategory> list = map.get(appId);
                list.add(category);
            }
        }

        return map;
    }

    private NewDatabaseCategory getDatabaseCategoryBySqlDatabase(String sqlDatabase) {
        NewDatabaseCategory category = NewDatabaseCategory.SqlServer;
        if (sqlDatabase.toLowerCase().indexOf(MYSQL) > -1)
            category = NewDatabaseCategory.MySql;

        return category;
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
