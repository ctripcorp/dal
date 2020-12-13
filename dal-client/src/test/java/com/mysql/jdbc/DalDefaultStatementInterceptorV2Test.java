package com.mysql.jdbc;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class DalDefaultStatementInterceptorV2Test extends DalDefaultStatementInterceptorV2{

    @Test
    public void postProcess() throws SQLException {
        DalDefaultStatementInterceptorV2 v2 = new DalDefaultStatementInterceptorV2();
        try {
            v2.postProcess(null, null, null, null, 0, false, true, null);
        } catch (SQLException e) {
            assertEquals(true, e instanceof CommunicationsException);
        }
    }
}