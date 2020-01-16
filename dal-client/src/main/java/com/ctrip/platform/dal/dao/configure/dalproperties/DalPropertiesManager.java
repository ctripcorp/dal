package com.ctrip.platform.dal.dao.configure.dalproperties;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lilj on 2018/7/22.
 */
public class DalPropertiesManager {
    private DalPropertiesProvider dalPropertiesProvider = DalElementFactory.DEFAULT.getDalPropertiesProvider();
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);

    private volatile DalPropertiesLocator dalPropertiesLocator = new DefaultDalPropertiesLocator();
    private volatile AbstractDalPropertiesLocator sqlServerDalPropertiesLocator = new SqlServerDalPropertiesLocator();
    private volatile AbstractDalPropertiesLocator mySqlDalPropertiesLocator = new MySqlDalPropertiesLocator();

    private volatile static DalPropertiesManager manager = null;

    public static DalPropertiesManager getInstance() {
        if (manager == null) {
            synchronized (DalPropertiesManager.class) {
                if (manager == null)
                    manager = new DalPropertiesManager();
            }
        }
        return manager;
    }

    public void setup() {
        setupOnlyOnce();
    }

    private void setupOnlyOnce() {
        if (!isInitialized.get()) {
            synchronized (isInitialized) {
                if (!isInitialized.get()) {
                    Map<String, String> properties = dalPropertiesProvider.getProperties();
                    dalPropertiesLocator.setProperties(properties);
                    sqlServerDalPropertiesLocator.setProperties(properties);
                    mySqlDalPropertiesLocator.setProperties(properties);
                    addPropertiesChangedListener();
                    isInitialized.set(true);
                }
            }
        }
    }

    private void addPropertiesChangedListener() {
        dalPropertiesProvider.addPropertiesChangedListener(new DalPropertiesChanged() {
            @Override
            public void onChanged(Map<String, String> map) {
                dalPropertiesLocator.setProperties(map);
                sqlServerDalPropertiesLocator.setProperties(map);
                mySqlDalPropertiesLocator.setProperties(map);
            }
        });
    }

    public void setDalPropertiesProvider(DalPropertiesProvider provider) {
        this.dalPropertiesProvider = provider;
    }

    public DalPropertiesLocator getDalPropertiesLocator() {
        setupOnlyOnce();
        return dalPropertiesLocator;
    }

    public AbstractDalPropertiesLocator getSqlServerDalPropertiesLocator() {
        setupOnlyOnce();
        return sqlServerDalPropertiesLocator;
    }

    public AbstractDalPropertiesLocator getMySqlDalPropertiesLocator() {
        setupOnlyOnce();
        return mySqlDalPropertiesLocator;
    }

    public void tearDown(){
        synchronized (isInitialized) {
            isInitialized.set(false);
            Map<String, String> dalProperties = new HashMap<>();
            dalProperties.put(DefaultDalPropertiesLocator.TABLE_PARSE_SWITCH_KEYNAME, "true");
            dalProperties.put(DefaultDalPropertiesLocator.IMPLICIT_ALL_SHARDS_SWITCH, "false");
            dalPropertiesLocator.setProperties(dalProperties);
        }
    }
}
