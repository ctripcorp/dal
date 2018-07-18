package com.ctrip.platform.dal.dao.configure;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.DefaultLogger;
import com.ctrip.platform.dal.dao.datasource.DefaultDalConnectionLocator;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;
import com.ctrip.platform.dal.dao.task.DefaultTaskFactory;

public class DalConfigureFactory implements DalConfigConstants {
    private static DalConfigureFactory factory = new DalConfigureFactory();

    /**
     * Load from classpath. For historic reason, we support both dal.xml and Dal.config for configure name.
     *
     * @return
     * @throws Exception
     */
    public static DalConfigure load() throws Exception {
        URL dalconfigUrl = getDalConfigUrl();
        if (dalconfigUrl == null)
            throw new IllegalStateException(
                    "Can not find " + DAL_XML + " or " + DAL_CONFIG + " to initilize dal configure");

        return load(dalconfigUrl);
    }

    public static DalConfigure load(URL url) throws Exception {
        return load(url.openStream());
    }

    public static DalConfigure load(String path) throws Exception {
        return load(new File(path));
    }

    public static DalConfigure load(File model) throws Exception {
        return load(new FileInputStream(model));
    }

    public static DalConfigure load(InputStream in) throws Exception {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            DalConfigure def = factory.getFromDocument(doc);
            in.close();
            return def;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Throwable e1) {

                }
        }
    }

    public DalConfigure getFromDocument(Document doc) throws Exception {
        Element root = doc.getDocumentElement();

        String name = getAttribute(root, NAME);

        DalLogger logger = readComponent(root, LOG_LISTENER, new DefaultLogger(), LOGGER);
        // To wrap with a sandbox logger
        // logger = new DalSafeLogger(logger);

        DalTaskFactory factory = readComponent(root, TASK_FACTORY, new DefaultTaskFactory(), FACTORY);

        DalConnectionLocator locator =
                readComponent(root, CONNECTION_LOCATOR, new DefaultDalConnectionLocator(), LOCATOR);

        Map<String, DatabaseSet> databaseSets = readDatabaseSets(getChildNode(root, DATABASE_SETS));

        locator.setup(getAllDbNames(databaseSets));

        DatabaseSelector selector =
                readComponent(root, DATABASE_SELECTOR, new DefaultDatabaseSelector(), SELECTOR);

        return new DalConfigure(name, databaseSets, logger, locator, factory, selector);
    }

    private Set<String> getAllDbNames(Map<String, DatabaseSet> databaseSets) {
        Set<String> dbNames = new HashSet<>();
        for (DatabaseSet dbSet : databaseSets.values()) {
            for (DataBase db : dbSet.getDatabases().values()) {
                dbNames.add(db.getConnectionString());
            }
        }
        return dbNames;
    }

    private <T extends DalComponent> T readComponent(Node root, String componentName, T defaultImpl,
            String implNodeName) throws Exception {
        Node node = getChildNode(root, componentName);
        T component = defaultImpl;

        if (node != null) {
            Node implNode = getChildNode(node, implNodeName);
            if (implNode != null)
                component = (T) Class.forName(implNode.getTextContent()).newInstance();
        }

        component.initialize(getSettings(node));
        return component;
    }

    private Map<String, String> getSettings(Node pNode) {
        Map<String, String> settings = new HashMap<>();

        if (pNode == null)
            return settings;

        Node settingsNode = getChildNode(pNode, SETTINGS);

        if (settingsNode != null) {
            NodeList children = settingsNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
                    settings.put(children.item(i).getNodeName(), children.item(i).getTextContent().trim());
            }
        }
        return settings;
    }

    private String getAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
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

    private Map<String, DatabaseSet> readDatabaseSets(Node databaseSetsNode) throws Exception {
        List<Node> databaseSetList = getChildNodes(databaseSetsNode, DATABASE_SET);
        Map<String, DatabaseSet> databaseSets = new HashMap<>();
        for (int i = 0; i < databaseSetList.size(); i++) {
            DatabaseSet databaseSet = readDatabaseSet(databaseSetList.get(i));
            databaseSets.put(databaseSet.getName(), databaseSet);
        }
        return databaseSets;
    }

    private DatabaseSet readDatabaseSet(Node databaseSetNode) throws Exception {
        checkAttribte(databaseSetNode, NAME, PROVIDER, SHARD_STRATEGY, SHARDING_STRATEGY);
        String shardingStrategy = "";
        
        if(hasAttribute(databaseSetNode, SHARD_STRATEGY))
            shardingStrategy = getAttribute(databaseSetNode, SHARD_STRATEGY);
        else if(hasAttribute(databaseSetNode, SHARDING_STRATEGY))
                shardingStrategy = getAttribute(databaseSetNode, SHARDING_STRATEGY);
        
        shardingStrategy = shardingStrategy.trim();
        
        List<Node> databaseList = getChildNodes(databaseSetNode, ADD);
        Map<String, DataBase> databases = new HashMap<>();
        for (int i = 0; i < databaseList.size(); i++) {
            DataBase database = readDataBase(databaseList.get(i), !shardingStrategy.isEmpty());
            databases.put(database.getName(), database);
        }

        if (shardingStrategy.isEmpty())
            return new DatabaseSet(getAttribute(databaseSetNode, NAME), getAttribute(databaseSetNode, PROVIDER),
                    databases);
        else
            return new DatabaseSet(getAttribute(databaseSetNode, NAME), getAttribute(databaseSetNode, PROVIDER),
                    shardingStrategy, databases);
    }

    private DataBase readDataBase(Node dataBaseNode, boolean isSharded) {
        checkAttribte(dataBaseNode, NAME, DATABASE_TYPE, SHARDING, CONNECTION_STRING);
        String sharding = isSharded ? getAttribute(dataBaseNode, SHARDING) : "";
        return new DataBase(getAttribute(dataBaseNode, NAME), getAttribute(dataBaseNode, DATABASE_TYPE).equals(MASTER),
                sharding, getAttribute(dataBaseNode, CONNECTION_STRING));
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

    private boolean hasAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName) != null;
    }

    public static URL getDalConfigUrl() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null)
            classLoader = DalClientFactory.class.getClassLoader();

        URL dalconfigUrl = classLoader.getResource(DAL_XML);
        if (dalconfigUrl == null)
            dalconfigUrl = classLoader.getResource(DAL_CONFIG);

        return dalconfigUrl;
    }

    private void checkAttribte(Node node, String... validNames) {
        NamedNodeMap map = node.getAttributes();
        if(map == null)
            return;
        
        for(int i = 0 ; i <map.getLength(); i++) {
            String name = map.item(i).getNodeName();
            boolean found = false;
            for(String candidate: validNames)
                if(name.equals(candidate)){
                    found = true;
                    break;
                }
            
            if(!found)
                throw new IllegalStateException("");
        }
    }
}
