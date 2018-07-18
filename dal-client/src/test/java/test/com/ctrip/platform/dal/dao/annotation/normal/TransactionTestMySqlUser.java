package test.com.ctrip.platform.dal.dao.annotation.normal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionTestMySqlUser implements TransactionTestUser{
    @Autowired
    private TransactionAnnoClassMySql test;
    
    public String perform() {
        return test.perform();
    }
    
    public String performNest() {
        return test.performNest();
    }
    
    public BaseTransactionAnnoClass getTransactionAnnoTest() {
        return test;
    }
}
