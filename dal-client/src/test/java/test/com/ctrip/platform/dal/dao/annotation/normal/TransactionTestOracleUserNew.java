package test.com.ctrip.platform.dal.dao.annotation.normal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionTestOracleUserNew implements TransactionTestUser{
    @Autowired
    private TransactionAnnoClassOracleNew test;
    
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
