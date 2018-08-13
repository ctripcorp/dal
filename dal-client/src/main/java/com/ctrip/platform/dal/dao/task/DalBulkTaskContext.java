package com.ctrip.platform.dal.dao.task;


import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lilj on 2018/8/6.
 */
public interface DalBulkTaskContext<T> extends DalTaskContext {
    Map<String, Boolean> getPojoFieldStatus();
    boolean isUpdatableEntity();
    List<T> getRawPojos();
    Set<String> getUnqualifiedColumns();
}
