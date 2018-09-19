package com.ctrip.framework.idgen.client.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class Version {

    private static final String VERSION_FILE_PATH = "/version.properties";
    private static final String VERSION_PROPERTY_KEY = "version";
    private static final String VERSION_DEFAULT_VALUE = "undefined";

    private static AtomicReference<String> versionRef = new AtomicReference<>(VERSION_DEFAULT_VALUE);

    public static String getVersion() {
        return versionRef.get();
    }

    public void initialize() {
        String version = VERSION_DEFAULT_VALUE;
        InputStream stream = Version.class.getResourceAsStream(VERSION_FILE_PATH);
        if (stream != null) {
            Properties properties = new Properties();
            try {
                properties.load(stream);
                version = properties.get(VERSION_PROPERTY_KEY).toString();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        versionRef.set(version);
    }

}
