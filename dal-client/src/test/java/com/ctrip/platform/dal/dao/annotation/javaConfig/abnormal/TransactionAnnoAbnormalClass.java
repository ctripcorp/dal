package com.ctrip.platform.dal.dao.annotation.javaConfig.abnormal;

import com.ctrip.platform.dal.dao.annotation.DalTransactional;
import com.ctrip.platform.dal.dao.annotation.Transactional;
import com.ctrip.platform.dal.dao.annotation.javaConfig.normal.AnotherClass;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import com.ctrip.platform.dal.dao.unitbase.MySqlDatabaseInitializer;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class TransactionAnnoAbnormalClass {
    public static final String DB_NAME = MySqlDatabaseInitializer.DATABASE_NAME;


    @DalTransactional(logicDbName = DB_NAME)
    public void publicMethod() {

    }

    @DalTransactional(logicDbName = DB_NAME)
    protected void protectedMethod() {

    }

    @DalTransactional(logicDbName = DB_NAME)
    void defaultMethod() {

    }

    @DalTransactional(logicDbName = DB_NAME)
    private void privateMethod() {

    }

    @DalTransactional(logicDbName = DB_NAME)
    final public void finalMethod() {

    }

    @DalTransactional(logicDbName = DB_NAME)
    static void staticMethod() {

    }
}
