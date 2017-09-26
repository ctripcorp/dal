package com.ctrip.platform.dal.daogen.host.java;

import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SpHost {
    private DatabaseCategory databaseCategory;
    private String packageName;
    private String dbName;
    private String pojoClassName;
    private String spName;
    private String callParameters;
    private List<JavaParameterHost> fields = new ArrayList<>();
    private String dbSetName;
    private boolean length;

    public Set<String> getDaoImports() {
        Set<String> imports = new TreeSet<>();

        imports.add(java.sql.SQLException.class.getName());
        imports.add(java.util.Map.class.getName());
        imports.add(java.sql.Types.class.getName());
        imports.addAll(getPojoImports());

        return imports;
    }

    public Set<String> getPojoImports() {
        Set<String> imports = new TreeSet<>();
        List<JavaParameterHost> allTypes = new ArrayList<>(fields);
        for (JavaParameterHost field : allTypes) {
            Class<?> clazz = field.getJavaClass();
            if (byte[].class.equals(clazz))
                continue;
            if (clazz.getPackage().getName().equals(String.class.getPackage().getName()))
                continue;
            imports.add(clazz.getName());
        }
        return imports;
    }

    public String getPojoClassName() {
        return pojoClassName;
    }

    public void setPojoClassName(String pojoClassName) {
        this.pojoClassName = pojoClassName;
    }

    public DatabaseCategory getDatabaseCategory() {
        return databaseCategory;
    }

    public void setDatabaseCategory(DatabaseCategory databaseCategory) {
        this.databaseCategory = databaseCategory;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public boolean isSpA() {
        return this.spName.contains("spA");
    }

    public List<JavaParameterHost> getFields() {
        return fields;
    }

    public void setFields(List<JavaParameterHost> fields) {
        this.fields = fields;
    }

    public boolean isSpa() {
        return null != this.spName && this.spName.contains("spA_");
    }

    public boolean isSp3() {
        return null != this.spName && this.spName.contains("sp3_");
    }

    public void setCallParameters(String callParameters) {
        this.callParameters = callParameters;
    }

    public String getCallParameters() {
        return this.callParameters;
    }

    public String getDbSetName() {
        return dbSetName;
    }

    public void setDbSetName(String dbSetName) {
        this.dbSetName = dbSetName;
    }

    public boolean isLength() {
        return length;
    }

    public void setLength(boolean length) {
        this.length = length;
    }

}
