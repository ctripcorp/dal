package com.mysql.jdbc;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class DalDefaultStatementInterceptorV2Test{


    @Test
    public void isCommunicationException() {
        DalDefaultStatementInterceptorV2 v2 = new DalDefaultStatementInterceptorV2();
        SQLException e = new SQLException();
        assertEquals(false, v2.isCommunicationsException(e));

        CommunicationsException exception = new CommunicationsException(null, 1000L, 1000L, null);
        assertEquals(true, v2.isCommunicationsException(exception));

        e.initCause(exception);
        assertEquals(true, v2.isCommunicationsException(e));

    }
}