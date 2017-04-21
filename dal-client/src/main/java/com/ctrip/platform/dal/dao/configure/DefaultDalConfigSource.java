package com.ctrip.platform.dal.dao.configure;

import org.w3c.dom.Node;

import java.util.Map;

public class DefaultDalConfigSource implements DalConfigSource {

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
    }

    @Override
    public Map<String, DatabaseSet> getDatabaseSets(Node databaseSetsNode) throws Exception {
        return DefaultDalConfigSourceParser.readDatabaseSets(databaseSetsNode);
    }

}
