package com.ctrip.framework.dal.cluster.client.extended;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/10/8
 */
public interface CustomDataSourceFactory extends CustomDataSourceConfigureConstants {

    DataSource createDataSource(Set<HostSpec> hosts, Properties info);

    default String type() {
        return Type.ch.name();
    }

    enum Type {
        ch()
    }
}
