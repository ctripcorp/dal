package com.ctrip.platform.dal.dao.configure.dalproperties;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lilj on 2018/7/22.
 */
public class DalPropertiesManager {
    private DalPropertiesProvider dalPropertiesProvider = DalElementFactory.DEFAULT.getDalPropertiesProvider();
    private AtomicBoolean isPropertiesListenerAdded = new AtomicBoolean(false);

    private DalPropertiesLocator dalPropertiesLocator = new DefaultDalPropertiesLocator();
    private AbstractDalPropertiesLocator sqlServerDalPropertiesLocator = new SqlServerDalPropertiesLocator();
    private AbstractDalPropertiesLocator mySqlDalPropertiesLocator = new MySqlDalPropertiesLocator();

    private volatile static DalPropertiesManager manager = null;

    public synchronized static DalPropertiesManager getInstance() {
        if (manager == null) {
            manager = new DalPropertiesManager();
        }
        return manager;
    }

    public synchronized void setup() {
        // set dal.properties
        Map<String, String> properties = dalPropertiesProvider.getProperties();

        dalPropertiesLocator.setProperties(properties);
        sqlServerDalPropertiesLocator.setProperties(properties);
        mySqlDalPropertiesLocator.setProperties(properties);

        boolean isListenerAdded = isPropertiesListenerAdded.get();
        if (!isListenerAdded) {
            addPropertiesChangedListener();
            isPropertiesListenerAdded.compareAndSet(false, true);
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
        return dalPropertiesLocator;
    }

    public AbstractDalPropertiesLocator getSqlServerDalPropertiesLocator() {
        return sqlServerDalPropertiesLocator;
    }

    public AbstractDalPropertiesLocator getMySqlDalPropertiesLocator() {
        return mySqlDalPropertiesLocator;
    }

}
