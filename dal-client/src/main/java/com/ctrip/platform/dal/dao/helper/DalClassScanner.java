package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DalClassScanner implements ClassScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DalClassScanner.class);

    private static final String URL_PROTOCOL_FILE = "file";
    private static final String URL_PROTOCOL_JAR = "jar";
    private static final String CLASS_NAME_SUFFIX = ".class";
    private static final String INNER_CLASS_HINT = "$";
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String PATH_SEPARATOR = "/";
    private static final String EMPTY_PACKAGE = "";
    private static final long URL_SCAN_TIMEOUT_SECONDS = 5L;

    private FileFilter classFileOrDirectoryFilter = new ClassFileOrDirectoryFilter();
    private ClassScanFilter classScanFilter;
    private ClassLoader classLoader;
//    private AtomicInteger fileCount = new AtomicInteger(0);
//    private AtomicInteger jarCount = new AtomicInteger(0);

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public DalClassScanner() {
        this(null, null);
    }

    public DalClassScanner(ClassLoader classLoader) {
        this(null, classLoader);
    }

    public DalClassScanner(ClassScanFilter classScanFilter) {
        this(classScanFilter, null);
    }

    public DalClassScanner(ClassScanFilter classScanFilter, ClassLoader classLoader) {
        this.classScanFilter = classScanFilter;
        this.classLoader = classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader();
    }

    @Override
    public List<Class<?>> getClasses(String packageName, boolean recursive) {
        long startTime = System.currentTimeMillis();
//        fileCount.set(0);
//        jarCount.set(0);
        List<Class<?>> classList = new Vector<>();
        if (null == packageName || packageName.trim().isEmpty()) {
            throw new IllegalArgumentException("packageName should not be null or empty");
        }
        packageName = packageName.trim();
        String packagePath = packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
        try {
            Enumeration<URL> urls = classLoader.getResources(packagePath);
            List<URL> urlList = new ArrayList<>();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    urlList.add(url);
                }
            }
            findClasses(classList, packageName, urlList, recursive);
        } catch (IOException e) {
            throw new DalRuntimeException(e);
        }
        long endTime = System.currentTimeMillis();

        LOGGER.info("=========================================================================");
        LOGGER.info(String.format("Time cost: %d ms", endTime - startTime));
        LOGGER.info(String.format("%d classes found", classList.size()));
//        LOGGER.info(String.format("%d files scanned", fileCount.get()));
//        LOGGER.info(String.format("%d jars scanned", jarCount.get()));
        LOGGER.info("=========================================================================");
        return classList;
    }

    private void findClasses(final List<Class<?>> classList, final String packageName,
                             final List<URL> urls, final boolean recursive) {
        int urlCount = urls.size();
        if (urlCount > 0) {
            final CountDownLatch latch = new CountDownLatch(urlCount);
            for (final URL url : urls) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String protocol = url.getProtocol();
                            String path = url.getPath();
                            if (URL_PROTOCOL_FILE.equals(protocol)) {
                                findClasses(classList, packageName, path, recursive);
                            } else if (URL_PROTOCOL_JAR.equals(protocol)) {
                                findClasses(classList, packageName, url, recursive);
                            }
                        } catch (Throwable t) {
                            LOGGER.error(t.getMessage(), t);
                        }
                        latch.countDown();
                    }
                });
            }
            try {
                latch.await(URL_SCAN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void findClasses(List<Class<?>> classList, String packageName, String path, boolean recursive) {
        File[] files = new File(path).listFiles(classFileOrDirectoryFilter);
        if (files != null) {
            for (File file : files) {
                try {
                    String fileName = file.getName();
//                    fileCount.incrementAndGet();
                    if (file.isFile()) {
//                        LOGGER.info("Scanning file: " + file.getPath());
                        String className = getClassName(packageName, fileName);
                        tryAddClass(classList, className);
                    } else if (recursive) {
                        String subPackageName = packageName.isEmpty() ?
                                fileName : packageName + PACKAGE_SEPARATOR + fileName;
                        String subPath = path.endsWith(PATH_SEPARATOR) ?
                                path + fileName : path + PATH_SEPARATOR + fileName;
                        findClasses(classList, subPackageName, subPath, recursive);
                    }
                } catch (Throwable t) {
                    LOGGER.warn(t.getMessage(), t);
                }
            }
        }
    }

    private void findClasses(List<Class<?>> classList, String packageName, URL url, boolean recursive) throws Throwable {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> jarEntries = jarFile.entries();
//        jarCount.incrementAndGet();
//        LOGGER.info("Scanning jar: " + jarFile.getName());
        while (jarEntries.hasMoreElements()) {
            try {
                JarEntry jarEntry = jarEntries.nextElement();
                String jarEntryName = jarEntry.getName();
                String jarEntryClassName = getClassName(jarEntryName);
                if (jarEntryClassName != null) {
                    String jarEntryPackageName = getPackageName(jarEntryClassName);
                    if (jarEntryPackageName.equals(packageName) ||
                            (recursive && jarEntryPackageName.startsWith(packageName))) {
                        tryAddClass(classList, jarEntryClassName);
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn(t.getMessage(), t);
            }
        }
    }

    private String getPackageName(String className) {
        int lastIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        if (lastIndex > 0) {
            return className.substring(0, lastIndex);
        }
        return EMPTY_PACKAGE;
    }

    private String getClassName(String jarEntryName) {
        int lastIndex = jarEntryName.lastIndexOf(CLASS_NAME_SUFFIX);
        if (lastIndex > 0) {
            String className = jarEntryName.substring(0, lastIndex).replace(PATH_SEPARATOR, PACKAGE_SEPARATOR);
            if (!isInnerClass(className)) {
                return className;
            }
        }
        return null;
    }

    private String getClassName(String packageName, String fileName) {
        int lastIndex = fileName.lastIndexOf(CLASS_NAME_SUFFIX);
        if (lastIndex > 0) {
            return packageName.isEmpty() ?
                    fileName.substring(0, lastIndex) :
                    packageName + PACKAGE_SEPARATOR + fileName.substring(0, lastIndex);
        }
        return null;
    }

    private void tryAddClass(List<Class<?>> classList, String className) {
        if (null == className) {
            return;
        }
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className, false, classLoader);
        } catch (Throwable t) {
//            LOGGER.warn(String.format("Class '%s' cannot be loaded", className));
        }
        if (clazz != null) {
            if (classScanFilter != null) {
                if (classScanFilter.accept(clazz)) {
                    classList.add(clazz);
                }
            } else {
                classList.add(clazz);
            }
        }
    }

    private class ClassFileOrDirectoryFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            String name = file.getName();
            return (file.isFile() && name.endsWith(CLASS_NAME_SUFFIX) && !isInnerClass(name)) ||
                    file.isDirectory();
        }
    }

    private boolean isInnerClass(String className) {
        return className.contains(INNER_CLASS_HINT);
    }

}
