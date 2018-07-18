package com.ctrip.platform.dal.daogen.sql.validate;

public class ValidateResult {
    private boolean passed;
    private String sql;
    private int affectRows;
    private StringBuffer msg = new StringBuffer();

    public ValidateResult(String sql) {
        this.sql = sql;
    }

    public boolean isPassed() {
        return passed;
    }

    public ValidateResult setPassed(boolean passed) {
        this.passed = passed;
        return this;
    }

    public String getMessage() {
        return msg.toString();
    }

    public String getSQL() {
        return this.sql;
    }

    public int getAffectRows() {
        return affectRows;
    }

    public void setAffectRows(int affectRows) {
        this.affectRows = affectRows;
    }

    public ValidateResult append(String msg) {
        this.msg.append(msg);
        return this;
    }

    public ValidateResult appendFormat(String format, Object... args) {
        this.msg.append(String.format(format, args));
        return this;
    }

    public ValidateResult appendLineFormat(String format, Object... args) {
        this.msg.append(String.format(format, args)).append(System.lineSeparator());
        return this;
    }

    public ValidateResult clearAppend(String msg) {
        this.msg = new StringBuffer();
        this.msg.append(msg);
        return this;
    }

    @Override
    public String toString() {
        return String.format("[Passed: %s, Message: %s]", this.passed, this.msg.toString());
    }
}
