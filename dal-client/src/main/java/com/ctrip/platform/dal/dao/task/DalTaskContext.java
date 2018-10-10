package com.ctrip.platform.dal.dao.task;


import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lilj on 2018/7/27.
 */
public interface DalTaskContext {
    Set<String> getTables();
    List<Map<String, Object>> getIdentityFields();
    DalTaskContext fork();
}