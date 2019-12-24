package com.ctrip.datasource.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.ucs.client.api.StrategyValidatedResult;
import com.ctrip.framework.ucs.client.api.Ucs;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;

public class CtripLocalizationValidator implements LocalizationValidator {

    private static final String UCS_VALIDATE_LOG_TYPE = "DAL.drc.ucs.validate";
    private static final String DAL_VALIDATE_LOG_TYPE = "DAL.drc.validate";
    private static final String PATTERN_DRC_VALIDATE_LOG_NAME = "%s-%s:%s";

    private static final String DAL_VALIDATE_PASS = "PASS";
    private static final String DAL_VALIDATE_WARN = "WARN";
    private static final String DAL_VALIDATE_REJECT = "REJECT";

    private Ucs ucs;
    private DalPropertiesLocator locator;
    private ClusterInfo clusterInfo;
    private LocalizationConfig config;

    public CtripLocalizationValidator(Ucs ucs, DalPropertiesLocator locator, ClusterInfo clusterInfo, LocalizationConfig config) {
        this.ucs = ucs;
        this.locator = locator;
        this.clusterInfo = clusterInfo;
        this.config = config;
    }

    @Override
    public boolean validate() {
        StrategyValidatedResult result = null;
        try {
            result = ucs.validateStrategyContext(config.getUnitStrategyId());
            Cat.logEvent(UCS_VALIDATE_LOG_TYPE, buildUcsValidateLogName(result));
        } catch (Throwable t) {
            Cat.logEvent(UCS_VALIDATE_LOG_TYPE, buildValidateLogName("EXCEPTION"), Event.SUCCESS, t.getMessage());
        }

        if (result != null && !result.shouldProcessDBOperation()) {
            try {
                boolean localized = locator.localizedForDrc(result.name());
                if (localized) {
                    Cat.logEvent(DAL_VALIDATE_LOG_TYPE, buildDalValidateLogName(false), DAL_VALIDATE_REJECT, "");
                    return false;
                } else {
                    Cat.logEvent(DAL_VALIDATE_LOG_TYPE, buildValidateLogName(DAL_VALIDATE_WARN));
                    return true;
                }
            } catch (Throwable t) {
                Cat.logEvent(DAL_VALIDATE_LOG_TYPE, buildDalValidateLogName(true), Event.SUCCESS, t.getMessage());
                return true;
            }
        } else {
            Cat.logEvent(DAL_VALIDATE_LOG_TYPE, buildDalValidateLogName(true));
            return true;
        }
    }

    private String buildUcsValidateLogName(StrategyValidatedResult result) {
        return buildValidateLogName(result != null ? result.name() + ":" + result.shouldProcessDBOperation() : "NoResult");
    }

    private String buildDalValidateLogName(boolean result) {
        return buildValidateLogName(result ? DAL_VALIDATE_PASS : DAL_VALIDATE_REJECT);
    }

    private String buildValidateLogName(String result) {
        return String.format(PATTERN_DRC_VALIDATE_LOG_NAME, clusterInfo.getClusterName(), clusterInfo.getShardIndex(), result);
    }

}
