package com.ctrip.datasource.configure.qconfig;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.datasource.datasource.IPDomainStatusChanged;
import com.ctrip.datasource.datasource.IPDomainStatusProvider;
import qunar.tc.qconfig.client.staticswitch.StaticSwitcher;
import qunar.tc.qconfig.client.staticswitch.StatusChangeListener;

public class IPDomainStatusProviderImpl implements IPDomainStatusProvider {
    private static final String SWITCH_KEYNAME = "dal_ip_status";
    private StaticSwitcher statusSwitch = StaticSwitcher.getSwitch(SWITCH_KEYNAME);

    @Override
    public IPDomainStatus getStatus() {
        return _getStatus(statusSwitch.status());
    }

    @Override
    public void addIPDomainStatusChangedListener(final IPDomainStatusChanged callback) {
        statusSwitch.addChangeListener(new StatusChangeListener() {
            @Override
            public void changed(boolean value) {
                IPDomainStatus status = _getStatus(value);
                callback.onChanged(status);
            }
        });
    }

    private IPDomainStatus _getStatus(boolean value) {
        return value ? IPDomainStatus.IP : IPDomainStatus.Domain;
    }

}
