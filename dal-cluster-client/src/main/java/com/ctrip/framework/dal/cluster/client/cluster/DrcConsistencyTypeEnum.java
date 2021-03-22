package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;

public enum DrcConsistencyTypeEnum {
    HIGH_AVAILABILITY("com.ctrip.datasource.datasource.validator.HighAvailabilityUcsPostValidator", "high_availability"),
    STRONGLY_CONSISTENCY("com.ctrip.datasource.datasource.validator.StrongConsistencyUcsPostValidator", "strongly_consistency"),
    READ_LATEST("com.ctrip.datasource.datasource.validator.ReadLatestUcsPostValidator", "read_latest"),
    STRONG_CONSISTENCY_CONTEXT_LOST_PASS("com.ctrip.datasource.datasource.validator.ContextLostIgnoreUcsPostValidator", "context_lost_pass"),
    STRONG_CONSISTENCY_CONTEXT_ERROR_PASS("com.ctrip.datasource.datasource.validator.ContextErrorIgnoreUcsPostValidator", "context_error_pass"),
    STRONG_CONSISTENCY_CONTEXT_PASS("com.ctrip.datasource.datasource.validator.ContextIgnoreUcsPostValidator", "context_pass"),
    CUSTOMIZED("", "customized");

    private static final String NO_CONSISTENCY_TYPE_MATCH = "Dal does't support '%s' consistency type, check your spell";
    private String clazz;
    private String alias;
    DrcConsistencyTypeEnum (String clazz, String alias) {
        this.clazz = clazz;
        this.alias = alias;
    }


    public static DrcConsistencyTypeEnum parse(String name) {
        for (DrcConsistencyTypeEnum drcConsistencyTypeEnum : DrcConsistencyTypeEnum.values()) {
            if (drcConsistencyTypeEnum.alias.equalsIgnoreCase(name))
                return drcConsistencyTypeEnum;
        }
        throw new ClusterConfigException(String.format(NO_CONSISTENCY_TYPE_MATCH, name));
    }

    public String getClazz() {
        return clazz;
    }
}
