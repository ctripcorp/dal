package com.ctrip.platform.dal.dao.helper;

import org.junit.Assert;
import org.junit.Test;

/**
 * @Author limingdong
 * @create 2021/11/25
 */
public class OrderedComparatorTest {

    private OrderedComparator orderedComparator = new OrderedComparator();

    @Test
    public void compare() {
        Ordered ordered1 = () -> 0;
        Ordered ordered2 = () -> 1;
        Assert.assertEquals(-1, orderedComparator.compare(ordered1, ordered2));

        PriorityOrdered ordered3 = () -> 0;
        PriorityOrdered ordered4 = () -> 1;
        Assert.assertEquals(-1, orderedComparator.compare(ordered3, ordered4));

        Assert.assertEquals(1, orderedComparator.compare(ordered1, ordered3));
    }
}