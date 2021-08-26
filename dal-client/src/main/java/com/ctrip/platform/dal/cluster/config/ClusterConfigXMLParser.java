package com.ctrip.platform.dal.cluster.config;

import com.ctrip.platform.dal.cluster.base.PropertyAccessor;
import com.ctrip.platform.dal.cluster.cluster.ClusterType;
import com.ctrip.platform.dal.cluster.cluster.DrcConsistencyTypeEnum;
import com.ctrip.platform.dal.cluster.cluster.ReadStrategyEnum;
import com.ctrip.platform.dal.cluster.database.DatabaseCategory;
import com.ctrip.platform.dal.cluster.database.DatabaseRole;
import com.ctrip.platform.dal.cluster.exception.ClusterConfigException;
import com.ctrip.platform.dal.cluster.exception.ClusterRuntimeException;
import com.ctrip.platform.dal.cluster.multihost.DefaultClusterRouteStrategyConfig;
import com.ctrip.platform.dal.cluster.sharding.idgen.ClusterIdGeneratorConfig;
import com.ctrip.platform.dal.cluster.sharding.strategy.*;
import com.ctrip.platform.dal.cluster.util.SPIUtils;
import com.ctrip.platform.dal.cluster.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class ClusterConfigXMLParser implements ClusterConfigParser, ClusterConfigValidator, ClusterConfigXMLConstants {

    private volatile IdGeneratorConfigXMLParser idGeneratorConfigXMLParser;

    @Override
    public ClusterConfig parse(String content, DalConfigCustomizedOption customizedOption) {
        StringReader reader = new StringReader(content);
        InputSource source = new InputSource(reader);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Document doc = factory.newDocumentBuilder().parse(source);
            return parse(doc, customizedOption);
        } catch (Throwable t) {
            throw new ClusterConfigException("parse cluster config error", t);
        }
    }

    @Override
    public ClusterConfig parse(InputStream stream, DalConfigCustomizedOption customizedOption) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Document doc = factory.newDocumentBuilder().parse(stream);
            return parse(doc, customizedOption);
        } catch (Throwable t) {
            throw new ClusterConfigException("parse cluster config error", t);
        }
    }

    private ClusterConfig parse(Document doc, DalConfigCustomizedOption customizedOption) {
        Element root = doc.getDocumentElement();
        if (root == null || !DAL.equalsIgnoreCase(root.getTagName()))
            throw new ClusterConfigException("root element should be <DAL>");
        Node clusterNode = getChildNode(root, CLUSTER);
        if (clusterNode == null)
            throw new ClusterConfigException("cluster element not found");
        return parseCluster(clusterNode, customizedOption);
    }

    private ClusterConfig parseCluster(Node clusterNode, DalConfigCustomizedOption customizedOption) {
        String name = getAttribute(clusterNode, NAME);
        if (StringUtils.isEmpty(name))
            throw new ClusterConfigException("cluster name undefined");
        ClusterType clusterType = ClusterType.NORMAL;
        String clusterTypeText = getAttribute(clusterNode, TYPE);
        if (!StringUtils.isEmpty(clusterTypeText))
            clusterType = ClusterType.parse(clusterTypeText);
        DatabaseCategory dbCategory = DatabaseCategory.parse(getAttribute(clusterNode, DB_CATEGORY));
        int version = Integer.parseInt(getAttribute(clusterNode, VERSION));
        ClusterConfigImpl clusterConfig = new ClusterConfigImpl(name, clusterType, dbCategory, version);

        clusterConfig.setCustomizedOption(customizedOption);
        Node databaseShardsNode = getChildNode(clusterNode, DATABASE_SHARDS);
        if (databaseShardsNode != null) {
            List<Node> databaseShardNodes = getChildNodes(databaseShardsNode, DATABASE_SHARD);
            for (Node databaseShardNode : databaseShardNodes)
                parseDatabaseShard(clusterConfig, databaseShardNode);
        }

        Node shardStrategiesNode = getChildNode(clusterNode, SHARD_STRATEGIES);
        if (shardStrategiesNode != null)
            parseShardStrategies(clusterConfig, shardStrategiesNode);

        Node idGeneratorsNode = getChildNode(clusterNode, ID_GENERATORS);
        if (idGeneratorsNode != null)
            parseIdGenerators(clusterConfig, idGeneratorsNode);

        Node routeStrategiesNode = getChildNode(clusterNode, ROUTE_STRATEGIES);
        if (!StringUtils.isEmpty(customizedOption.getReadStrategy())) {
            initReadStrategy(clusterConfig, customizedOption);
        } else if (routeStrategiesNode != null)
            parseRouteStrategies(clusterConfig, routeStrategiesNode);
        else
            initReadStrategy(clusterConfig);

        parseDrcConfig(clusterConfig, clusterNode);

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
        databaseShardConfig.setZone(getAttribute(databaseShardNode, ZONE));
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
        databaseConfig.setZone(getAttribute(databaseNode, ZONE));
        String readWeight = getAttribute(databaseNode, READ_WEIGHT);
        if (!StringUtils.isEmpty(readWeight))
            databaseConfig.setReadWeight(Integer.parseInt(readWeight));
        databaseConfig.setTags(getAttribute(databaseNode, TAGS));
    }

    private void parseShardStrategies(ClusterConfigImpl clusterConfig, Node strategiesNode) {
        for (Node modStrategyNode : getChildNodes(strategiesNode, MOD_STRATEGY))
            parseModStrategy(clusterConfig, modStrategyNode);
        for (Node modStrategyNode : getChildNodes(strategiesNode, USER_HINT_STRATEGY))
            parseUserHintStrategy(clusterConfig, modStrategyNode);
        for (Node customStrategyNode : getChildNodes(strategiesNode, CUSTOM_STRATEGY))
            parseCustomStrategy(clusterConfig, customStrategyNode);
    }

    private void parseModStrategy(ClusterConfigImpl clusterConfig, Node strategyNode) {
        parseShardStrategy(clusterConfig, strategyNode, new ModShardStrategy());
    }

    private void parseUserHintStrategy(ClusterConfigImpl clusterConfig, Node strategyNode) {
        parseShardStrategy(clusterConfig, strategyNode, new UserHintStrategy());
    }

    private void parseCustomStrategy(ClusterConfigImpl clusterConfig, Node strategyNode) {
        String className = getAttribute(strategyNode, CLASS);
        try {
            ShardStrategy strategy = instancingCustomShardStrategy(clusterConfig, className);
            parseShardStrategy(clusterConfig, strategyNode, strategy);
        } catch (Throwable t) {
            if (!clusterConfig.getCustomizedOption().isIgnoreShardingResourceNotFound()) {
                throw new ClusterRuntimeException("invalid custom strategy impl class", t);
            }
        }
    }

    private ShardStrategy instancingCustomShardStrategy(ClusterConfigImpl clusterConfig, String className) {
        ShardStrategy strategy = null;
        try{
            strategy = (ShardStrategy) Class.forName(className).newInstance();
        } catch (Throwable t){
            if (!clusterConfig.getCustomizedOption().isIgnoreShardingResourceNotFound()) {
                throw new ClusterRuntimeException("invalid custom strategy impl class", t);
            }
            strategy = new NonsupportCustomStrategy(className);
        }
        return strategy;
    }

    private void parseShardStrategy(ClusterConfigImpl clusterConfig, Node strategyNode, ShardStrategy strategy) {
        if (strategy instanceof PropertyAccessor)
            loadProperties((PropertyAccessor) strategy, strategyNode);
        if (strategy instanceof ConfigElement) {
            loadSubElements((ConfigElement) strategy, strategyNode);
            ((ConfigElement) strategy).start();
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

    private void parseIdGenerators(ClusterConfigImpl clusterConfig, Node idGeneratorsNode) {
        List<Node> idGeneratorNodes = getChildNodes(idGeneratorsNode, ID_GENERATOR);
        if (idGeneratorNodes.size() > 1)
            throw new ClusterRuntimeException("multiple idGenerators configured");
        if (idGeneratorNodes.size() == 1) {
            Node idGeneratorNode = idGeneratorNodes.get(0);
            ClusterIdGeneratorConfig idGeneratorConfig = getIdGeneratorConfigXMLParser().parse(clusterConfig.getClusterName(), idGeneratorNode);
            clusterConfig.setIdGeneratorConfig(idGeneratorConfig);
        }
    }

    protected void initReadStrategy(ClusterConfigImpl clusterConfig, DalConfigCustomizedOption customizedOption) {
        DefaultClusterRouteStrategyConfig routeStrategyConfig = new DefaultClusterRouteStrategyConfig(customizedOption.getReadStrategy());
        clusterConfig.setRouteStrategyConfig(routeStrategyConfig);
    }

    protected void initReadStrategy(ClusterConfigImpl clusterConfig) {
        DefaultClusterRouteStrategyConfig routeStrategyConfig = new DefaultClusterRouteStrategyConfig(ReadStrategyEnum.READ_MASTER.name());
        clusterConfig.setRouteStrategyConfig(routeStrategyConfig);
    }

    private void parseRouteStrategies(ClusterConfigImpl clusterConfig, Node routeStrategiesNode) {
        List<Node> routeStrategyNodes = getRouteStrategyNodes(clusterConfig.getClusterType(), routeStrategiesNode);
        if (routeStrategyNodes.size() > 1)
            throw new ClusterRuntimeException("multiple routeStrategies configured");
        if (routeStrategyNodes.size() == 1) {
            Node routeStrategyNode = routeStrategyNodes.get(0);
            DefaultClusterRouteStrategyConfig routeStrategyConfig = new DefaultClusterRouteStrategyConfig(routeStrategyNode.getNodeName());
            List<Node> propertyNodes = getChildNodes(routeStrategyNode, PROPERTY);
            propertyNodes.forEach(node -> {
                String propertyName = getAttribute(node, NAME);
                String propertyValue = getAttribute(node, VALUE);
                if (propertyName != null && propertyValue != null)
                    routeStrategyConfig.setProperty(propertyName, propertyValue);
            });
            clusterConfig.setRouteStrategyConfig(routeStrategyConfig);
        }
    }

    protected List<Node> getRouteStrategyNodes(ClusterType clusterType, Node routeStrategiesNode) {
        List<Node> readStrategyNodes = new ArrayList<>();
        // mgr-strategy
        List<Node> mgrStrategyNodes = getChildNodes(routeStrategiesNode, clusterType.defaultRouteStrategies());
        // read-strategy
        for (ReadStrategyEnum readStrategyEnum : ReadStrategyEnum.values()){
            Node node = getChildNode(routeStrategiesNode, readStrategyEnum.name());
            if (node != null)
                readStrategyNodes.add(node);
        }
        readStrategyNodes.addAll(mgrStrategyNodes);

        return readStrategyNodes;
    }

    private void parseDrcConfig(ClusterConfigImpl clusterConfig, Node clusterNode) {
        Node unitStrategyIdNode = getChildNode(clusterNode, UNIT_STRATEGY_ID);
        if (unitStrategyIdNode != null) {
            String unitStrategyIdText = unitStrategyIdNode.getTextContent();
            if (!StringUtils.isEmpty(unitStrategyIdText)) {
                try {
                    clusterConfig.setUnitStrategyId(Integer.parseInt(unitStrategyIdText));
                } catch (NumberFormatException e) {
                    throw new ClusterRuntimeException("unitStrategyId should be a number", e);
                }
            }
        }
        Node zoneIdNode = getChildNode(clusterNode, ZONE_ID);
        if (zoneIdNode != null) {
            String zoneIdText = zoneIdNode.getTextContent();
            if (!StringUtils.isEmpty(zoneIdText)) {
                clusterConfig.setZoneId(zoneIdText);
            }
        }
        Node unitConsistencyStrategyNode = getChildNode(clusterNode, CONSISTENCY_TYPE);
        // default consistency type is HIGH_AVAILABILITY
        clusterConfig.setDrcConsistencyType(DrcConsistencyTypeEnum.HIGH_AVAILABILITY);
        if (unitConsistencyStrategyNode != null) {
            String unitConsistencyStrategyText = unitConsistencyStrategyNode.getTextContent();
            clusterConfig.setDrcConsistencyType(DrcConsistencyTypeEnum.parse(unitConsistencyStrategyText));
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
                return attribute != null ? attribute.trim() : null;
            }
        }
        return defaultValue;
    }

    private IdGeneratorConfigXMLParser getIdGeneratorConfigXMLParser() {
        if (idGeneratorConfigXMLParser == null) {
            synchronized (this) {
                if (idGeneratorConfigXMLParser == null) {
                    idGeneratorConfigXMLParser = SPIUtils.getInstance(IdGeneratorConfigXMLParser.class);
                    if (idGeneratorConfigXMLParser == null)
                        throw new ClusterRuntimeException("load IdGeneratorConfigXMLParser failed");
                }
            }
        }
        return idGeneratorConfigXMLParser;
    }

    @Override
    public void validateShardStrategies(String clusterName, String config) {
        parseShardStrategies(buildClusterConfigForValidation(clusterName), buildXmlNode(fillShardStrategiesNodeContent(config)));
    }

    @Override
    public void validateIdGenerators(String clusterName, String config) {
        parseIdGenerators(buildClusterConfigForValidation(clusterName), buildXmlNode(fillIdGeneratorsNodeContent(config)));
    }

    private ClusterConfigImpl buildClusterConfigForValidation(String clusterName) {
        return new ClusterConfigImpl(clusterName, DatabaseCategory.MYSQL, 1);
    }

    private String fillShardStrategiesNodeContent(String config) {
        return String.format("<%s>%s</%s>", SHARD_STRATEGIES, config, SHARD_STRATEGIES);
    }

    private String fillIdGeneratorsNodeContent(String config) {
        return String.format("<%s>%s</%s>", ID_GENERATORS, config, ID_GENERATORS);
    }

    private Node buildXmlNode(String content) {
        StringReader reader = new StringReader(content);
        InputSource source = new InputSource(reader);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Document doc = factory.newDocumentBuilder().parse(source);
            return doc.getDocumentElement();
        } catch (Throwable t) {
            throw new ClusterRuntimeException("parse xml content error", t);
        }
    }

}
