package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.FirstAidKit;
import com.ctrip.platform.dal.dao.configure.IDataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.IDataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.SwitchableDataSourceStatus;

/**
 * @author c7ch23en
 */
public class WrappedForceSwitchableDataSource extends ForceSwitchableDataSource {

    private final IForceSwitchableDataSource dataSource;

    public WrappedForceSwitchableDataSource(IForceSwitchableDataSource dataSource) {
        super(new NullDataSourceConfigureProvider());
        this.dataSource = dataSource;
    }

    @Override
    public SwitchableDataSourceStatus forceSwitch(FirstAidKit configure, String ip, Integer port) {
        return dataSource.forceSwitch(configure, ip, port);
    }

    @Override
    public FirstAidKit getFirstAidKit() {
        return dataSource != null ? dataSource.getFirstAidKit() : null;
    }

    @Override
    public SwitchableDataSourceStatus getStatus() {
        return dataSource != null ? dataSource.getStatus() : null;
    }

    @Override
    public SwitchableDataSourceStatus restore() {
        return dataSource.restore();
    }

    @Override
    public void addListener(SwitchListener listener) {
        dataSource.addListener(listener);
    }

    public IForceSwitchableDataSource getInnerDataSource() {
        return dataSource;
    }

    private static class NullDataSourceConfigureProvider implements IDataSourceConfigureProvider {
        @Override
        public IDataSourceConfigure getDataSourceConfigure() {
            return null;
        }

        @Override
        public IDataSourceConfigure forceLoadDataSourceConfigure() {
            return null;
        }
    }

}
