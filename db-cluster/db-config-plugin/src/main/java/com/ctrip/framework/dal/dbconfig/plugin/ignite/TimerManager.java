package com.ctrip.framework.dal.dbconfig.plugin.ignite;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.service.validator.AppIdIpCheckCache;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lzyan on 2018/11/5.
 */
public class TimerManager {
    private static Logger logger = LoggerFactory.getLogger(TimerManager.class);

    private static class TimerManagerSingletonHolder {
        private static final TimerManager instance = new TimerManager();
    }

    private List<Timer> timerList = null;


    //========= Private Constructor =========
    private TimerManager() {
        timerList = Lists.newArrayList();
    }

    //Expose method
    public static TimerManager getInstance() {
        return TimerManagerSingletonHolder.instance;
    }

    //getter


    //Register appId-Ip cache timer
    public boolean registerAppIdIpCacheTimer() {
        logger.info("registerAppIdIpCacheTimer(): register appId-Ip cache timer ...");
        boolean isSuccess = true;
        try {
            final AppIdIpCheckCache appIdIpCheckCache = AppIdIpCheckCache.getInstance();

            //prewarm, first time try/catch exception to avoid effect qconfig ignite
            try {
                logger.info("registerAppIdIpCacheTimer(): init and preWarm cache first time");
                appIdIpCheckCache.reBuildNormalCache();
            } catch (Exception ex) {
                logger.error("registerAppIdIpCacheTimer(): prewarm cache error!", ex);
            }

            //start schedule task
            long periodMs = 60 * 60000L;    // Default: 1 hour
            String cacheNormalRefreshIntervalMin = PluginIgniteConfig.getInstance().getIgniteParamValue(CommonConstants.CACHE_NORMAL_REFRESH_INTERVAL_MIN);
            if (!Strings.isNullOrEmpty(cacheNormalRefreshIntervalMin)) {
                periodMs = Long.parseLong(cacheNormalRefreshIntervalMin) * 60000L;
            }
            logger.info("registerAppIdIpCacheTimer(): periodMs=" + periodMs);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        appIdIpCheckCache.reBuildNormalCache();
                    } catch (Exception e) {
                        logger.error("registerAppIdIpCacheTimer() -> run(): appId-Ip cache refresh error!", e);
                    }
                }
            }, periodMs, periodMs);
            timerList.add(timer);
        } catch (Exception e) {
            logger.error("registerAppIdIpCacheTimer(): start timer to refresh cache error!", e);
            isSuccess = false;
        }
        return isSuccess;
    }

    //release resource
    public void release() {
        logger.info("release(): release resource, close all timer ...");
        //Close all registered timer
        if (timerList != null) {
            for (Timer timer : timerList) {
                if (timer != null) {
                    timer.cancel();
                }
            }
        }
    }


}
