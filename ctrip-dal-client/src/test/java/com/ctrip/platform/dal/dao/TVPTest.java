package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TVPTest {
    @Test
    public void testTVP() throws Exception {
        // testTVPBatchInsert();
        testTVPBatchUpdate();
        // testTVPBatchDelete();
        // testTVPInsert();
        // testTVPUpdate();
        // testTVPDelete();
    }

    private void testTVPBatchInsert() throws SQLException {
        DalTableDao<Person> client = new DalTableDao<>(new DalDefaultJpaParser<>(Person.class));
        DalHints hints = DalHints.createIfAbsent(null);
        List<Person> daoPojos = new ArrayList<>();
        Person p1 = new Person();
        p1.setID(1);
        p1.setName("Insert1");
        p1.setAge(10);
        p1.setBirth(Date.valueOf("2017-07-11"));
        p1.setTest("Test Insert1");
        daoPojos.add(p1);

        Person p2 = new Person();
        p2.setID(2);
        p2.setName("Insert2");
        p2.setAge(20);
        p2.setBirth(Date.valueOf("2017-07-11"));
        p2.setTest("Test Insert2");
        daoPojos.add(p2);

        Person p3 = new Person();
        p3.setID(3);
        p3.setName("Insert3");
        p3.setAge(30);
        p3.setBirth(Date.valueOf("2017-07-11"));
        p3.setTest("Test Insert3");
        daoPojos.add(p3);

        Person p4 = new Person();
        p4.setID(4);
        p4.setName("Insert4");
        p4.setAge(40);
        p4.setBirth(Date.valueOf("2017-07-11"));
        p4.setTest("Test Insert4");
        daoPojos.add(p4);

        int[] result = client.batchInsert(hints, daoPojos);
    }

    private void testTVPBatchUpdate() throws SQLException {
        DalTableDao<Person> client = new DalTableDao<>(new DalDefaultJpaParser<>(Person.class));
        DalHints hints = DalHints.createIfAbsent(null);
        List<Person> daoPojos = new ArrayList<>();
        Person p1 = new Person();
        p1.setID(1);    //57958
        p1.setName("Update1");
        p1.setAge(10);
        p1.setBirth(Date.valueOf("2017-07-11"));
        p1.setTest("Test Update1");
        daoPojos.add(p1);

        Person p2 = new Person();
        p2.setID(2);    //57959
        p2.setName("Update2");
        p2.setAge(20);
        p2.setBirth(Date.valueOf("2017-07-11"));
        p2.setTest("Test Update2");
        daoPojos.add(p2);

        int[] result = client.batchUpdate(hints, daoPojos);
    }

    private void testTVPBatchDelete() throws SQLException {
        DalTableDao<Person> client = new DalTableDao<>(new DalDefaultJpaParser<>(Person.class));
        DalHints hints = DalHints.createIfAbsent(null);
        List<Person> daoPojos = new ArrayList<>();
        Person p1 = new Person();
        p1.setID(57961);
        p1.setName("Update1");
        p1.setAge(10);
        p1.setBirth(Date.valueOf("2017-07-11"));
        p1.setTest("Test Update1");
        daoPojos.add(p1);

        int[] result = client.batchDelete(hints, daoPojos);
    }

    private void testTVPInsert() throws SQLException {
        Connection connection = null;
        try {
            SQLServerDataTable sourceDataTable = getInsertDataTable();
            connection = DalClientFactory.getDalConfigure().getLocator().getConnection("TVP");

            // Using table-valued parameter with a SQLServerCallableStatement.
            SQLServerCallableStatement pStmt =
                    (SQLServerCallableStatement) connection.prepareCall("exec spT_Person_i ?");
            pStmt.setStructured(1, "TVP_Person", sourceDataTable);
            pStmt.execute();
        } catch (Throwable e) {
            String s = e.getMessage();
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    private void testTVPUpdate() throws SQLException {
        Connection connection = null;
        try {
            SQLServerDataTable sourceDataTable = getUpdateDataTable();
            connection = DalClientFactory.getDalConfigure().getLocator().getConnection("TVP");

            // Using table-valued parameter with a SQLServerCallableStatement.
            SQLServerCallableStatement pStmt =
                    (SQLServerCallableStatement) connection.prepareCall("exec spT_Person_u ?");
            pStmt.setStructured(1, "TVP_Person", sourceDataTable);
            pStmt.execute();
        } catch (Throwable e) {
            String s = e.getMessage();
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    private void testTVPDelete() throws SQLException {
        Connection connection = null;
        try {
            SQLServerDataTable sourceDataTable = getDeleteDataTable();
            connection = DalClientFactory.getDalConfigure().getLocator().getConnection("TVP");

            // Using table-valued parameter with a SQLServerCallableStatement.
            SQLServerCallableStatement pStmt =
                    (SQLServerCallableStatement) connection.prepareCall("exec spT_Person_d ?");
            pStmt.setStructured(1, "TVP_Person", sourceDataTable);
            pStmt.execute();
        } catch (Throwable e) {

        } finally {
            if (connection != null)
                connection.close();
        }
    }

    private SQLServerDataTable getInsertDataTable() throws SQLServerException {
        // Create an in-memory data table.
        SQLServerDataTable sourceDataTable = new SQLServerDataTable();

        // Define metadata for the data table.
        sourceDataTable.addColumnMetadata("Age", Types.INTEGER);
        sourceDataTable.addColumnMetadata("Birth", Types.DATE);
        sourceDataTable.addColumnMetadata("ID", Types.INTEGER);
        sourceDataTable.addColumnMetadata("Name", Types.VARCHAR);
        sourceDataTable.addColumnMetadata("Test", Types.NVARCHAR);

        // Populate the data table.
        int id1 = 1;
        int id2 = 2;
        sourceDataTable.addRow(1, Date.valueOf("2017-07-07"), id1, "Insert1", "Test Insert1");
        sourceDataTable.addRow(2, Date.valueOf("2017-07-07"), id2, "Insert2", "Test Insert2");

        return sourceDataTable;
    }

    private SQLServerDataTable getUpdateDataTable() throws SQLServerException {
        // Create an in-memory data table.
        SQLServerDataTable sourceDataTable = new SQLServerDataTable();

        // Define metadata for the data table.
        sourceDataTable.addColumnMetadata("Age", Types.INTEGER);
        sourceDataTable.addColumnMetadata("Birth", Types.DATE);
        sourceDataTable.addColumnMetadata("ID", Types.INTEGER);
        sourceDataTable.addColumnMetadata("Name", Types.VARCHAR);
        sourceDataTable.addColumnMetadata("Test", Types.NVARCHAR);

        // Populate the data table.
        int id1 = 57950;
        int id2 = 57951;
        sourceDataTable.addRow(1, Date.valueOf("2017-07-10"), id1, "Update3", null);
        sourceDataTable.addRow(2, Date.valueOf("2017-07-10"), id2, "Update4", null);

        return sourceDataTable;
    }

    private SQLServerDataTable getDeleteDataTable() throws SQLServerException {
        // Create an in-memory data table.
        SQLServerDataTable sourceDataTable = new SQLServerDataTable();

        // Define metadata for the data table.
        sourceDataTable.addColumnMetadata("Age", Types.INTEGER);
        sourceDataTable.addColumnMetadata("Birth", Types.DATE);
        sourceDataTable.addColumnMetadata("ID", Types.INTEGER);
        sourceDataTable.addColumnMetadata("Name", Types.VARCHAR);
        sourceDataTable.addColumnMetadata("Test", Types.NVARCHAR);

        // Populate the data table.
        int id1 = 57948;
        int id2 = 57949;
        sourceDataTable.addRow(null, null, id1, null, null);
        sourceDataTable.addRow(null, null, id2, null, null);

        return sourceDataTable;
    }

}
