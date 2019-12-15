package com.ctrip.datasource.cluster;

import com.ctrip.datasource.datasource.CtripLocalizationValidator;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.ucs.client.api.UcsClient;
import org.junit.Test;

public class CtripLocalizationValidatorTest {

    @Test
    public void testInit() {
        CtripLocalizationValidator validator = new CtripLocalizationValidator(UcsClient.getInstance(),
                new LocalizationConfig() {
            @Override
            public int getUnitStrategyId() {
                return 1;
            }
        });
    }

}
