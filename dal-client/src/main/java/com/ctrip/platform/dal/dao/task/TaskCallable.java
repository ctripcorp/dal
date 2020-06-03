package com.ctrip.platform.dal.dao.task;

import java.util.concurrent.Callable;

public interface TaskCallable<T> extends Callable<T> {

    DalTaskContext getDalTaskContext();

    String getPreparedDbShard();

}
