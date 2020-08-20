package com.ctrip.platform.dal.dao.configure;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author c7ch23en
 */
public abstract class InjectableComponentSupport implements InjectableComponent<DatabaseSets> {

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private DatabaseSets databaseSets;

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        if (!initialized.get())
            synchronized (initialized) {
                if (!initialized.get()) {
                    pInitialize(settings);
                    initialized.set(true);
                }
            }
    }

    protected abstract void pInitialize(Map<String, String> settings) throws Exception;

    @Override
    public void inject(DatabaseSets object) {
        if (!initialized.get())
            synchronized (initialized) {
                if (!initialized.get()) {
                    databaseSets = object;
                    return;
                }
            }
        throw new IllegalStateException("Injection forbidden after initialization");
    }

    protected DatabaseSets getDatabaseSets() {
        return databaseSets;
    }

}
