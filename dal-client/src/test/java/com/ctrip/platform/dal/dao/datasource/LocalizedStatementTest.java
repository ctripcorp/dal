package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.datasource.log.OperationType;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocalizedStatementTest {

    @Test
    public void getOperationType() {
        String sql = "select * from table where id = 1";
        LocalizedStatement statement = new LocalizedStatement(null);
        assertEquals(OperationType.QUERY, statement.getOperationType(sql));

        sql = "insert into table (id, name, age) values (1, 'test', 12)";
        assertEquals(OperationType.INSERT, statement.getOperationType(sql));

        sql = "update table set name =  'test1' where id = 1";
        assertEquals(OperationType.UPDATE, statement.getOperationType(sql));

        sql =  "delete from table where id = 1";
        assertEquals(OperationType.UPDATE, statement.getOperationType(sql));
    }
}