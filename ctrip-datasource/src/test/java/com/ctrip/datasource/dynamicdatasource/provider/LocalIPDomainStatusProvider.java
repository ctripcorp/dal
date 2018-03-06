package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.datasource.datasource.IPDomainStatusChanged;

import java.util.concurrent.atomic.AtomicBoolean;

public class LocalIPDomainStatusProvider extends AbstractIPDomainStatusProvider {
    private AtomicBoolean atomicStatus = new AtomicBoolean(true);
    private IPDomainStatusChanged callback;

    public void triggerIPDomainStatusChanged() {
        boolean value = atomicStatus.get();
        value = !value;
        atomicStatus.set(value);

        IPDomainStatus status = value ? IPDomainStatus.IP : IPDomainStatus.Domain;
        callback.onChanged(status);
    }

    @Override
    public void addIPDomainStatusChangedListener(final IPDomainStatusChanged callback) {
        if (callback == null)
            return;

        this.callback = callback;
    }

}
