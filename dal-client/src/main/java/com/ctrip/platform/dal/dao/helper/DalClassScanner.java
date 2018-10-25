package com.ctrip.platform.dal.dao.helper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DalClassScanner implements ClassScanner {

    private static final String URL_PROTOCOL_FILE = "file";
    private static final String URL_PROTOCOL_JAR = "jar";
    private static final String CLASS_NAME_SUFFIX = ".class";
    private static final String INNER_CLASS_HINT = "$";
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String PATH_SEPARATOR = "/";
    private static final String EMPTY_PACKAGE = "";

    private FileFilter classFileOrDirectoryFilter = new ClassFileOrDirectoryFilter();
    private ClassScanFilter classScanFilter;
    private ClassLoader classLoader;

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

    public List<Class<?>> getClasses(String packageName, boolean recursive) {
        List<Class<?>> classList = new ArrayList<>();
        packageName = packageName.trim();
        String packagePath = packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> urls = classLoader.getResources(packagePath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    String path = url.getPath();
                    if (URL_PROTOCOL_FILE.equals(protocol)) {
                        findClasses(classList, packageName, path, recursive);
                    } else if (URL_PROTOCOL_JAR.equals(protocol)) {
                        findClasses(classList, packageName, url, recursive);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classList;
    }

    private void findClasses(List<Class<?>> classList, String packageName, String path, boolean recursive) {
        try {
            File[] files = new File(path).listFiles(classFileOrDirectoryFilter);
            if (files != null) {
                for (File file : files) {
                    try {
                        String fileName = file.getName();
                        if (file.isFile()) {
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
                        t.printStackTrace();
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void findClasses(List<Class<?>> classList, String packageName, URL url, boolean recursive) {
        try {
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
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
                    t.printStackTrace();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
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
            t.printStackTrace();
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
