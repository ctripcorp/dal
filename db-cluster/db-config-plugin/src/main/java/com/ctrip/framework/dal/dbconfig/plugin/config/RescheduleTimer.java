package com.ctrip.framework.dal.dbconfig.plugin.config;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shenjie on 2019/7/19.
 */
public class RescheduleTimer extends Timer {
    private Runnable task;
    private TimerTask timerTask;

    public void schedule(Runnable runnable, long delay, long period) {
        task = runnable;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };
        this.schedule(timerTask, delay, period);
    }

    public void reschedule(long delay, long period) {
        timerTask.cancel();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };
        this.schedule(timerTask, delay, period);
    }
}
