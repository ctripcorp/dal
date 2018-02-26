package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.exceptions.DalException;

/**
 * Setup at global level to simplify 
 * 
 * @author jhhe
 *
 */
public interface DatabaseSelector {
    String select(SelectionContext context) throws DalException;
}
