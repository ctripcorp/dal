package com.ctrip.platform.dal.daogen.resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jasig.cas.client.util.AssertionHolder;
import org.springframework.jdbc.support.JdbcUtils;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroup;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.enums.AddUser;
import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.enums.RoleType;
import com.ctrip.platform.dal.daogen.utils.DataSourceUtil;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Resource
@Singleton
@Path("setupDb")
public class SetupDBResource {
	private static ClassLoader classLoader = null;
	private static ObjectMapper mapper = new ObjectMapper();

	private static String webXml = "web.xml";
	private static String jdbcProperties = "jdbc.properties";
	private static String jdbcDriverClassName = "jdbc.driverClassName";
	private static String jdbcUrl = "jdbc.url";
	private static String jdbcUsername = "jdbc.username";
	private static String jdbcPassword = "jdbc.password";
	private String jdbcUrlTemplate = "jdbc:mysql://%s:%s/%s";
	private static String scriptFile = "script.sql";
	private static String createTable = "CREATE TABLE";
	private boolean initialized = false;

	static {
		classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = Configuration.class.getClassLoader();
		}
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
			boolean result = resourceExists(jdbcProperties);
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
				initialized = true;
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
	@Path("initializeDb")
	public Status initializeDb(@FormParam("dbaddress") String dbaddress,
			@FormParam("dbport") String dbport,
			@FormParam("dbuser") String dbuser,
			@FormParam("dbpassword") String dbpassword,
			@FormParam("dbcatalog") String dbcatalog,
			@FormParam("groupName") String groupName,
			@FormParam("groupComment") String groupComment) {
		Status status = Status.OK;
		try {
			boolean jdbcResult = initializeJdbcProperties(dbaddress, dbport,
					dbuser, dbpassword, dbcatalog);
			if (!jdbcResult) {
				status = Status.ERROR;
				status.setInfo("Error occured while initializing the jdbc.properties file.");
				return status;
			}
			initializeConfig();
			boolean isSetupTables = setupTables();
			if (!isSetupTables) {
				status = Status.ERROR;
				status.setInfo("Error occured while setting up the tables.");
				return status;
			}
			boolean isSetupAdmin = setupAdmin(groupName, groupComment);
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
	public Status connectionTest(@FormParam("dbtype") String dbtype,
			@FormParam("dbaddress") String dbaddress,
			@FormParam("dbport") String dbport,
			@FormParam("dbuser") String dbuser,
			@FormParam("dbpassword") String dbpassword) {
		Status status = Status.OK;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DataSourceUtil.getConnection(dbaddress, dbport, dbuser,
					dbpassword, DatabaseType.valueOf(dbtype).getValue());
			rs = conn.getMetaData().getCatalogs();
			Set<String> allCatalog = new HashSet<String>();
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

	private boolean resourceExists(String fileName) {
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

	private boolean jdbcPropertiesValid() throws Exception {
		boolean result = true;
		Properties properties = new Properties();
		InputStream inStream = classLoader.getResourceAsStream(jdbcProperties);
		properties.load(inStream);
		String driverClassName = properties.getProperty(jdbcDriverClassName);
		result &= (driverClassName != null);
		String url = properties.getProperty(jdbcUrl);
		result &= (url != null);
		String userName = properties.getProperty(jdbcUsername);
		result &= (userName != null);
		String password = properties.getProperty(jdbcPassword);
		result &= (password != null);
		return result;
	}

	private boolean tableConsistent() throws Exception {
		boolean result = false;
		Set<String> catalogTableNames = SpringBeanGetter.getSetupDBDao()
				.getCatalogTableNames(null);
		if (catalogTableNames == null || catalogTableNames.size() == 0) {
			return result;
		}
		String scriptContent = getScriptContent(scriptFile);
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
		Set<String> set = new HashSet<String>();
		if (script == null || script.length() == 0) {
			return set;
		}

		String[] array = script.toUpperCase().split(";");
		for (int i = 0; i < array.length; i++) {
			int beginIndex = array[i].indexOf(createTable);
			if (beginIndex == -1) {
				continue;
			}

			int endIndex = array[i].indexOf("(");
			String temp = array[i].substring(beginIndex, endIndex);
			String tableName = temp.replaceAll("`", "").trim();
			if (tableName != null && tableName.length() > 0) {
				set.add(tableName);
			}
		}
		return set;
	}

	private boolean initializeJdbcProperties(String dbaddress, String dbport,
			String dbuser, String dbpassword, String dbcatalog) {
		boolean result = false;
		try {
			Properties properties = new Properties();
			properties
					.setProperty(jdbcDriverClassName, "com.mysql.jdbc.Driver"); // Currently
																				// fixed.
			properties.setProperty(jdbcUrl, String.format(jdbcUrlTemplate,
					dbaddress, dbport, dbcatalog));
			properties.setProperty(jdbcUsername, dbuser);
			properties.setProperty(jdbcPassword, dbpassword);
			URL url = classLoader.getResource(webXml);
			String path = url.getPath().replace(webXml, jdbcProperties);
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
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream(scriptPath);
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
		SpringBeanGetter.initializeApplicationContext();
	}

	private boolean setupTables() throws Exception, InvocationTargetException {
		boolean scriptExists = resourceExists(scriptFile);
		if (!scriptExists) {
			throw new Exception("script.sql not found.");
		}
		String scriptContent = getScriptContent(scriptFile);
		return SpringBeanGetter.getSetupDBDao().executeSqlScript(scriptContent);
	}

	private boolean setupAdmin(String groupName, String groupComment)
			throws Exception {
		boolean result = false;
		String userNo = AssertionHolder.getAssertion().getPrincipal()
				.getAttributes().get("employee").toString();
		if (userNo == null || groupName == null || groupName.isEmpty()) {
			return result;
		}

		LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(
				userNo);
		if (user == null) {
			user = new LoginUser();
			user.setUserNo(userNo);
			user.setUserName(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("sn").toString());
			user.setUserEmail(AssertionHolder.getAssertion().getPrincipal()
					.getAttributes().get("mail").toString());
			int userResult = SpringBeanGetter.getDaoOfLoginUser().insertUser(
					user);
			if (userResult <= 0) {
				return result;
			}
			user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
		}

		DalGroup group = new DalGroup();
		group.setId(DalGroupResource.SUPER_GROUP_ID);
		group.setGroup_name(groupName);
		group.setGroup_comment(groupComment);
		group.setCreate_user_no(userNo);
		group.setCreate_time(new Timestamp(System.currentTimeMillis()));

		int groupResult = SpringBeanGetter.getDaoOfDalGroup().insertDalGroup(
				group);
		if (groupResult <= 0) {
			return result;
		}

		int userGroupResult = SpringBeanGetter.getDalUserGroupDao()
				.insertUserGroup(user.getId(), DalGroupResource.SUPER_GROUP_ID,
						RoleType.Admin.getValue(), AddUser.Allow.getValue());
		if (userGroupResult <= 0) {
			return result;
		}
		return true;
	}
}
