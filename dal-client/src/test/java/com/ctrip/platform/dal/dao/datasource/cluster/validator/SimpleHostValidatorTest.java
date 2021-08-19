package com.ctrip.platform.dal.dao.datasource.cluster.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Author limingdong
 * @create 2021/8/19
 */
public class SimpleHostValidatorTest extends AbstractHostValidatorTest {

    private SimpleHostValidator simpleHostValidator;

    @Before
    public void setUp() throws Exception {
        blackListTimeOut = 20;
        simpleHostValidator = new SimpleHostValidator(buildConnectionFactory(), configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
        simpleHostValidator.ONE_SECOND = 5l;  // for test validate
    }

    @Test
    public void validate() throws InterruptedException {
        Assert.assertTrue(simpleHostValidator.validate(null));
        Assert.assertTrue(simpleHostValidator.available(hostSpec1));

        simpleHostValidator.addToBlackAndRemoveFromPre(hostSpec1);
        Assert.assertFalse(simpleHostValidator.available(hostSpec1));

        TimeUnit.MILLISECONDS.sleep(6); // big than 5l to make shouldValidate return true
        simpleHostValidator.triggerValidate();
        TimeUnit.MILLISECONDS.sleep(10); // 10 + 5 < 20, so not due to blackListTimeOut

        Assert.assertTrue(simpleHostValidator.available(hostSpec1));

        // test blackListTimeOut
        simpleHostValidator.addToBlackAndRemoveFromPre(hostSpec1);
        TimeUnit.MILLISECONDS.sleep(blackListTimeOut / 2);
        Assert.assertFalse(simpleHostValidator.available(hostSpec1));
        TimeUnit.MILLISECONDS.sleep(blackListTimeOut / 2 + 1);
        Assert.assertTrue(simpleHostValidator.available(hostSpec1));

        simpleHostValidator.destroy();
    }
}