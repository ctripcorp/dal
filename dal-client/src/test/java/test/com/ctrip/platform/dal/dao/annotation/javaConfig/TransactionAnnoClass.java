package test.com.ctrip.platform.dal.dao.annotation.javaConfig;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import test.com.ctrip.platform.dal.dao.unitbase.MySqlDatabaseInitializer;

import com.ctrip.platform.dal.dao.annotation.DalTransactional;
import com.ctrip.platform.dal.dao.annotation.Transactional;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;

@Component
public class TransactionAnnoClass {
    public static final String DB_NAME = MySqlDatabaseInitializer.DATABASE_NAME;
    
    @Autowired
    private AnotherClass test;

    public AnotherClass getTest() {
        return test;
    }

    @DalTransactional(logicDbName = DB_NAME)
    public String perform() {
        Assert.assertTrue(DalTransactionManager.isInTransaction());
        return null;
    }
    
    @Transactional(logicDbName = DB_NAME)
    public String performOld() {
        Assert.assertTrue(DalTransactionManager.isInTransaction());
        return null;
    }
}
