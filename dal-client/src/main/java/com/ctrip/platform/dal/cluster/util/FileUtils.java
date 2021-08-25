package com.ctrip.platform.dal.cluster.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author c7ch23en
 */
public class FileUtils {

    public static InputStream getResourceInputStream(String fileName) throws IOException {
        return getResourceInputStream(fileName, Thread.currentThread().getContextClassLoader());
    }

    public static InputStream getResourceInputStream(String fileName, ClassLoader classLoader) throws IOException {
        if (classLoader == null)
            throw new RuntimeException("Class loader is null");
        Set<String> candidateFileNames = getCandidateFileNames(fileName);
        for (String candidateFileName : candidateFileNames) {
            URL url = classLoader.getResource(candidateFileName);
            if (url != null)
                return url.openStream();
        }
        throw new RuntimeException("Resource not found, candidate file names: " + candidateFileNames);
    }

    private static Set<String> getCandidateFileNames(String fileName) {
        if (fileName == null)
            return new HashSet<>();
        Set<String> candidateFileNames = new LinkedHashSet<>();
        candidateFileNames.add(fileName);
        candidateFileNames.add(fileName.toLowerCase());
        candidateFileNames.add(fileName.toUpperCase());
        return candidateFileNames;
    }

}
