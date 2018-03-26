package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalQueryDao;

public class BaseDao {
    public static final String DATA_BASE = "dao";
    public static final DatabaseCategory dbCategory = DatabaseCategory.MySql;
    public DalQueryDao queryDao = new DalQueryDao(DATA_BASE);
}
