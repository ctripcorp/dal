package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.datasource.ClosableDataSource;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.exceptions.UnsupportedFeatureException;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class ClusterDataSource extends DataSourceDelegate implements DataSource {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private final Cluster cluster;
    private final DataSourceConfigureProvider provider;
    private final AtomicReference<DataSource> dataSourceRef = new AtomicReference<>();

    public ClusterDataSource(Cluster cluster, DataSourceConfigureProvider provider) {
        this.cluster = cluster;
        this.provider = provider;
        prepare();
    }

    protected void prepare() {
        cluster.addListener(event -> {
            try {
                DataSource previous = dataSourceRef.getAndSet(createInnerDataSource());
                if (previous instanceof ClosableDataSource)
                    ((ClosableDataSource) previous).close();
            } catch (Throwable t) {
                String msg = "Cluster switch listener error";
                LOGGER.error(msg, t);
                throw new DalRuntimeException(msg, t);
            }
        });
        dataSourceRef.set(createInnerDataSource());
    }

    protected DataSource createInnerDataSource() {
        return null;
    }

    protected void check() {
        if (cluster.dbShardingEnabled())
            throw new UnsupportedFeatureException("ClusterDataSource does not support sharding cluster, " +
                    "cluster name: " + cluster.getClusterName());
    }

    @Override
    public DataSource getDelegated() {
        return dataSourceRef.get();
    }

}
