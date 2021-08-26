package com.ctrip.platform.dal.cluster.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class ObjectHolderTest {

    @Test
    public void testObjectHolder() {
        ObjectHolder<Object> holder = new ObjectHolder<>();

        Assert.assertNull(holder.get());
        Assert.assertNull(holder.getAndSet(new Object()));
        Assert.assertNotNull(holder.get());
        holder.set(null);
        Assert.assertNull(holder.getAndSet(null));

        Object obj1 = new Object();
        Object holdObj = holder.getOrCreate(() -> obj1);
        Assert.assertEquals(obj1, holdObj);

        Object obj2 = new Object();
        holdObj = holder.getOrCreate(() -> obj2);
        Assert.assertNotEquals(obj2, holdObj);
        Assert.assertEquals(obj1, holdObj);
    }

}
