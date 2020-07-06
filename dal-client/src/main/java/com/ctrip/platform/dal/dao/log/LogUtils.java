package com.ctrip.platform.dal.dao.log;

import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.IClusterDataSourceIdentity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class LogUtils {

    public static Map<String, String> buildPropertiesFromDataSourceId(DataSourceIdentity dataSourceId) {
        if (dataSourceId instanceof IClusterDataSourceIdentity) {
            IClusterDataSourceIdentity _dataSourceId = (IClusterDataSourceIdentity) dataSourceId;
            Map<String, String> properties = new HashMap<>();
            if (_dataSourceId.getClusterName() != null)
                properties.put("DAL.cluster", _dataSourceId.getClusterName());
            if (_dataSourceId.getShardIndex() != null)
                properties.put("DAL.cluster.shard", String.valueOf(_dataSourceId.getShardIndex()));
            if (_dataSourceId.getDatabaseRole() != null)
                properties.put("DAL.cluster.role", _dataSourceId.getDatabaseRole().getValue());
            return properties;
        }
        return null;
    }

}
