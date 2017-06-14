package com.ctrip.platform.dal.daogen.host.java;

import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.utils.DatabaseSetUtils;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class SpDbHost {
    private DatabaseCategory databaseCategory;
    private String packageName;
    private String dbSetName;
    // <SpHost spName, SpHost>
    private HashMap<String, SpHost> spHosts;

    public SpDbHost(String dbSetName, String packageName) throws Exception {
        this.dbSetName = dbSetName;
        this.packageName = packageName;
        this.databaseCategory = DatabaseCategory.SqlServer;
        String dbType = DbUtils.getDbType(DatabaseSetUtils.getAllInOneName(dbSetName));
        if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
            this.databaseCategory = DatabaseCategory.MySql;
        }
        this.spHosts = new HashMap<>();
    }

    public void addSpHost(SpHost host) {
        if (!this.spHosts.containsKey(host.getSpName()))
            this.spHosts.put(host.getSpName(), host);
    }

    public DatabaseCategory getDatabaseCategory() {
        return this.databaseCategory;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getDbSetName() {
        return this.dbSetName;
    }

    public Collection<SpHost> getSpHosts() {
        return this.spHosts.values();
    }

    public Set<String> getDaoImports() {
        Set<String> imports = new TreeSet<>();
        imports.add("com.ctrip.platform.dal.dao.*");
        imports.add("com.ctrip.platform.dal.dao.helper.*");
        imports.add(java.sql.SQLException.class.getName());
        imports.add(java.sql.Types.class.getName());
        imports.add(java.util.Map.class.getName());

        return imports;
    }

    public Set<String> getTestImports() {
        Set<String> imports = new TreeSet<>();
        imports.add(java.util.Map.class.getName());

        return imports;
    }

    public Set<String> getPojoImports() {
        Set<String> imports = new TreeSet<>();
        for (SpHost host : this.spHosts.values()) {
            imports.addAll(host.getPojoImports());
        }

        return imports;
    }
}
