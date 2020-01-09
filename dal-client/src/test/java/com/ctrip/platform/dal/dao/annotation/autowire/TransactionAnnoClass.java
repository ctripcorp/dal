package com.ctrip.platform.dal.dao.annotation.autowire;

import com.ctrip.platform.dal.dao.annotation.Transactional;
import org.springframework.stereotype.Component;
import com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer;

@Component
public class TransactionAnnoClass {
    public static final String DB_NAME = OracleDatabaseInitializer.DATABASE_NAME;
    
    @Transactional(logicDbName = DB_NAME)
    public String perform() {
        return null;
    }
}
