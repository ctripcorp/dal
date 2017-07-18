package test.com.ctrip.platform.dal.dao.annotation.autowire;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import test.com.ctrip.platform.dal.dao.unittests.TransactionTestUser;

@Component
public class TransactionTestUserFail implements TransactionTestUser{
    @Autowired
    private TransactionAnnoClass test;
    
    public String perform() {
        return test.perform();
    }
    
    public String performNest() {
        return test.perform();
    }
}
