package com.ctrip.platform.dal.daogen.host.java;

import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FreeSqlHost {
    private String packageName;
    private String dbSetName;
    private String className;
    private List<JavaMethodHost> methods = new ArrayList<>();
    private List<JavaParameterHost> fields;
    private DatabaseCategory databaseCategory;
    private boolean length;

    public Set<String> getDaoImports() {
        Set<String> imports = new TreeSet<>();
        imports.add("com.ctrip.platform.dal.common.enums.DatabaseCategory");
        imports.add("com.ctrip.platform.dal.dao.*");
        imports.add("com.ctrip.platform.dal.dao.sqlbuilder.*");

        imports.add(java.sql.SQLException.class.getName());
        imports.add(java.sql.Types.class.getName());
        imports.add(List.class.getName());

        List<JavaParameterHost> allTypes = new ArrayList<>();
        for (JavaMethodHost method : methods) {
            if (null != method.getParameters())
                allTypes.addAll(method.getParameters());
            if (null != method.getFields())
                allTypes.addAll(method.getFields());
        }

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

    public Set<String> getTestImports() {
        Set<String> imports = new TreeSet<>();
        imports.add(List.class.getName());
        imports.addAll(this.getPojoImports());
        return imports;
    }

    public Set<String> getPojoImports() {
        Set<String> imports = new TreeSet<>();

        List<JavaParameterHost> allTypes = new ArrayList<>();
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

    public String pageBegain() {
        return "(pageNo - 1) * pageSize";
    }

    public String pageEnd() {
        return "pageSize";
    }

    public List<JavaParameterHost> getFields() {
        return fields;
    }

    public void setFields(List<JavaParameterHost> fields) {
        this.fields = fields;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDbSetName() {
        return dbSetName;
    }

    public void setDbSetName(String dbSetName) {
        this.dbSetName = dbSetName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<JavaMethodHost> getMethods() {
        return methods;
    }

    public void setMethods(List<JavaMethodHost> methods) {
        this.methods = methods;
    }

    public DatabaseCategory getDatabaseCategory() {
        return databaseCategory;
    }

    public void setDatabaseCategory(DatabaseCategory databaseCategory) {
        this.databaseCategory = databaseCategory;
    }

    public boolean isLength() {
        return length;
    }

    public void setLength(boolean length) {
        this.length = length;
    }

}
