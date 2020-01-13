package com.ctrip.platform.dal.dao.helper.EntityManagerTest;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;

public class EntityManagerInitializer {
    private static final String DATABASE_NAME = "dao_test";
    private static DalClient client = null;

    private static final String CREATE_TABLE_GRAND_PARENT =
            "CREATE TABLE `GrandParentTable`" + "(" + "`grandParentId` INT(11) NOT NULL AUTO_INCREMENT,"
                    + "`grandParentName` VARCHAR(50) NULL," + "PRIMARY KEY (`grandParentId`)" + ")";

    private static final String CREATE_TABLE_PARENT =
            "CREATE TABLE `ParentTable`" + "(" + "`parentId` INT(11) NOT NULL AUTO_INCREMENT,"
                    + "`parentName` VARCHAR(50) NULL," + "PRIMARY KEY (`parentId`)" + ")";

    private static final String CREATE_TABLE_CHILD =
            "CREATE TABLE `ChildTable`" + "(" + "`childId` INT(11) NOT NULL AUTO_INCREMENT,"
                    + "`childName` VARCHAR(50) NULL," + "PRIMARY KEY (`childId`)" + ")";

    private static final String CREATE_TABLE_CHILD_WITH_ALL_FIELDS = "CREATE TABLE `ChildWithAllFields`" + "("
            + "`grandParentId` INT(11) NOT NULL AUTO_INCREMENT," + "`grandParentName` VARCHAR(50) NULL,"
            + "`parentId` INT(11) NOT NULL," + "`parentName` VARCHAR(50) NULL," + "`childId` INT(11) NOT NULL,"
            + "`childName` VARCHAR(50) NULL," + "PRIMARY KEY (`grandParentId`)" + ")";

    private static final String DROP_TABLE_GRAND_PARENT = "DROP TABLE IF EXISTS `GrandParentTable`";

    private static final String DROP_TABLE_PARENT = "DROP TABLE IF EXISTS `ParentTable`";

    private static final String DROP_TABLE_CHILD = "DROP TABLE IF EXISTS `ChildTable`";

    private static final String DROP_TABLE_CHILD_WITH_ALL_FIELDS = "DROP TABLE IF EXISTS `ChildWithAllFields`";

    static {
        try {
            DalClientFactory.initClientFactory();
            client = DalClientFactory.getClient(DATABASE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUpBeforeClass() throws Exception {
        dropAllTables();
        createAllTables();
    }

    public static void tearDownAfterClass() throws Exception {
        dropAllTables();
    }

    private static void dropAllTables() throws Exception {
        String[] sqls = new String[] {DROP_TABLE_GRAND_PARENT, DROP_TABLE_PARENT, DROP_TABLE_CHILD,
                DROP_TABLE_CHILD_WITH_ALL_FIELDS,};

        client.batchUpdate(sqls, new DalHints());
    }

    private static void createAllTables() throws Exception {
        String[] sqls = new String[] {CREATE_TABLE_GRAND_PARENT, CREATE_TABLE_PARENT, CREATE_TABLE_CHILD,
                CREATE_TABLE_CHILD_WITH_ALL_FIELDS};

        client.batchUpdate(sqls, new DalHints());
    }
}
