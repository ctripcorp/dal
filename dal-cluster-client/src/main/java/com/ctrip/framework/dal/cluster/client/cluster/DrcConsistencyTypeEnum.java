package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;

public enum DrcConsistencyTypeEnum {
    HIGH_AVAILABILITY("com.ctrip.datasource.datasource.validator.HighAvailabilityUcsPostValidator"),
    STRONG_CONSISTENCY("com.ctrip.datasource.datasource.validator.StrongConsistencyUcsPostValidator"),
    READ_LATEST("com.ctrip.datasource.datasource.validator.ReadLatestUcsPostValidator"),
    STRONG_CONSISTENCY_CONTEXT_LOST_PASS("com.ctrip.datasource.datasource.validator.ContextLostIgnoreUcsPostValidator"),
    STRONG_CONSISTENCY_CONTEXT_ERROR_PASS("com.ctrip.datasource.datasource.validator.ContextErrorIgnoreUcsPostValidator"),
    STRONG_CONSISTENCY_CONTEXT_PASS("com.ctrip.datasource.datasource.validator.ContextIgnoreUcsPostValidator"),
    CUSTOMIZED("");

    private static final String NO_CONSISTENCY_TYPE_MATCH_ = "Dal does't support '%s' consistency type, check your spell";
    private String clazz;
    DrcConsistencyTypeEnum (String clazz) {
        this.clazz = clazz;
    }


    public static DrcConsistencyTypeEnum parse(String name) {
        for (DrcConsistencyTypeEnum drcConsistencyTypeEnum : DrcConsistencyTypeEnum.values()) {
            if (drcConsistencyTypeEnum.name().equalsIgnoreCase(name))
                return drcConsistencyTypeEnum;
        }
        throw new ClusterConfigException(String.format(NO_CONSISTENCY_TYPE_MATCH_, name));
    }

    public String getClazz() {
        return clazz;
    }
}
