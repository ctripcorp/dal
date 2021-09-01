package com.ctrip.platform.dal.dao.log;

import java.util.List;

public class DefaultDaoCallerParser implements DaoCallerParseInterceptor {

    @Override
    public List<String> includedPackageSpace() {
        return null;
    }
}
