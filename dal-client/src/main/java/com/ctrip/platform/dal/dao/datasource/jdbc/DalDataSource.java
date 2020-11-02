package com.ctrip.platform.dal.dao.datasource.jdbc;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.datasource.cluster.DataSourceDelegate;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author c7ch23en
 */
public abstract class DalDataSource extends DataSourceDelegate {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    public static final int CONTINUOUS_ERROR_DURATION_THRESHOLD = 60 * 1000;  // ms
    public static final int CONTINUOUS_ERROR_REPORT_PERIOD = 30 * 1000;  // ms

    private final AtomicLong firstAppearContinuousErrorTimeAtom = new AtomicLong(0);
    private final AtomicLong continuousErrorCountAtom = new AtomicLong(0);
    private final AtomicLong lastReportContinuousErrorTimeAtom = new AtomicLong(0);

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        return new DalConnection(connection, this, createSqlContext());
    }

    protected abstract SqlContext createSqlContext();

    public abstract DatabaseCategory getDatabaseCategory();

    public void handleException(SQLException e, boolean isUpdateOperation, Connection connection) {
        if (e != null) {
            long nowTime = System.currentTimeMillis();
            long firstAppear;
            long continuousErrorCount;
            synchronized (firstAppearContinuousErrorTimeAtom) {
                firstAppear = firstAppearContinuousErrorTimeAtom.get();
                firstAppearContinuousErrorTimeAtom.compareAndSet(0, nowTime);
                continuousErrorCount = continuousErrorCountAtom.incrementAndGet();
            }
            if (firstAppear > 0 && nowTime - firstAppear >= CONTINUOUS_ERROR_DURATION_THRESHOLD &&
                    needToReport(nowTime, continuousErrorCount)) {
                LOGGER.reportError(getDataSourceName());
            }
        } else if (isUpdateOperation) {
            synchronized (firstAppearContinuousErrorTimeAtom) {
                continuousErrorCountAtom.set(0);
                firstAppearContinuousErrorTimeAtom.set(0);
            }
        }
    }

    private boolean needToReport(long nowTime, long continuousErrorCount) {
        synchronized (lastReportContinuousErrorTimeAtom) {
            long lastReport = lastReportContinuousErrorTimeAtom.get();
            if ((lastReport == 0 || nowTime - lastReport >= CONTINUOUS_ERROR_REPORT_PERIOD) &&
                    continuousErrorCount >= 3) {
                lastReportContinuousErrorTimeAtom.set(nowTime);
                return true;
            }
            return false;
        }
    }

    protected abstract String getDataSourceName();

    public long getFirstAppearContinuousErrorTime() {
        return firstAppearContinuousErrorTimeAtom.get();
    }

    public long getLastReportContinuousErrorTime() {
        return lastReportContinuousErrorTimeAtom.get();
    }

    public long getContinuousErrorCount() {
        return continuousErrorCountAtom.get();
    }

}
