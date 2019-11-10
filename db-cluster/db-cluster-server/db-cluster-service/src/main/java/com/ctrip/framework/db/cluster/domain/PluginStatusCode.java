package com.ctrip.framework.db.cluster.domain;

/**
 * Created by shenjie on 2019/5/14.
 */
public class PluginStatusCode {
    public static final int OK = 0;
    public static final int FILE_NOT_FOUND = 1;
    public static final int ILLEGAL_PARAMS = 2;
    public static final int FORBIDDEN = 3;
    public static final int CONCURRENT_LIMIT = -1;
    public static final int NOT_DEFINED = 99;
    public static final int TITAN_KEY_DISABLE = 100;
    public static final int TITAN_KEY_CANNOT_READ = 101;
    public static final int TITAN_KEY_CANNOT_WRITE = 102;
    public static final int TITAN_ILLEGAL_REQUEST = 103;
    public static final int TITAN_NOT_DEFINED = 199;

    public PluginStatusCode() {
    }
}
