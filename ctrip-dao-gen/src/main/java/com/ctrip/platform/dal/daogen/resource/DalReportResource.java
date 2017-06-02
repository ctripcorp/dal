package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.enums.HttpMethod;
import com.ctrip.platform.dal.daogen.report.All;
import com.ctrip.platform.dal.daogen.report.App;
import com.ctrip.platform.dal.daogen.report.CMSApp;
import com.ctrip.platform.dal.daogen.report.CMSAppInfo;
import com.ctrip.platform.dal.daogen.report.Client;
import com.ctrip.platform.dal.daogen.report.DalReport;
import com.ctrip.platform.dal.daogen.report.Dept;
import com.ctrip.platform.dal.daogen.report.Filter;
import com.ctrip.platform.dal.daogen.report.NameDomain;
import com.ctrip.platform.dal.daogen.report.NameDomainCount;
import com.ctrip.platform.dal.daogen.report.Root;
import com.ctrip.platform.dal.daogen.report.DALVersion;
import com.ctrip.platform.dal.daogen.report.Machines;
import com.ctrip.platform.dal.daogen.report.RawInfo;
import com.ctrip.platform.dal.daogen.report.Report;
import com.ctrip.platform.dal.daogen.report.TypeDomains;
import com.ctrip.platform.dal.daogen.report.Types;
import com.ctrip.platform.dal.daogen.report.Url;
import com.ctrip.platform.dal.daogen.report.Version;
import com.ctrip.platform.dal.daogen.utils.HttpUtil;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Resource
@Singleton
@Path("report")
public class DalReportResource {
    private static final String DAL_VERSION_URL =
            "http://cat.ctripcorp.com/cat/r/e?domain=All&type=DAL.version&forceDownload=json";
    private static final String DAL_VERSION_URL_FORMAT =
            "http://cat.ctripcorp.com/cat/r/e?op=graphs&domain=All&ip=%s&type=DAL.version&name=%s&forceDownload=json";
    private static final String DAL_JAVA = "java";

    private static final String CMS_ALL_APPS_URL = "http://osg.ops.ctripcorp.com/api/11209";
    private static final String CMS_TOKEN = "70c152d9c4980f8843c497ed9b6b5386";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REQUEST_BODY = "request_body";

    public void Test() throws Exception {
        getAllAppInfo();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getRaw")
    public RawInfo getRaw() throws Exception {
        return getRawInfo();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getReportByDept")
    public List<Dept> getReportByDept(@QueryParam("dept") String dept) throws Exception {
        List<Dept> list = null;
        if (dept == null || dept.length() == 0)
            return list;

        Map<String, List<String>> map = getMapByDept(dept);
        if (map == null || map.size() == 0)
            return list;

        list = new ArrayList<>();
        Map<String, CMSApp> appInfo = getAllAppInfo();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<String> appIds = entry.getValue();
            if (appIds == null || appIds.size() == 0)
                continue;

            Dept d = new Dept();
            d.setVersion(entry.getKey());
            List<App> apps = new ArrayList<>();
            processAppList(appIds, appInfo, apps);
            d.setApps(apps);
            list.add(d);
        }
        return list;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getReportByVersion")
    public List<Client> getReportByVersion(@QueryParam("version") String version) throws Exception {
        List<Client> list = null;
        if (version == null || version.length() == 0)
            return list;

        Map<String, List<String>> map = getMapByVersion(version);
        if (map == null || map.size() == 0)
            return list;

        list = new ArrayList<>();
        Map<String, CMSApp> appInfo = getAllAppInfo();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<String> appIds = entry.getValue();
            if (appIds == null || appIds.size() == 0)
                continue;

            Client c = new Client();
            c.setDept(entry.getKey());
            List<App> apps = new ArrayList<>();
            processAppList(appIds, appInfo, apps);
            c.setApps(apps);
            list.add(c);
        }
        return list;
    }

    private void processAppList(List<String> appIds, Map<String, CMSApp> appInfo, List<App> apps) {
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

    private Map<String, List<String>> getMapByDept(String dept) throws Exception {
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

    private Map<String, List<String>> getMapByVersion(String version) throws Exception {
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

    private List<DalReport> getDalReport(String dept, String version) throws Exception {
        List<DalReport> list = new ArrayList<>();
        Filter filter = new Filter();
        if (dept != null && dept.length() > 0)
            filter.setDept(dept);
        if (version != null && version.length() > 0)
            filter.setVersion(version);
        try {
            RawInfo raw = getRawInfo();
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
                        list.add(report);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return list;
    }

    private RawInfo getRawInfo() throws Exception {
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
        if (map != null) {
            List<String> depts = Arrays.asList(ips);
            raw.setDepts(depts);

            List<String> versions = new ArrayList<>(map.keySet());
            versions = getFuzzyList(versions, DAL_JAVA);
            Collections.sort(versions);
            raw.setVersions(versions);
        }

        return raw;
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

    private Map<String, CMSApp> getAllAppInfo() throws Exception {
        Map<String, CMSApp> map = new HashMap<>();
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
        return map;
    }

}
