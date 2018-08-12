package com.ctrip.datasource.configure.qconfig;

import com.ctrip.datasource.datasource.DalPropertiesChanged;
import com.ctrip.datasource.datasource.DalPropertiesProvider;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lilj on 2018/7/22.
 */
public class DalPropertiesProviderImpl implements DalPropertiesProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DalPropertiesProviderImpl.class);
    private static final String SWITCH_KEYNAME = "TableParseSwitch";
    private static final String DAL_APPNAME = "dal";
    private static final String DAL_PROPERTIES = "dal.properties";

    private AtomicReference<MapConfig> mapConfigReference = new AtomicReference<>();

    private MapConfig getConfig() {
        return mapConfigReference.get();
    }

    @Override
    public TableParseSwitch getTableParseSwitch() {
        refreshDalPropertiesMapConfig();
        return _getTableParseSwitch();
    }

    private void refreshDalPropertiesMapConfig() {
        if (!Foundation.app().isAppIdSet())
            return;

        try {
            MapConfig config = getMapConfig();
            if (config != null) {
                mapConfigReference.set(config);
            }
        } catch (Throwable e) {
            String msg = "从QConfig读取dal.properties配置时发生异常，如果您没有使用配置中心，可以忽略这个异常:" + e.getMessage();
            LOGGER.warn(msg, e);
        }
    }

    private MapConfig getMapConfig() {
        return MapConfig.get(DAL_APPNAME, DAL_PROPERTIES, null); // get dal.properties from QConfig
    }

    @Override
    public void addTableParseSwitchChangedListener(final DalPropertiesChanged callback) {
        final MapConfig config = getConfig();
        if (config == null)
            return;
        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> map) {
                if (map == null || map.isEmpty())
                    throw new RuntimeException("Parameter for onLoad event is null.");

                TableParseSwitch status = _getTableParseSwitch();
                callback.onTableParseSwitchChanged(status);
            }
        });
    }

    private TableParseSwitch _getTableParseSwitch() {
        TableParseSwitch tableParseSwitch = null;
        MapConfig config = mapConfigReference.get();
        if (config == null)
            return tableParseSwitch;

        try {
            Map<String, String> map = config.asMap();
            Boolean status = Boolean.parseBoolean(map.get(SWITCH_KEYNAME));
            String log = "dal.properties 中 TableParseSwitch 配置: " + status.toString();
            LOGGER.info(log);
            tableParseSwitch = status ? TableParseSwitch.ON : TableParseSwitch.OFF;
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }

        return tableParseSwitch;
    }

}
