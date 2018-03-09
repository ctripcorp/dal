package com.ctrip.platform.dal.daogen.host;

import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;

import java.sql.SQLException;
import java.util.*;

public class DalConfigHost {
    private String name;
    // <DatabaseSet ID, DatabaseSet>
    private Map<Integer, DatabaseSet> databaseSet;
    // <DatabaseSet ID,<DatabaseSetEntry ID, DatabaseSetEntry>>
    private Map<Integer, HashMap<Integer, DatabaseSetEntry>> databaseSetEntries;
    // Key:DatabaseSet Entry ID
    private Map<Integer, DatabaseSetEntry> databaseSetEntryMap;

    public DalConfigHost(String name) {
        this.name = name;
        this.databaseSet = new HashMap<>();
        this.databaseSetEntries = new HashMap<>();
        this.databaseSetEntryMap = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public Collection<DatabaseSet> getDatabaseSet() {
        return this.databaseSet.values();
    }

    public Collection<DatabaseSetEntry> getDatabaseSetEntry(int setId) {
        return this.databaseSetEntries.containsKey(setId) ? this.databaseSetEntries.get(setId).values() : null;
    }

    public Map<String, DatabaseSetEntry> getDatabaseSetEntryMap() throws SQLException {
        Map<String, DatabaseSetEntry> map = null;
        if (databaseSetEntryMap != null && databaseSetEntryMap.size() > 0) {
            map = new HashMap<>();
            Set<String> set = new HashSet<>();
            for (Map.Entry<Integer, DatabaseSetEntry> entry : databaseSetEntryMap.entrySet()) {
                set.add(entry.getValue().getConnectionString());
            }

            List<DalGroupDB> dbs = BeanGetter.getDaoOfDalGroupDB().getGroupDbsByDbNames(set);

            if (dbs != null && dbs.size() > 0) {
                for (DalGroupDB db : dbs) {
                    DatabaseSetEntry e = new DatabaseSetEntry();
                    e.setConnectionString(db.getDbname());
                    e.setProviderName(db.getDb_providerName());
                    e.setDbAddress(db.getDb_address());
                    e.setDbPort(db.getDb_port());
                    e.setUserName(db.getDb_user());
                    e.setPassword(db.getDb_password());
                    e.setDbCatalog(db.getDb_catalog());
                    map.put(e.getConnectionString(), e);
                }
            }
        }

        return map;
    }

    public void addDatabaseSet(DatabaseSet set) {
        if (!this.databaseSet.containsKey(set.getId())) {
            this.databaseSet.put(set.getId(), set);
        }
    }

    public void addDatabaseSet(List<DatabaseSet> sets) {
        for (DatabaseSet databaseSet : sets) {
            this.addDatabaseSet(databaseSet);
        }
    }

    public void addDatabaseSetEntry(DatabaseSetEntry entry) {
        Integer databaseSetId = entry.getDatabaseSet_Id();
        Integer databaseSetEntryId = entry.getId();

        if (!this.databaseSetEntries.containsKey(databaseSetId)) {
            this.databaseSetEntries.put(databaseSetId, new HashMap<Integer, DatabaseSetEntry>());
        }
        if (!this.databaseSetEntries.get(databaseSetId).containsKey(databaseSetEntryId)) {
            this.databaseSetEntries.get(databaseSetId).put(databaseSetEntryId, entry);
        }
        if (!this.databaseSetEntryMap.containsKey(databaseSetEntryId)) {
            this.databaseSetEntryMap.put(databaseSetEntryId, entry);
        }
    }

    public void addDatabaseSetEntry(List<DatabaseSetEntry> entries) {
        for (DatabaseSetEntry databaseSetEntry : entries) {
            this.addDatabaseSetEntry(databaseSetEntry);
        }
    }
}
