package com.ctrip.platform.dal.dao.configure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public abstract class AbstractResourceLoader implements ResourceLoader<String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public DefaultResource getResource(String resourceName) {
        logger.info(String.format("Load file [%s]", getResourceNameForLog(resourceName)));
        try {
            Set<String> candidateResourceNames = getCandidateResourceNames(resourceName);
            for (String candidateResourceName : candidateResourceNames) {
                try {
                    String content = getResourceContent(candidateResourceName);
                    if (content != null) {
                        logger.info(String.format("File [%s] loaded", getResourceNameForLog(candidateResourceName)));
                        return new DefaultResource(content);
                    }
                } catch (FileNotFoundException e) {
                    // File not found, continue.
                } catch (IOException e) {
                    throw new RuntimeException(String.format("Errored loading file [%s]",
                            getResourceNameForLog(candidateResourceName)), e);
                }
            }
            logger.info(String.format("File [%s] not found. Candidate file names: %s",
                    getResourceNameForLog(resourceName), candidateResourceNames));
            return null;
        } catch (Throwable t) {
            logger.error(String.format("Errored loading file [%s]", getResourceNameForLog(resourceName)));
            throw t;
        }
    }

    protected abstract String getResourceContent(String resourceName) throws IOException;

    protected String getResourceNameForLog(String resourceName) {
        return resourceName;
    }

    protected Set<String> getCandidateResourceNames(String rawName) {
        Set<String> candidateNames = new LinkedHashSet<>();
        if (rawName != null) {
            candidateNames.add(rawName);
            candidateNames.add(rawName.toLowerCase());
            candidateNames.add(rawName.toUpperCase());
        }
        return candidateNames;
    }

    protected String getString(InputStream stream) throws IOException {
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    protected FileNotFoundException createFileNotFoundException(String resourceName) {
        return new FileNotFoundException(String.format("File [%s] not found", getResourceNameForLog(resourceName)));
    }

}
