package com.ctrip.framework.dal.dbconfig.plugin.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author c7ch23en
 */
@RunWith(Suite.class)
@SuiteClasses({
        DefaultDataSourceCryptoTest.class,
        Soa2KeyServiceTest.class
})
public class AllTests {}
