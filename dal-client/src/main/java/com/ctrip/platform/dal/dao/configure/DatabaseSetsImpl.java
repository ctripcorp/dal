package com.ctrip.platform.dal.dao.configure;

import java.util.Collection;

/**
 * @author c7ch23en
 */
public class DatabaseSetsImpl implements DatabaseSets {

    private final Collection<DatabaseSet> databaseSets;

    public DatabaseSetsImpl(Collection<DatabaseSet> databaseSets) {
        this.databaseSets = databaseSets;
    }

    @Override
    public Collection<DatabaseSet> getAll() {
        return databaseSets;
    }

    @Override
    public Collection<DatabaseSet> getByDatabaseKey(String key) {
        throw new UnsupportedOperationException("unsupported yet");
    }

}
