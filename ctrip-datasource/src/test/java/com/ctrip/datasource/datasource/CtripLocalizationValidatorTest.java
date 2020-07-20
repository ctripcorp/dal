package com.ctrip.datasource.datasource;

import com.ctrip.datasource.util.CtripEnvUtils;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.LocalizationState;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.ucs.client.api.RequestContext;
import com.ctrip.framework.ucs.client.api.StrategyValidatedResult;
import com.ctrip.framework.ucs.client.api.UcsClient;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DefaultDalPropertiesLocator;
import com.ctrip.platform.dal.dao.datasource.ValidationResult;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class CtripLocalizationValidatorTest {

    private static final CtripEnvUtils ENV_UTILS = (CtripEnvUtils) DalElementFactory.DEFAULT.getEnvUtils();

    @Before
    public void before() {
        ENV_UTILS.setZone("testAppZone");
    }

    @After
    public void after() {
        ENV_UTILS.setZone(null);
    }

    @Test
    public void testActiveLocalizationValidation() {
        CtripLocalizationValidator validator = new CtripLocalizationValidator(mockUcsClient(),
                mockDalPropertiesLocator(), mockClusterInfo(), mockLocalizationConfig(LocalizationState.ACTIVE));
        ValidationResult result = validator.validateRequest();
        Assert.assertFalse(result.getValidationResult());
        Assert.assertEquals(StrategyValidatedResult.ShardBlock.name(), result.getUcsValidationMessage());
        Assert.assertEquals(CtripLocalizationValidator.DAL_VALIDATE_REJECT, result.getDalValidationMessage());
        Assert.assertFalse(validator.validateZone());
    }

    @Test
    public void testPreparedLocalizationValidation() {
        CtripLocalizationValidator validator = new CtripLocalizationValidator(mockUcsClient(),
                mockDalPropertiesLocator(), mockClusterInfo(), mockLocalizationConfig(LocalizationState.PREPARED));
        ValidationResult result = validator.validateRequest();
        Assert.assertTrue(result.getValidationResult());
        Assert.assertEquals(StrategyValidatedResult.ShardBlock.name(), result.getUcsValidationMessage());
        Assert.assertEquals(CtripLocalizationValidator.DAL_VALIDATE_WARN, result.getDalValidationMessage());
        Assert.assertTrue(validator.validateZone());
    }

    private UcsClient mockUcsClient() {
        return new UcsClient() {
            @Override
            public RequestContext getCurrentRequestContext() {
                return new RequestContext() {
                    @Override
                    public StrategyValidatedResult validate(int expectStrategyId) {
                        return StrategyValidatedResult.ShardBlock;
                    }
                };
            }

            @Override
            public void logRequestContextKey() {}
        };
    }

    private DalPropertiesLocator mockDalPropertiesLocator() {
        return new DefaultDalPropertiesLocator() {
            @Override
            public boolean localizedForDrc(String situation) {
                return true;
            }
        };
    }

    private ClusterInfo mockClusterInfo() {
        return new ClusterInfo("mockcluster", 1, DatabaseRole.MASTER, true);
    }

    private LocalizationConfig mockLocalizationConfig(LocalizationState localizationState) {
        return new LocalizationConfigImpl(1, "testConfigZone", localizationState);
    }

}
