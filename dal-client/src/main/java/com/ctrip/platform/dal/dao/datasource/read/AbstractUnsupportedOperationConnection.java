package com.ctrip.platform.dal.dao.datasource.read;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public abstract class AbstractUnsupportedOperationConnection implements Connection {

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throw new UnsupportedOperationException("dal does not support nativeSQL");
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        // no need to do
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new UnsupportedOperationException("dal does not support getTypeMap");
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("dal does not support setTypeMap");
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        throw new UnsupportedOperationException("dal does not support setHoldability");
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new UnsupportedOperationException("dal does not support createClob");
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new UnsupportedOperationException("dal does not support createBlob");
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new UnsupportedOperationException("dal does not support createNClob");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new UnsupportedOperationException("dal does not support createSQLXML");
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        throw new UnsupportedOperationException("dal does not support isValid");
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new UnsupportedOperationException("dal does not support setClientInfo");
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new UnsupportedOperationException("dal does not support setClientInfo");
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        throw new UnsupportedOperationException("dal does not support getClientInfo");
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new UnsupportedOperationException("dal does not support getClientInfo");
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new UnsupportedOperationException("dal does not support createArrayOf");
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new UnsupportedOperationException("dal does not support createStruct");
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new UnsupportedOperationException("dal does not support abort");
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new UnsupportedOperationException("dal does not support setNetworkTimeout");
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new UnsupportedOperationException("dal does not support getNetworkTimeout");
    }
}
