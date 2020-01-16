package com.ctrip.platform.dal.dao.helper;

public interface ClassScanFilter {

    boolean accept(Class<?> clazz);

}
