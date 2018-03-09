package test.com.ctrip.platform.dal.dao.annotation.normal;


public interface TransactionTestUser {
    String perform();

    String performNest();
    
    BaseTransactionAnnoClass getTransactionAnnoTest();
}
