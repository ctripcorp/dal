package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.datasource.IPDomainStatusChanged;
import com.ctrip.platform.dal.dao.datasource.IPDomainStatusProvider;
import qunar.tc.qconfig.client.staticswitch.StaticSwitch;
import qunar.tc.qconfig.client.staticswitch.StatusChangeListener;

public class IPDomainStatusProviderImpl implements IPDomainStatusProvider {
    private static final String SWITCH_KEYNAME = "dal";
    private StaticSwitch statusSwitch = null;

    private volatile static IPDomainStatusProviderImpl instance = null;

    public synchronized static IPDomainStatusProviderImpl getInstance() {
        if (instance == null) {
            instance = new IPDomainStatusProviderImpl();
            instance.initializeSwitch();
        }
        return instance;
    }

    private DataSourceConfigureLocator locator = DataSourceConfigureLocator.getInstance();

    private void initializeSwitch() {
        try {
            statusSwitch = StaticSwitch.getSwitch(SWITCH_KEYNAME);
        } catch (Throwable e) {
            throw new RuntimeException("Error occured while initialize QConfig switch.", e);
        }
    }

    @Override
    public void addIPDomainStatusChangedListener(final IPDomainStatusChanged callback) {
        statusSwitch.addChangeListener(new StatusChangeListener() {
            @Override
            public void changed(boolean value) {
                IPDomainStatus status = value ? IPDomainStatus.IP : IPDomainStatus.Domain;
                IPDomainStatus currentStatus = locator.getIPDomainStatus();
                if (currentStatus.equals(status))
                    return;

                locator.setIPDomainStatus(status);
                callback.onChanged(status);
            }
        });
    }

}
