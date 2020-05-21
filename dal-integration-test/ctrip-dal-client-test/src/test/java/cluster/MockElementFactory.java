package cluster;

import com.ctrip.datasource.datasource.CtripLocalizationValidatorFactory;
import com.ctrip.datasource.util.CtripDalElementFactory;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.ucs.client.api.RequestContext;
import com.ctrip.framework.ucs.client.api.StrategyValidatedResult;
import com.ctrip.framework.ucs.client.api.UcsClient;
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
    private LocalizationValidatorFactory factory;

    @Override
    public LocalizationValidatorFactory getLocalizationValidatorFactory() {
        return getFactory();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    protected DalPropertiesLocator getLocator() {
        if (locator == null)
            synchronized (this) {
                if (locator == null)
                    locator = new DefaultDalPropertiesLocator();
            }
        return locator;
    }

    private LocalizationValidatorFactory getFactory() {
        if (factory == null)
            synchronized (this) {
                if (factory == null)
                    factory = new MockLocalizationValidatorFactory(getLocator());
            }
        return factory;
    }

    static class MockLocalizationValidatorFactory extends CtripLocalizationValidatorFactory {

        public MockLocalizationValidatorFactory(DalPropertiesLocator locator) {
            super(new UcsClient() {
                @Override
                public RequestContext getCurrentRequestContext() {
                    return new RequestContext() {
                        @Override
                        public StrategyValidatedResult validate(int expectStrategyId) {
                            return StrategyValidatedResult.ShardBlock;
                        }
                    };
                }
            }, locator);
        }

        @Override
        public LocalizationValidator createValidator(ClusterInfo clusterInfo, LocalizationConfig localizationConfig) {
            return super.createValidator(clusterInfo, localizationConfig);
        }

    }

}
