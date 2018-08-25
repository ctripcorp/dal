package com.ctrip.platform.dal.dao.helper;

import java.util.Set;

/**
 * Created by lilj on 2018/7/26.
 */
public interface TableParser {
    Set<String> getTablesFromSqls(String...sqls);

}
