package com.ctrip.platform.dal.dao.helper;

import java.util.List;

public interface ClassScanner {

    List<Class<?>> getClasses(String packageName, boolean recursive);

}
