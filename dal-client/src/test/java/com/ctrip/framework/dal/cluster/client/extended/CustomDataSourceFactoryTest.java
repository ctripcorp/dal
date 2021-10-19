package com.ctrip.framework.dal.cluster.client.extended;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalDataSource;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static com.ctrip.framework.dal.cluster.client.extended.CustomDataSourceConfigureConstants.DATASOURCE_FACTORY;
import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.DRIVER_CLASS_NAME;

/**
 * @Author limingdong
 * @create 2021/10/19
 */
public class CustomDataSourceFactoryTest {

    private static final String DATASOURCE_FACTORY_PATH = "com.ctrip.framework.dal.cluster.client.extendedCustomDataSourceFactoryTest$MockHiveCustomDataSourceFactory.class";

    private static final String DRIVER = "com.driver";

    private static final String TYPE_KEY = "type";

    private static final String TYPE_VALUE = "hive";

    private CustomDataSourceFactory dataSourceFactory;

    private HostSpec hostSpec = HostSpec.of("test_ip", 123, "test_zone");

    private Set<HostSpec> hostSpecs = new HashSet<>();

    private Properties properties = new Properties();

    @Before
    public void setUp() throws Exception {
        hostSpecs.add(hostSpec);

        properties.put(DATASOURCE_FACTORY, DATASOURCE_FACTORY_PATH);
        properties.put(DRIVER_CLASS_NAME, DRIVER);
        properties.put(TYPE_KEY, TYPE_VALUE);
        dataSourceFactory = new MockHiveCustomDataSourceFactory();
    }

    @Test
    public void createDataSource() {
        DataSource dataSource = dataSourceFactory.createDataSource(hostSpecs, properties);
        Assert.assertNotNull(dataSource);
    }

    class MockHiveCustomDataSourceFactory implements CustomDataSourceFactory {

        @Override
        public DataSource createDataSource(Set<HostSpec> hosts, Properties info) {
            if (TYPE_VALUE.equalsIgnoreCase(info.getProperty(TYPE_KEY))) {
                DataSourceIdentity sourceIdentity = new DataSourceIdentity() {
                    @Override
                    public String getId() {
                        return hosts.toString();
                    }

                    @Override
                    public SqlContext createSqlContext() {
                        return null;
                    }
                };
                return new MockHiveDataSource(sourceIdentity);
            }

            return null;
        }

        @Override
        public String type() {
            return TYPE_VALUE;
        }
    }

    class MockHiveDataSource extends DalDataSource implements DataSource {

        public MockHiveDataSource(DataSourceIdentity dataSourceId) {
            super(dataSourceId);
        }

        @Override
        protected SqlContext createSqlContext() {
            return null;
        }

        @Override
        public DatabaseCategory getDatabaseCategory() {
            return DatabaseCategory.Custom;
        }

        @Override
        public DataSource getDelegated() {
            return null;
        }
    }
}