package com.ctrip.platform.dal.dao.helper;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class StringValueComparatorTest {

    @Test
    public void testCompare() {
        StringValueComparator<String> comparator = new StringValueComparator<>();
        Assert.assertTrue(comparator.compare(null, "123") < 0);
        Assert.assertTrue(comparator.compare("123", null) > 0);
        Assert.assertEquals(0, comparator.compare(null, null));
        Assert.assertEquals(0, comparator.compare("123", "123"));
        Assert.assertTrue(comparator.compare("1234", "123") > 0);
        Assert.assertTrue(comparator.compare("123", "321") < 0);
    }

}
