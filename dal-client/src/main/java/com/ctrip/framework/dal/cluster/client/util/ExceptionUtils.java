package com.ctrip.framework.dal.cluster.client.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExceptionUtils {

    public static void throwSQLExceptionIfNeeded(SQLException e) throws SQLException {
        List<SQLException> exceptions = new ArrayList<>();
        exceptions.add(e);
        throwSQLExceptionIfNeeded(exceptions);
    }

    public static void throwSQLExceptionIfNeeded(List<SQLException> exceptions) throws SQLException {
        if (exceptions != null && !exceptions.isEmpty()) {
            StringWriter buffer = new StringWriter();
            PrintWriter out = null;
            try {
                out = new PrintWriter(buffer);

                for (SQLException exception : exceptions) {
                    exception.printStackTrace(out);
                }
            } finally {
                if (out != null) {
                    out.close();
                }
            }

            throw new SQLException(buffer.toString());
        }
    }
}
