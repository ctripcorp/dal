package com.ctrip.platform.dal.daogen.log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by yj.huang on 16-5-26.
 */
public class AppIdReader {
    public static void main(String[] args) {
        printAppId();
    }

    public static void printAppId() {
        final String APP_PROPERTIES_CLASSPATH = "/META-INF/app.properties";
        Properties m_appProperties = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_CLASSPATH);
        if (in == null) {
            in = AppIdReader.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
        }
        try {
            m_appProperties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("App ID： " + m_appProperties.getProperty("app.id"));
    }
}
