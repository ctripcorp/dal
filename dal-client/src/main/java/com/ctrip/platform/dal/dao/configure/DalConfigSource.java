package com.ctrip.platform.dal.dao.configure;

import org.w3c.dom.Node;

import java.util.Map;

public interface DalConfigSource extends DalComponent {
    Map<String, DatabaseSet> getDatabaseSets(Node databaseSetsNode) throws Exception;
}