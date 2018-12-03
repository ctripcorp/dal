package com.ctrip.datasource.configure.file;

import com.ctrip.datasource.datasource.IPDomainStatusChanged;
import com.ctrip.datasource.datasource.IPDomainStatusProvider;
import com.ctrip.platform.dal.common.enums.IPDomainStatus;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Properties;

public class PropertyFileIPDomainStatusProvider implements IPDomainStatusProvider {
    private static final String PROPERTY_FILE_NAME = "ipdomainstatus.properties";
    private static final String STATUS = "status";

    private Properties properties = new Properties();

    public PropertyFileIPDomainStatusProvider() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = PropertyFileIPDomainStatusProvider.class.getClassLoader();
        }

        URL url = classLoader.getResource(PROPERTY_FILE_NAME);
        if (url == null)
            return;

        try {
            properties.load(new FileReader(new File(url.toURI())));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IPDomainStatus getStatus() {
        boolean value = Boolean.valueOf(properties.getProperty(STATUS));
        return value ? IPDomainStatus.IP : IPDomainStatus.Domain;
    }

    @Override
    public void addIPDomainStatusChangedListener(IPDomainStatusChanged callback) {}
}