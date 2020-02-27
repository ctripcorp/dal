package cluster;

import com.ctrip.datasource.datasource.CtripLocalizationValidatorFactory;
import com.ctrip.datasource.util.CtripDalElementFactory;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.ucs.client.api.StrategyValidatedResult;
import com.ctrip.framework.ucs.client.api.Ucs;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DefaultDalPropertiesLocator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidatorFactory;

/**
 * @author c7ch23en
 */
public class MockElementFactory extends CtripDalElementFactory {

    private static DalPropertiesLocator locator;
    private MockLocalizationValidatorFactory factory;

    public MockElementFactory() {
        locator = new DefaultDalPropertiesLocator();
        factory = new MockLocalizationValidatorFactory(locator);
    }

    @Override
    public LocalizationValidatorFactory getLocalizationValidatorFactory() {
        return factory;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    protected DalPropertiesLocator getLocator() {
        return locator;
    }

    static class MockLocalizationValidatorFactory extends CtripLocalizationValidatorFactory {

        public MockLocalizationValidatorFactory(DalPropertiesLocator locator) {
            super(new Ucs() {
                @Override
                public StrategyValidatedResult validateStrategyContext(int expectStrategyId) {
                    return StrategyValidatedResult.ShardBlock;
                }
            }, locator);
        }

        @Override
        public LocalizationValidator createValidator(ClusterInfo clusterInfo, LocalizationConfig localizationConfig) {
            return super.createValidator(clusterInfo, localizationConfig);
        }

    }

}
