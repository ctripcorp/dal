package com.ctrip.framework.dal.dbconfig.plugin.entity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author c7ch23en
 */
@RunWith(Suite.class)
@SuiteClasses({
        ConnectionCheckInputTester.class,
        ConnectionCheckOutputTester.class,
        KeyGetOutputTester.class,
        MhaInputTester.class,
        SiteInputTester.class,
        SiteOutputTester.class,
        SoaKeyResponseTester.class,
})
public class AllTests {}
