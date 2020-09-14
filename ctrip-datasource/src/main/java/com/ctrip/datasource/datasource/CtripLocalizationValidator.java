package com.ctrip.datasource.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.dal.cluster.client.config.LocalizationState;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.framework.ucs.client.api.RequestContext;
import com.ctrip.framework.ucs.client.api.StrategyValidatedResult;
import com.ctrip.framework.ucs.client.api.UcsClient;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidator;
import com.ctrip.platform.dal.dao.datasource.ValidationResult;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;

public class CtripLocalizationValidator implements LocalizationValidator {

    private static final String UCS_VALIDATE_LOG_TYPE = "DAL.drc.ucs.validate";
    private static final String DAL_VALIDATE_LOG_TYPE = "DAL.drc.validate";
    private static final String PATTERN_DRC_VALIDATE_LOG_NAME = "%s-%s:%s";
    private static final String PATTERN_ZONE_MISMATCH_LOG_NAME = "ZoneMismatch:%s";
    private static final String PATTERN_ZONE_UNDEFINED_LOG_NAME = "ZoneUndefined:%s";

    protected static final String DAL_VALIDATE_PASS = "PASS";
    protected static final String DAL_VALIDATE_WARN = "WARN";
    protected static final String DAL_VALIDATE_REJECT = "REJECT";

    private static final EnvUtils ENV_UTILS = DalElementFactory.DEFAULT.getEnvUtils();

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
    public ValidationResult validateRequest(boolean isUpdateOperation) {
        boolean bResult;
        String ucsMsg = null;
        String dalMsg = null;
        RequestContext context = ucsClient.getCurrentRequestContext();
        StrategyValidatedResult result = null;
        try {
            result = context.validate(config.getUnitStrategyId());
            if (result != null)
                ucsMsg = result.name();
            Cat.logEvent(UCS_VALIDATE_LOG_TYPE, buildUcsValidateLogName(result), Event.SUCCESS, buildUcsValidateLogMessage(result));
        } catch (Throwable t) {
            Cat.logEvent(UCS_VALIDATE_LOG_TYPE, buildValidateLogName("EXCEPTION"), Event.SUCCESS, t.getMessage());
        }

        if (result != null && !result.shouldProcessDBOperation()) {
            try {
                if (config.getLocalizationState() == LocalizationState.ACTIVE && locator.localizedForDrc(result.name(), isUpdateOperation)) {
                    Cat.logEvent(DAL_VALIDATE_LOG_TYPE, buildDalValidateLogName(false), DAL_VALIDATE_REJECT, "");
                    bResult = false;
                    dalMsg = DAL_VALIDATE_REJECT;
                } else {
                    Cat.logEvent(DAL_VALIDATE_LOG_TYPE, buildValidateLogName(DAL_VALIDATE_WARN));
                    bResult = true;
                    dalMsg = DAL_VALIDATE_WARN;
                }
            } catch (Throwable t) {
                Cat.logEvent(DAL_VALIDATE_LOG_TYPE, buildDalValidateLogName(true), Event.SUCCESS, t.getMessage());
                bResult = true;
                dalMsg = DAL_VALIDATE_PASS;
            }
        } else {
            Cat.logEvent(DAL_VALIDATE_LOG_TYPE, buildDalValidateLogName(true));
            bResult = true;
            dalMsg = DAL_VALIDATE_PASS;
        }
        return new ValidationResult(bResult, ucsMsg, dalMsg);
    }

    @Override
    public boolean validateZone() {
        try {
            if (config.getLocalizationState() == LocalizationState.ACTIVE) {
                String configZone = config.getZoneId();
                String appZone = ENV_UTILS.getZone();
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
            }
            return true;
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
