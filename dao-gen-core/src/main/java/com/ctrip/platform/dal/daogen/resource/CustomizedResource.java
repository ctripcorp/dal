package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.UserInfo;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.entity.DefaultUserInfo;
import com.ctrip.platform.dal.daogen.utils.Configuration;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CustomizedResource {
    private static final Object LOCK = new Object();
    private ClassLoader classLoader = null;
    private final String CONF_PROPERTIES = "conf.properties";
    private final String USER_INFO_CLASS_NAME = "userinfo_class";
    private UserInfo userInfo = null;
    private Boolean isDefaultUser = null;

    private final String CONFIG_CLASS_NAME = "config_class";

    private CustomizedResource() {
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = Configuration.class.getClassLoader();
            }
            userInfo = getUserInfo();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static CustomizedResource INSTANCE = null;

    public static CustomizedResource getInstance() {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new CustomizedResource();
                }
            }
        }

        return INSTANCE;
    }

    public boolean isDefaultInstance() {
        if (isDefaultUser == null) {
            synchronized (LOCK) {
                if (isDefaultUser == null) {
                    isDefaultUser = userInfo instanceof DefaultUserInfo;
                }
            }
        }
        return isDefaultUser.booleanValue();
    }

    public boolean isDefaultInstanceByRequest(HttpServletRequest request) {
        Boolean result = RequestUtil.isDefaultUser(request);
        if (result != null) {
            return result.booleanValue();
        }

        boolean value = isDefaultInstance();
        HttpSession session = RequestUtil.getSession(request);
        session.setAttribute(Consts.DEFAULT_USER, value);
        return value;
    }

    public String getEmployee(String userNo) {
        if (userNo == null || userNo.isEmpty()) {
            return userInfo.getEmployee(userNo);
        }
        return DefaultUserInfo.getInstance().getEmployee(userNo);
    }

    public String getName(String userNo) {
        if (userNo == null || userNo.isEmpty()) {
            return userInfo.getName(userNo);
        }
        return DefaultUserInfo.getInstance().getName(userNo);
    }

    public String getMail(String userNo) {
        if (userNo == null || userNo.isEmpty()) {
            return userInfo.getMail(userNo);
        }
        return DefaultUserInfo.getInstance().getMail(userNo);
    }

    private UserInfo getUserInfo() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String className = getUserInfoClassName();
        if (className == null || className.isEmpty()) {
            return DefaultUserInfo.getInstance(); // set to default
        }

        Class<?> clazz = Class.forName(className);
        return (UserInfo) clazz.newInstance();
    }

    private String getUserInfoClassName() throws IOException {
        return getClassNameFromConf(USER_INFO_CLASS_NAME);
    }

    public String getConfigClassName() throws IOException {
        return getClassNameFromConf(CONFIG_CLASS_NAME);
    }

    private String getClassNameFromConf(String className) throws IOException {
        if (className == null || className.isEmpty()) {
            return null;
        }
        Properties properties = new Properties();
        InputStream inStream = classLoader.getResourceAsStream(CONF_PROPERTIES);
        properties.load(inStream);
        return properties.getProperty(className);
    }

    public void logOut(HttpServletRequest request, HttpServletResponse response) {
        userInfo.logOut(request, response);
    }

    public DalGroupDB getDefaultDBInfo(String dbType) {
        return userInfo.getDefaultDBInfo(dbType);
    }

}
