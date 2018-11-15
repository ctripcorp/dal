package com.ctrip.datasource.helper.DNS;

import com.ctrip.datasource.common.enums.ResolveStatus;
import com.ctrip.platform.dal.dao.helper.Action;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DNSInfo {
    private int ALWAYS_FAIL_CHECK_INTERVAL_IN_MINUTE = 3;
    private int LOG_PING_ERROR_INTERVAL_IN_MINUTE = 1;

    private Date lastSuccessTimestamp;

    private Date lastFailTimestamp;

    private Date lastLogErrorTimestamp;

    private ResolveStatus m_LastStatus;

    public void SetSuccessStatus() {
        m_LastStatus = ResolveStatus.Success;
        lastSuccessTimestamp = new Date();
    }

    public void SetFailStatus(Action action) {
        m_LastStatus = ResolveStatus.Fail;
        Date now = new Date();
        lastFailTimestamp = now;

        try {
            tryLogError(now, action);
        } catch (Throwable e) {
        }
    }

    private void tryLogError(Date now, Action action) throws Exception {
        Boolean need = needLogError(now);
        if (!need)
            return;

        lastLogErrorTimestamp = now;

        if (action == null)
            return;

        action.invoke();
    }

    private Boolean needLogError(Date now) {
        Boolean intervalReached = logErrorIntervalReached(now);
        if (!intervalReached)
            return false;

        Boolean meetSituation = meetLogErrorSituation(now);
        if (!meetSituation)
            return false;

        return true;
    }

    private Boolean logErrorIntervalReached(Date now) {
        Boolean result = false;
        long diffInMillis = now.getTime() - lastLogErrorTimestamp.getTime();
        long elapsedMinutes = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);

        if (elapsedMinutes >= LOG_PING_ERROR_INTERVAL_IN_MINUTE)
            return true;

        return result;
    }

    private Boolean meetLogErrorSituation(Date now) {
        if (isAlwaysFail(now))
            return true;

        return false;
    }

    private Boolean isAlwaysFail(Date now) {
        Boolean result = false;
        if (m_LastStatus == ResolveStatus.Success)
            return result;

        long diffInMillis = now.getTime() - lastSuccessTimestamp.getTime();
        long elapsedMinutes = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);

        if (elapsedMinutes >= ALWAYS_FAIL_CHECK_INTERVAL_IN_MINUTE)
            result = true;

        return result;
    }

}
