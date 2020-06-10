package com.ctrip.platform.dal.dao.datasource;

/**
 * @author c7ch23en
 */
public class ValidationResult {

    private final boolean result;
    private final String ucsMessage;
    private final String dalMessage;

    public ValidationResult(boolean result, String ucsMessage, String dalMessage) {
        this.result = result;
        this.ucsMessage = ucsMessage;
        this.dalMessage = dalMessage;
    }

    public boolean getValidationResult() {
        return result;
    }

    public String getUcsValidationMessage() {
        return ucsMessage;
    }

    public String getDalValidationMessage() {
        return dalMessage;
    }

}
