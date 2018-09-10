package com.ctrip.platform.dal.daogen.report.newReport;

import com.alibaba.fastjson.annotation.JSONField;

public class TypeDetails {
    @JSONField(name = "DAL.version")
    private NewDALversion daLVersion;

    @JSONField(name = "SQL.database")
    private SQLdatabase sqlDatabase;

    @JSONField(name = "Ctrip.datasource.version")
    private CtripDatasourceVersion ctripDatasourceVersion;

    @JSONField(name = "Cat.Client.Version")
    private CatClientVersion catClientVersion;

    public NewDALversion getDaLVersion() {
        return daLVersion;
    }

    public void setDaLVersion(NewDALversion daLVersion) {
        this.daLVersion = daLVersion;
    }

    public SQLdatabase getSqlDatabase() {
        return sqlDatabase;
    }

    public void setSqlDatabase(SQLdatabase sqlDatabase) {
        this.sqlDatabase = sqlDatabase;
    }

    public CtripDatasourceVersion getCtripDatasourceVersion() {
        return ctripDatasourceVersion;
    }

    public void setCtripDatasourceVersion(CtripDatasourceVersion ctripDatasourceVersion) {
        this.ctripDatasourceVersion = ctripDatasourceVersion;
    }

    public CatClientVersion getCatClientVersion() {
        return catClientVersion;
    }

    public void setCatClientVersion(CatClientVersion catClientVersion) {
        this.catClientVersion = catClientVersion;
    }

}
