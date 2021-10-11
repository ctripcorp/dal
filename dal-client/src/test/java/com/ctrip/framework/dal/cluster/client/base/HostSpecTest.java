package com.ctrip.framework.dal.cluster.client.base;

import com.ctrip.framework.dal.cluster.client.exception.DalMetadataException;
import org.junit.Test;

import static org.junit.Assert.*;

public class HostSpecTest {

    @Test
    public void getTrimLowerCaseZone() {

        HostSpec hostSpec = HostSpec.of("ip0", 0, " shaoy");
        assertEquals("shaoy", hostSpec.getTrimLowerCaseZone());

        hostSpec = HostSpec.of("ip0", 0, " shaoy ");
        assertEquals("shaoy", hostSpec.getTrimLowerCaseZone());

        hostSpec = HostSpec.of("ip0", 0, "   ");
        try {
            hostSpec.getTrimLowerCaseZone();
            fail();
        } catch (Throwable e) {
            assertEquals(true, e instanceof DalMetadataException);
            assertEquals(String.format(" of %s zone msg lost", hostSpec.toString()), e.getMessage());
        }
    }
}