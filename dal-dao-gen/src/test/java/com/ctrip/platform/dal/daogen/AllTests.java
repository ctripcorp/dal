package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.daogen.resource.DatabaseResourceTest;
import com.ctrip.platform.dal.daogen.resource.FileResourceTest;
import com.ctrip.platform.dal.daogen.sql.builder.SQLBuilderTests;
import com.ctrip.platform.dal.daogen.sql.validate.SQLValidationTests;
import com.ctrip.platform.dal.daogen.utils.AllInOneNameUtilsTest;
import com.ctrip.platform.dal.daogen.utils.DalClusterUtilsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author c7ch23en
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        SQLBuilderTests.class,
        SQLValidationTests.class,
        FileResourceTest.class,
        DatabaseResourceTest.class,
        AllInOneNameUtilsTest.class,
        DalClusterUtilsTest.class

})
public class AllTests {
}
