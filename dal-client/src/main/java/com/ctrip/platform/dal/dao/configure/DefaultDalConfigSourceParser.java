package com.ctrip.platform.dal.dao.configure;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDalConfigSourceParser implements DalConfigConstants {
    public static Map<String, DatabaseSet> readDatabaseSets(Node databaseSetsNode) throws Exception {
        List<Node> databaseSetList = getChildNodes(databaseSetsNode, DATABASE_SET);
        Map<String, DatabaseSet> databaseSets = new HashMap<>();
        for (int i = 0; i < databaseSetList.size(); i++) {
            DatabaseSet databaseSet = readDatabaseSet(databaseSetList.get(i));
            databaseSets.put(databaseSet.getName(), databaseSet);
        }
        return databaseSets;
    }

    private static DatabaseSet readDatabaseSet(Node databaseSetNode) throws Exception {
        List<Node> databaseList = getChildNodes(databaseSetNode, ADD);
        Map<String, DataBase> databases = new HashMap<>();
        for (int i = 0; i < databaseList.size(); i++) {
            DataBase database = readDataBase(databaseList.get(i));
            databases.put(database.getName(), database);
        }

        if (hasAttribute(databaseSetNode, SHARD_STRATEGY))
            return new DatabaseSet(
                    getAttribute(databaseSetNode, NAME),
                    getAttribute(databaseSetNode, PROVIDER),
                    getAttribute(databaseSetNode, SHARD_STRATEGY),
                    databases);
        else if (hasAttribute(databaseSetNode, SHARDING_STRATEGY))
            return new DatabaseSet(
                    getAttribute(databaseSetNode, NAME),
                    getAttribute(databaseSetNode, PROVIDER),
                    getAttribute(databaseSetNode, SHARDING_STRATEGY),
                    databases);
        else
            return new DatabaseSet(
                    getAttribute(databaseSetNode, NAME),
                    getAttribute(databaseSetNode, PROVIDER),
                    databases);
    }

    private static DataBase readDataBase(Node dataBaseNode) {
        return new DataBase(
                getAttribute(dataBaseNode, NAME),
                getAttribute(dataBaseNode, DATABASE_TYPE).equals(MASTER),
                getAttribute(dataBaseNode, SHARDING),
                getAttribute(dataBaseNode, CONNECTION_STRING));
    }

    private static List<Node> getChildNodes(Node node, String name) {
        List<Node> nodes = new ArrayList<>();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (!children.item(i).getNodeName().equalsIgnoreCase(name))
                continue;
            nodes.add(children.item(i));
        }
        return nodes;
    }

    private static boolean hasAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName) != null;
    }


    private static String getAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    private static Node getChildNode(Node node, String name) {
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
