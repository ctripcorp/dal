package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.framework.dal.cluster.client.util.PropertiesUtils;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;

/**
 * @author c7ch23en
 */
public class CtripLocalConnectionStringProvider implements ConnectionStringProvider {

    private static final String FILE_DATABASE_CONFIG = "Database.Config";
    private static final String DATABASE_ENTRY = "add";
    private static final String DATABASE_ENTRY_NAME = "name";
    private static final String DATABASE_ENTRY_CONNECTION_STRING = "connectionString";
    private static final String PROPERTY_DISABLE_TABLE_SHARDING = "disableTableSharding";

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private final CtripLocalResourceLoader resourceLoader;
    private final DatabaseSets databaseSets;
    private final String userDefinedFile;

    public CtripLocalConnectionStringProvider() {
        this(null);
    }
    public CtripLocalConnectionStringProvider(CtripLocalContext context) {
        this(new CtripLocalResourceLoader(context), context);
    }

    public CtripLocalConnectionStringProvider(CtripLocalResourceLoader resourceLoader, CtripLocalContext context) {
        this.resourceLoader = resourceLoader;
        this.databaseSets = context == null ? null : context.getDatabaseSets();
        this.userDefinedFile = context == null ? null : context.getUserDefinedDatabaseConfigFile();
    }

    @Override
    public Map<String, DalConnectionString> getConnectionStrings(Set<String> names) throws Exception {
        Resource<String> localDatabasesResource =
                resourceLoader.getResource(CtripLocalDatabasePropertiesParser.FILE_LOCAL_DATABASES);
        Properties localDatabasesProperties = localDatabasesResource == null ? null :
                PropertiesUtils.toProperties(localDatabasesResource.getContent());
        Map<String, DalConnectionString>  connectionStrings = parseLocalDatabases(localDatabasesProperties, names);
        Resource<String> databaseConfigResource = resourceLoader.getResource(getDatabaseConfigFileName());
        if (databaseConfigResource != null && !StringUtils.isTrimmedEmpty(databaseConfigResource.getContent()))
            connectionStrings.putAll(parseDatabaseConfig(databaseConfigResource.getContent(), names));
        return connectionStrings;
    }

    /* local-databases.properties */

    protected Map<String, DalConnectionString> parseLocalDatabases(Properties properties, Set<String> names) {
        Map<String, DalConnectionString> connectionStrings = parseLocalDatabases(properties);
        if (names != null)
            names.forEach(databaseKey -> {
                databaseKey = databaseKey.toLowerCase();
                if (!connectionStrings.containsKey(databaseKey))
                    connectionStrings.put(databaseKey, parseLocalDatabase(properties, databaseKey));
            });
        return connectionStrings;
    }

    protected Map<String, DalConnectionString> parseLocalDatabases(Properties properties) {
        Map<String, DalConnectionString> connectionStrings = new HashMap<>();
        if (databaseSets != null)
            for (DatabaseSet databaseSet : databaseSets.getAll()) {
                String databaseSetName = databaseSet.getName();
                for (DataBase database : databaseSet.getDatabases().values()) {
                    String databaseKey = database.getConnectionString().toLowerCase();
                    if (!connectionStrings.containsKey(databaseKey))
                        connectionStrings.put(databaseKey,
                                parseLocalDatabase(properties, databaseSetName, databaseKey));
                }
            }
        return connectionStrings;
    }

    protected DalConnectionString parseLocalDatabase(Properties properties,
                                                      String databaseSetName, String databaseKey) {
        Properties databaseSetProperties = null;
        if (!StringUtils.isEmpty(databaseSetName))
            databaseSetProperties = PropertiesUtils.filterProperties(properties, databaseSetName);
        Properties databaseProperties = null;
        if (!StringUtils.isEmpty(databaseKey))
            databaseProperties = PropertiesUtils.filterProperties(properties, databaseKey);
        return buildConnectionString(
                PropertiesUtils.mergeProperties(databaseSetProperties, databaseProperties), databaseKey);
    }

    protected DalConnectionString parseLocalDatabase(Properties properties, String databaseKey) {
        return parseLocalDatabase(properties, null, databaseKey);
    }

    protected DalConnectionString buildConnectionString(Properties connectionStringProperties, String databaseKey) {
        CtripLocalDatabasePropertiesParser propertiesParser =
                CtripLocalDatabasePropertiesParser.newInstance(connectionStringProperties, databaseKey);
        String connStr = propertiesParser.buildConnectionString();
        boolean disableTableSharding = PropertiesUtils.getBoolPropertyNotEmpty(connectionStringProperties,
                PROPERTY_DISABLE_TABLE_SHARDING, true);
        return new LocalConnectionString(databaseKey, connStr, connStr, disableTableSharding);
    }

    /* database.config */

    protected Map<String, DalConnectionString> parseDatabaseConfig(String content, Set<String> names) {
        try (StringReader reader = new StringReader(content)) {
            InputSource source = new InputSource(reader);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Document doc = factory.newDocumentBuilder().parse(source);
            Element root = doc.getDocumentElement();
            Map<String, DalConnectionString> connectionStrings = new HashMap<>();
            List<Node> databaseEntries = getChildNodes(root, DATABASE_ENTRY);
            for (Node databaseEntry : databaseEntries) {
                String name = getAttribute(databaseEntry, DATABASE_ENTRY_NAME);
                String connStr = getAttribute(databaseEntry, DATABASE_ENTRY_CONNECTION_STRING);
                if (StringUtils.isEmpty(name) || StringUtils.isEmpty(connStr))
                    continue;
                String keyName = ConnectionStringKeyHelper.getKeyName(name);
                if (!names.contains(keyName))
                    continue;
                DalConnectionString connectionString =
                        new LocalConnectionString(keyName, connStr, connStr, false);
                connectionStrings.put(keyName, connectionString);
            }
            return connectionStrings;
        } catch (Throwable t) {
            String msg = String.format("Parse file %s error, msg: %s", getDatabaseConfigFileName(), t.getMessage());
            LOGGER.error(msg, t);
            throw new RuntimeException(msg, t);
        }
    }

    private String getDatabaseConfigFileName() {
        return StringUtils.isTrimmedEmpty(userDefinedFile) ? FILE_DATABASE_CONFIG : userDefinedFile;
    }

    private List<Node> getChildNodes(Node node, String name) {
        List<Node> nodes = new ArrayList<>();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
            if (children.item(i).getNodeName().equalsIgnoreCase(name))
                nodes.add(children.item(i));
        return nodes;
    }

    private String getAttribute(Node node, String attributeName) {
        Node item = node.getAttributes().getNamedItem(attributeName);
        return item != null ? item.getNodeValue() : null;
    }

    @Override
    public void addConnectionStringChangedListener(String name, ConnectionStringChanged callback) {}

}
