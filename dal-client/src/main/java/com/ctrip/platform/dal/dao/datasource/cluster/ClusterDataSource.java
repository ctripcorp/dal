package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.ConnectionString;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.datasource.*;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalDataSource;
import com.ctrip.platform.dal.dao.datasource.log.ClusterDbSqlContext;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.UnsupportedFeatureException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class ClusterDataSource extends DalDataSource implements DataSource,
        ClosableDataSource, SingleDataSourceWrapper, DataSourceConfigureChangeListener {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private static final String CAT_LOG_NAME_MULTI_HOST = "createMultiHostDataSource:%s";
    private static final String CAT_LOG_NAME_MULTI_HOST_FAIL = "createMultiHostDataSource:EXCEPTION:%s";

    private final DataSourceIdentity id;
    private final Cluster cluster;
    private final DataSourceConfigureProvider provider;
    private final MultiHostDataSource delegated;

    public ClusterDataSource(DataSourceIdentity id, Cluster cluster, DataSourceConfigureProvider provider) {
        super(id);
        this.id = id;
        this.cluster = cluster;
        this.provider = provider;
        check();
        this.delegated = createInnerDataSource();
        warmUp();
    }

    public String getClusterName() {
        return cluster.getClusterName();
    }

    // TODO: to be refactored when sharding supported
    public int getShardIndex() {
        return 0;
    }

    protected void check() {
        if (cluster.dbShardingEnabled())
            throw new UnsupportedFeatureException("ClusterDataSource does not support sharding cluster, " +
                    "cluster name: " + cluster.getClusterName());
    }

    protected MultiHostDataSource createInnerDataSource() {
        try {
            List<Database> databases = cluster.getDatabases();
            Map<HostSpec, DataSourceConfigure> dataSourceConfigs = new HashMap<>();
            databases.forEach(database -> {
                ConnectionString connString = database.getConnectionString();
                HostSpec host = HostSpec.of(connString.getPrimaryHost(), connString.getPrimaryPort(), database.getZone());
                DataSourceIdentity id = new ClusterDataSourceIdentity(database);
                DataSourceConfigure config = provider.getDataSourceConfigure(id);
                dataSourceConfigs.put(host, config);
            });
            MultiHostClusterProperties properties = new MultiHostClusterPropertiesAdapter(cluster.getRouteStrategyConfig());
            LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format(CAT_LOG_NAME_MULTI_HOST, getClusterName()), "");
            return new MultiHostDataSource(buildShardMeta(dataSourceConfigs.keySet()), dataSourceConfigs, properties);
        } catch (Throwable t) {
            LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format(CAT_LOG_NAME_MULTI_HOST_FAIL, getClusterName()), "");
            throw t;
        }
    }

    private ShardMeta buildShardMeta(Set<HostSpec> configuredHosts) {
        return new ShardMeta() {
            @Override
            public int shardIndex() {
                return getShardIndex();
            }

            @Override
            public Set<HostSpec> configuredHosts() {
                return configuredHosts;
            }

            @Override
            public String clusterName() {
                return getClusterName();
            }
        };
    }

    public void warmUp() {
        try (Connection connection = getConnection()) {
            // do nothing
        } catch (SQLException e) {
            LOGGER.warn("warm up exception", e);
        }
    }

    @Override
    public void close() {
        LOGGER.info(String.format("close ClusterDataSource '%s'", id.getId()));
        if (delegated != null)
            delegated.close();
    }

    @Override
    public void configChanged(DataSourceConfigureChangeEvent event) throws SQLException {
        // TODO: listen to datasource.properties changes
    }

    @Override
    public SingleDataSource getSingleDataSource() {
        return delegated != null ? delegated.getSingleDataSource() : null;
    }

    @Override
    public void forceRefreshDataSource(String name, DataSourceConfigure configure) {}

    @Override
    protected SqlContext createSqlContext() {
        SqlContext context = id.createSqlContext();
        if (context instanceof ClusterDbSqlContext) {
            ((ClusterDbSqlContext) context).populateShard(getShardIndex());
            ((ClusterDbSqlContext) context).populateRole(DatabaseRole.MASTER);
        }
        return context;
    }

    @Override
    public DatabaseCategory getDatabaseCategory() {
        return DatabaseCategory.MySql;
    }

    @Override
    public DataSource getDelegated() {
        return delegated;
    }

    @Override
    public void handleException(SQLException e, boolean isUpdateOperation, Connection connection) {
        super.handleException(e, isUpdateOperation, connection);
        if (delegated != null)
            delegated.handleException(e, isUpdateOperation, connection);
    }

}
