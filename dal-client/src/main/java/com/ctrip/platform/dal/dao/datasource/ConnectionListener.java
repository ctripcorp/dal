package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.Ordered;

import java.sql.Connection;

/**
 * @author wenchao.meng
 *         <p>
 *         Feb 08, 2018
 */
public interface ConnectionListener extends Ordered{

    void onCreateConnection(String poolDesc, Connection connection);


    void onReleaseConnection(String poolDesc, Connection connection);


}
