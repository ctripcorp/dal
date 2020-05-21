package com.ctrip.datasource.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.framework.ucs.client.api.RequestContext;
import com.ctrip.framework.ucs.client.api.StrategyValidatedResult;
import com.ctrip.framework.ucs.client.api.UcsClient;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;

public class CtripLocalizationValidator implements LocalizationValidator {

    private static final String UCS_VALIDATE_LOG_TYPE = "DAL.drc.ucs.validate";
    private static final String DAL_VALIDATE_LOG_TYPE = "DAL.drc.validate";
    private static final String PATTERN_DRC_VALIDATE_LOG_NAME = "%s-%s:%s";
    private static final String PATTERN_ZONE_MISMATCH_LOG_NAME = "ZoneMismatch:%s";
    private static final String PATTERN_ZONE_UNDEFINED_LOG_NAME = "ZoneUndefined:%s";

    private static final String DAL_VALIDATE_PASS = "PASS";
    private static final String DAL_VALIDATE_WARN = "WARN";
    private static final String DAL_VALIDATE_REJECT = "REJECT";

    private UcsClient ucsClient;
    private DalPropertiesLocator locator;
    private ClusterInfo clusterInfo;
    private LocalizationConfig config;

    public CtripLocalizationValidator(UcsClient ucsClient, DalPropertiesLocator locator, ClusterInfo clusterInfo, LocalizationConfig config) {
        this.ucsClient = ucsClient;
        this.locator = locator;
        this.clusterInfo = clusterInfo;
        this.config = config;
    }

    @Override
    public boolean validateRequest() {
        RequestContext context = ucsClient.getCurrentRequestContext();
        StrategyValidatedResult result = null;
        try {
            result = context.validate(config.getUnitStrategyId());
            Cat.logEvent(UCS_VALIDATE_LOG_TYPE, buildUcsValidateLogName(result), Event.SUCCESS, buildUcsValidateLogMessage(result));
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

    @Override
    public boolean validateZone() {
        try {
            if (config != null) {
                String configZone = config.getZoneId();
                String appZone = Foundation.server().getZone();
                if (!StringUtils.isEmpty(configZone) && !StringUtils.isEmpty(appZone)) {
                    if (configZone.equalsIgnoreCase(appZone))
                        return true;
                    else {
                        Cat.logEvent(DalLogTypes.DAL_VALIDATION, buildZoneMismatchLogName(), Event.SUCCESS,
                                String.format("shard: %d, configZone: %s, appZone: %s", clusterInfo.getShardIndex(), configZone, appZone));
                        return false;
                    }
                }
                Cat.logEvent(DalLogTypes.DAL_VALIDATION, buildZoneUndefinedLogName(), Event.SUCCESS,
                        String.format("shard: %d, configZone: %s, appZone: %s", clusterInfo.getShardIndex(), configZone, appZone));
                return true;
            }
        } catch (Throwable t) {
            // ignore
        }
        Cat.logEvent(DalLogTypes.DAL_VALIDATION, buildZoneUndefinedLogName(), Event.SUCCESS,
                String.format("shard: %d", clusterInfo.getShardIndex()));
        return true;
    }

    @Override
    public LocalizationConfig getLocalizationConfig() {
        return config;
    }

    private String buildUcsValidateLogName(StrategyValidatedResult result) {
        return buildValidateLogName(result != null ? result.name() : "NoResult");
    }

    private String buildUcsValidateLogMessage(StrategyValidatedResult result) {
        return result != null ? "shouldProcessDBOperation: " + result.shouldProcessDBOperation() : "";
    }

    private String buildDalValidateLogName(boolean result) {
        return buildValidateLogName(result ? DAL_VALIDATE_PASS : DAL_VALIDATE_REJECT);
    }

    private String buildValidateLogName(String result) {
        return String.format(PATTERN_DRC_VALIDATE_LOG_NAME, clusterInfo.getClusterName(), clusterInfo.getShardIndex(), result);
    }

    private String buildZoneMismatchLogName() {
        return String.format(PATTERN_ZONE_MISMATCH_LOG_NAME, clusterInfo.getClusterName());
    }

    private String buildZoneUndefinedLogName() {
        return String.format(PATTERN_ZONE_UNDEFINED_LOG_NAME, clusterInfo.getClusterName());
    }

}
