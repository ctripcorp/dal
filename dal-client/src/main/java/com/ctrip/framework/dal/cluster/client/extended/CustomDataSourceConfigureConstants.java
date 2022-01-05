package com.ctrip.framework.dal.cluster.client.extended;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfigXMLConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;

/**
 * @Author limingdong
 * @create 2021/10/11
 */
public interface CustomDataSourceConfigureConstants extends DataSourceConfigureConstants {

    String DB_NAME = ClusterConfigXMLConstants.DB_NAME;

    String DATASOURCE_FACTORY = "dataSourceFactory";

}
