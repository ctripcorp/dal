package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigTest;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfileTest;
import com.ctrip.framework.dal.dbconfig.plugin.entity.*;
import com.ctrip.framework.dal.dbconfig.plugin.handler.AdminHandlerDispatcherTest;
import com.ctrip.framework.dal.dbconfig.plugin.handler.titan.TitanKeyGetHandlerTest;
import com.ctrip.framework.dal.dbconfig.plugin.ignite.PluginIgniteConfigTest;
import com.ctrip.framework.dal.dbconfig.plugin.service.DefaultDataSourceCryptoTest;
import com.ctrip.framework.dal.dbconfig.plugin.service.Soa2KeyServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author c7ch23en
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        PluginConfigTest.class,
        EnvProfileTest.class,

        // entity
        ConnectionCheckInputTester.class,
        ConnectionCheckOutputTester.class,
        KeyGetOutputTester.class,
        MhaInputTester.class,
        SiteInputTester.class,
        SiteOutputTester.class,
        SoaKeyResponseTester.class,

        // handler
        AdminHandlerDispatcherTest.class,
        TitanKeyGetHandlerTest.class,

        PluginIgniteConfigTest.class,

        // service
        DefaultDataSourceCryptoTest.class,
        Soa2KeyServiceTest.class,

        DbConfigAdminPluginTest.class,
        MongoAdminPluginTest.class,
        MongoServerPluginTest.class,
        TitanAdminPluginTest.class,
        TitanServerPluginTest.class
})
public class AllTests {}
