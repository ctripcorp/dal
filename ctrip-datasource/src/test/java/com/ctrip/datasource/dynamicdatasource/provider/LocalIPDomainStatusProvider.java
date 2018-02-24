package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.platform.dal.dao.datasource.IPDomainStatusChanged;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalIPDomainStatusProvider extends AbstractIPDomainStatusProvider {
    private AtomicBoolean atomicStatus = new AtomicBoolean(true);
    private ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
    private long delay = 5 * 1000;

    @Override
    public void addIPDomainStatusChangedListener(final IPDomainStatusChanged callback) {
        if (callback == null)
            return;

        // emulate dynamic switching
        service.schedule(new IPDomainStatusSwitchTask(callback), delay, TimeUnit.MILLISECONDS);
    }

    class IPDomainStatusSwitchTask implements Runnable {
        private IPDomainStatusChanged callback;

        public IPDomainStatusSwitchTask(IPDomainStatusChanged callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            boolean value = atomicStatus.get();
            value = !value;
            atomicStatus.set(value);

            IPDomainStatus status = value ? IPDomainStatus.IP : IPDomainStatus.Domain;
            callback.onChanged(status);
        }
    }

}
