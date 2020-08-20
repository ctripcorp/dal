package com.ctrip.datasource.configure;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.framework.foundation.config.local.Config;
import com.ctrip.platform.dal.dao.configure.AbstractResourceLoader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class CtripFxLocalResourceLoader extends AbstractResourceLoader {

    private final String groupId;

    public CtripFxLocalResourceLoader(String groupId) {
        this.groupId = groupId;
    }

    @Override
    protected String getResourceContent(String resourceName) throws IOException {
        Config config = Foundation.server().localConfig().createPrioritizedConfig(groupId, resourceName);
        if (config != null)
            return config.getContent();
        throw createFileNotFoundException(resourceName);
    }

    @Override
    protected String getResourceNameForLog(String resourceName) {
        return String.format("framework-local:%s:%s", groupId, resourceName);
    }

    @Override
    protected Set<String> getCandidateResourceNames(String rawName) {
        Set<String> candidateNames = new HashSet<>();
        candidateNames.add(rawName);
        return candidateNames;
    }

}
