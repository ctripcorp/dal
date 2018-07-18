package com.ctrip.platform.dal.dao.configure;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataSourceConfigureParser implements DataSourceConfigureConstants {
    public synchronized static DataSourceConfigureParser getInstance() {
        if (dataSourceConfigureParser == null) {
            dataSourceConfigureParser = new DataSourceConfigureParser();
        }
        return dataSourceConfigureParser;
    }

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfigureParser.class);
    private static DataSourceConfigureParser dataSourceConfigureParser = null;
    private static final String DBPOOL_CONFIG = "datasource.xml";
    private static final String LOCATION = "location";
    private static final String RESOURCE_NODE = "Datasource";
    private static final String NAME = "name";
    private static final String PROD_SUFFIX = "_SH";

    private String databaseConfigLocation = null;
    private String dataSourceXmlLocation = null;
    private boolean dataSourceXmlExist = false;

    private DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();

    private DataSourceConfigureParser() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = DataSourceConfigureParser.class.getClassLoader();
            }
            URL url = classLoader.getResource(DBPOOL_CONFIG);
            if (url != null) {
                dataSourceXmlExist = true;
                dataSourceXmlLocation = url.getFile();
                logger.info("datasource property will use file :" + url.getFile());
                parse(url.openStream());
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean isDataSourceXmlExist() {
        return dataSourceXmlExist;
    }

    public String getDataSourceXmlLocation() {
        return dataSourceXmlLocation;
    }

    public String getDatabaseConfigLocation() {
        return databaseConfigLocation;
    }

    private void parse(InputStream in) throws Exception {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            parseDocument(doc);
            in.close();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
    }

    private void parseDocument(Document doc) throws Exception {
        Element root = doc.getDocumentElement();
        if (hasAttribute(root, LOCATION)) {
            databaseConfigLocation = getAttribute(root, LOCATION);
        }
        List<Node> resourceList = getChildNodes(root, RESOURCE_NODE);
        for (Node resource : resourceList) {
            DataSourceConfigure dataSourceConfigure = parseResource(resource);
            dataSourceConfigureLocator.addUserPoolPropertiesConfigure(dataSourceConfigure.getName(),
                    dataSourceConfigure);
        }
    }

    private DataSourceConfigure parseResource(Node resource) {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setName(getAttribute(resource, NAME));

        // The following are key connection parameters, developer do not need to provide them in case the configure
        // provider is set
        if (hasAttribute(resource, USER_NAME)) {
            dataSourceConfigure.setUserName(getAttribute(resource, USER_NAME));
        }
        if (hasAttribute(resource, PASSWORD)) {
            dataSourceConfigure.setPassword(getAttribute(resource, PASSWORD));
        }
        if (hasAttribute(resource, CONNECTION_URL)) {
            dataSourceConfigure.setConnectionUrl(getAttribute(resource, CONNECTION_URL));
        }
        if (hasAttribute(resource, DRIVER_CLASS_NAME)) {
            dataSourceConfigure.setDriverClass(getAttribute(resource, DRIVER_CLASS_NAME));
        }

        // The following are common options
        List<String> properties = new ArrayList<>();
        properties.add(TESTWHILEIDLE);
        properties.add(TESTONBORROW);
        properties.add(TESTONRETURN);

        properties.add(VALIDATIONQUERY);
        properties.add(VALIDATIONQUERYTIMEOUT);
        properties.add(VALIDATIONINTERVAL);

        properties.add(TIMEBETWEENEVICTIONRUNSMILLIS);
        properties.add(MINEVICTABLEIDLETIMEMILLIS);

        properties.add(MAX_AGE);
        properties.add(MAXACTIVE);
        properties.add(MINIDLE);
        properties.add(MAXWAIT);
        properties.add(INITIALSIZE);

        properties.add(REMOVEABANDONEDTIMEOUT);
        properties.add(REMOVEABANDONED);
        properties.add(LOGABANDONED);

        properties.add(VALIDATORCLASSNAME);
        properties.add(INIT_SQL);
        properties.add(INIT_SQL2);

        properties.add(JDBC_INTERCEPTORS);
        processProperties(dataSourceConfigure, resource, properties);

        /**
         * Special handing for connectionProperties and option. If connectionProperties is not set, we will use option's
         * value if connectionProperties is set, we will ignore option's value
         */
        if (hasAttribute(resource, CONNECTIONPROPERTIES)) {
            String value = getAttribute(resource, CONNECTIONPROPERTIES);
            dataSourceConfigure.setProperty(CONNECTIONPROPERTIES, value);
        } else {
            if (hasAttribute(resource, OPTION)) {
                String value = getAttribute(resource, OPTION);
                dataSourceConfigure.setProperty(OPTION, value);
                dataSourceConfigure.setProperty(CONNECTIONPROPERTIES, value);
            }
        }

        return dataSourceConfigure;
    }

    private void processProperties(DataSourceConfigure dataSourceConfigure, Node resource, List<String> properties) {
        for (String property : properties) {
            if (hasAttribute(resource, property)) {
                String value = getAttribute(resource, property);
                dataSourceConfigure.setProperty(property, value);
            }
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

    private String getAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    public String getPossibleName(String name) {
        name = name.toUpperCase();
        return name.endsWith(PROD_SUFFIX) ? name.substring(0, name.length() - PROD_SUFFIX.length())
                : name + PROD_SUFFIX;
    }

}
