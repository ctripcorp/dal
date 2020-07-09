package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.daogen.sql.builder.SQLBuilderTests;
import com.ctrip.platform.dal.daogen.sql.validate.SQLValidationTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author c7ch23en
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        SQLBuilderTests.class,
        SQLValidationTests.class
})
public class AllTests {
}
