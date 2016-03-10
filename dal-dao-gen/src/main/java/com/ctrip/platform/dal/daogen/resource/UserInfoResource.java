package com.ctrip.platform.dal.daogen.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.UserInfo;
import com.ctrip.platform.dal.daogen.entity.DefaultUserInfo;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class UserInfoResource {
    private UserInfoResource() {
    }

    private static class Lazy {
        private static final UserInfoResource INSTANCE = new UserInfoResource();
    }

    public static final UserInfoResource getInstance() {
        return Lazy.INSTANCE;
    }

    private static final Object LOCK = new Object();
    private static ClassLoader classLoader = null;
    private static final String CONF_PROPERTIES = "conf.properties";
    private static final String USER_INFO_CLASS_NAME = "userinfo_class";

    private static UserInfo userInfo = null;

    public static boolean isDefaultInstance() {
        return userInfo instanceof DefaultUserInfo;
    }

    static {
        try {
            synchronized (LOCK) {
                classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader == null) {
                    classLoader = Configuration.class.getClassLoader();
                }

                userInfo = getUserInfo();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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

    private static UserInfo getUserInfo()
            throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String className = getUserInfoClassName();
        if (className == null || className.isEmpty()) {
            return DefaultUserInfo.getInstance(); // set to default
        }

        Class<?> clazz = Class.forName(className);
        return (UserInfo) clazz.newInstance();
    }

    private static String getUserInfoClassName() throws IOException {
        Properties properties = new Properties();
        InputStream inStream = classLoader.getResourceAsStream(CONF_PROPERTIES);
        properties.load(inStream);
        return properties.getProperty(USER_INFO_CLASS_NAME);
    }

    public void logOut(HttpServletRequest request, HttpServletResponse response) {
        userInfo.logOut(request, response);
    }

}
