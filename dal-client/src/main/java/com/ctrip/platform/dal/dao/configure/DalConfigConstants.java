package com.ctrip.platform.dal.dao.configure;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DalConfigConstants {
    public static final String DAL_XML = "dal.xml";
    public static final String DAL_CONFIG = "Dal.config";

    public static String NAME = "name";
    public static String DATABASE_SETS = "databaseSets";
    public static String DATABASE_SET = "databaseSet";
    public static String ADD = "add";
    public static String PROVIDER = "provider";
    public static String SHARD_STRATEGY = "shardStrategy";
    public static String SHARDING_STRATEGY = "shardingStrategy";
    public static String DATABASE_TYPE = "databaseType";
    public static String SHARDING = "sharding";
    public static String CONNECTION_STRING = "connectionString";
    public static String MASTER = "Master";
    public static String LOG_LISTENER = "LogListener";
    public static String TASK_FACTORY = "TaskFactory";
    public static String FACTORY = "factory";
    public static String LOGGER = "logger";
    public static String SETTINGS = "settings";
    public static String CONNECTION_LOCATOR = "ConnectionLocator";
    public static String LOCATOR = "locator";
    public static String CONFIG_SOURCE = "ConfigSource";
    public static String SOURCE = "source";

    public static String getAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    public static Node getChildNode(Node node, String name) {
        NodeList children = node.getChildNodes();
        Node found = null;
        for (int i = 0; i < children.getLength(); i++) {
            if (!children.item(i).getNodeName().equalsIgnoreCase(name))
                continue;
            found = children.item(i);
            break;
        }
        return found;
    }

}
