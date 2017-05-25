package com.ctrip.platform.dal.dao;

public class CallSpByIndexValidator {
    public static void validate(DalParser<?> parser, boolean callSpbyName) {
        if(callSpbyName)
            return;

        String outputIdName = parser.isAutoIncrement() ? parser.getPrimaryKeyNames()[0] : null;
        
        // Is this an exceptional case??? Shall we report error?
        if(outputIdName == null)
            return;
        
        // User enabled call SP by column Index
        int outputIdIndex = 0;
        int index = 0;
        for(String name: parser.getColumnNames()) {
            if(name.equals(outputIdName)) {
                outputIdIndex = index;
                break;
            }
            index++;
        }
        
        /**
         * For some old table, the primary key is not the first, and the parameters' sequence is not the same with table columns.
         * To be safe, call Sp by index is not recommended in this case. 
         */
        if(outputIdIndex != 0)
            throw new RuntimeException(String.format("Cannot call SP by index because the primary key %s is not the first column in table %s", outputIdName, parser.getTableName()));
    }
}
