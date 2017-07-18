package test.com.ctrip.platform.dal.dao.annotation.autowire;

import org.springframework.stereotype.Component;

import test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer;

import com.ctrip.platform.dal.dao.annotation.Transactional;

@Component
public class TransactionAnnoClass {
    public static final String DB_NAME = OracleDatabaseInitializer.DATABASE_NAME;
    
    @Transactional(logicDbName = DB_NAME)
    public String perform() {
        return null;
    }
}
