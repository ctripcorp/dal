package com.ctrip.platform.dal.dao.task;

public class SingleReplaceTask<T> extends AbstractSingleInsertTask<T> {
    public static final String TMPL_SQL_REPLACE = "REPLACE INTO %s (%s) VALUES(%s)";

    @Override
    public String getSqlTpl(){
        return TMPL_SQL_REPLACE;
    }
}
