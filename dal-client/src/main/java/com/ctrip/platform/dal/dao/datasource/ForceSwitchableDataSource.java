package com.ctrip.platform.dal.dao.datasource;


import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public class ForceSwitchableDataSource extends RefreshableDataSource implements IForceSwitchableDataSource {
    private IDataSourceConfigureProvider provider;
    private SwitchableDataSourceStatus switchableDataSourceStatus;
    private CopyOnWriteArraySet<SwitchListener> listeners = new CopyOnWriteArraySet<>();
    private static Map<String, String> cache = new ConcurrentHashMap<>();

    public ForceSwitchableDataSource(String name, IDataSourceConfigureProvider provider) throws SQLException {
        super(name, DataSourceConfigure.valueOf(provider.forceLoadDataSourceConfigure()));
        this.provider = provider;
        switchableDataSourceStatus = new SwitchableDataSourceStatus(false, provider.getDataSourceConfigure().getHostName(), provider.getDataSourceConfigure().getPort());
    }

    public SwitchableDataSourceStatus forceSwitch(String ip, Integer port) {
//        new configure with ip and port
        String name = getSingleDataSource().getName();
        DataSourceConfigure configure = getSingleDataSource().getDataSourceConfigure().clone();
        SwitchableDataSourceStatus oldStatus = new SwitchableDataSourceStatus(switchableDataSourceStatus.isForceSwitched(), switchableDataSourceStatus.getHostName(), switchableDataSourceStatus.getPort());
        configure.replaceURL(ip, port);

        try {
            refreshDataSource(name, configure, new DefaultForceSwitchListener());
        } catch (SQLException e) {
            throw new DalRuntimeException(e);
        }

//          返回之前的status
        return oldStatus;
    }

    public SwitchableDataSourceStatus getStatus() {
//        检查连接和 ds的配置，如果一样就返回，不一样就再拿一次存起来
        org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) getSingleDataSource().getDataSource();

        String url = ds.getUrl();

        if(isUrlChanged(url))
            switchableDataSourceStatus = new SwitchableDataSourceStatus(switchableDataSourceStatus.isForceSwitched(), ConnectionStringParser.parseHostNameFromURL(url), Integer.parseInt(ConnectionStringParser.parsePortFromURL(url)));

        return switchableDataSourceStatus;
    }

    public SwitchableDataSourceStatus restore() {
        String name = getSingleDataSource().getName();
        DataSourceConfigure configure = DataSourceConfigure.valueOf(provider.forceLoadDataSourceConfigure());

        SwitchableDataSourceStatus oldStatus = new SwitchableDataSourceStatus(switchableDataSourceStatus.isForceSwitched(), switchableDataSourceStatus.getHostName(), switchableDataSourceStatus.getPort());

        try {
            refreshDataSource(name, configure, new DefaultRestoreListener());
        } catch (SQLException e) {
            throw new DalRuntimeException(e);
        }
        //        返回之前的status
        return oldStatus;
    }

    public void addListener(SwitchListener listener) {
        listeners.add(listener);
    }

    private synchronized void addCache(String url) {
        cache.put(DataSourceConfigure.HOST_NAME, ConnectionStringParser.parseHostNameFromURL(url));
        cache.put(DataSourceConfigure.PORT, ConnectionStringParser.parsePortFromURL(url));
        cache.put(DataSourceConfigure.CONNECTION_URL,url);
    }

    private synchronized boolean isUrlChanged(String url){
        return url.equalsIgnoreCase(cache.get(DataSourceConfigure.CONNECTION_URL));
    }

    private class DefaultForceSwitchListener implements ForceSwitchListener{
        public void onCreatePoolSuccess(DataSourceConfigure configure){
            for (SwitchListener listener : listeners)
                listener.onForceSwitchSuccess(new SwitchableDataSourceStatus(true, configure.getHostName(), configure.getPort(), true));
             addCache(configure.getConnectionUrl());
        }

        public void onCreatePoolFail(DataSourceConfigure configure,Throwable e) {
            for (SwitchListener listener : listeners)
                listener.onForceSwitchFail(new SwitchableDataSourceStatus(true, configure.getHostName(), configure.getPort(), false), e);
            addCache(configure.getConnectionUrl());
        }
    }

    private class DefaultRestoreListener implements RestoreListener{
        public void onCreatePoolSuccess(DataSourceConfigure configure) {
            for (SwitchListener listener : listeners)
                listener.onRestoreSuccess(new SwitchableDataSourceStatus(false, configure.getHostName(), configure.getPort(), true));
            addCache(configure.getConnectionUrl());
        }

        public void onCreatePoolFail(DataSourceConfigure configure, Throwable e) {
            for (SwitchListener listener : listeners)
                listener.onRestoreFail(new SwitchableDataSourceStatus(false, configure.getHostName(), configure.getPort(), false), e);
            addCache(configure.getConnectionUrl());
        }
    }

}
