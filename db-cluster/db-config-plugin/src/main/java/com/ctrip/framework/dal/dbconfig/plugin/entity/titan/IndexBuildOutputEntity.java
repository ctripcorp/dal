package com.ctrip.framework.dal.dbconfig.plugin.entity.titan;


import java.util.Set;

/**
 * Created by lzyan on 2018/09/25.
 */
public class IndexBuildOutputEntity {
    //field
    private Set<String> dbKeyIndexSet;
    private Set<String> dbIndexSet;


    //constructor
    public IndexBuildOutputEntity(){}
    public IndexBuildOutputEntity(Set<String> dbKeyIndexSet, Set<String> dbIndexSet){
        this.dbKeyIndexSet = dbKeyIndexSet;
        this.dbIndexSet = dbIndexSet;
    }

    //setter/getter
    public Set<String> getDbKeyIndexSet() {
        return dbKeyIndexSet;
    }
    public void setDbKeyIndexSet(Set<String> dbKeyIndexSet) {
        this.dbKeyIndexSet = dbKeyIndexSet;
    }

    public Set<String> getDbIndexSet() {
        return dbIndexSet;
    }
    public void setDbIndexSet(Set<String> dbIndexSet) {
        this.dbIndexSet = dbIndexSet;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IndexBuildOutputEntity{");
        sb.append("dbKeyIndexSet='").append(dbKeyIndexSet).append('\'');
        sb.append(", dbIndexSet='").append(dbIndexSet).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
