package com.ctrip.platform.dal.dao.configure.dalproperties;

public class SqlServerDalPropertiesLocator extends AbstractDalPropertiesLocator {
    private static final String JAVA_SQL_SERVER_ERROR_CODES = "JavaSqlServerErrorCodes";

    /*
     * Sql State:"08S01","08006"; Error Codes:"3906";
     */
    private static final String[] SQL_SERVER_DEFAULT_ERROR_CODES = new String[] {"08S01", "08006", "3906"};

    @Override
    protected String getErrorCodesKey() {
        return JAVA_SQL_SERVER_ERROR_CODES;
    }

    @Override
    protected String[] getDefaultErrorCodes() {
        int length = SQL_SERVER_DEFAULT_ERROR_CODES.length;
        String[] array = new String[length];
        for (int i = 0; i < length; i++) {
            array[i] = SQL_SERVER_DEFAULT_ERROR_CODES[i];
        }

        return array;
    }

}
