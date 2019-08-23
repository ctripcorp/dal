package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.base.PropertyAccessor;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;
import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;
import com.ctrip.framework.dal.cluster.client.sharding.strategy.ModShardStrategy;
import com.ctrip.framework.dal.cluster.client.sharding.strategy.ShardStrategy;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class ClusterConfigXMLParser implements ClusterConfigParser, ClusterConfigXMLConstants {

    @Override
    public ClusterConfig parse(String content) {
        StringReader reader = new StringReader(content);
        InputSource source = new InputSource(reader);
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
            return parse(doc);
        } catch (Throwable t) {
            throw new ClusterConfigException("parse cluster config error", t);
        }
    }

    @Override
    public ClusterConfig parse(InputStream stream) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
            return parse(doc);
        } catch (Throwable t) {
            throw new ClusterConfigException("parse cluster config error", t);
        }
    }

    private ClusterConfig parse(Document doc) {
        Element root = doc.getDocumentElement();
        if (root == null || !DAL.equalsIgnoreCase(root.getTagName()))
            throw new ClusterConfigException("root element should be <DAL>");
        Node clusterNode = getChildNode(root, CLUSTER);
        if (clusterNode == null)
            throw new ClusterConfigException("cluster element not found");
        return parseCluster(clusterNode);
    }

    private ClusterConfig parseCluster(Node clusterNode) {
        String name = getAttribute(clusterNode, NAME);
        if (StringUtils.isEmpty(name))
            throw new ClusterConfigException("cluster name undefined");
        DatabaseCategory dbCategory = DatabaseCategory.parse(getAttribute(clusterNode, DB_CATEGORY));
        int version = Integer.parseInt(getAttribute(clusterNode, VERSION));
        ClusterConfigImpl clusterConfig = new ClusterConfigImpl(name, dbCategory, version);

        Node databaseShardsNode = getChildNode(clusterNode, DATABASE_SHARDS);
        if (databaseShardsNode != null) {
            List<Node> databaseShardNodes = getChildNodes(databaseShardsNode, DATABASE_SHARD);
            for (Node databaseShardNode : databaseShardNodes)
                parseDatabaseShard(clusterConfig, databaseShardNode);
        }

        Node shardStrategiesNode = getChildNode(clusterNode, SHARD_STRATEGIES);
        if (shardStrategiesNode != null) {
            List<Node> modStrategyNodes = getChildNodes(shardStrategiesNode, MOD_STRATEGY);
            for (Node modStrategyNode : modStrategyNodes)
                parseModStrategy(clusterConfig, modStrategyNode);
            List<Node> customStrategyNodes = getChildNodes(shardStrategiesNode, CUSTOM_STRATEGY);
            for (Node customStrategyNode :customStrategyNodes)
                parseCustomStrategy(clusterConfig, customStrategyNode);
        }

        return clusterConfig;
    }

    private void parseDatabaseShard(ClusterConfigImpl clusterConfig, Node databaseShardNode) {
        int index = Integer.parseInt(getAttribute(databaseShardNode, INDEX));
        DatabaseShardConfigImpl databaseShardConfig = new DatabaseShardConfigImpl(clusterConfig, index);
        setAttributesForDatabaseShard(databaseShardConfig, databaseShardNode);
        List<Node> databaseNodes = getChildNodes(databaseShardNode, DATABASE);
        for (Node databaseNode : databaseNodes)
            parseDatabase(databaseShardConfig, databaseNode);
        clusterConfig.addDatabaseShardConfig(databaseShardConfig);
    }

    private void setAttributesForDatabaseShard(DatabaseShardConfigImpl databaseShardConfig, Node databaseShardNode) {
        databaseShardConfig.setMasterDomain(getAttribute(databaseShardNode, MASTER_DOMAIN));
        String masterPort = getAttribute(databaseShardNode, MASTER_PORT);
        if (!StringUtils.isEmpty(masterPort))
            databaseShardConfig.setMasterPort(Integer.parseInt(masterPort));
        databaseShardConfig.setMasterKeys(getAttribute(databaseShardNode, MASTER_KEYS));
        databaseShardConfig.setSlaveDomain(getAttribute(databaseShardNode, SLAVE_DOMAIN));
        String slavePort = getAttribute(databaseShardNode, SLAVE_PORT);
        if (!StringUtils.isEmpty(slavePort))
            databaseShardConfig.setSlavePort(Integer.parseInt(slavePort));
        databaseShardConfig.setSlaveKeys(getAttribute(databaseShardNode, SLAVE_KEYS));
    }

    private void parseDatabase(DatabaseShardConfigImpl databaseShardConfig, Node databaseNode) {
        DatabaseConfigImpl databaseConfig = new DatabaseConfigImpl(databaseShardConfig);
        setAttributesForDatabase(databaseConfig, databaseNode);
        databaseShardConfig.addDatabaseConfig(databaseConfig);
    }

    private void setAttributesForDatabase(DatabaseConfigImpl databaseConfig, Node databaseNode) {
        String role = getAttribute(databaseNode, ROLE);
        if (!StringUtils.isEmpty(role))
            databaseConfig.setRole(DatabaseRole.parse(role));
        databaseConfig.setIp(getAttribute(databaseNode, IP));
        String port = getAttribute(databaseNode, PORT);
        if (!StringUtils.isEmpty(port))
            databaseConfig.setPort(Integer.parseInt(port));
        databaseConfig.setDbName(getAttribute(databaseNode, DB_NAME));
        databaseConfig.setUid(getAttribute(databaseNode, UID));
        databaseConfig.setPwd(getAttribute(databaseNode, PWD));
        String readWeight = getAttribute(databaseNode, READ_WEIGHT);
        if (!StringUtils.isEmpty(readWeight))
            databaseConfig.setReadWeight(Integer.parseInt(readWeight));
        databaseConfig.setTags(getAttribute(databaseNode, TAGS));
    }

    private void parseModStrategy(ClusterConfigImpl clusterConfig, Node strategyNode) {
        parseShardStrategy(clusterConfig, strategyNode, new ModShardStrategy());
    }

    private void parseCustomStrategy(ClusterConfigImpl clusterConfig, Node strategyNode) {
        String className = getAttribute(strategyNode, CLASS);
        try {
            ShardStrategy strategy = (ShardStrategy) Class.forName(className).newInstance();
            parseShardStrategy(clusterConfig, strategyNode, strategy);
        } catch (Throwable t) {
            throw new ClusterRuntimeException("invalid custom strategy impl class", t);
        }
    }

    private void parseShardStrategy(ClusterConfigImpl clusterConfig, Node strategyNode, ShardStrategy strategy) {
        if (strategy instanceof PropertyAccessor)
            loadProperties((PropertyAccessor) strategy, strategyNode);
        if (strategy instanceof ConfigElement) {
            loadSubElements((ConfigElement) strategy, strategyNode);
        }
        boolean isDefault = Boolean.parseBoolean(getAttribute(strategyNode, DEFAULT));
        if (isDefault)
            clusterConfig.setDefaultStrategy(strategy);
        else
            clusterConfig.addShardStrategy(strategy);
    }

    private void loadProperties(PropertyAccessor object, Node node) {
        List<Node> propertyNodes = getChildNodes(node, PROPERTY);
        for (Node propertyNode : propertyNodes) {
            String propertyName = getAttribute(propertyNode, NAME);
            String propertyValue = getAttribute(propertyNode, VALUE);
            if (propertyName != null && propertyValue != null)
                object.setProperty(propertyName, propertyValue);
        }
    }

    private void loadSubElements(ConfigElement object, Node node) {
        List<Node> tablesNodes = getChildNodes(node, TABLES);
        for (Node tablesNode : tablesNodes) {
            TablesElement tables = new TablesElement();
            loadProperties(tables, tablesNode);
            List<Node> tableNodes = getChildNodes(tablesNode, TABLE);
            for (Node tableNode : tableNodes) {
                String tableName = getAttribute(tableNode, NAME);
                TableElement table = new TableElement(tableName);
                loadProperties(table, tableNode);
                tables.addSubElement(table);
            }
            object.addSubElement(tables);
        }
    }

    private List<Node> getChildNodes(Node parent, String name) {
        List<Node> nodes = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (name.equalsIgnoreCase(child.getNodeName()))
                nodes.add(child);
        }
        return nodes;
    }

    private Node getChildNode(Node parent, String name) {
        List<Node> nodes = getChildNodes(parent, name);
        if (nodes.size() > 1)
            throw new ClusterRuntimeException("more than one child nodes found");
        if (nodes.size() == 0)
            return null;
        return nodes.get(0);
    }

    private String getAttribute(Node node, String name) {
        return getAttribute(node, name, null);
    }

    private String getAttribute(Node node, String name, String defaultValue) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node attributeNode = attributes.getNamedItem(name);
            if (attributeNode != null) {
                String attribute = attributeNode.getNodeValue();
                if (!StringUtils.isEmpty(attribute))
                    return attribute.trim();
            }
        }
        return defaultValue;
    }

}
