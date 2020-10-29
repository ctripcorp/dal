package com.ctrip.platform.dal.application.fireman;

import com.ctrip.platform.dal.dao.configure.FirstAidKit;
import com.ctrip.platform.dal.dao.configure.IDataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.IDataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.SwitchableDataSourceStatus;
import com.ctrip.platform.dal.dao.datasource.ForceSwitchableDataSource;
import com.ctrip.platform.dal.dao.datasource.IForceSwitchableDataSource;

/**
 * @author c7ch23en
 */
public class WrappedForceSwitchableDataSource extends ForceSwitchableDataSource {

    private final IForceSwitchableDataSource dataSource;

    public WrappedForceSwitchableDataSource(IForceSwitchableDataSource dataSource) {
        super(new IDataSourceConfigureProvider() {
            @Override
            public IDataSourceConfigure getDataSourceConfigure() {
                return null;
            }

            @Override
            public IDataSourceConfigure forceLoadDataSourceConfigure() {
                return null;
            }
        });
        this.dataSource = dataSource;
    }

    @Override
    public SwitchableDataSourceStatus forceSwitch(FirstAidKit configure, String ip, Integer port) {
        return super.forceSwitch(configure, ip, port);
    }

    @Override
    public SwitchableDataSourceStatus getStatus() {
        return super.getStatus();
    }

    @Override
    public SwitchableDataSourceStatus restore() {
        return super.restore();
    }

    @Override
    public void addListener(SwitchListener listener) {
        super.addListener(listener);
    }

    @Override
    public FirstAidKit getFirstAidKit() {
        return super.getFirstAidKit();
    }

}
