package com.ctrip.platform.dal.dao.log;

public interface LogFilter {

    boolean filter(Throwable throwable);

}
