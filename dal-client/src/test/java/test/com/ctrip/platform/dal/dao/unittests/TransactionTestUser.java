package test.com.ctrip.platform.dal.dao.unittests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionTestUser {
    @Autowired
    private TransactionTestClassSqlServer test;
    
    public String perform() {
        return test.perform();
    }
    
    public String performNest() {
        return test.performNest();
    }
}
