package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.cluster.base.ListenableSupport;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.MultiHostConnectionStringConfigure;
import com.ctrip.platform.dal.cluster.base.HostSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class MockConnectionStringConfigureProvider extends ListenableSupport<DalConnectionStringConfigure>
        implements ConnectionStringConfigureProvider {

    private final String dbName;
    private boolean isNormalConfig = true;

    public MockConnectionStringConfigureProvider(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public String getDbName() {
        return dbName;
    }

    @Override
    public DalConnectionStringConfigure getConnectionString() throws Exception {
        if (isNormalConfig) {
            return new DalConnectionStringConfigure() {
                @Override
                public String getName() {
                    return dbName;
                }

                @Override
                public String getVersion() {
                    return "1";
                }

                @Override
                public String getHostName() {
                    return "localhost";
                }

                @Override
                public String getUserName() {
                    return "user";
                }

                @Override
                public String getPassword() {
                    return "pwd";
                }

                @Override
                public String getConnectionUrl() {
                    return "jdbc:mysql://localhost:3306/" + dbName;
                }

                @Override
                public String getDriverClass() {
                    return "com.mysql.jdbc.Driver";
                }
            };
        } else {
            return new MultiHostConnectionStringConfigure() {
                @Override
                public List<HostSpec> getHosts() {
                    List<HostSpec> hosts = new ArrayList<>();
                    hosts.add(HostSpec.of("localhost", 3306, "z1"));
                    hosts.add(HostSpec.of("localhost", 3307, "z2"));
                    hosts.add(HostSpec.of("localhost", 3308, "z3"));
                    return hosts;
                }

                @Override
                public String getDbName() {
                    return dbName;
                }

                @Override
                public String getZonesPriority() {
                    return "z3,z2,z1";
                }

                @Override
                public Long getFailoverTimeMS() {
                    return 10000L;
                }

                @Override
                public Long getBlacklistTimeoutMS() {
                    return null;
                }

                @Override
                public Long getFixedValidatePeriodMS() {
                    return null;
                }

                @Override
                public String getName() {
                    return dbName;
                }

                @Override
                public String getVersion() {
                    return null;
                }

                @Override
                public String getUserName() {
                    return "user";
                }

                @Override
                public String getPassword() {
                    return "pwd";
                }

                @Override
                public String getConnectionUrl() {
                    return null;
                }

                @Override
                public String getDriverClass() {
                    return "com.mysql.jdbc.Driver";
                }
            };
        }
    }

    public void setNormalConfig(boolean normalConfig) {
        isNormalConfig = normalConfig;
    }

}
