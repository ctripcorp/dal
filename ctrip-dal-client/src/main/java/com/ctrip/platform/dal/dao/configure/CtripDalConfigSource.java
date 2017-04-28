package com.ctrip.platform.dal.dao.configure;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import qunar.tc.qconfig.client.TypedConfig;

import com.ctrip.platform.dal.exceptions.DalException;

public class CtripDalConfigSource implements DalConfigConstants, DalConfigSource {
    public static final String USE_LOCAL_DAL_CONFIG = "dal.config.uselocal";
    private static final Logger LOGGER = LoggerFactory.getLogger(CtripDalConfigSource.class);
    private static final String DAL_CONFIG_XML = "dal.config.xml";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public void initialize(Map<String, String> settings) throws Exception {}

    @Override
    public Map<String, DatabaseSet> getDatabaseSets(Node databaseSetsNode) throws Exception {
        Map<String, DatabaseSet> map = new HashMap<>();
        try {
            if (isUseLocalDalConfig()) {
                map = DefaultDalConfigSourceParser.readDatabaseSets(databaseSetsNode);
            } else {
                TypedConfig<String> config = TypedConfig.get(DAL_CONFIG_XML, new TypedConfig.Parser<String>() {
                    public String parse(String s) {
                        return s;
                    }
                });

                String content = config.current();
                if (content != null && content.length() > 0) {
                    InputStream stream = new ByteArrayInputStream(content.getBytes(CHARSET));
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
                    Element root = doc.getDocumentElement();
                    Node node = getChildNode(root, DATABASE_SETS);
                    map = DefaultDalConfigSourceParser.readDatabaseSets(node);
                }
            }
        } catch (Throwable e) {
            String msg = "从QConfig读取dal.config配置时发生异常:";
            LOGGER.error(msg + e.getMessage(), e);
            throw new DalException(msg + e.getMessage());
            // Fallback to local dal.config?
            // return DefaultDalConfigSourceParser.readDatabaseSets(databaseSetsNode);
        }

        return map;
    }

    private boolean isUseLocalDalConfig() {
        boolean b = false;
        String useLocal = System.getProperty(USE_LOCAL_DAL_CONFIG);
        if (useLocal != null && useLocal.length() > 0) {
            try {
                b = Boolean.parseBoolean(useLocal);
            } catch (Throwable e) {
            }
        }
        return b;
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
}
