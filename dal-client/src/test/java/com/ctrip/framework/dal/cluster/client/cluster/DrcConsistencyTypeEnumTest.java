package com.ctrip.framework.dal.cluster.client.cluster;

import org.junit.Assert;
import org.junit.Test;

import static com.ctrip.framework.dal.cluster.client.cluster.DrcConsistencyTypeEnum.HIGH_AVAILABILITY;

public class DrcConsistencyTypeEnumTest {

    @Test
    public void parse() {
        Assert.assertEquals(HIGH_AVAILABILITY.name(), DrcConsistencyTypeEnum.parse("high_availability").name());
        try {
            DrcConsistencyTypeEnum.parse("test");
        } catch (Exception e) {
        Assert.assertEquals("Dal does't support 'test' consistency type, check your spell", e.getMessage());
        }
    }
}