package com.ctrip.platform.dal.dao.log;

import java.util.List;

public interface DaoCallerParseInterceptor {

    /**
     * To get more accurate dao caller method name, user should provide his dao caller class path, we will filter stack trace
     * under the result packages.
     * @return
     */
    List<String> includedPackageSpace();
}
