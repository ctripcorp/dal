package com.ctrip.platform.dal.daogen.java;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

public class SpDbHost {
	private DatabaseCategory databaseCategory;
	private String packageName;
	private String dbName;
	private HashMap<String, SpHost> spHosts;
	
	public SpDbHost(String dbName, String packageName)
	{
		this.dbName = dbName;
		this.packageName = packageName;
		this.databaseCategory = DatabaseCategory.SqlServer;
		if (!DbUtils.getDbType(this.dbName).equalsIgnoreCase(
				"Microsoft SQL Server")) {
			this.databaseCategory = DatabaseCategory.MySql;
		}
		this.spHosts = new HashMap<String, SpHost>();
	}
	
	public void addSpHost(SpHost host)
	{
		if(!this.spHosts.containsKey(host.getSpName()))
			this.spHosts.put(host.getSpName(), host);
	}
	
	public DatabaseCategory getDatabaseCategory()
	{
		return this.databaseCategory;
	}
	
	public String getPackageName()
	{
		return this.packageName;
	}
	
	public String getDbName()
	{
		return this.dbName;
	}
	
	public Collection<SpHost> getSpHosts()
	{
		return this.spHosts.values();
	}
	
	public Set<String> getDaoImports()
	{
		Set<String> imports = new TreeSet<String>();
		imports.add("com.ctrip.platform.dal.dao.*");
		imports.add("com.ctrip.platform.dal.dao.helper.*");
		imports.add(java.sql.SQLException.class.getName());
		imports.add(java.sql.Types.class.getName());
		imports.add(java.util.Map.class.getName());
		
		return imports;
	}
	
	public Set<String> getTestImports()
	{
		Set<String> imports = new TreeSet<String>();		
		imports.add(java.util.Map.class.getName());
		
		return imports;
	}
	
	public Set<String> getPojoImports()
	{
		Set<String> imports = new TreeSet<String>();
		for (SpHost host : this.spHosts.values()) {
			imports.addAll(host.getPojoImports());
		}
		
		return imports;
	}
}