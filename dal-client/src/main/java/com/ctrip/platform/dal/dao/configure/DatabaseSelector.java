package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.exceptions.DalException;

import java.util.HashMap;

/**
 * Setup at global level to simplify
 * 
 * @author jhhe
 *
 */
public interface DatabaseSelector extends DalComponent {
    DataBase select(SelectionContext context) throws DalException;
    HashMap<String, Object> parseDalHints(DalHints dalHints);
}
