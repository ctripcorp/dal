package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.enums.AddUser;
import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.enums.RoleType;
import com.ctrip.platform.dal.daogen.utils.Configuration;
import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;
import com.ctrip.platform.dal.daogen.utils.MD5Util;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.support.JdbcUtils;

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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Resource
@Singleton
@Path("setupDb")
public class SetupDBResource {
    private static final Object LOCK = new Object();
    private static ClassLoader classLoader = null;
    private static ObjectMapper mapper = new ObjectMapper();

    private static final java.lang.String WEB_XML = "web.xml";
    private static final String JDBC_PROPERTIES = "jdbc.properties";
    private static final String JDBC_DRIVER_CLASS_NAME = "jdbc.driverClassName";
    private static final String JDBC_URL = "jdbc.url";
    private static final String JDBC_USERNAME = "jdbc.username";
    private static final String JDBC_PASSWORD = "jdbc.password";
    private String jdbcUrlTemplate = "jdbc:mysql://%s:%s/%s";
    private static final String SCRIPT_FILE = "script.sql";
    private static final String CREATE_TABLE = "CREATE TABLE";
    private static boolean initialized = false;
    private static Boolean jdbcInitialized = null;

    static {
        synchronized (LOCK) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = Configuration.class.getClassLoader();
            }
        }
    }

    public static boolean isJdbcInitialized() {
        if (jdbcInitialized == null) {
            synchronized (LOCK) {
                if (jdbcInitialized == null) {
                    try {
                        jdbcInitialized = resourceExists(JDBC_PROPERTIES) & jdbcPropertiesValid();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return jdbcInitialized.booleanValue();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("setupDbCheck")
    public Status setupDbCheck() throws Exception {
        Status status = Status.OK;
        if (initialized) {
            status.setInfo("");
            return status;
        }

        try {
            boolean result = resourceExists(JDBC_PROPERTIES);
            if (result) {
                boolean valid = jdbcPropertiesValid();
                if (!valid) {
                    result = false;
                    status = Status.ERROR;
                    status.setInfo("!valid");
                }
            } else {
                result = false;
                status = Status.ERROR;
                status.setInfo("!jdbc");
            }

            if (result && !initialized) {
                synchronized (LOCK) {
                    if (!initialized) {
                        initialized = true;
                        jdbcInitialized = true;
                    }
                }

                status.setInfo("initialized");
                initializeConfig();
            }
        } catch (Exception e) {
            status = Status.ERROR;
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
        Status status = Status.ERROR;

        try {
            boolean jdbcInitialized = initializeJdbcProperties(dbaddress, dbport, dbuser, dbpassword, dbcatalog);
            if (jdbcInitialized) {
                boolean result = tableConsistent(dbcatalog);
                if (result) {
                    status = Status.OK;
                }
                boolean flag = clearJdbcProperties();
            }
        } catch (Exception e) {
            status = Status.ERROR;
            status.setInfo(e.getMessage());
        }

        return status;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("initializeJdbc")
    public Status initializeJdbc(@FormParam("dbaddress") String dbaddress, @FormParam("dbport") String dbport,
                                 @FormParam("dbuser") String dbuser, @FormParam("dbpassword") String dbpassword,
                                 @FormParam("dbcatalog") String dbcatalog) {
        Status status = Status.OK;
        try {
            boolean result = initializeJdbcProperties(dbaddress, dbport, dbuser, dbpassword, dbcatalog);
            if (!result) {
                status = Status.ERROR;
            }
        } catch (Exception e) {
            status = Status.ERROR;
            status.setInfo(e.getMessage());
        }
        return status;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("initializeDb")
    public Status initializeDb(@Context HttpServletRequest request, @FormParam("dbaddress") String dbaddress, @FormParam("dbport") String dbport,
                               @FormParam("dbuser") String dbuser, @FormParam("dbpassword") String dbpassword, @FormParam("dbcatalog") String dbcatalog,
                               @FormParam("groupName") String groupName, @FormParam("groupComment") String groupComment, @FormParam("adminNo") String adminNo,
                               @FormParam("adminName") String adminName, @FormParam("adminEmail") String adminEmail, @FormParam("adminPass") String adminPass) {
        Status status = Status.OK;
        try {
            boolean jdbcResult = initializeJdbcProperties(dbaddress, dbport, dbuser, dbpassword, dbcatalog);
            if (!jdbcResult) {
                status = Status.ERROR;
                status.setInfo("Error occured while initializing the jdbc.properties file.");
                return status;
            }

            initializeConfig(); //to be deleted
            boolean isSetupTables = setupTables();
            if (!isSetupTables) {
                status = Status.ERROR;
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
                status = Status.ERROR;
                status.setInfo("Error occured while setting up the admin.");
                return status;
            }
        } catch (Exception e) {
            status = Status.ERROR;
            status.setInfo(e.getMessage());
        }
        return status;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("connectionTest")
    public Status connectionTest(@FormParam("dbtype") String dbtype, @FormParam("dbaddress") String dbaddress,
                                 @FormParam("dbport") String dbport, @FormParam("dbuser") String dbuser,
                                 @FormParam("dbpassword") String dbpassword) {
        Status status = Status.OK;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DataSourceUtil.getConnection(dbaddress, dbport, dbuser, dbpassword, DatabaseType.valueOf(dbtype).getValue());
            rs = conn.getMetaData().getCatalogs();
            Set<String> allCatalog = new HashSet<>();
            while (rs.next()) {
                allCatalog.add(rs.getString("TABLE_CAT"));
            }
            status.setInfo(mapper.writeValueAsString(allCatalog));
        } catch (SQLException e) {
            status = Status.ERROR;
            status.setInfo(e.getMessage());
            return status;
        } catch (JsonProcessingException e) {
            status = Status.ERROR;
            status.setInfo(e.getMessage());
            return status;
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeConnection(conn);
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

    private static boolean jdbcPropertiesValid() throws Exception {
        boolean result = true;
        Properties properties = new Properties();
        InputStream inStream = classLoader.getResourceAsStream(JDBC_PROPERTIES);
        properties.load(inStream);
        String driverClassName = properties.getProperty(JDBC_DRIVER_CLASS_NAME);
        result &= (driverClassName != null && driverClassName.trim().length() > 0);
        String url = properties.getProperty(JDBC_URL);
        result &= (url != null && url.trim().length() > 0);
        String userName = properties.getProperty(JDBC_USERNAME);
        result &= (userName != null && userName.trim().length() > 0);
        String password = properties.getProperty(JDBC_PASSWORD);
        result &= (password != null && password.trim().length() > 0);
        return result;
    }

    private boolean tableConsistent(String catalog) throws Exception {
        boolean result = false;
        Set<String> catalogTableNames = SpringBeanGetter.getSetupDBDao().getCatalogTableNames(catalog);
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

    private boolean initializeJdbcProperties(String dbaddress, String dbport, String dbuser, String dbpassword, String dbcatalog) {
        boolean result = false;
        try {
            Properties properties = new Properties();
            properties.setProperty(JDBC_DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver"); // Currently
            // fixed.
            properties.setProperty(JDBC_URL, String.format(jdbcUrlTemplate, dbaddress, dbport, dbcatalog));
            properties.setProperty(JDBC_USERNAME, dbuser);
            properties.setProperty(JDBC_PASSWORD, dbpassword);
            URL url = classLoader.getResource(WEB_XML);
            String path = url.getPath().replace(WEB_XML, JDBC_PROPERTIES);
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            properties.store(fileOutputStream, "");
            fileOutputStream.close();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean clearJdbcProperties() {
        boolean result = false;
        try {
            Properties properties = new Properties();
            properties.setProperty(JDBC_DRIVER_CLASS_NAME, "");
            properties.setProperty(JDBC_URL, "");
            properties.setProperty(JDBC_USERNAME, "");
            properties.setProperty(JDBC_PASSWORD, "");
            URL url = classLoader.getResource(WEB_XML);
            String path = url.getPath().replace(WEB_XML, JDBC_PROPERTIES);
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            properties.store(fileOutputStream, "");
            fileOutputStream.close();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    private void initializeConfig() throws Exception {
        SpringBeanGetter.refreshApplicationContext();
    }

    private boolean setupTables() throws Exception {
        boolean scriptExists = resourceExists(SCRIPT_FILE);
        if (!scriptExists) {
            throw new Exception("script.sql not found.");
        }
        String scriptContent = getScriptContent(SCRIPT_FILE);
        return SpringBeanGetter.getSetupDBDao().executeSqlScript(scriptContent);
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

        int userResult = SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
        if (userResult <= 0) {
            return result;
        }
        user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(user.getUserNo());

        DalGroup group = new DalGroup();
        group.setId(DalGroupResource.SUPER_GROUP_ID);
        group.setGroup_name(dalGroup.getGroup_name());
        group.setGroup_comment(dalGroup.getGroup_comment());
        group.setCreate_user_no(user.getUserNo());
        group.setCreate_time(new Timestamp(System.currentTimeMillis()));

        int groupResult = SpringBeanGetter.getDaoOfDalGroup().insertDalGroup(group);
        if (groupResult <= 0) {
            return result;
        }

        int userGroupResult = SpringBeanGetter.getDalUserGroupDao().insertUserGroup(user.getId(), DalGroupResource.SUPER_GROUP_ID, RoleType.Admin.getValue(), AddUser.Allow.getValue());
        if (userGroupResult <= 0) {
            return result;
        }
        return true;
    }
}
