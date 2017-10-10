package com.ctrip.platform.dal.daogen.host.csharp;

import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;

import java.util.List;

public class CSharpFreeSqlHost {
    private String nameSpace;

    private String dbSetName;

    private String className;

    private List<CSharpMethodHost> methods;

    private DatabaseCategory databaseCategory;

    private String projectName;

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
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

    public List<CSharpMethodHost> getMethods() {
        return methods;
    }

    public void setMethods(List<CSharpMethodHost> methods) {
        this.methods = methods;
    }

    public DatabaseCategory getDatabaseCategory() {
        return databaseCategory;
    }

    public void setDatabaseCategory(DatabaseCategory databaseCategory) {
        this.databaseCategory = databaseCategory;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String pageBegain() {
        return "(pageNo - 1) * pageSize";
        /*
         * if (this.databaseCategory == DatabaseCategory.MySql) { return "(pageNo - 1) * pageSize"; } else { return
         * "(pageNo - 1) * pageSize + 1"; }
         */
    }

    public String pageEnd() {
        return "pageSize";
        /*
         * if (this.databaseCategory == DatabaseCategory.MySql) { return "pageSize"; } else { return
         * "pageSize * pageNo"; }
         */
    }

}
