package com.ctrip.platform.dal.dao.configure.dalproperties;

import static com.ctrip.platform.dal.dao.configure.dalproperties.DefaultDalPropertiesLocator.STATEMENT_INTERCEPTORS;

public class MySqlDalPropertiesLocator extends AbstractDalPropertiesLocator {
    private static final String JAVA_MYSQL_ERROR_CODES = "JavaMySqlErrorCodes";

    /*
     * Sql State:"08S01"
     */
    private static final String[] MYSQL_DEFAULT_ERROR_CODES = new String[] {"08S01"};

    @Override
    protected String getErrorCodesKey() {
        return JAVA_MYSQL_ERROR_CODES;
    }

    @Override
    protected String[] getDefaultErrorCodes() {
        int length = MYSQL_DEFAULT_ERROR_CODES.length;
        String[] array = new String[length];
        for (int i = 0; i < length; i++) {
            array[i] = MYSQL_DEFAULT_ERROR_CODES[i];
        }

        return array;
    }
}
