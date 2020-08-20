package com.ctrip.platform.dal.dao.configure;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author c7ch23en
 */
public class ClasspathResourceLoader extends AbstractResourceLoader {

    @Override
    protected String getResourceContent(String resourceName) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(resourceName);
        if (url != null) {
            try (InputStream stream = url.openStream()) {
                return getString(stream);
            }
        }
        throw createFileNotFoundException(resourceName);
    }

    @Override
    protected String getResourceNameForLog(String resourceName) {
        return "classpath:" + resourceName;
    }

}
