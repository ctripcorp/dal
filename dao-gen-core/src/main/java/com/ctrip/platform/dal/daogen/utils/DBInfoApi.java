package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.entity.DBLevelInfo;
import com.ctrip.platform.dal.daogen.entity.DbInfos;

import java.util.List;

public interface DBInfoApi {
    public List<DBLevelInfo> getDBLevelInfo(String dbType);

    public List<DbInfos> getAllDbInfos(String nameBases);
}
