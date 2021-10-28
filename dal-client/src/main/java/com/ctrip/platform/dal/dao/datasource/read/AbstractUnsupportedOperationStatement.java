package com.ctrip.platform.dal.dao.datasource.read;

import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractUnsupportedOperationStatement implements Statement {

    @Override
    public int getMaxFieldSize() throws SQLException {
        throw new UnsupportedOperationException("dal does not support getMaxFieldSize");
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        throw new UnsupportedOperationException("dal does not support setMaxFieldSize");
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new UnsupportedOperationException("dal does not support setEscapeProcessing");
    }

    @Override
    public void cancel() throws SQLException {
        throw new UnsupportedOperationException("dal does not support cancel");
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException("dal does not support setCursorName");
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException("dal does not support setFetchDirection");
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException("dal does not support getFetchDirection");
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        throw new UnsupportedOperationException("dal does not support getMoreResults");
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new UnsupportedOperationException("dal does not support setPoolable");
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new UnsupportedOperationException("dal does not support isPoolable");
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new UnsupportedOperationException("dal does not support closeOnCompletion");
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        throw new UnsupportedOperationException("dal does not support isCloseOnCompletion");
    }

}
