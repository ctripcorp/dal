package com.ctrip.platform.dal.dao.configure;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DalConfigConstants {
  public static final String DAL_XML = "dal.xml";
  public static final String DAL_CONFIG = "Dal.config";

  public static final String NAME = "name";
  public static final String DATABASE_SETS = "databaseSets";
  public static final String DATABASE_SET = "databaseSet";
  public static final String ADD = "add";
  public static final String PROVIDER = "provider";
  public static final String SHARD_STRATEGY = "shardStrategy";
  public static final String SHARDING_STRATEGY = "shardingStrategy";
  public static final String DATABASE_TYPE = "databaseType";
  public static final String SHARDING = "sharding";
  public static final String CONNECTION_STRING = "connectionString";
  public static final String MASTER = "Master";
  public static final String LOG_LISTENER = "LogListener";
  public static final String TASK_FACTORY = "TaskFactory";
  public static final String FACTORY = "factory";
  public static final String LOGGER = "logger";
  public static final String SETTINGS = "settings";
  public static final String CONNECTION_LOCATOR = "ConnectionLocator";
  public static final String LOCATOR = "locator";
  public static final String CONFIG_SOURCE = "ConfigSource";
  public static final String SOURCE = "source";

  public static final String USE_LOCAL_DAL_CONFIG = "dal.config.uselocal";

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
