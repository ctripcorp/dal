package com.ctrip.framework.dal.dbconfig.plugin.ignite;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import org.junit.Before;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class PluginIgniteConfigTest {

    PluginIgniteConfig config;

    @Before
    public void init(){
        config = PluginIgniteConfig.getInstance();
    }

    @Test
    public void testGetIgniteParamValue() throws Exception {
        String ignitePrewarmCacheEnabled = config.getIgniteParamValue(CommonConstants.IGNITE_PREWARM_CACHE_ENABLED);
        System.out.println("ignitePrewarmCacheEnabled=" + ignitePrewarmCacheEnabled);
        assert (true);
    }

}
