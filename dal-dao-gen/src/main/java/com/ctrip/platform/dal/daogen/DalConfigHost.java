package com.ctrip.platform.dal.daogen;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;

public class DalConfigHost {
	private String name;
	private Map<Integer, DatabaseSet> databaseSet;
	private Map<Integer, HashMap<String, DatabaseSetEntry>> databaseSetEntries;
	
	public DalConfigHost(String name){
		this.name = name;
		this.databaseSet = new HashMap<Integer, DatabaseSet>();
		this.databaseSetEntries = new HashMap<Integer, HashMap<String, DatabaseSetEntry>>();
	}
	
	public String getName(){
		return this.name;
	}
	
	public Collection<DatabaseSet> getDatabaseSet(){
		return this.databaseSet.values();
	}
	
	public Collection<DatabaseSetEntry> getDatabaseSetEntry(int setId){
		
		return this.databaseSetEntries.containsKey(setId) ? 
				this.databaseSetEntries.get(setId).values() : null;
	}
	
	public void addDatabaseSet(DatabaseSet set){
		if(!this.databaseSet.containsKey(set.getId())){
			this.databaseSet.put(set.getId(), set);
		}
	}
	
	public void addDatabaseSet(List<DatabaseSet> sets){
		for (DatabaseSet databaseSet : sets) {
			this.addDatabaseSet(databaseSet);
		}
	}
	
	public void addDatabaseSetEntry(DatabaseSetEntry entry){
		if(!this.databaseSetEntries.containsKey(entry.getDatabaseSet_Id())){
			this.databaseSetEntries.put(entry.getDatabaseSet_Id(), 
					new HashMap<String, DatabaseSetEntry>());
		}
		if(!this.databaseSetEntries.get(entry.getDatabaseSet_Id()).containsKey(entry.getId())){
			this.databaseSetEntries.get(entry.getDatabaseSet_Id()).put(entry.getName(), entry);
		}
	}
	
	public void addDatabaseSetEntry(List<DatabaseSetEntry> entries){
		for (DatabaseSetEntry databaseSetEntry : entries) {
			this.addDatabaseSetEntry(databaseSetEntry);
		}
	}
}
