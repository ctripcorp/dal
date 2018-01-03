package com.ctrip.platform.dal.log;

public class LogEntry {
    public static final int INFO = 0;
    public static final int WARN = 1;
    public static final int ERROR = 2;
    public static final int ERROR2 = 3;

    public int type;
    public String msg;
    public Throwable e;
}