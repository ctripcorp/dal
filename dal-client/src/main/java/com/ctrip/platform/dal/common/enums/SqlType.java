package com.ctrip.platform.dal.common.enums;

public enum SqlType {

    SELECT(true, true, false, 0), //
    INSERT(false, false, true, 1), //
    UPDATE(false, false, true, 2), //
    DELETE(false, false, true, 3), //
    SELECT_FOR_UPDATE(false, true, true, 4), //
    REPLACE(false, false, true, 5), //
    TRUNCATE(false, false, true, 6), //
    CREATE(false, false, true, 7), //
    DROP(false, false, true, 8), //
    LOAD(false, false, true, 9), //
    MERGE(false, false, true, 10), //
    SHOW(true, true, false, 11), //
    EXECUTE(false, false, true, 12), //
    SELECT_FOR_IDENTITY(false, true, false, 13), //
    EXPLAIN(true, true, false, 14), //
    ALTER(false, false, true, 15), //
    UNKNOWN_SQL_TYPE(false, false, true, -100); //

    private boolean isRead;

    private boolean isQuery;

    private boolean isWrite;

    private int i;

    SqlType(boolean isRead, boolean isQuery, boolean isWrite, int i) {
        this.isRead = isRead;
        this.isQuery = isQuery;
        this.isWrite = isWrite;
        this.i = i;
    }

    public int value() {
        return this.i;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isQuery() {
        return isQuery;
    }

    public boolean isWrite() {
        return isWrite;
    }

    public static SqlType valueOf(int i) {
        for (SqlType t : values()) {
            if (t.value() == i) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid SqlType:" + i);
    }
}
