package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigTest;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfileTest;
import com.ctrip.framework.dal.dbconfig.plugin.ignite.PluginIgniteConfigTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author c7ch23en
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        PluginConfigTest.class,
        EnvProfileTest.class,
        com.ctrip.framework.dal.dbconfig.plugin.entity.AllTests.class,
        PluginIgniteConfigTest.class,
        com.ctrip.framework.dal.dbconfig.plugin.service.AllTests.class,
        DbConfigAdminPluginTest.class,
        MongoAdminPluginTest.class,
        TitanAdminPluginTest.class,
        TitanServerPluginTest.class
})
public class AllTests {}
