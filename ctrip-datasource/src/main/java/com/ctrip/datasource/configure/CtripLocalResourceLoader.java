package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.ClasspathResourceLoader;
import com.ctrip.platform.dal.dao.configure.CommonFileLoader;
import com.ctrip.platform.dal.dao.configure.Resource;
import com.ctrip.platform.dal.dao.configure.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author c7ch23en
 */
public class CtripLocalResourceLoader implements ResourceLoader<String> {

    private static final String GROUP_ID = "DAL";
    private static final String DEFAULT_PATH_WINDOWS = "/D:/WebSites/CtripAppData/";
    private static final String DEFAULT_PATH_LINUX = "/opt/ctrip/AppData/";
    private static final String PATH_CLASSPATH = "$classpath";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommonFileLoader commonFileLoader;
    private final ClasspathResourceLoader classpathLoader;
    private final CtripFxLocalResourceLoader fxPathLoader;
    private final String userDefinedPath;
    private final String defaultPath;

    public CtripLocalResourceLoader() {
        this(null);
    }

    public CtripLocalResourceLoader(String userDefinedPath) {
        this(new CommonFileLoader(),
                new ClasspathResourceLoader(),
                new CtripFxLocalResourceLoader(GROUP_ID),
                userDefinedPath);
    }

    public CtripLocalResourceLoader(CommonFileLoader commonFileLoader,
                                    ClasspathResourceLoader classpathLoader,
                                    CtripFxLocalResourceLoader fxPathLoader) {
        this(commonFileLoader, classpathLoader, fxPathLoader, null);
    }

    public CtripLocalResourceLoader(CommonFileLoader commonFileLoader,
                                    ClasspathResourceLoader classpathLoader,
                                    CtripFxLocalResourceLoader fxPathLoader,
                                    String userDefinedPath) {
        this.commonFileLoader = commonFileLoader;
        this.classpathLoader = classpathLoader;
        this.fxPathLoader = fxPathLoader;
        this.userDefinedPath = userDefinedPath;
        this.defaultPath = getDefaultPath();
    }

    public String getDefaultPath() {
        String os = null;
        try {
            os = System.getProperty("os.name");
        } catch (Throwable t) {
            logger.warn("Failed to get 'os.name' from system properties", t);
        }
        return os != null && os.startsWith("Windows") ? DEFAULT_PATH_WINDOWS : DEFAULT_PATH_LINUX;
    }

    @Override
    public Resource<String> getResource(String resourceName) {
        Resource<String> resource = null;
        if (PATH_CLASSPATH.equalsIgnoreCase(userDefinedPath)) {
            resource = classpathLoader.getResource(resourceName);
        } else if (!StringUtils.isEmpty(userDefinedPath)) {
            resource = commonFileLoader.getResource(userDefinedPath + resourceName);
        }
        if (resource == null)
            resource = fxPathLoader.getResource(resourceName);
        if (resource == null)
            resource = commonFileLoader.getResource(defaultPath + resourceName);
        return resource;
    }

}
