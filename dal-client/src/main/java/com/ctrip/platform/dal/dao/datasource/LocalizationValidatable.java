package com.ctrip.platform.dal.dao.datasource;

/**
 * @author c7ch23en
 */
public interface LocalizationValidatable {

    boolean validate();

    ValidationStatus getLastValidationStatus();

    ValidationResult getLastValidationResult();

    enum ValidationStatus {
        OK,
        FAILED,
        SKIPPED,
        UNKNOWN
    }

}
