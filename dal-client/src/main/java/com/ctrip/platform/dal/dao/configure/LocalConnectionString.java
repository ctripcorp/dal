package com.ctrip.platform.dal.dao.configure;

/**
 * @author c7ch23en
 */
public class LocalConnectionString extends ConnectionString implements DalLocalConnectionString {

    private final boolean tableShardingDisabled;

    public LocalConnectionString(String name, String ipConnectionString, String domainConnectionString,
                                 boolean tableShardingDisabled) {
        super(name, ipConnectionString, domainConnectionString);
        this.tableShardingDisabled = tableShardingDisabled;
    }

    @Override
    public boolean tableShardingDisabled() {
        return tableShardingDisabled;
    }

}
