package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.enums.AddUser;
import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.enums.RoleType;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.Configuration;
import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;
import com.ctrip.platform.dal.daogen.utils.MD5Util;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.ResourceUtils;
import com.ctrip.platform.dal.daogen.utils.XmlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Resource
@Singleton
@Path("setupDb")
public class SetupDBResource {
    private static final Object LOCK = new Object();
    private static ClassLoader classLoader = null;
    private static ObjectMapper mapper = new ObjectMapper();

    private static final String LOGIC_DBNAME = "dao";
    private static final String WEB_XML = "web.xml";
    private static final String DATASOURCE_XML = "datasource.xml";
    private static final String DATASOURCE = "Datasource";
    private static final String DATASOURCE_NAME = "name";
    private static final String DATASOURCE_USERNAME = "userName";
    private static final String DATASOURCE_PASSWORD = "password";
    private static final String DATASOURCE_CONNECTION_URL = "connectionUrl";
    private static final String DATASOURCE_DRIVER_CLASS = "driverClassName";
    private static final String DATASOURCE_MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private String jdbcUrlTemplate = "jdbc:mysql://%s:%s/%s";
    private static final String SCRIPT_FILE = "script.sql";
    private static final String CREATE_TABLE = "CREATE TABLE";
    private static boolean initialized = false;
    private static Boolean dalInitialized = null;

    static {
        synchronized (LOCK) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = Configuration.class.getClassLoader();
            }
        }
    }

    public static boolean isDalInitialized() {
        if (dalInitialized == null) {
            synchronized (LOCK) {
                if (dalInitialized == null) {
                    try {
                        dalInitialized = resourceExists(DATASOURCE_XML);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return dalInitialized.booleanValue();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("setupDbCheck")
    public Status setupDbCheck() throws Exception {
        Status status = Status.OK();
        if (initialized) {
            status.setInfo("");
            return status;
        }

        try {
            boolean valid = datasourceXmlValid();
            if (!valid) {
                status = Status.ERROR();
                status.setInfo("!valid");
            }

            if (valid && !initialized) {
                synchronized (LOCK) {
                    if (!initialized) {
                        initialized = true;
                        dalInitialized = true;
                    }
                }

                status.setInfo("initialized");
            }
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getMessage());
        }
        return status;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("tableConsistentCheck")
    public Status tableConsistentCheck(@FormParam("dbaddress") String dbaddress, @FormParam("dbport") String dbport,
            @FormParam("dbuser") String dbuser, @FormParam("dbpassword") String dbpassword,
            @FormParam("dbcatalog") String dbcatalog) {
        Status status = Status.ERROR();

        try {
            boolean initialized = initializeDatasourceXml(dbaddress, dbport, dbuser, dbpassword, dbcatalog);
            if (initialized) {
                boolean result = tableConsistent(dbcatalog);
                if (result) {
                    status = Status.OK();
                }
            }
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getMessage());
        }

        return status;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("initializeDal")
    public Status initializeDal(@FormParam("dbaddress") String dbaddress, @FormParam("dbport") String dbport,
            @FormParam("dbuser") String dbuser, @FormParam("dbpassword") String dbpassword,
            @FormParam("dbcatalog") String dbcatalog) {
        Status status = Status.OK();
        try {
            boolean result = initializeDatasourceXml(dbaddress, dbport, dbuser, dbpassword, dbcatalog);
            if (!result) {
                status = Status.ERROR();
            }
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getMessage());
        }
        return status;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("initializeDb")
    public Status initializeDb(@Context HttpServletRequest request, @FormParam("dbaddress") String dbaddress,
            @FormParam("dbport") String dbport, @FormParam("dbuser") String dbuser,
            @FormParam("dbpassword") String dbpassword, @FormParam("dbcatalog") String dbcatalog,
            @FormParam("groupName") String groupName, @FormParam("groupComment") String groupComment,
            @FormParam("adminNo") String adminNo, @FormParam("adminName") String adminName,
            @FormParam("adminEmail") String adminEmail, @FormParam("adminPass") String adminPass) {
        Status status = Status.OK();
        try {
            boolean result = initializeDatasourceXml(dbaddress, dbport, dbuser, dbpassword, dbcatalog);
            if (!result) {
                status = Status.ERROR();
                status.setInfo("Error occured while initializing the jdbc.properties file.");
                return status;
            }

            boolean isSetupTables = setupTables();
            if (!isSetupTables) {
                status = Status.ERROR();
                status.setInfo("Error occured while setting up the tables.");
                return status;
            }

            DalGroup group = new DalGroup();
            group.setGroup_name(groupName);
            group.setGroup_comment(groupComment);

            LoginUser user = new LoginUser();
            user.setUserNo(adminNo);
            user.setUserName(adminName);
            user.setUserEmail(adminEmail);
            user.setPassword(MD5Util.parseStrToMd5L32(adminPass));

            boolean isSetupAdmin = setupAdmin(group, user);
            if (!isSetupAdmin) {
                status = Status.ERROR();
                status.setInfo("Error occured while setting up the admin.");
                return status;
            }
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getMessage());
        }
        return status;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("connectionTest")
    public Status connectionTest(@FormParam("dbtype") String dbtype, @FormParam("dbaddress") String dbaddress,
            @FormParam("dbport") String dbport, @FormParam("dbuser") String dbuser,
            @FormParam("dbpassword") String dbpassword) throws Exception {
        Status status = Status.OK();
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DataSourceUtil.getConnection(dbaddress, dbport, dbuser, dbpassword,
                    DatabaseType.valueOf(dbtype).getValue());
            rs = conn.getMetaData().getCatalogs();
            Set<String> allCatalog = new HashSet<>();
            while (rs.next()) {
                allCatalog.add(rs.getString("TABLE_CAT"));
            }
            status.setInfo(mapper.writeValueAsString(allCatalog));
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            status = Status.ERROR();
            status.setInfo(e.getMessage());
        } finally {
            ResourceUtils.close(rs);
            ResourceUtils.close(conn);
        }

        return status;
    }

    private static boolean resourceExists(String fileName) {
        boolean result = false;
        if (fileName == null || fileName.length() == 0) {
            return result;
        }

        URL url = classLoader.getResource(fileName);
        if (url != null) {
            result = true;
        }
        return result;
    }

    private static boolean datasourceXmlValid() throws Exception {
        boolean result = true;
        Document document = XmlUtil.getDocument(DATASOURCE_XML);
        if (document == null)
            return false;
        Element root = document.getRootElement();
        List<Element> nodes = XmlUtil.getChildElements(root, DATASOURCE);
        if (nodes == null || nodes.size() == 0)
            return false;
        Element node = nodes.get(0);
        String userName = XmlUtil.getAttribute(node, DATASOURCE_USERNAME);
        result &= (userName != null && userName.trim().length() > 0);
        String password = XmlUtil.getAttribute(node, DATASOURCE_PASSWORD);
        result &= (password != null && password.trim().length() > 0);
        String url = XmlUtil.getAttribute(node, DATASOURCE_CONNECTION_URL);
        result &= (url != null && url.trim().length() > 0);
        return result;
    }

    private boolean tableConsistent(String catalog) throws Exception {
        boolean result = false;
        Set<String> catalogTableNames = BeanGetter.getSetupDBDao().getCatalogTableNames(catalog);
        if (catalogTableNames == null || catalogTableNames.size() == 0) {
            return result;
        }
        String scriptContent = getScriptContent(SCRIPT_FILE);
        Set<String> scriptTableNames = getScriptTableNames(scriptContent);
        result = true;
        if (scriptTableNames != null && scriptTableNames.size() > 0) {
            for (String tableName : scriptTableNames) {
                if (!catalogTableNames.contains(tableName)) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    private Set<String> getScriptTableNames(String script) {
        Set<String> set = new HashSet<>();
        if (script == null || script.length() == 0) {
            return set;
        }

        String[] array = script.toUpperCase().split(";");
        for (int i = 0; i < array.length; i++) {
            int beginIndex = array[i].indexOf(CREATE_TABLE);
            if (beginIndex == -1) {
                continue;
            }

            beginIndex += CREATE_TABLE.length();
            int endIndex = array[i].indexOf("(");
            String temp = array[i].substring(beginIndex, endIndex);
            String tableName = temp.replaceAll("`", "").trim();
            if (tableName != null && tableName.length() > 0) {
                set.add(tableName);
            }
        }
        return set;
    }

    private boolean initializeDatasourceXml(String dbaddress, String dbport, String dbuser, String dbpassword,
            String dbcatalog) throws Exception {
        boolean result = false;
        try {
            String connectionUrl = String.format(jdbcUrlTemplate, dbaddress, dbport, dbcatalog);
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("Datasources");
            root.addElement("Datasource").addAttribute(DATASOURCE_NAME, LOGIC_DBNAME)
                    .addAttribute(DATASOURCE_USERNAME, dbuser).addAttribute(DATASOURCE_PASSWORD, dbpassword)
                    .addAttribute(DATASOURCE_CONNECTION_URL, connectionUrl)
                    .addAttribute(DATASOURCE_DRIVER_CLASS, DATASOURCE_MYSQL_DRIVER);

            URL url = classLoader.getResource(WEB_XML);
            String path = url.getPath().replace(WEB_XML, DATASOURCE_XML);

            try (FileWriter fileWriter = new FileWriter(path)) {
                XMLWriter writer = new XMLWriter(fileWriter);
                writer.write(document);
                writer.close();
            }

            DalClientFactory.getClient(LOGIC_DBNAME);
            result = true;
        } catch (Throwable e) {
            throw e;
        }
        return result;
    }

    private String getScriptContent(String scriptPath) throws Exception {
        if (scriptPath == null || scriptPath.length() == 0) {
            return null;
        }
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(scriptPath);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        return stringBuffer.toString();
    }

    private boolean setupTables() throws Exception {
        boolean scriptExists = resourceExists(SCRIPT_FILE);
        if (!scriptExists) {
            throw new Exception("script.sql not found.");
        }
        String scriptContent = getScriptContent(SCRIPT_FILE);
        return BeanGetter.getSetupDBDao().executeSqlScript(scriptContent);
    }

    private boolean setupAdmin(DalGroup dalGroup, LoginUser user) throws Exception {
        boolean result = false;
        String groupName = dalGroup.getGroup_name();
        if (groupName == null || groupName.isEmpty()) {
            return result;
        }

        String userName = user.getUserName();
        if (userName == null || userName.isEmpty()) {
            return result;
        }

        int userResult = BeanGetter.getDaoOfLoginUser().insertUser(user);
        if (userResult <= 0) {
            return result;
        }
        user = BeanGetter.getDaoOfLoginUser().getUserByNo(user.getUserNo());

        DalGroup group = new DalGroup();
        group.setId(DalGroupResource.SUPER_GROUP_ID);
        group.setGroup_name(dalGroup.getGroup_name());
        group.setGroup_comment(dalGroup.getGroup_comment());
        group.setCreate_user_no(user.getUserNo());
        group.setCreate_time(new Timestamp(System.currentTimeMillis()));

        int groupResult = BeanGetter.getDaoOfDalGroup().insertDalGroup(group);
        if (groupResult <= 0) {
            return result;
        }

        int userGroupResult = BeanGetter.getDalUserGroupDao().insertUserGroup(user.getId(),
                DalGroupResource.SUPER_GROUP_ID, RoleType.Admin.getValue(), AddUser.Allow.getValue());
        if (userGroupResult <= 0) {
            return result;
        }
        return true;
    }
}
