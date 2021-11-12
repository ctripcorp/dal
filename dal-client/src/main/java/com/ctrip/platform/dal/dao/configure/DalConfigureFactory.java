package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.config.DalConfigCustomizedOption;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.DefaultLogger;
import com.ctrip.platform.dal.dao.cluster.ClusterManager;
import com.ctrip.platform.dal.dao.cluster.ClusterManagerImpl;
import com.ctrip.platform.dal.dao.datasource.DefaultDalConnectionLocator;
import com.ctrip.platform.dal.dao.helper.ClassScanFilter;
import com.ctrip.platform.dal.dao.helper.ClassScanner;
import com.ctrip.platform.dal.dao.helper.DalClassScanner;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;
import com.ctrip.platform.dal.dao.task.DefaultTaskFactory;
import com.ctrip.platform.dal.exceptions.DalConfigException;
import com.ctrip.platform.dal.sharding.idgen.*;
import org.w3c.dom.*;

import javax.persistence.Entity;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ctrip.framework.dal.cluster.client.extended.CustomDataSourceConfigureConstants.DATASOURCE_FACTORY;
import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.DRIVER_CLASS_NAME;

public class DalConfigureFactory implements DalConfigConstants {
    private static DalConfigureFactory factory = new DalConfigureFactory();

    private IdGeneratorFactoryManager idGenFactoryManager = new IdGeneratorFactoryManager();
    private ClassScanner entityScanner = new DalClassScanner(new ClassScanFilter() {
        @Override
        public boolean accept(Class<?> clazz) {
            return clazz.isAnnotationPresent(Entity.class) &&
                    clazz.isAnnotationPresent(Database.class) &&
                    !clazz.isInterface();
        }
    });

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
                readComponent(root, CONNECTION_LOCATOR, new DefaultDalConnectionLocator(), LOCATOR, false);
        // read config of ''
        DalConfigCustomizedOption defaultOption = readOverridableProperty(root);

        Map<String, DatabaseSet> databaseSets = readDatabaseSets(getChildNode(root, DATABASE_SETS));
        if (locator instanceof InjectableComponentSupport) {
            ((InjectableComponentSupport) locator).inject(new DatabaseSetsImpl(databaseSets.values()));
        }
        locator.initialize(getSettings(getChildNode(root, CONNECTION_LOCATOR)));

        Map<String, DatabaseSet> clusters = readClusters(getChildNode(root, DATABASE_SETS), locator, defaultOption);
        clusters.putAll(databaseSets);

        locator.setup(clusters.values());

        DatabaseSetAdapter adapter = new ClusterDatabaseSetAdapter(locator);
        tryAdaptDatabaseSets(clusters, adapter);
        Map<String, DalConnectionString> connectionStrings =
                DataSourceConfigureLocatorManager.getInstance().getAllConnectionStrings();
        adapter = new LocalDatabaseSetAdapter(connectionStrings);
        tryAdaptDatabaseSets(clusters, adapter);

        DatabaseSelector selector =
                readComponent(root, DATABASE_SELECTOR, new DefaultDatabaseSelector(), SELECTOR);

        return new DalConfigure(name, clusters, logger, locator, factory, selector);
    }

    private <T extends DalComponent> T readComponent(Node root, String componentName, T defaultImpl,
                                                     String implNodeName) throws Exception {
        return readComponent(root, componentName, defaultImpl, implNodeName, true);
    }

    private <T extends DalComponent> T readComponent(Node root, String componentName, T defaultImpl,
            String implNodeName, boolean autoInitialize) throws Exception {
        Node node = getChildNode(root, componentName);
        T component = defaultImpl;

        if (node != null) {
            Node implNode = getChildNode(node, implNodeName);
            if (implNode != null)
                component = (T) Class.forName(implNode.getTextContent()).newInstance();
        }

        if (autoInitialize)
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

    protected DalConfigCustomizedOption readOverridableProperty(Element root) {
        DefaultDalConfigCustomizedOption defaultOption = new DefaultDalConfigCustomizedOption();
        String attribute = getAttribute(getChildNode(root, DATABASE_SETS), IGNORE_RESOURCE_NOT_FOUND);
        if (!StringUtils.isEmpty(attribute) && attribute.trim().equalsIgnoreCase("sharding")) {
            defaultOption.ignoreShardingResourceNotFound(true);
        }
        return defaultOption;
    }

    private String getAttribute(Node node, String attributeName) {
        Node attributeNode = node.getAttributes().getNamedItem(attributeName);
        if (attributeNode == null || attributeNode.getNodeValue() == null) {
            return null;
        }
        return attributeNode.getNodeValue().trim();
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

    private Map<String, DatabaseSet> readClusters(Node databaseSetsNode, DalConnectionLocator locator, DalConfigCustomizedOption defaultOption) throws Exception {
        Map<String, DatabaseSet> databaseSets = new HashMap<>();

        ClusterManager clusterManager = new ClusterManagerImpl(locator.getIntegratedConfigProvider());
        List<Node> clusterList = getChildNodes(databaseSetsNode, CLUSTER);
        for (Node node : clusterList) {
            DalConfigCustomizedOption option = defaultOption.clone();
            String name = getDatabaseSetName(node);
            overrideDefaultConfig(node, option);
            Cluster cluster = readCluster(node, clusterManager, option);
            databaseSets.put(name, new ClusterDatabaseSet(name, cluster, locator, getSettings(node)));
        }

        return databaseSets;
    }

    private Map<String, DatabaseSet> readDatabaseSets(Node databaseSetsNode) throws Exception {
        Map<String, DatabaseSet> databaseSets = new HashMap<>();

        List<Node> databaseSetList = getChildNodes(databaseSetsNode, DATABASE_SET);
        for (Node node : databaseSetList) {
            DatabaseSet databaseSet = readDatabaseSet(node);
            databaseSets.put(databaseSet.getName(), databaseSet);
        }

        return databaseSets;
    }

    private String getDatabaseSetName(Node clusterNode) {
        return getAttribute(clusterNode, ALIAS, getAttribute(clusterNode, NAME));
    }

    private Cluster readCluster(Node clusterNode, ClusterManager clusterManager, DalConfigCustomizedOption customizedClass) throws Exception {
        String name = getAttribute(clusterNode, NAME);
        if (StringUtils.isEmpty(name))
            throw new DalConfigException("empty cluster name");
        return clusterManager.getOrCreateCluster(name, customizedClass);
    }

    private void overrideDefaultConfig(Node clusterNode, DalConfigCustomizedOption defaultOption) {
        ((DefaultDalConfigCustomizedOption)defaultOption)
                .consistencyTypeCustomizedClass(getAttribute(clusterNode, CONSISTENCY_TYPE_CUSTOMIZED_CLASS, null))
                .readStrategy(getAttribute(clusterNode, ROUTE_STRATEGY, ""))
                .delegate(getAttribute(clusterNode, DATASOURCE_FACTORY, ""))
                .jdbcDriver(getAttribute(clusterNode, DRIVER_CLASS_NAME, ""))
                // todo-lhj what's default tag
                .tag(getAttribute(clusterNode, TAG, ""))
                .queryConsistent(Boolean.valueOf(getAttribute(clusterNode, QUERY_CONSISTENT, "false")));
    }

    private DatabaseSet readDatabaseSet(Node databaseSetNode) throws Exception {
        checkAttribte(databaseSetNode, NAME, PROVIDER, SHARD_STRATEGY, SHARDING_STRATEGY);
        String shardingStrategy = "";
        
        if(hasAttribute(databaseSetNode, SHARD_STRATEGY))
            shardingStrategy = getAttribute(databaseSetNode, SHARD_STRATEGY);
        else if(hasAttribute(databaseSetNode, SHARDING_STRATEGY))
                shardingStrategy = getAttribute(databaseSetNode, SHARDING_STRATEGY);
        
        shardingStrategy = shardingStrategy.trim();

        IIdGeneratorConfig idGenConfig = getIdGenConfig(databaseSetNode);
        
        List<Node> databaseList = getChildNodes(databaseSetNode, ADD);
        Map<String, DataBase> databases = new HashMap<>();
        for (Node node : databaseList) {
            DataBase database = readDataBase(node, !shardingStrategy.isEmpty());
            databases.put(database.getName(), database);
        }

        if (shardingStrategy.isEmpty())
            return new DefaultDatabaseSet(getAttribute(databaseSetNode, NAME), getAttribute(databaseSetNode, PROVIDER),
                    databases, idGenConfig, getSettings(databaseSetNode));
        else
            return new DefaultDatabaseSet(getAttribute(databaseSetNode, NAME), getAttribute(databaseSetNode, PROVIDER),
                    shardingStrategy, databases, idGenConfig, getSettings(databaseSetNode));
    }

    private void tryAdaptDatabaseSets(Map<String, DatabaseSet> databaseSets, DatabaseSetAdapter adapter) {
        for (Map.Entry<String, DatabaseSet> entry : new HashMap<>(databaseSets).entrySet()) {
            try {
                databaseSets.put(entry.getKey(), adapter.adapt(entry.getValue()));
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private IIdGeneratorConfig getIdGenConfig(Node databaseSetNode) throws Exception {
        Node idGeneratorNode = getChildNode(databaseSetNode, ID_GENERATOR);
        if (null == idGeneratorNode) {
            return null;
        }
        Node includesNode = getChildNode(idGeneratorNode, INCLUDES);
        Node excludesNode = getChildNode(idGeneratorNode, EXCLUDES);
        if (includesNode != null && excludesNode != null) {
            throw new DalConfigException("<includes> and <excludes> nodes cannot be configured together within <IdGenerator> node");
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
        String logicDbName = getAttribute(databaseSetNode, NAME);
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

    private DataBase readDataBase(Node dataBaseNode, boolean isSharded) throws Exception {
        checkAttribte(dataBaseNode, NAME, DATABASE_TYPE, SHARDING, CONNECTION_STRING, CONNECTION_STRING_PROVIDER);
        String sharding = isSharded ? getAttribute(dataBaseNode, SHARDING) : "";
        String connectionStringProvider = null;
        if (hasAttribute(dataBaseNode, CONNECTION_STRING_PROVIDER)) {
            connectionStringProvider = getAttribute(dataBaseNode, CONNECTION_STRING_PROVIDER);
        }

        DataBase dataBase = new DefaultDataBase(getAttribute(dataBaseNode, NAME), getAttribute(dataBaseNode, DATABASE_TYPE).equals(MASTER),
                sharding, getAttribute(dataBaseNode, CONNECTION_STRING));

        if (StringUtils.isEmpty(connectionStringProvider)) {
            return dataBase;
        }
        else {
            return new ProviderDataBase(dataBase, connectionStringProvider);
        }
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
