package com.ctrip.platform.dal.daogen.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause.Entry;
import com.ctrip.platform.dal.daogen.dao.DalGroupDBDao;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.utils.ConnectionStringUtil;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

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
		this.databaseSet = new HashMap<Integer, DatabaseSet>();
		this.databaseSetEntries = new HashMap<Integer, HashMap<Integer, DatabaseSetEntry>>();
		this.databaseSetEntryMap = new HashMap<Integer, DatabaseSetEntry>();
	}

	public String getName() {
		return this.name;
	}

	public Collection<DatabaseSet> getDatabaseSet() {
		return this.databaseSet.values();
	}

	public Collection<DatabaseSetEntry> getDatabaseSetEntry(int setId) {
		return this.databaseSetEntries.containsKey(setId) ? this.databaseSetEntries
				.get(setId).values() : null;
	}

	public Collection<DatabaseSetEntry> getDatabaseSetEntryMap() {
		Collection<DatabaseSetEntry> collection = null;
		if (databaseSetEntryMap != null) {
			collection = new ArrayList<DatabaseSetEntry>();
			Map<String, DatabaseSetEntry> map = new HashMap<String, DatabaseSetEntry>();
			for (Map.Entry<Integer, DatabaseSetEntry> entry : databaseSetEntryMap
					.entrySet()) {
				String key = String.format("'%s'", entry.getValue()
						.getConnectionString()); // getName()
				if (!map.containsKey(key)) {
					map.put(key, entry.getValue());
				}
			}

			DalGroupDBDao dao = SpringBeanGetter.getDaoOfDalGroupDB();
			List<DalGroupDB> dbs = dao.getGroupDbsByDbNames(map.keySet());

			if (dbs != null) {
				for (DalGroupDB db : dbs) {
					DatabaseSetEntry e = new DatabaseSetEntry();
					e.setConnectionString(db.getDbname());
					String connectionString = ConnectionStringUtil
							.GetConnectionString(db.getDb_providerName()
									.toLowerCase(), db.getDb_address(), db
									.getDb_port(), db.getDb_user(), db
									.getDb_password(), db.getDb_catalog());
					e.setAllInOneConnectionString(connectionString);
					e.setProviderName(db.getDb_providerName());
					collection.add(e);
				}
			}
		}

		return collection;
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
			this.databaseSetEntries.put(databaseSetId,
					new HashMap<Integer, DatabaseSetEntry>());
		}
		if (!this.databaseSetEntries.get(databaseSetId).containsKey(
				databaseSetEntryId)) {
			this.databaseSetEntries.get(databaseSetId).put(databaseSetEntryId,
					entry);
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
