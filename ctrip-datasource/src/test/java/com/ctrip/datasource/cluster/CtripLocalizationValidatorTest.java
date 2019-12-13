package com.ctrip.datasource.cluster;

import com.ctrip.datasource.datasource.CtripLocalizationValidator;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import org.junit.Test;

public class CtripLocalizationValidatorTest {

    @Test
    public void testInit() {
        CtripLocalizationValidator validator = new CtripLocalizationValidator();
        validator.initialize(new LocalizationConfig() {
            @Override
            public int getUnitStrategyId() {
                return 100;
            }
        });
    }

}
