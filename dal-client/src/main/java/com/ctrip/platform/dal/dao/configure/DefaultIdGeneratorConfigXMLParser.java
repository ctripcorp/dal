package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfigXMLConstants;
import com.ctrip.framework.dal.cluster.client.config.IdGeneratorConfigXMLParser;
import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;
import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGeneratorConfig;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.sharding.idgen.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultIdGeneratorConfigXMLParser implements IdGeneratorConfigXMLParser, DalConfigConstants {

    private IdGeneratorFactoryManager idGenFactoryManager = new IdGeneratorFactoryManager();

    public DefaultIdGeneratorConfigXMLParser() {}

    @Override
    public ClusterIdGeneratorConfig parse(String clusterName, String content) {
        StringReader reader = new StringReader(content);
        InputSource source = new InputSource(reader);
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
            return parse(clusterName, doc);
        } catch (Throwable t) {
            throw new DalRuntimeException("parse cluster IdGenerator config error", t);
        }
    }

    @Override
    public ClusterIdGeneratorConfig parse(String clusterName, InputStream stream) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
            return parse(clusterName, doc);
        } catch (Throwable t) {
            throw new ClusterConfigException("parse cluster IdGenerator config error", t);
        }
    }

    private ClusterIdGeneratorConfig parse(String clusterName, Document doc) {
        Element root = doc.getDocumentElement();
        if (root == null || !ClusterConfigXMLConstants.ID_GENERATORS.equalsIgnoreCase(root.getTagName()))
            throw new ClusterConfigException("root element should be <IdGenerators>");
        List<Node> idGeneratorNodes = getChildNodes(root, ID_GENERATOR);
        if (idGeneratorNodes.size() > 1)
            throw new DalRuntimeException("the count of <IdGenerator> nodes should be only one");
        if (idGeneratorNodes.size() == 0)
            throw new DalRuntimeException("<IdGenerator> node does not exist");
        return parse(clusterName, idGeneratorNodes.get(0));
    }

    @Override
    public ClusterIdGeneratorConfig parse(String logicDbName, Node idGeneratorNode) {
        return getIdGenConfig(logicDbName, idGeneratorNode);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private IIdGeneratorConfig getIdGenConfig(String logicDbName, Node idGeneratorNode) {
        if (null == idGeneratorNode) {
            return null;
        }
        Node includesNode = getChildNode(idGeneratorNode, INCLUDES);
        Node excludesNode = getChildNode(idGeneratorNode, EXCLUDES);
        if (includesNode != null && excludesNode != null) {
            throw new DalRuntimeException("<includes> and <excludes> nodes cannot be configured together within <IdGenerator> node");
        }
        IIdGeneratorFactory dbDefaultFactory = getIdGenFactoryForNode(idGeneratorNode);
        Map<String, IIdGeneratorFactory> tableFactoryMap = null;
        if (includesNode != null) {
            tableFactoryMap = getIdGenFactoriesForNode(includesNode, INCLUDE, dbDefaultFactory);
            if (dbDefaultFactory instanceof NullIdGeneratorFactory) {
                dbDefaultFactory = idGenFactoryManager.getOrCreateDefaultFactory();
            } else {
                dbDefaultFactory = idGenFactoryManager.getOrCreateNullFactory();
            }
        } else if (excludesNode != null) {
            if (dbDefaultFactory instanceof NullIdGeneratorFactory) {
                tableFactoryMap = getIdGenFactoriesForNode(excludesNode,
                        EXCLUDE, idGenFactoryManager.getOrCreateDefaultFactory());
            } else {
                tableFactoryMap = getIdGenFactoriesForNode(excludesNode,
                        EXCLUDE, idGenFactoryManager.getOrCreateNullFactory());
            }
        }
        String sequenceDbName = getAttribute(idGeneratorNode, SEQUENCE_DATABASE_NAME, logicDbName);
        String entityDbName = getAttribute(idGeneratorNode, ENTITY_DATABASE_NAME, logicDbName);
        String entityPackage = getAttribute(idGeneratorNode, ENTITY_PACKAGE, null);
        return new IdGeneratorConfig(sequenceDbName, entityDbName, entityPackage, dbDefaultFactory, tableFactoryMap);
    }

    private IIdGeneratorFactory getIdGenFactoryForNode(Node node) {
        return getIdGenFactoryForNode(node, idGenFactoryManager.getOrCreateDefaultFactory());
    }

    private IIdGeneratorFactory getIdGenFactoryForNode(Node node, final IIdGeneratorFactory defaultFactory) {
        String className = getAttribute(node, FACTORY, null);
        if (className != null && !className.trim().isEmpty()) {
            return idGenFactoryManager.getOrCreateFactory(className);
        }
        return defaultFactory;
    }

    private Map<String, IIdGeneratorFactory> getIdGenFactoriesForNode(Node node, String subTag, final IIdGeneratorFactory defaultFactory) {
        Map<String, IIdGeneratorFactory> factories = new HashMap<>();
        List<Node> subNodes = getChildNodes(node, subTag);
        for (Node subNode : subNodes) {
            Node tablesNode = getChildNode(subNode, TABLES);
            if (null == tablesNode) {
                continue;
            }
            IIdGeneratorFactory factory = getIdGenFactoryForNode(subNode, defaultFactory);
            List<Node> tableNodes = getChildNodes(tablesNode, TABLE);
            for (Node tableNode : tableNodes) {
                String tableName = tableNode.getTextContent();
                if (tableName != null && !tableName.trim().isEmpty()) {
                    factories.put(tableName.trim().toLowerCase(), factory);
                }
            }
        }
        return factories;
    }

    private String getAttribute(Node node, String attributeName) {
        String attribute =  node.getAttributes().getNamedItem(attributeName).getNodeValue();
        if (attribute != null) {
            attribute = attribute.trim();
        }
        return attribute;
    }

    private String getAttribute(Node node, String attributeName, String defaultValue) {
        String attribute = defaultValue;
        try {
            attribute =  node.getAttributes().getNamedItem(attributeName).getNodeValue();
        } catch (NullPointerException e) {}
        if (attribute != null && !attribute.trim().isEmpty()) {
            return attribute.trim();
        }
        return defaultValue;
    }

    private Node getChildNode(Node node, String name) {
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

    private List<Node> getChildNodes(Node node, String name) {
        List<Node> nodes = new ArrayList<>();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (!children.item(i).getNodeName().equalsIgnoreCase(name))
                continue;
            nodes.add(children.item(i));
        }
        return nodes;
    }

}
