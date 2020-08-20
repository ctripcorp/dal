package com.ctrip.platform.dal.dao.configure;

import java.util.Collection;

/**
 * @author c7ch23en
 */
public interface DatabaseSets {

    Collection<DatabaseSet> getAll();

    Collection<DatabaseSet> getByDatabaseKey(String key);

}
