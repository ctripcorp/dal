package com.ctrip.platform.dal.dao.datasource.read;

public class PrimaryVisitedManager {


    private static final ThreadLocal<Boolean> PRIMARY_VISITED = ThreadLocal.withInitial(() -> false);

    /**
     * Judge primary data source visited in current thread.
     *
     * @return primary data source visited or not in current thread
     */
    public static boolean getPrimaryVisited() {
        return PRIMARY_VISITED.get();
    }

    /**
     * Set primary data source visited in current thread.
     */
    public static void setPrimaryVisited() {
        PRIMARY_VISITED.set(true);
    }

    /**
     * Clear primary data source visited.
     */
    public static void clear() {
        PRIMARY_VISITED.remove();
    }
}
