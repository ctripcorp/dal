package com.ctrip.platform.dal.dao.strategy;

public class LocalContextReadWriteStrategy {

    private static InheritableThreadLocal<Boolean> forceMaster = new InheritableThreadLocal<Boolean>();

    public static boolean getReadFromMaster() {
        Boolean shouldReadFromMaster = forceMaster.get();

        return shouldReadFromMaster != null && shouldReadFromMaster;
    }

    public static void setReadFromMaster() {
        forceMaster.set(true);
    }

    public static void clearContext() {
        forceMaster.remove();
    }
}
