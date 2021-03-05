package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.datasource.log.OperationType;

/**
 * @author c7ch23en
 */
public interface LocalizationValidatable {

    boolean validate(OperationType operationType);

    ValidationStatus getLastValidationStatus();

    ValidationResult getLastValidationResult();

    enum ValidationStatus {
        OK,
        FAILED,
        SKIPPED,
        UNKNOWN
    }

}
