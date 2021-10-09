package com.ctrip.framework.dal.cluster.client.extended;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/10/8
 */
public interface CustomDataSourceFactory extends DataSourceConfigureConstants {

    DataSource createDataSource(Set<HostSpec> hosts, Properties info);
}
