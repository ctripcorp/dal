package com.ctrip.platform.dal.dao.configure;

public interface FreshnessReader {
    int INVALID = -1;
    
    /**
     * get freshness for given slave db
     * @param logicDbName
     * @param slaveDbName
     * @return
     */
    int getSlaveFreshness(String logicDbName, String slaveDbName);
}
