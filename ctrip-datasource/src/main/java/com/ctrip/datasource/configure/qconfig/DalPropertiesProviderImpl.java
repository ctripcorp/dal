package com.ctrip.datasource.configure.qconfig;

import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesChanged;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesProvider;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;
import qunar.tc.qconfig.client.exception.ResultUnexpectedException;
import qunar.tc.qconfig.common.util.Constants;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lilj on 2018/7/22.
 */
public class DalPropertiesProviderImpl implements DalPropertiesProvider {
    private static final String DAL_APPNAME = "dal";
    private static final String DAL_PROPERTIES = "dal.properties";

    private static final String DALPROPERTIES_GET_MAPCONFIG = "DalProperties::getMapConfig";
    private static final String DALPROPERTIES_GET_PROPERTIES = "DalProperties::getProperties";
    private static final String DALPROPERTIES_ADD_LISTENER = "DalProperties::setListener";
    private static final String DALPROPERTIES_PROPERTIES_CHANGED = "DalProperties::propertiesChanged";

    private static final Object LOCK = new Object();
    private volatile MapConfig mapConfig = null;

    private AtomicReference<Map<String, String>> propertiesRef = new AtomicReference<>();

    @Override
    public Map<String, String> getProperties() {
        try {
            MapConfig config = getMapConfig();
            return getPropertiesMap(config);
        } catch (Throwable e) {
            throw e;
        }
    }

    protected Map<String, String> getPropertiesMap(MapConfig config) {
        Map<String, String> map = new HashMap<>();
        Transaction transaction = Cat.newTransaction(DalLogTypes.DAL, DALPROPERTIES_GET_PROPERTIES);
        try {
            Map<String, String> temp = config.asMap();
            for (Map.Entry<String, String> entry : temp.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }

            propertiesRef.set(map);
            transaction.addData(mapToString(map));
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            if ((e instanceof ResultUnexpectedException) && (((ResultUnexpectedException) e).getStatus() == Constants.FILE_NOT_FIND)) {
                propertiesRef.set(map);
                transaction.addData("local dal.properties not exist, we will use default values later");
                transaction.setStatus(Transaction.SUCCESS);
            } else {
                transaction.setStatus(e);
                Cat.logError("An error occurred while getting dal.properties from QConfig.", e);
            }
        } finally {
            transaction.complete();
        }
        return map;
    }

    @Override
    public void addPropertiesChangedListener(final DalPropertiesChanged callback) {
        Transaction transaction = Cat.newTransaction(DalLogTypes.DAL, DALPROPERTIES_ADD_LISTENER);

        try {
            MapConfig config = getMapConfig();
            config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
                @Override
                public void onLoad(Map<String, String> map) {
                    Transaction transaction = Cat.newTransaction(DalLogTypes.DAL, DALPROPERTIES_PROPERTIES_CHANGED);

                    try {
                        callback.onChanged(map);
                        String oldProperties = mapToString(propertiesRef.get());
                        String newProperties = mapToString(map);
                        propertiesRef.set(map);
                        transaction.addData(String.format("Old dal.properties:%s", oldProperties));
                        transaction.addData(String.format("New dal.properties:%s", newProperties));
                        transaction.setStatus(Transaction.SUCCESS);
                    } catch (Throwable e) {
                        transaction.setStatus(e);
                        Cat.logError(e);
                    } finally {
                        transaction.complete();
                    }
                }
            });
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            Cat.logError("An error occurred while adding listener for dal.properties.", e);
        } finally {
            transaction.complete();
        }
    }

    private MapConfig getMapConfig() {
        if (mapConfig == null) {
            synchronized (LOCK) {
                if (mapConfig == null) {
                    Transaction transaction = Cat.newTransaction(DalLogTypes.DAL, DALPROPERTIES_GET_MAPCONFIG);

                    try {
                        mapConfig = MapConfig.get(DAL_APPNAME, DAL_PROPERTIES, null);
                        transaction.setStatus(Transaction.SUCCESS);
                    } catch (Throwable e) {
                        transaction.setStatus(e);
                        Cat.logError("An error occurred while getting MapConfig from QConfig.", e);
                    } finally {
                        transaction.complete();
                    }
                }
            }
        }

        return mapConfig;
    }

    private String mapToString(Map<String, String> map) {
        if (map == null || map.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(String.format("%s:%s", entry.getKey(), entry.getValue()));
                sb.append(System.lineSeparator());
            }
        } catch (Throwable e) {
        }

        return sb.toString();
    }

}
